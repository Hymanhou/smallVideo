#version 300 es
precision mediump float;
uniform sampler2D inputImageTexture;
const lowp int GAUSSIAN_SAMPLES = 25;
const lowp int radiouis = 5;
in highp vec2 textureCoordinate;
in highp vec2 blurCoordinates[GAUSSIAN_SAMPLES];
out vec4 outFragColor;
void main(){
    outFragColor = texture(inputImageTexture, textureCoordinate);
    vec3 color_sum = vec3(0.0);
    for(int i = 0; i < GAUSSIAN_SAMPLES; i++) {
        color_sum += texture(inputImageTexture, blurCoordinates[i]).rgb;
    }

    vec3 variance = vec3(0.0);
    for(int i = 0; i < GAUSSIAN_SAMPLES; i++) {
        vec3 color = texture(inputImageTexture, blurCoordinates[i]).rgb;
        variance = variance + color * color;
    }

    highp vec3 dr = (variance - color_sum * color_sum / vec3(GAUSSIAN_SAMPLES)) / vec3(GAUSSIAN_SAMPLES);
    highp vec3 mean =  color_sum / vec3(GAUSSIAN_SAMPLES);
    highp vec3 k_factor = vec3(0.0);//dr / (dr + vec3(0.4));

    vec3 smooth_color = (vec3(1.0) - k_factor) * mean + k_factor * outFragColor.rgb;
    smooth_color = clamp(smooth_color, vec3( 0.0 ), vec3( 1.0 ));
    outFragColor.rgb = smooth_color;
}