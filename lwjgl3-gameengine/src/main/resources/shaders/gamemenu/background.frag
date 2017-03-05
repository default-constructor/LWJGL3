#version 450

in DATA {
	vec2 textureCoordinates;
} fs_in;

layout (location = 0) out vec4 color;

uniform sampler2D gamemenuTexture;

void main() {
	color = texture(gamemenuTexture, fs_in.textureCoordinates);
}