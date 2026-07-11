#version 330

out vec2 texCoord0;

void main() {
    vec2 position;
    position.x = (gl_VertexID == 2) ? 3.0 : -1.0;
    position.y = (gl_VertexID == 1) ? 3.0 : -1.0;
    texCoord0 = position * 0.5 + 0.5;
    gl_Position = vec4(position, 0.0, 1.0);
}
