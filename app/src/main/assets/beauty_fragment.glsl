#version 300 es
precision mediump float;
uniform sampler2D inputImageTexture;
const lowp int GAUSSIAN_SAMPLES = 9;
in highp vec2 textureCoordinate;
in highp vec2 blurCoordinates[GAUSSIAN_SAMPLES];

uniform mediump float distanceNormaliztionFactor;

out vec4 outFragColor;
void main(){
    lowp vec4 centerColor;
    lowp float gaussianWeightTotal;
    lowp vec4 sum;
    lowp vec4 sampleColor;
    lowp float distanceFromCenterColor;
    lowp float gaussianWeight;

    centerColor = texture(inputImageTexture, blurCoordinates[4]);
    gaussianWeightTotal = 0.18;
    sum = centerColor * 0.18;

    sampleColor = texture(inputImageTexture, blurCoordinates[0]);
    distanceFromCenterColor = min(distance(centerColor, sampleColor) * distanceNormaliztionFactor, 1.0);
    gaussianWeight = 0.05 * (1.0 - distanceFromCenterColor);
    gaussianWeightTotal += gaussianWeight;
    sum += sampleColor * gaussianWeight;
    
    sampleColor = texture(inputImageTexture, blurCoordinates[1]);
    distanceFromCenterColor = min(distance(centerColor, sampleColor) * distanceNormaliztionFactor, 1.0);
    gaussianWeight = 0.09 * (1.0 - distanceFromCenterColor);
    gaussianWeightTotal += gaussianWeight;
    sum += sampleColor * gaussianWeight;
    
    sampleColor = texture(inputImageTexture, blurCoordinates[2]);
    distanceFromCenterColor = min(distance(centerColor, sampleColor) * distanceNormaliztionFactor, 1.0);
    gaussianWeight = 0.12 * (1.0 - distanceFromCenterColor);
    gaussianWeightTotal += gaussianWeight;
    sum += sampleColor * gaussianWeight;
    
    sampleColor = texture(inputImageTexture, blurCoordinates[3]);
    distanceFromCenterColor = min(distance(centerColor, sampleColor) * distanceNormaliztionFactor, 1.0);
    gaussianWeight = 0.15 * (1.0 - distanceFromCenterColor);
    gaussianWeightTotal += gaussianWeight;
    sum += sampleColor * gaussianWeight;

    sampleColor = texture(inputImageTexture, blurCoordinates[5]);
    distanceFromCenterColor = min(distance(centerColor, sampleColor) * distanceNormaliztionFactor, 1.0);
    gaussianWeight = 0.15 * (1.0 - distanceFromCenterColor);
    gaussianWeightTotal += gaussianWeight;
    sum += sampleColor * gaussianWeight;

    sampleColor = texture(inputImageTexture, blurCoordinates[6]);
    distanceFromCenterColor = min(distance(centerColor, sampleColor) * distanceNormaliztionFactor, 1.0);
    gaussianWeight = 0.12 * (1.0 - distanceFromCenterColor);
    gaussianWeightTotal += gaussianWeight;
    sum += sampleColor * gaussianWeight;

    sampleColor = texture(inputImageTexture, blurCoordinates[7]);
    distanceFromCenterColor = min(distance(centerColor, sampleColor) * distanceNormaliztionFactor, 1.0);
    gaussianWeight = 0.09 * (1.0 - distanceFromCenterColor);
    gaussianWeightTotal += gaussianWeight;
    sum += sampleColor * gaussianWeight;

    sampleColor = texture(inputImageTexture, blurCoordinates[8]);
    distanceFromCenterColor = min(distance(centerColor, sampleColor) * distanceNormaliztionFactor, 1.0);
    gaussianWeight = 0.05 * (1.0 - distanceFromCenterColor);
    gaussianWeightTotal += gaussianWeight;
    sum += sampleColor * gaussianWeight;

    outFragColor = sum / gaussianWeightTotal;
}

