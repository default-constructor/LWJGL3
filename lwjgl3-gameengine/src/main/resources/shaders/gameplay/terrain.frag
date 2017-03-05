#version 400 core

in vec2 terrainCoordinates;
in vec3 surfaceNormal;
in vec3 toLightVector;
in vec3 toCameraVector;
in float visibility;

out vec4 color;

uniform sampler2D backgroundTexture;
uniform sampler2D blendMap;

uniform vec3 lightColor;
uniform vec3 lightAttenuation;

uniform vec3 skyColor;

uniform float shineDamper;
uniform float reflectivity;

void main(void) {
	vec4 blendMapColor = texture(blendMap, terrainCoordinates);
	float backgroundTextureAmount = 1 - (blendMapColor.r + blendMapColor.g + blendMapColor.b);
	vec2 tiledCoords = terrainCoordinates * 40.0;
	vec4 backgroundTextureColor = texture(backgroundTexture, tiledCoords) * backgroundTextureAmount;
	vec4 totalColor = backgroundTextureColor;

	vec3 unitNormal = normalize(surfaceNormal);
	vec3 unitVectorToCamera = normalize(toCameraVector);

	vec3 totalDiffuse = vec3(0.0);
	vec3 totalSpecular = vec3(0.0);

	float distance = length(toLightVector);
	float attenuationFactor = lightAttenuation.x + (lightAttenuation.y * distance) + (lightAttenuation.z * distance * distance);
	vec3 unitLightVector = normalize(toLightVector);
	float nDotl = dot(unitNormal, unitLightVector);
	float brightness = max(nDotl, 0.0);
	vec3 lightDirection = -unitLightVector;
	vec3 reflectedLightDirection = reflect(lightDirection, unitNormal);
	float specularFactor = dot(reflectedLightDirection, unitVectorToCamera);
	specularFactor = max(specularFactor, 0.0);
	float damperFactor = pow(specularFactor, shineDamper);
	totalDiffuse = totalDiffuse + (brightness * lightColor) / attenuationFactor;
	totalSpecular = totalSpecular + (damperFactor * reflectivity * lightColor) / attenuationFactor;

	color = vec4(totalDiffuse, 1.0) * totalColor + vec4(totalSpecular, 1.0);
	color = mix(vec4(skyColor, 1.0), color, visibility);
}