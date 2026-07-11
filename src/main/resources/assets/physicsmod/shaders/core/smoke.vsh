#version 330

#moj_import <minecraft:dynamictransforms.glsl>
#moj_import <minecraft:projection.glsl>
#moj_import <minecraft:light.glsl>
#moj_import <minecraft:fog.glsl>
#moj_import <physicsmod:smoke_vertex.glsl>

in vec3 Position;
in vec4 Color;
in vec2 UV0;
in vec3 Normal;

out float sphericalVertexDistance;
out float cylindricalVertexDistance;

void main() {
	physics_transformVertex(Position, Normal);
    gl_Position = physics_projectionMatrix * vec4(physics_pass_worldPos, 1.0);
    physics_setSmokeAttributes(UV0);

    sphericalVertexDistance = fog_spherical_distance(physics_finalPosition.xyz);
    cylindricalVertexDistance = fog_cylindrical_distance(physics_finalPosition.xyz);
}