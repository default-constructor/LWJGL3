#version 450

in DATA {
	vec2 textureCoordinates;
} fs_in;

out vec4 color;

uniform sampler2D gameplayTexture;

void main() {
	color = texture(gameplayTexture, fs_in.textureCoordinates);
}
