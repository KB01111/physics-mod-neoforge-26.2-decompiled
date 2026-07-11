#version 150

in vec2 vUV;
out vec4 fragColor;

uniform sampler2D physics_depth;

uniform sampler3D  physics_density0;
uniform sampler3D  physics_density1;
uniform sampler3D  physics_density2;
uniform sampler3D  physics_density3;

uniform usampler3D physics_occupancy0;
uniform usampler3D physics_occupancy1;
uniform usampler3D physics_occupancy2;
uniform usampler3D physics_occupancy3;

uniform sampler3D  physics_light0;
uniform sampler3D  physics_light1;
uniform sampler3D  physics_light2;
uniform sampler3D  physics_light3;

uniform usamplerBuffer PhysicsSmokeMeta;

layout(std140) uniform PhysicsSmokeVolumeFrame {
    mat4 uInvViewProj;
    mat4 uPrevViewProj;
    ivec4 uParams0;
    vec4 uGrid;
    vec4 uRaymarch;
    vec4 uShadow;
    vec4 uSmokeColor;
    vec4 uLight0;
    vec4 uLight1;
    vec4 uLight2;
    vec4 uCameraPos;
    vec4 uCascadeSize[4];
    vec4 uBoundsMin[4];
    vec4 uBoundsMax[4];
    ivec4 uCascadeOffsets[4];
    int zZeroToOne;
    int uReverseZ;
};

layout(std140) uniform Fog {
    vec4 FogColor;
    float FogEnvironmentalStart;
    float FogEnvironmentalEnd;
    float FogRenderDistanceStart;
    float FogRenderDistanceEnd;
    float FogSkyEnd;
    float FogCloudsEnd;
};

float smoke_linear_fog_value(float vertexDistance, float fogStart, float fogEnd) {
    if (vertexDistance <= fogStart) {
        return 0.0;
    } else if (vertexDistance >= fogEnd) {
        return 1.0;
    }

    return (vertexDistance - fogStart) / max(fogEnd - fogStart, 1e-6);
}

float smoke_total_fog_value(float sphericalVertexDistance, float cylindricalVertexDistance) {
    return max(
        smoke_linear_fog_value(sphericalVertexDistance, FogEnvironmentalStart, FogEnvironmentalEnd),
        smoke_linear_fog_value(cylindricalVertexDistance, FogRenderDistanceStart, FogRenderDistanceEnd)
    );
}

vec3 apply_smoke_fog_premultiplied(vec3 premultipliedColor, float alpha, float sphericalVertexDistance, float cylindricalVertexDistance) {
    float fogValue = smoke_total_fog_value(sphericalVertexDistance, cylindricalVertexDistance);
    float fogBlend = clamp(fogValue * FogColor.a, 0.0, 1.0);
    return mix(premultipliedColor, FogColor.rgb * alpha, fogBlend);
}

const int MAX_CASCADES = 4;
const int MAX_STEPS = 420;
const int MAX_SHADOW_STEPS = 16;
const int BRICK = 16;
const uint META_STRIDE = 8u;
const float PI = 3.14159265;
const float JITTER_STRENGTH = 1.0;
const float SIGMA_CLAMP = 200.0;

float hash12(vec2 p) {
    vec3 p3 = fract(vec3(p.xyx) * 0.1031);
    p3 += dot(p3, p3.yzx + 33.33);
    return fract((p3.x + p3.y) * p3.z);
}

float depth01ToNdcZ(float z01) {
    return (zZeroToOne == 1) ? z01 : (z01 * 2.0 - 1.0);
}

vec3 unproject01(vec2 uv, float z01) {
    vec2 ndc = uv * 2.0 - 1.0;
    vec4 h = uInvViewProj * vec4(ndc, depth01ToNdcZ(z01), 1.0);

    float invW = (abs(h.w) > 1e-6) ? (1.0 / h.w) : 0.0;
    return h.xyz * invW;
}

bool rayBoxIntersect(vec3 ro, vec3 rd, vec3 bmin, vec3 bmax, out float t0, out float t1) {
    vec3 invD = 1.0 / rd;
    vec3 tBot = (bmin - ro) * invD;
    vec3 tTop = (bmax - ro) * invD;
    vec3 tMin = min(tBot, tTop);
    vec3 tMax = max(tBot, tTop);
    t0 = max(max(tMin.x, tMin.y), tMin.z);
    t1 = min(min(tMax.x, tMax.y), tMax.z);
    return t1 >= max(t0, 0.0);
}

bool pointInAABB(vec3 p, vec3 bmin, vec3 bmax) {
    return all(greaterThanEqual(p, bmin)) && all(lessThanEqual(p, bmax));
}

float edgeDistUVW(vec3 uvw) {
    vec3 e = min(uvw, vec3(1.0) - uvw);
    return min(e.x, min(e.y, e.z));
}

float stepDtForCascade(int c) {
    return uCascadeSize[c].w * max(0.01, uRaymarch.y);
}

float phaseHG(float cosT, float g) {
    float g2 = g * g;
    float denom = pow(max(1.0 + g2 - 2.0 * g * cosT, 1e-4), 1.5);
    return (1.0 - g2) / max(4.0 * PI * denom, 1e-6);
}

bool cascadeOccupied(int c) {
    return texelFetch(PhysicsSmokeMeta, c * int(META_STRIDE) + 0).r != 0u;
}

ivec3 cascadeMetaMinVoxel(int c) {
    ivec3 occSize = (ivec3(uGrid.xyz) + BRICK - 1) / BRICK;
    return ivec3(
        int(occSize.x * BRICK - 1) - int(texelFetch(PhysicsSmokeMeta, c * int(META_STRIDE) + 4).r),
        int(occSize.y * BRICK - 1) - int(texelFetch(PhysicsSmokeMeta, c * int(META_STRIDE) + 5).r),
        int(occSize.z * BRICK - 1) - int(texelFetch(PhysicsSmokeMeta, c * int(META_STRIDE) + 6).r)
    );
}

ivec3 cascadeMetaMaxVoxel(int c) {
    return ivec3(
        int(texelFetch(PhysicsSmokeMeta, c * int(META_STRIDE) + 1).r),
        int(texelFetch(PhysicsSmokeMeta, c * int(META_STRIDE) + 2).r),
        int(texelFetch(PhysicsSmokeMeta, c * int(META_STRIDE) + 3).r)
    );
}

void getCascadeWrittenBounds(int c, out vec3 bminW, out vec3 bmaxW) {
    vec3 fullMin = uBoundsMin[c].xyz;
    vec3 fullMax = uBoundsMax[c].xyz;
    vec3 sizeW = fullMax - fullMin;
    ivec3 occSize = (ivec3(uGrid.xyz) + BRICK - 1) / BRICK;
    vec3 cellW = sizeW / vec3(occSize * BRICK);

    ivec3 minV = clamp(cascadeMetaMinVoxel(c), ivec3(0), (occSize * BRICK) - ivec3(1));
    ivec3 maxV = clamp(cascadeMetaMaxVoxel(c), ivec3(0), (occSize * BRICK) - ivec3(1));

    bminW = fullMin + vec3(minV) * cellW;
    bmaxW = fullMin + vec3(maxV + ivec3(1)) * cellW;
}

int outermostOccupiedCascade() {
    int last = -1;
    for (int c = 0; c < MAX_CASCADES; ++c) {
        if (c >= uParams0.x) break;
        if (cascadeOccupied(c)) last = c;
    }
    return last;
}

int finestContainingCascade(vec3 p, int farIdx) {
    for (int c = 0; c < MAX_CASCADES; ++c) {
        if (c >= uParams0.x || c > farIdx) break;
        if (pointInAABB(p, uBoundsMin[c].xyz, uBoundsMax[c].xyz)) {
            return c;
        }
    }
    return -1;
}

float skipLeadingEmptyCascades(vec3 ro, vec3 rd, float t, int farIdx) {
    vec3 p = ro + rd * t;

    int firstContaining = finestContainingCascade(p, farIdx);
    if (firstContaining < 0) {
        return t;
    }

    if (cascadeOccupied(firstContaining)) {
        return t;
    }

    float bestExit = t;
    int lastEmpty = -1;

    for (int c = firstContaining; c < MAX_CASCADES; ++c) {
        if (c >= uParams0.x || c > farIdx) break;
        if (!pointInAABB(p, uBoundsMin[c].xyz, uBoundsMax[c].xyz)) break;
        if (cascadeOccupied(c)) break;

        float t0, t1;
        if (rayBoxIntersect(ro, rd, uBoundsMin[c].xyz, uBoundsMax[c].xyz, t0, t1)) {
            if (t1 > bestExit) {
                bestExit = t1;
                lastEmpty = c;
            }
        }
    }

    if (lastEmpty < 0) {
        return t;
    }

    float eps = 1e-3 * stepDtForCascade(min(lastEmpty + 1, farIdx));
    return bestExit + eps;
}

float sampleDensity(int c, vec3 uvw) {
    if (c == 0) return texture(physics_density0, uvw).r;
    if (c == 1) return texture(physics_density1, uvw).r;
    if (c == 2) return texture(physics_density2, uvw).r;
    return texture(physics_density3, uvw).r;
}

vec3 sampleLight(int c, vec3 uvw) {
    if (c == 0) return texture(physics_light0, uvw).rgb;
    if (c == 1) return texture(physics_light1, uvw).rgb;
    if (c == 2) return texture(physics_light2, uvw).rgb;
    return texture(physics_light3, uvw).rgb;
}

uint fetchOcc(int c, ivec3 tc) {
    if (c == 0) return texelFetch(physics_occupancy0, tc, 0).r;
    if (c == 1) return texelFetch(physics_occupancy1, tc, 0).r;
    if (c == 2) return texelFetch(physics_occupancy2, tc, 0).r;
    return texelFetch(physics_occupancy3, tc, 0).r;
}

float exitOccCellFast(vec3 uvw, vec3 rd, vec3 sizeW, ivec3 cell, vec3 invOccSize) {
    vec3 invSizeW = 1.0 / max(sizeW, vec3(1e-9));
    vec3 dir = rd * invSizeW;

    vec3 cellMin = vec3(cell) * invOccSize;
    vec3 cellMax = cellMin + invOccSize;

    vec3 bound;
    bound.x = (dir.x >= 0.0) ? cellMax.x : cellMin.x;
    bound.y = (dir.y >= 0.0) ? cellMax.y : cellMin.y;
    bound.z = (dir.z >= 0.0) ? cellMax.z : cellMin.z;

    vec3 t3;
    t3.x = (abs(dir.x) > 1e-10) ? ((bound.x - uvw.x) / dir.x) : 1e20;
    t3.y = (abs(dir.y) > 1e-10) ? ((bound.y - uvw.y) / dir.y) : 1e20;
    t3.z = (abs(dir.z) > 1e-10) ? ((bound.z - uvw.z) / dir.z) : 1e20;

    float tExit = min(t3.x, min(t3.y, t3.z));
    return max(tExit, 0.0);
}

float shadowTransmittanceCoarse(vec3 p, vec3 ldir, int cascade) {
    if (!cascadeOccupied(cascade)) return 1.0;

    vec3 clipMin, clipMax;
    getCascadeWrittenBounds(cascade, clipMin, clipMax);

    float t0, t1;
    if (!rayBoxIntersect(p, ldir, clipMin, clipMax, t0, t1)) return 1.0;

    vec3 fullMin = uBoundsMin[cascade].xyz;
    vec3 fullSize = uBoundsMax[cascade].xyz - fullMin;

    float dtS = stepDtForCascade(cascade) * uShadow.x;
    float tS = max(t0, 0.0);

    float tau = 0.0;

    for (int s = 0; s < MAX_SHADOW_STEPS; ++s) {
        if (tS > t1) break;

        vec3 ps = p + ldir * tS;
        vec3 uvwS = (ps - fullMin) / fullSize;

        float d = sampleDensity(cascade, clamp(uvwS, vec3(0.0), vec3(1.0)));
        float sigmaT = min(d * uRaymarch.x, SIGMA_CLAMP);

        tau += sigmaT * dtS;
        if (tau > 6.0) break;
        tS += dtS;
    }

    return exp(-tau * uShadow.y);
}

struct VolOut {
    vec3 L;
    float T;
    float sphericalFogDistance;
    float cylindricalFogDistance;
};

VolOut renderVolume(vec3 ro, vec3 rd, float tMaxClamp) {
    int farIdx = outermostOccupiedCascade();
    if (farIdx < 0) {
        return VolOut(vec3(0.0), 1.0, 0.0, 0.0);
    }

    vec3 marchMin, marchMax;
    getCascadeWrittenBounds(farIdx, marchMin, marchMax);

    float tEnter, tExit;
    if (!rayBoxIntersect(ro, rd, marchMin, marchMax, tEnter, tExit)) {
        return VolOut(vec3(0.0), 1.0, 0.0, 0.0);
    }

    float t = max(tEnter, 0.0);
    float tMax = min(tExit, tMaxClamp);
    if (t > tMax) return VolOut(vec3(0.0), 1.0, 0.0, 0.0);

    float dtJ = stepDtForCascade(0);
    t += (hash12(gl_FragCoord.xy) - 0.5) * dtJ * JITTER_STRENGTH;

    t = skipLeadingEmptyCascades(ro, rd, t, farIdx);
    if (t > tMax) return VolOut(vec3(0.0), 1.0, 0.0, 0.0);

    vec3 L = vec3(0.0);
    float T = 1.0;
    float fogWeight = 0.0;
    float sphericalFogDistance = 0.0;
    float cylindricalFogDistance = 0.0;

    vec3 ldirN = normalize(-uLight0.xyz);
    float ph = phaseHG(dot(rd, ldirN), 0.4);
    ph = 1.0;

    float albedo = clamp(uLight1.w, 0.0, 1.0);
    int shadowMask = max(int(uShadow.z + 0.5), 1) - 1;
    ivec3 occSize = (ivec3(uGrid.xyz) + BRICK - 1) / BRICK;
    ivec3 occPackSize = (occSize + 1) >> 1;
    vec3 invOccSize = 1.0 / max(vec3(occSize), vec3(1.0));
    vec3 invOccPackSize = 1.0 / max(vec3(occPackSize), vec3(1.0));

    float Tl = 1.0;

    int c0 = 0;
    vec3 p0 = ro + rd * t;
    bool foundStart = false;

    for (int c = 0; c < MAX_CASCADES; ++c) {
        if (c >= uParams0.x) break;
        if (!cascadeOccupied(c)) continue;

        if (pointInAABB(p0, uBoundsMin[c].xyz, uBoundsMax[c].xyz)) {
            c0 = c;
            foundStart = true;
            break;
        }
    }

    if (!foundStart) {
        for (int c = 0; c < MAX_CASCADES; ++c) {
            if (c >= uParams0.x) break;
            if (cascadeOccupied(c)) {
                c0 = c;
                break;
            }
        }
    }

    float _t0, tExitC0;
    rayBoxIntersect(ro, rd, uBoundsMin[c0].xyz, uBoundsMax[c0].xyz, _t0, tExitC0);

    for (int i = 0; i < MAX_STEPS; ++i) {
        if (t > tMax) break;
        if (T < uRaymarch.z) {
            T = 0.0;
            break;
        }

        t = skipLeadingEmptyCascades(ro, rd, t, farIdx);
        if (t > tMax) break;

        vec3 p = ro + rd * t;

        for (int k = 0; k < MAX_CASCADES - 1; ++k) {
            if (c0 >= farIdx) break;
            if (t <= tExitC0) break;

            int nextC = c0 + 1;
            while (nextC <= farIdx && !cascadeOccupied(nextC)) {
                nextC++;
            }
            if (nextC > farIdx) break;

            c0 = nextC;
            rayBoxIntersect(ro, rd, uBoundsMin[c0].xyz, uBoundsMax[c0].xyz, _t0, tExitC0);
        }

        int c1 = c0;
        int nextBlend = c0 + 1;
        while (nextBlend <= farIdx && !cascadeOccupied(nextBlend)) {
            nextBlend++;
        }
        if (nextBlend <= farIdx) {
            c1 = nextBlend;
        }

        float dt = stepDtForCascade(c0);

        vec3 bmin0 = uBoundsMin[c0].xyz;
        vec3 size0 = uBoundsMax[c0].xyz - bmin0;
        vec3 uvw0 = (p - bmin0) / size0;

        vec3 bmin1 = bmin0;
        vec3 size1 = size0;
        vec3 uvw1 = uvw0;
        if (c1 != c0) {
            bmin1 = uBoundsMin[c1].xyz;
            size1 = uBoundsMax[c1].xyz - bmin1;
            uvw1 = (p - bmin1) / size1;
        }

        float w0 = 1.0;
        float w1 = 0.0;
        if (c1 != c0) {
            float e0 = edgeDistUVW(clamp(uvw0, vec3(0.0), vec3(1.0)));
            w0 = smoothstep(0.0, uGrid.w, e0);
            w1 = 1.0 - w0;
        }

        bool occHit = false;
        float jump = 1e20;

        vec3 uvwOcc0 = clamp(uvw0, vec3(0.0), vec3(0.999999));
        ivec3 b0 = ivec3(floor(uvwOcc0 * vec3(occSize)));
        ivec3 g0 = b0 >> 1;
        uint bit0 = uint((b0.x & 1) | ((b0.y & 1) << 1) | ((b0.z & 1) << 2));
        uint mask0 = fetchOcc(c0, g0);

        if (mask0 == 0u) {
            jump = min(jump, exitOccCellFast(uvwOcc0, rd, size0, g0, invOccPackSize));
        } else {
            if ((mask0 & (1u << bit0)) != 0u) occHit = true;
            jump = min(jump, exitOccCellFast(uvwOcc0, rd, size0, b0, invOccSize));
        }

        if (c1 != c0) {
            vec3 uvwOcc1 = clamp(uvw1, vec3(0.0), vec3(0.999999));
            ivec3 b1 = ivec3(floor(uvwOcc1 * vec3(occSize)));
            ivec3 g1 = b1 >> 1;
            uint bit1 = uint((b1.x & 1) | ((b1.y & 1) << 1) | ((b1.z & 1) << 2));
            uint mask1 = fetchOcc(c1, g1);

            if (mask1 == 0u) {
                jump = min(jump, exitOccCellFast(uvwOcc1, rd, size1, g1, invOccPackSize));
            } else {
                if ((mask1 & (1u << bit1)) != 0u) occHit = true;
                jump = min(jump, exitOccCellFast(uvwOcc1, rd, size1, b1, invOccSize));
            }
        }

        if (!occHit) {
            jump = max(jump, dt);
            t += jump + (1e-3 * dt);
            continue;
        }

        float d = 0.0;
        d += w0 * sampleDensity(c0, clamp(uvw0, vec3(0.0), vec3(1.0)));
        if (c1 != c0) d += w1 * sampleDensity(c1, clamp(uvw1, vec3(0.0), vec3(1.0)));

        d = max(d, 0.0);
        float sigmaT = min(d * uRaymarch.x, SIGMA_CLAMP);

        if (sigmaT > 0.0) {
            float tau = sigmaT * dt;
            float att = exp(-tau);
            float alphaStep = 1.0 - att;

            float stepIntegral = alphaStep / max(sigmaT, 1e-6);
            float sigmaS = sigmaT * albedo;

            float fogContribution = T * alphaStep;
            vec3 cameraRelativeP = p - uCameraPos.xyz;
            sphericalFogDistance += length(cameraRelativeP) * fogContribution;
            cylindricalFogDistance += max(length(cameraRelativeP.xz), abs(cameraRelativeP.y)) * fogContribution;
            fogWeight += fogContribution;

            if ((i & shadowMask) == 0) {
                if (c1 != c0 && w1 > 0.001) {
                    Tl = mix(
                        shadowTransmittanceCoarse(p, ldirN, c0),
                        shadowTransmittanceCoarse(p, ldirN, c1),
                        w1);
                } else {
                    Tl = shadowTransmittanceCoarse(p, ldirN, c0);
                }
            }

            vec3 lm = vec3(0.0);
            if (uLight0.w > 0.0) {
                lm += w0 * sampleLight(c0, clamp(uvw0, vec3(0.0), vec3(1.0)));
                if (c1 != c0) lm += w1 * sampleLight(c1, clamp(uvw1, vec3(0.0), vec3(1.0)));
            }

            vec3 Li = (uLight2.xyz + (uLight1.xyz * Tl)) * lm;
            L += T * (sigmaS * stepIntegral) * Li * ph * uSmokeColor.rgb;
            T *= att;
        }

        t += dt;
    }

    float invFogWeight = fogWeight > 1e-6 ? 1.0 / fogWeight : 0.0;
    return VolOut(L, T, sphericalFogDistance * invFogWeight, cylindricalFogDistance * invFogWeight);
}

void main() {
	float nearDepth01 = (uReverseZ == 1) ? 1.0 : 0.0;
	float farDepth01  = (uReverseZ == 1) ? 0.0 : 1.0;
	
	vec3 pNear = unproject01(vUV, nearDepth01);
	vec3 pFar = unproject01(vUV, farDepth01);

    vec3 ro = pNear;
    vec3 rd = normalize(pFar - pNear);

    float depth01 = texture(physics_depth, vUV).r;

    float tClamp = 1e20;
    
	bool check = (uReverseZ == 1) ? (depth01 > 1e-6) : (depth01 < 0.999999);
    
    if (check) {
        vec3 surfW = unproject01(vUV, depth01);
        tClamp = max(dot(surfW - ro, rd) - 1e-3, 0.0);
    }

    VolOut r = renderVolume(ro, rd, tClamp);
    float alpha = 1.0 - clamp(r.T, 0.0, 1.0);
    vec3 foggedColor = apply_smoke_fog_premultiplied(r.L, alpha, r.sphericalFogDistance, r.cylindricalFogDistance);
    fragColor = vec4(foggedColor, alpha);
}