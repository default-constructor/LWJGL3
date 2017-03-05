#version 450

in vec4 position;
in vec2 textureCoordinates;
in vec3 normal;

out DATA {
	vec2 textureCoordinates;
} vs_out;

uniform mat4 projectionMatrix;
uniform mat4 transformationMatrix;
uniform mat4 viewMatrix;

void main(void) {
	vec4 worldPosition = transformationMatrix * position;
	gl_Position = projectionMatrix * worldPosition;
	vs_out.textureCoordinates = textureCoordinates;
}
