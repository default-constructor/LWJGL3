#version 450

in vec3 position;
in vec2 textureCoordinates;
in vec3 normal;

out DATA {
	vec2 textureCoordinates;
} vs_out;

uniform mat4 projectionMatrix;
uniform mat4 transformationMatrix;
uniform mat4 viewMatrix;

void main(void) {
	vec4 worldPosition = transformationMatrix * vec4(position, 1.0);
	vec4 positionRelativeToCamera = viewMatrix * worldPosition;

	gl_Position = projectionMatrix * positionRelativeToCamera;

	vs_out.textureCoordinates = textureCoordinates;
}
