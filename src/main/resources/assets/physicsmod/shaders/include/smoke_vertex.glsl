ivec4 physics_light;
vec4 physics_offset;
vec4 physics_offsetNew;

out vec2 physics_pass_uv0;
out vec3 physics_pass_normal;
out float physics_pass_transparencyModulator;
out vec3 physics_pass_fireColor;
flat out ivec4 physics_pass_objectID;
flat out float physics_pass_particleDensity;
out vec3 physics_pass_worldPos;

layout(std140) uniform PhysicsSmoke {
	mat4 physics_viewMatrix;
	mat4 physics_projectionMatrix;
	mat4 physics_invProjectionMatrix;
	vec4 physics_smokeColor_Density;
	vec4 physics_smokeDenseColor_RenderPercent;
	vec4 physics_cameraOffset_Padding;
	int physics_zZeroToOne;
};

uniform sampler2D physics_lightmap;
uniform samplerBuffer PhysicsSmokeInstances;

vec4 physics_finalPosition;
vec3 physics_normal;

vec4 physics_loadInstanceData(int instanceId, int word) {
	return texelFetch(PhysicsSmokeInstances, instanceId * 3 + word);
}

void physics_loadInstance(int instanceId) {
	vec4 lightWord = physics_loadInstanceData(instanceId, 0);
	physics_light = ivec4(round(lightWord));
	physics_offset = physics_loadInstanceData(instanceId, 1);
	physics_offsetNew = physics_loadInstanceData(instanceId, 2);
}

mat4 physics_rotationMatrix(vec3 axis, float angle) {
    axis = normalize(axis);
    float s = sin(angle);
    float c = cos(angle);
    float oc = 1.0 - c;

    return mat4(oc * axis.x * axis.x + c,           oc * axis.x * axis.y - axis.z * s,  oc * axis.z * axis.x + axis.y * s,  0.0,
                oc * axis.x * axis.y + axis.z * s,  oc * axis.y * axis.y + c,           oc * axis.y * axis.z - axis.x * s,  0.0,
                oc * axis.z * axis.x - axis.y * s,  oc * axis.y * axis.z + axis.x * s,  oc * axis.z * axis.z + c,           0.0,
                0.0,                                0.0,                                0.0,                                1.0);
}

float physics_remap(float value, float oldMin, float oldMax, float newMin, float newMax) {
	return newMin + (value - oldMin) / (oldMax - oldMin) * (newMax - newMin);
}

void physics_transformVertex(vec3 position, vec3 normal) {
	physics_loadInstance(gl_InstanceID);

	float objIDx = float(physics_light.x) / 255.0;
	float objIDy = float(physics_light.y) / 255.0;
	float objScale = physics_remap(float(physics_light.w) / 255.0, 0.0, 1.0, 0.25, 5.0);
	float randomScale = ((objIDx + objIDy) * 0.5 * 0.3 + 0.4) * objScale;
	mat4 randomRotation = physics_rotationMatrix(vec3(0.2 + objIDy, 0.6, 0.4 + objIDx), (objIDx + objIDy) * 2.0);
	physics_normal = normalize((randomRotation * vec4(normal, 0.0)).xyz);

	float renderPercent = physics_smokeDenseColor_RenderPercent.w;
	vec3 currentOffset = mix(physics_offset.xyz, physics_offsetNew.xyz, renderPercent) - physics_cameraOffset_Padding.xyz;
	physics_finalPosition = vec4((randomRotation * vec4(position * randomScale, 1.0)).xyz + currentOffset, 1.0);

	physics_pass_normal = normalize(mat3(physics_viewMatrix) * physics_normal);
	physics_pass_worldPos = (physics_viewMatrix * physics_finalPosition).xyz;
}

void physics_setSmokeAttributes(vec2 texCoords) {
	ivec2 remappedLightCoords = ivec2(physics_light.z & 0xF, (physics_light.z >> 4) & 0xF);
	float t = (1.0 - pow(clamp(float(remappedLightCoords.x) / 15.0, 0.0, 1.0), 3.0)) * 1.2;

    vec3 c0 = vec3(0.0);
	vec3 c1 = vec3(1.0, 0.2, 0.0);
	vec3 c2 = vec3(4.0, 2.0, 0.2);

	vec3 fireColor = mix(c2, c1, smoothstep(0.0, 0.7, t));
	fireColor = mix(fireColor, c0, smoothstep(0.7, 1.0, t));

  	vec2 lightmapSize = vec2(textureSize(physics_lightmap, 0));
    physics_pass_fireColor = texture(physics_lightmap, vec2(remappedLightCoords) / lightmapSize).rgb + fireColor;

    physics_pass_uv0 = texCoords;
    physics_pass_objectID = physics_light;
	physics_pass_particleDensity = physics_offsetNew.w;
    physics_pass_transparencyModulator = physics_offset.w;
}
