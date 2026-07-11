#version 150

in vec2 vUV;
out vec4 fragColor;

uniform sampler2D physics_texture;

void main() {
    float d = texture(physics_texture, vUV).r;
    fragColor = vec4(d, d, d, 1.0);
}