#version 150

in vec2 vUV;
out vec4 fragColor;

uniform sampler2D physics_sceneColor;
uniform sampler2D physics_sceneDepth;
uniform sampler2D physics_smokeLow;
uniform sampler2D physics_historySmoke;
uniform sampler2D physics_historyDepth;

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

float depth01ToNdcZ(float z01) {
    return (zZeroToOne == 1) ? z01 : (z01 * 2.0 - 1.0);
}

vec3 unproject01(vec2 uv, float z01) {
    vec2 ndc = uv * 2.0 - 1.0;
    vec4 h = uInvViewProj * vec4(ndc, depth01ToNdcZ(z01), 1.0);

    float invW = (abs(h.w) > 1e-6) ? (1.0 / h.w) : 0.0;
    return h.xyz * invW;
}

bool in01(vec2 uv) {
    return all(greaterThanEqual(uv, vec2(0.0))) && all(lessThanEqual(uv, vec2(1.0)));
}

void upsampleLT(vec2 uv, float depth0, out vec4 lt, out vec4 ltMin, out vec4 ltMax) {
    ivec2 lowSizeI = textureSize(physics_smokeLow, 0);
    vec2 lowSize = vec2(lowSizeI);

    vec2 p = uv * lowSize - 0.5;
    ivec2 i0 = ivec2(floor(p));
    vec2 f = fract(p);

    ivec2 i00 = clamp(i0 + ivec2(0, 0), ivec2(0), lowSizeI - 1);
    ivec2 i10 = clamp(i0 + ivec2(1, 0), ivec2(0), lowSizeI - 1);
    ivec2 i01 = clamp(i0 + ivec2(0, 1), ivec2(0), lowSizeI - 1);
    ivec2 i11 = clamp(i0 + ivec2(1, 1), ivec2(0), lowSizeI - 1);

    vec2 uv00 = (vec2(i00) + 0.5) / lowSize;
    vec2 uv10 = (vec2(i10) + 0.5) / lowSize;
    vec2 uv01 = (vec2(i01) + 0.5) / lowSize;
    vec2 uv11 = (vec2(i11) + 0.5) / lowSize;

    vec4 s00 = texture(physics_smokeLow, uv00);
    vec4 s10 = texture(physics_smokeLow, uv10);
    vec4 s01 = texture(physics_smokeLow, uv01);
    vec4 s11 = texture(physics_smokeLow, uv11);

    float d00 = texture(physics_sceneDepth, uv00).r;
    float d10 = texture(physics_sceneDepth, uv10).r;
    float d01 = texture(physics_sceneDepth, uv01).r;
    float d11 = texture(physics_sceneDepth, uv11).r;

    float w00 = (1.0 - f.x) * (1.0 - f.y);
    float w10 = f.x * (1.0 - f.y);
    float w01 = (1.0 - f.x) * f.y;
    float w11 = f.x * f.y;

    float z00 = 1.0 - smoothstep(0.0, uShadow.w, abs(d00 - depth0));
    float z10 = 1.0 - smoothstep(0.0, uShadow.w, abs(d10 - depth0));
    float z01 = 1.0 - smoothstep(0.0, uShadow.w, abs(d01 - depth0));
    float z11 = 1.0 - smoothstep(0.0, uShadow.w, abs(d11 - depth0));

    w00 *= z00; w10 *= z10; w01 *= z01; w11 *= z11;
    float wsum = w00 + w10 + w01 + w11;

    if (wsum < 1e-6) {
        w00 = (1.0 - f.x) * (1.0 - f.y);
        w10 = f.x * (1.0 - f.y);
        w01 = (1.0 - f.x) * f.y;
        w11 = f.x * f.y;
        wsum = w00 + w10 + w01 + w11;
    }

    lt = (s00 * w00 + s10 * w10 + s01 * w01 + s11 * w11) / wsum;
    ltMin = min(min(s00, s10), min(s01, s11));
    ltMax = max(max(s00, s10), max(s01, s11));
}

void main() {
    float depth0 = texture(physics_sceneDepth, vUV).r;

    vec4 curLT;
    vec4 curMin;
    vec4 curMax;
    upsampleLT(vUV, depth0, curLT, curMin, curMax);

    vec4 outLT = curLT;

    if (uParams0.z != 0) {
        vec3 wpos = unproject01(vUV, depth0);
        vec4 prevH = uPrevViewProj * vec4(wpos, 1.0);

        if (abs(prevH.w) > 1e-6) {
            vec3 prevNdc = prevH.xyz / prevH.w;
            vec2 prevUV = prevNdc.xy * 0.5 + 0.5;
            float prevDepth01 = prevNdc.z;
            
            if (zZeroToOne == 0) {
				prevDepth01 = prevDepth01 * 0.5 + 0.5;
			}

            if (in01(prevUV)) {
                vec4 histLT = texture(physics_historySmoke, prevUV);
                float histDepth = texture(physics_historyDepth, prevUV).r;

                if (abs(histDepth - prevDepth01) < uLight2.w) {
                    vec4 lo = curMin - 0.05 * (curMax - curMin);
                    vec4 hi = curMax + 0.05 * (curMax - curMin);
                    histLT = clamp(histLT, lo, hi);
                    outLT = mix(curLT, histLT, uRaymarch.w);
                }
            }
        }
    }

    fragColor = outLT;
}