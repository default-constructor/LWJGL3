#version 450

layout (location = 0) in vec4 position;
layout (location = 1) in vec2 textureCoordinates;

out DATA {
	vec2 textureCoordinates;
} vs_out;

uniform mat4 projectionMatrix;
uniform mat4 transformationMatrix;
uniform mat4 viewMatrix;

void main() {
	gl_Position = projectionMatrix * position;
	vs_out.textureCoordinates = textureCoordinates;
}