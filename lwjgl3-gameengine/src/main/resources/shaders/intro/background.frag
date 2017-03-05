#version 450

in DATA {
	vec2 textureCoordinates;
} fs_in;

layout (location = 0) out vec4 color;

uniform sampler2D introTexture;

void main() {
	color = texture(introTexture, fs_in.textureCoordinates);
}
