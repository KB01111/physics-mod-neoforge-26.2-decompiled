#version 330

flat in uint lightcoords;

out vec4 fragColor;

void main() {
	fragColor = vec4(gl_FragCoord.z, float(lightcoords) / 255.0, 0.0, 1.0);
}
