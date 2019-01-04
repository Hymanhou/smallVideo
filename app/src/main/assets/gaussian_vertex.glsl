#version 300 es
layout(location=0) in vec4 a_position;
layout(location=1) in vec2 a_texCoord;
uniform mat4 u_texMatrix;
uniform highp float width;
uniform highp float height;
uniform highp float sigma = 1.5;
out vec2 v_texCoord;
out vec2 blurCoords[8];
out float weightMatrix[8];
void main() {
    gl_Position = a_position;
    v_texCoord = (u_texMatrix * vec4(a_texCoord, 0, 1)).xy;

    highp float x_step = 2.0 / width;
    highp float y_step = 2.0 / height;

    blurCoords[0] = a_texCoord + vec2(-1 * x_step, 1 * y_step);
    blurCoords[1] = a_texCoord + vec2(0, y_step);
    blurCoords[2] = a_texCoord + vec2(x_step, y_step);
    blurCoords[3] = a_texCoord + vec2(x_step, 0);
    blurCoords[4] = a_texCoord + vec2(x_step, -1 * y_step);
    blurCoords[5] = a_texCoord + vec2(0, -1 * y_step);
    blurCoords[6] = a_texCoord + vec2(-1 * x_step, -1 * y_step);
    blurCoords[7] = a_texCoord + vec2(-1 * x_step, 0);

    weightMatrix[0] = gussian_weight(x_step, y_step);
    weightMatrix[1] = gussian_weight(0, y_step);
    weightMatrix[2] = weightMatrix[0];
    weightMatrix[3] = weightMatrix[1];
    weightMatrix[4] = weightMatrix[0];
    weightMatrix[5] = weightMatrix[1];
    weightMatrix[6] = weightMatrix[0];
    weightMatrix[7] = weightMatrix[1];

    float sum = 0;
    for(int i = 0; i < 7; i++ ){
        sum += weightMatrix[i];
    }

    for(int i = 0; i < 7; i++ ){
        weightMatrix[i] = weightMatrix[i]/sum;
    }
}

float gussian_weight(float x, float y){
    float e = 2.718281828;
    float pi = 3.1415926;
    float k = 1 / (2 * pi * sigma * sigma);
    float ex = -1 * (x*x + y*y) / 2 * sigma * sigma;
    return k * sqrt(e, ex);
}
