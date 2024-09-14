#version 410

#define ambientStrength 0.5
#define specularStrength 0.1

in vec4 FragPosLightSpace;

in vec3 FragPos;
in vec2 uv;
in vec3 norm;
flat in float layer;

uniform sampler2DArray diffuseMap;
uniform vec3 lightColor;
uniform vec3 viewPos;
uniform vec3 lightPos;

uniform sampler2D shadowMap;

out vec4 FragColor;

float rand(vec2 co){ return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453); }

float ShadowCalculation(vec4 fragPosLightSpace, vec3 lightDir, vec3 normal) {
    vec3 projCoords = fragPosLightSpace.xyz / fragPosLightSpace.w;
    projCoords = projCoords * 0.5 + 0.5;

    float closestDepth = texture(shadowMap, projCoords.xy).r;
    float currentDepth = projCoords.z;

    if (currentDepth > 1.0)
        currentDepth = 0.0;

    float cosTheta = dot(normal, normalize(lightDir));
    float magic_bias_constant = 0.002;
    float bias = magic_bias_constant*tan(acos(cosTheta));

    float shadow = 0.0;

    vec2 texelSize = 1.0 / textureSize(shadowMap, 0);
    for(int x = -1; x <= 1; ++x)
    {
        for(int y = -1; y <= 1; ++y)
        {
            vec2 offset = vec2(x, y) + rand(projCoords.xy + vec2(x, y));
            float pcfDepth = texture(shadowMap, projCoords.xy + offset * texelSize).r;
            shadow += currentDepth - bias > pcfDepth ? 1.0 : 0.0;
        }
    }
    shadow /= 9.0;

    if(projCoords.z > 1.0)
        shadow = 0.0;

    return shadow;
}

void main() {
    if (texture(diffuseMap, vec3(uv, layer)).a <= 0.5) {
        discard;
    }

    vec3 normal = normalize(norm);
    vec3 lightDir = vec3(0.4, 0.5, -0.25);
    //vec3 lightDir = normalize(lightPos - FragPos);


    // DIF
    float diff = max(dot(normal, lightDir), 0.0);
    vec3 diffuse = diff * lightColor;


    // SPEC
    vec3 viewDir = normalize(viewPos - FragPos);
    vec3 reflectDir = reflect(-lightDir, normal);

    float spec = pow(max(dot(viewDir, reflectDir), 0.0), 64.0);
    vec3 specular = specularStrength * spec * lightColor;

    // AMBIENT
    vec3 ambient = ambientStrength * lightColor;

    float shadow = ShadowCalculation(FragPosLightSpace, lightDir, normal);

    //vec3 result = (ambient + diffuse + specular) * color.xyz;
    //FragColor = vec4(result, 1.0) * texture(diffuseMap, vec3(uv, layer));

    vec3 result = (ambient + (1.0 - shadow) * (diffuse + specular));
    FragColor = vec4(result, 1.0) * texture(diffuseMap, vec3(uv, layer));

}