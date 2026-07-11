in vec2 physics_pass_uv0;
in vec3 physics_pass_normal;
in float physics_pass_transparencyModulator;
in vec3 physics_pass_fireColor;
flat in ivec4 physics_pass_objectID;
flat in float physics_pass_particleDensity;
in vec3 physics_pass_worldPos;

layout(std140) uniform PhysicsSmoke {
	mat4 physics_viewMatrix;
	mat4 physics_projectionMatrix;
	mat4 physics_invProjectionMatrix;
	vec4 physics_smokeColor_Density;
	vec4 physics_smokeDenseColor_RenderPercent;
	vec4 physics_cameraOffset_Padding;
	int physics_zZeroToOne;
};

uniform sampler2D physics_smokeSampler;
uniform sampler2D physics_depth;

vec3 physics_decodeDepth(vec2 texCoord, float z, mat4 invProjectionMatrix) {
	if (physics_zZeroToOne == 0) {
		z = z * 2.0 - 1.0;
	}
	
    float x = texCoord.x * 2.0 - 1.0;
    float y = texCoord.y * 2.0 - 1.0;

    vec4 projectedPos = vec4(x, y, z, 1.0);

    vec4 position = invProjectionMatrix * projectedPos;

    return position.xyz / position.w;
}

vec3 physics_decodeDepth(vec2 texCoord, sampler2D depthMap, mat4 invProjectionMatrix) {
    return physics_decodeDepth(texCoord, texture(depthMap, texCoord).x, invProjectionMatrix);
}

float physics_remapClamp(float value, float oldMin, float oldMax, float newMin, float newMax) {
	return clamp(newMin + (value - oldMin) / (oldMax - oldMin) * (newMax - newMin), newMin, newMax);
}

const int physics_ditherDispersed[64] = int[]( 0, 48, 12, 60, 3, 51, 15, 61, 32, 16, 44, 28, 35, 19, 47, 31, 8, 56, 4, 52, 11, 59, 7,
				55, 40, 24, 36, 20, 43, 27, 39, 23, 2, 50, 14, 62, 1, 49, 13, 61, 34, 18, 46, 30, 33, 17, 45, 29, 10,
				58, 6, 54, 9, 57, 5, 53, 42, 26, 38, 22, 41, 25, 37, 21 );

float physics_calcDitherDispersed(vec2 offset) {
	int x = int(mod(gl_FragCoord.x + offset.x, 8.0));
	int y = int(mod(gl_FragCoord.y + offset.y, 8.0));
	return physics_ditherDispersed[x + y * 8] / 64.0;
}

vec3 physics_calculateSmoke() {
	vec4 texColor = texture(physics_smokeSampler, physics_pass_uv0);
	float addTransparency = 1.0 - pow((normalize(physics_pass_normal).z * 0.5 + 0.5) * 2.5, 2.0);
	ivec2 depthSize = textureSize(physics_depth, 0);
	vec3 oldWorldPos = physics_decodeDepth(gl_FragCoord.xy / vec2(depthSize), physics_depth, physics_invProjectionMatrix);
	float depthOld = length(oldWorldPos);
	float depthNew = length(physics_pass_worldPos);
	float depthBasedTransparency = clamp((depthOld - depthNew) * (depthNew - 0.5), 0.0, 1.0);
    float densityBasedTransparency = physics_remapClamp(physics_pass_particleDensity, 1.0, 20.0, 0.8, 1.0);
    float actualTransparency = texColor.r * physics_pass_transparencyModulator * physics_smokeColor_Density.w * addTransparency * depthBasedTransparency * densityBasedTransparency;

    if (physics_calcDitherDispersed(vec2(physics_pass_objectID.xy) / 255.0 * 8.0) >= actualTransparency) {
    	discard;
    }

    float densityBasedColor = physics_remapClamp(physics_pass_particleDensity, 20.0, 1.0, 0.0, 1.0);
    float smokeColorOffset = (1.0 - texColor.r) * 0.3 + 0.4;
	vec3 finalSmokeColor = physics_pass_fireColor.rgb * mix(physics_smokeDenseColor_RenderPercent.xyz, physics_smokeColor_Density.xyz, densityBasedColor) * smokeColorOffset;

	return finalSmokeColor;
}
