#version 300 es
precision mediump float;
uniform smapler2D inputImageTexture;
const lowp int GAUSSIAN_SAMPLES = 9;
in highp vec2 textureCoordinate;
in highp vec2 blurCoordinates[GAUSSIAN_SAMPLES];
out vec4 outFragColor;
void main(){
  lowp vec3 sum = vec3(0.0);
  lowp vec4 fragColor = texture(inputImageTexture, textureCoordinate);

  sum += texture(inputImageTexture, blurCoordinates[0]).rgb * 0.05;
  sum += texture(inputImageTexture, blurCoordinates[1]).rgb * 0.09;
  sum += texture(inputImageTexture, blurCoordinates[2]).rgb * 0.12;
  sum += texture(inputImageTexture, blurCoordinates[3]).rgb * 0.15;
  sum += texture(inputImageTexture, blurCoordinates[4]).rgb * 0.18;
  sum += texture(inputImageTexture, blurCoordinates[5]).rgb * 0.15;
  sum += texture(inputImageTexture, blurCoordinates[6]).rgb * 0.12;
  sum += texture(inputImageTexture, blurCoordinates[7]).rgb * 0.09;
  sum += texture(inputImageTexture, blurCoordinates[8]).rgb * 0.05;

  outFragColor = vec4(sum, fragColor.a);
}

