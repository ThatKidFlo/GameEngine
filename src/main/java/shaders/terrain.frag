#version 400 core

// output from the vertex shader is the input for the fragment shader
in vec2 pass_textureCoordinates;
in vec3 surfaceNormal;
in vec3 toLightVector;
in vec3 toCameraVector;
in float visibility;

out vec4 out_colour;

uniform sampler2D backgroundTexture;
uniform sampler2D redTexture;
uniform sampler2D greenTexture;
uniform sampler2D blueTexture;
uniform sampler2D blendmap;

uniform vec3 lightColour;
uniform float shineDamper;
uniform float reflectivity;
uniform vec3 skyColour;

void main(void) {

    vec4 blendmapColour = texture(blendmap, pass_textureCoordinates);

    // should render background texture when blendmap contains black.
    float backTextureAmount = 1 - (blendmapColour.r + blendmapColour.g + blendmapColour.b);

    vec2 tiledCoords = pass_textureCoordinates * 40.0;

    vec4 backgroundTextureColor = texture(backgroundTexture, tiledCoords) * backTextureAmount;
    vec4 redTextureColor = texture(redTexture, tiledCoords) * blendmapColour.r;
    vec4 greenTextureColor = texture(greenTexture, tiledCoords) * blendmapColour.g;
    vec4 blueTextureColor = texture(blueTexture, tiledCoords) * blendmapColour.b;

    vec4 totalColour = backgroundTextureColor + redTextureColor + greenTextureColor + blueTextureColor;

    vec3 unitNormal = normalize(surfaceNormal);
    vec3 unitLightVector = normalize(toLightVector);

    float nDotl = dot(unitNormal, unitLightVector);
    float brightness = max(nDotl, 0.2);
    vec3 diffuse = brightness * lightColour;

    vec3 unitVectorToCamera = normalize(toCameraVector);
    vec3 lightDirection = -unitLightVector;
    vec3 reflectedLightDirection = reflect(lightDirection, unitNormal);

    float specularFactor = dot(reflectedLightDirection, unitVectorToCamera);
    specularFactor = max(specularFactor, 0.0);

    float dampedFactor = pow(specularFactor, shineDamper);

    vec3 finalSpecular = dampedFactor * reflectivity * lightColour;
    out_colour = vec4(diffuse, 1.0) * totalColour + vec4(finalSpecular, 1.0);
    out_colour = mix(vec4(skyColour, 1.0), out_colour, visibility);
}
