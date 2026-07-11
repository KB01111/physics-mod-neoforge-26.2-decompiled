#version 330

in vec3 Position;
in vec2 UV0;

uniform PhysicsRipple {
    mat4 ModelViewMat;
    mat4 ProjMat;
    vec4 RippleCameraPos_RenderPercent;
    vec4 RippleSimulationCenter_PreviousCenter;
    vec4 RippleSimulationTexel_DampingPropagationStep;
    vec4 RippleSimulationRange_BorderImpulsePadding;
};

uniform samplerBuffer PhysicsRippleInstances;

out vec2 texCoord0;
out float impulseStrength;
out float impulseMode;
out vec2 impulseShapeParams;

vec4 loadInstanceData(int instanceId, int word) {
    return texelFetch(PhysicsRippleInstances, instanceId * 2 + word);
}

void main() {
    vec4 impulsePosition = loadInstanceData(gl_InstanceID, 0);
    vec4 impulseShape = loadInstanceData(gl_InstanceID, 1);

    vec3 rippleCameraPos = RippleCameraPos_RenderPercent.xyz;
    float radius = impulseShape.x;

    vec3 currentPos = impulsePosition.xyz - rippleCameraPos;
    vec4 currentWorldPos = ModelViewMat * vec4(Position.xyz * radius + currentPos, 1.0);
    gl_Position = ProjMat * currentWorldPos;

    texCoord0 = UV0;
    impulseStrength = impulsePosition.w;
    impulseMode = impulseShape.y;
    impulseShapeParams = impulseShape.zw;
}
