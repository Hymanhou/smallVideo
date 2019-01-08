#version 300 es
layout(location=0) in vec4 a_position;
layout(location=1) in vec4 inputTextureCoordinate;
const int GAUSSIAN_SAMPLES = 9;

uniform vec2 stepOffset;

out vec2 textureCoordinate;
out vec2 blurCoordinates[GAUSSIAN_SAMPLES];

void main() {
    gl_Position = a_position;
    textureCoordinate = inputTextureCoordinate.xy;

    //calculate the positions for the blur
    int multiplier = 0;
    vec2 blurStep;

    for (int i = 0; i < GAUSSIAN_SAMPLES; i++) {
        //calculate the direction
        multiplier = (i - ((GAUSSIAN_SAMPLES - 1)/2));
        //blur in x
        blurStep = float(multiplier) * stepOffset;
        blurCoordinates[i] = inputTextureCoordinate.xy + blurStep;
    }
}
