#version 330

in vec2 texCoord0;
in float impulseStrength;
in float impulseMode;
in vec2 impulseShapeParams;

out vec4 fragColor;

void main() {
    vec2 local = texCoord0 * 2.0 - 1.0;
    float dist = length(local);
    if (dist > 1.0) {
        discard;
    }

    float softness = max(impulseShapeParams.x, 0.001);
    float width = max(impulseShapeParams.y, 0.001);

    // A signed impulse creates both a crest and a trough so the simulation starts
    // with a visible propagating ring instead of a flat height offset.
    float crest = exp(-dist * dist / softness);
    float trough = exp(-pow(dist - width, 2.0) / (softness * 0.35));
    float shape = crest - trough * 0.58;

    if (impulseMode > 1.5) {
        // Boat wakes start as a larger, softer disturbance.  The simulation
        // handles the spreading, so no angular/delayed particle fan is needed.
        shape *= smoothstep(1.0, 0.0, dist);
        shape *= 1.15;
    } else if (impulseMode > 0.5) {
        shape *= smoothstep(1.0, 0.0, dist);
    }

    fragColor = vec4(shape * impulseStrength, 0.0, 0.0, 1.0);
}
