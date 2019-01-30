#version 300 es
precision mediump float;
uniform sampler2D inputImageTexture;
const lowp int GAUSSIAN_SAMPLES = 25;
const float RANGE_MIN = 0.0;
const float RANGE_MAX = 255.0;
in highp vec2 textureCoordinate;
in highp vec2 blurCoordinates[GAUSSIAN_SAMPLES];
out vec4 outFragColor;
void main(){
    outFragColor = texture(inputImageTexture, textureCoordinate);
    vec3 color_sum = vec3(RANGE_MIN);
    for(int i = 0; i < GAUSSIAN_SAMPLES; i++) {
        color_sum += texture(inputImageTexture, blurCoordinates[i]).rgb;
    }
    color_sum = color_sum / vec3(25.0);
    if(color_sum.r * RANGE_MAX > RANGE_MIN && color_sum.r * RANGE_MAX < RANGE_MAX) {
        outFragColor.r = RANGE_MIN;
    }
    if(color_sum.g * RANGE_MAX > RANGE_MIN && color_sum.g * RANGE_MAX < RANGE_MAX) {
        outFragColor.g = RANGE_MIN;
    }
    if(color_sum.b * RANGE_MAX > RANGE_MIN && color_sum.b * RANGE_MAX < RANGE_MAX) {
        outFragColor.b = RANGE_MIN;
    }
}
