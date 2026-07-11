#version 150

in vec2 vUV;
out vec4 fragColor;

uniform sampler2D physics_texture;

void main() {
    fragColor = texture(physics_texture, vUV);
}