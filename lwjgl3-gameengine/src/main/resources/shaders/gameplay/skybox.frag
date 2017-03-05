#version 450

in DATA {
	vec2 textureCoordinates;
} fs_in;

out vec4 color;

uniform sampler2D backgroundTexture;

uniform vec3 skyColor;

void main() {
	color = texture(backgroundTexture, fs_in.textureCoordinates);
}
