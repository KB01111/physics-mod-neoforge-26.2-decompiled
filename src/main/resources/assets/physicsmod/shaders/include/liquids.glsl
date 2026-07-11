layout(std140) uniform PhysicsLiquid {
	mat4 physics_modelViewMat;
	mat4 physics_projMat;
	mat4 physics_invProjectionMatrix;
	mat4 physics_invViewMatrix;
	mat4 physics_viewMatrix;
	vec4 physics_liquidCameraPosRenderPercent;
	vec4 physics_waterBounds;
	vec4 physics_cameraOffset;
	int physics_zZeroToOne;
};

uniform sampler2D physics_liquidData;

vec3 physics_normal;
vec4 physics_fragdepth;

float physics_remap(float value, float oldMin, float oldMax, float newMin, float newMax) {
	return newMin + (value - oldMin) / (oldMax - oldMin) * (newMax - newMin);
}

float physics_sampleDepth(vec2 texCoord) {
	return texture(physics_liquidData, texCoord).x;
}

int physics_sampleLightCoords(vec2 texCoord) {
	return int(round(texture(physics_liquidData, texCoord).y * 255.0));
}

vec3 physics_decodeDepth(vec2 texCoord, mat4 invProjectionMatrix) {
	float z = physics_sampleDepth(texCoord);
	
	if (physics_zZeroToOne == 0) z = z * 2.0 - 1.0;
	
	float x = texCoord.x * 2.0 - 1.0;
	float y = texCoord.y * 2.0 - 1.0;
	vec4 projectedPos = vec4(x, y, z, 1.0);
	vec4 position = invProjectionMatrix * projectedPos;
	return position.xyz / position.w;
}

vec2 physics_waterCoords(vec3 viewPos, vec3 normal) {
	vec3 worldPos = (physics_invViewMatrix * vec4(viewPos, 1.0)).xyz + physics_cameraOffset.xyz;
	vec3 n = abs(normalize(normal));

	if (n.x > n.y && n.x > n.z) {
		return vec2(
			physics_remap(fract(worldPos.y), 0.0, 1.0, physics_waterBounds.x, physics_waterBounds.y),
			physics_remap(fract(worldPos.z), 0.0, 1.0, physics_waterBounds.z, physics_waterBounds.w)
		);
	} else if (n.y > n.z) {
		return vec2(
			physics_remap(fract(worldPos.x), 0.0, 1.0, physics_waterBounds.x, physics_waterBounds.y),
			physics_remap(fract(worldPos.z), 0.0, 1.0, physics_waterBounds.z, physics_waterBounds.w)
		);
	}

	return vec2(
		physics_remap(fract(worldPos.x), 0.0, 1.0, physics_waterBounds.x, physics_waterBounds.y),
		physics_remap(fract(worldPos.y), 0.0, 1.0, physics_waterBounds.z, physics_waterBounds.w)
	);
}

vec3 physics_getNormalFromDepthViewSpace() {
	vec2 texCoord = gl_FragCoord.xy / vec2(textureSize(physics_liquidData, 0));
	vec2 texelSize = 1.0 / vec2(textureSize(physics_liquidData, 0));
	vec3 eyePos = physics_decodeDepth(texCoord, physics_invProjectionMatrix);
	vec3 ddx1 = physics_decodeDepth(texCoord + vec2(texelSize.x, 0.0), physics_invProjectionMatrix) - eyePos;
	vec3 ddx2 = eyePos - physics_decodeDepth(texCoord - vec2(texelSize.x, 0.0), physics_invProjectionMatrix);
	if (abs(ddx1.z) > abs(ddx2.z)) {
		ddx1 = ddx2;
	}

	vec3 ddy1 = physics_decodeDepth(texCoord + vec2(0.0, texelSize.y), physics_invProjectionMatrix) - eyePos;
	vec3 ddy2 = eyePos - physics_decodeDepth(texCoord - vec2(0.0, texelSize.y), physics_invProjectionMatrix);
	if (abs(ddy1.z) > abs(ddy2.z)) {
		ddy1 = ddy2;
	}

	return normalize(cross(ddx1, ddy1));
}

vec3 physics_getNormalFromDepth() {
	return normalize((physics_invViewMatrix * vec4(physics_getNormalFromDepthViewSpace(), 0.0)).xyz);
}
