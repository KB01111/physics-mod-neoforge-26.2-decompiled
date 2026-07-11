#version 330

#moj_import <minecraft:dynamictransforms.glsl>
#moj_import <minecraft:projection.glsl>
#moj_import <minecraft:light.glsl>
#moj_import <minecraft:fog.glsl>
#moj_import <minecraft:sample_lightmap.glsl>

in vec3 Position;
in ivec2 UV2;
in vec3 Normal;

uniform sampler2D Sampler2;

out float sphericalVertexDistance;
out float cylindricalVertexDistance;
out vec4 vertexColor;
out vec2 texCoord0;
out vec4 normal;

void main() {
    vec4 cameraSpacePos = ModelViewMat * vec4(Position, 1.0);
    gl_Position = ProjMat * cameraSpacePos;

    sphericalVertexDistance = fog_spherical_distance(cameraSpacePos.xyz);
    cylindricalVertexDistance = fog_cylindrical_distance(cameraSpacePos.xyz);

    vertexColor = minecraft_mix_light(normalize(Light0_Direction), normalize(Light1_Direction), Normal, vec4(1.0))
        * sample_lightmap(Sampler2, UV2);
    texCoord0 = (TextureMat * vec4(vec2(0.0), 0.0, 1.0)).xy;
    normal = ProjMat * ModelViewMat * vec4(Normal, 0.0);
}
