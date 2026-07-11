#version 330

#moj_import <minecraft:fog.glsl>
#moj_import <minecraft:light.glsl>
#moj_import <minecraft:sample_lightmap.glsl>
#moj_import <physicsmod:liquids.glsl>

uniform sampler2D Sampler0;
uniform sampler2D Sampler2;

in vec2 pass_textureCoords;

out vec4 fragColor;

void main() {
	vec2 screenUv = gl_FragCoord.xy / vec2(textureSize(physics_liquidData, 0));
	int lightCoords = physics_sampleLightCoords(screenUv);
	ivec2 remappedLightCoords = ivec2(lightCoords & 0xF, (lightCoords >> 4) & 0xF);
	float physics_fragZ = physics_sampleDepth(screenUv);

	physics_normal = physics_getNormalFromDepth();
	
	if (physics_fragZ == 0.0) {
		discard;
	}

	vec3 eyePos = physics_decodeDepth(screenUv, physics_invProjectionMatrix);
	float vertexDistance = length(eyePos);
	vec2 uv0 = physics_waterCoords(eyePos, physics_normal);

	vec4 color = vec4(0.24705884, 0.46274513, 0.8941177, 1.0) * texture(Sampler0, uv0);
	color = minecraft_mix_light(normalize(Light0_Direction), normalize(Light1_Direction), physics_normal, color)
		* vec4(sample_lightmap(Sampler2, remappedLightCoords * 16).rgb, 1.0);
	fragColor = apply_fog(color, vertexDistance, vertexDistance, FogEnvironmentalStart, FogEnvironmentalEnd, FogRenderDistanceStart, FogRenderDistanceEnd, FogColor);
}
