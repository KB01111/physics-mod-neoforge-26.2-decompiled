#version 330

in vec3 Position;

layout(std140) uniform PhysicsLiquid {
	mat4 physics_modelViewMat;
	mat4 physics_projMat;
	mat4 physics_invProjectionMatrix;
	mat4 physics_invViewMatrix;
	mat4 physics_viewMatrix;
	vec4 physics_liquidCameraPosRenderPercent;
	vec4 physics_waterBounds;
	vec4 physics_cameraOffset;
};

uniform samplerBuffer PhysicsLiquidInstances;

flat out uint lightcoords;

void main() {
	int instanceBase = gl_InstanceID * 2;
	vec4 offset = texelFetch(PhysicsLiquidInstances, instanceBase + 0);
	vec4 offsetNew = texelFetch(PhysicsLiquidInstances, instanceBase + 1);

	vec3 liquidCameraPos = physics_liquidCameraPosRenderPercent.xyz;
	float renderPercent = physics_liquidCameraPosRenderPercent.w;
	float scale = offsetNew.w;
	vec4 currentWorldPos = physics_modelViewMat * vec4(Position * scale + mix(offset.xyz - liquidCameraPos, offsetNew.xyz - liquidCameraPos, renderPercent), 1.0);
	gl_Position = physics_projMat * currentWorldPos;
	lightcoords = uint(round(offset.w));
}
