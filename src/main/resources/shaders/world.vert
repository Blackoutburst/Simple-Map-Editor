#version 410

layout(location = 0) in vec3 aPosition;
layout(location = 1) in vec2 aUv;
layout(location = 2) in float aNormal;
layout(location = 3) in float aLayer;

uniform mat4 view;
uniform mat4 projection;
uniform mat4 lightView;
uniform mat4 lightProjection;

out vec3 FragPos;
out vec2 uv;
out vec3 norm;
out vec4 FragPosLightSpace;
flat out float layer;

vec3 getNormal(int index) {
	const vec3 normals[6] = vec3[6](
		vec3(0.0f, 1.0f, 0.0f), // TOP
		vec3(0.0f, 0.0f, -1.0f), // FRONT
		vec3(0.0f, 0.0f, 1.0f), // BACK
		vec3(-1.0f, 0.0f, 0.0f), // LEFT
		vec3(1.0f, 0.0f, 0.0f), // RIGHT
		vec3(0.0f, -1.0f, 0.0f) // BOTTOM
	);
	return normals[index];
}

void main() {
	FragPos = aPosition;
	norm = getNormal(int(aNormal));
	layer = aLayer;

	uv = aUv;

	FragPosLightSpace = lightProjection * lightView * vec4(aPosition, 1.0);
	gl_Position = projection * view * vec4(aPosition, 1.0);
}