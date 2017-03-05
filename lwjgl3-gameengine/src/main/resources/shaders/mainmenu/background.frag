#version 450

in DATA {
	vec2 textureCoordinates;
} fs_in;

layout (location = 0) out vec4 color;

uniform sampler2D mainmenuTexture;

void main() {
	color = texture(mainmenuTexture, fs_in.textureCoordinates);
}
