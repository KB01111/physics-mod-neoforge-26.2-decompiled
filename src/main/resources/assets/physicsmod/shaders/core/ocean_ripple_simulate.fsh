#version 330

uniform PhysicsRipple {
    mat4 ModelViewMat;
    mat4 ProjMat;
    vec4 RippleCameraPos_RenderPercent;
    vec4 RippleSimulationCenter_PreviousCenter;
    vec4 RippleSimulationTexel_DampingPropagationStep;
    vec4 RippleSimulationRange_BorderImpulsePadding;
};

uniform sampler2D physics_ripple_state;
uniform sampler2D physics_ripple_impulse;

in vec2 texCoord0;
out vec4 fragColor;

float inside01(vec2 uv) {
    vec2 inside = step(vec2(0.0), uv) * step(uv, vec2(1.0));
    return inside.x * inside.y;
}

vec4 sampleState(vec2 uv) {
    return texture(physics_ripple_state, uv) * inside01(uv);
}

void main() {
    vec2 resolution = vec2(textureSize(physics_ripple_state, 0));
    vec2 texel = 1.0 / resolution;

    float rippleRange = RippleSimulationRange_BorderImpulsePadding.x;
    float damping = RippleSimulationTexel_DampingPropagationStep.y;
    float propagation = RippleSimulationTexel_DampingPropagationStep.z;
    float simulationStep = RippleSimulationTexel_DampingPropagationStep.w;
    float borderDamping = RippleSimulationRange_BorderImpulsePadding.y;
    float impulseStrength = RippleSimulationRange_BorderImpulsePadding.z;

    vec2 center = RippleSimulationCenter_PreviousCenter.xy;
    vec2 previousCenter = RippleSimulationCenter_PreviousCenter.zw;
    vec2 reproject = (center - previousCenter) / max(rippleRange * 2.0, 0.001);
    vec2 oldUv = texCoord0 + reproject;


    vec4 state = sampleState(oldUv);
    float current = state.r;
    float previous = state.g;

    float left = sampleState(oldUv - vec2(texel.x, 0.0)).r;
    float right = sampleState(oldUv + vec2(texel.x, 0.0)).r;
    float top = sampleState(oldUv - vec2(0.0, texel.y)).r;
    float bottom = sampleState(oldUv + vec2(0.0, texel.y)).r;
    float laplacian = left + right + top + bottom - current * 4.0;

    float impulse = texture(physics_ripple_impulse, texCoord0).r * impulseStrength;
    float simulated = ((current * 2.0 - previous) * damping) + laplacian * propagation + impulse;

    float edge = min(min(texCoord0.x, texCoord0.y), min(1.0 - texCoord0.x, 1.0 - texCoord0.y));
    float edgeFade = smoothstep(0.0, borderDamping, edge);
    simulated *= edgeFade;
    current *= edgeFade;
    previous *= edgeFade;

    float nextCurrent = mix(current, simulated, simulationStep);
    float nextPrevious = mix(previous, current, simulationStep);
    fragColor = vec4(nextCurrent, nextPrevious, 0.0, 1.0);
}
