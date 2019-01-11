#version 300 es
precision mediump float;
uniform sampler2D inputImageTexture;
const lowp int GAUSSIAN_SAMPLES = 25;
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
    gaussianWeightTotal = 0.0;
    sum = vec4(0.0);

    sampleColor = texture(inputImageTexture, blurCoordinates[0]);
    distanceFromCenterColor = min(distance(centerColor, sampleColor) * distanceNormaliztionFactor, 1.0);
    gaussianWeight = 0.0244 * (1.0 - distanceFromCenterColor);
    gaussianWeightTotal += gaussianWeight;
    sum += sampleColor * gaussianWeight;

    sampleColor = texture(inputImageTexture, blurCoordinates[1]);
    distanceFromCenterColor = min(distance(centerColor, sampleColor) * distanceNormaliztionFactor, 1.0);
    gaussianWeight = 0.0381 * (1.0 - distanceFromCenterColor);
    gaussianWeightTotal += gaussianWeight;
    sum += sampleColor * gaussianWeight;

    sampleColor = texture(inputImageTexture, blurCoordinates[2]);
    distanceFromCenterColor = min(distance(centerColor, sampleColor) * distanceNormaliztionFactor, 1.0);
    gaussianWeight = 0.0351 * (1.0 - distanceFromCenterColor);
    gaussianWeightTotal += gaussianWeight;
    sum += sampleColor * gaussianWeight;

    sampleColor = texture(inputImageTexture, blurCoordinates[3]);
    distanceFromCenterColor = min(distance(centerColor, sampleColor) * distanceNormaliztionFactor, 1.0);
    gaussianWeight = 0.0281 * (1.0 - distanceFromCenterColor);
    gaussianWeightTotal += gaussianWeight;
    sum += sampleColor * gaussianWeight;

    sampleColor = texture(inputImageTexture, blurCoordinates[4]);
    distanceFromCenterColor = min(distance(centerColor, sampleColor) * distanceNormaliztionFactor, 1.0);
    gaussianWeight = 0.0244 * (1.0 - distanceFromCenterColor);
    gaussianWeightTotal += gaussianWeight;
    sum += sampleColor * gaussianWeight;

    sampleColor = texture(inputImageTexture, blurCoordinates[5]);
    distanceFromCenterColor = min(distance(centerColor, sampleColor) * distanceNormaliztionFactor, 1.0);
    gaussianWeight = 0.0281 * (1.0 - distanceFromCenterColor);
    gaussianWeightTotal += gaussianWeight;
    sum += sampleColor * gaussianWeight;

    sampleColor = texture(inputImageTexture, blurCoordinates[6]);
    distanceFromCenterColor = min(distance(centerColor, sampleColor) * distanceNormaliztionFactor, 1.0);
    gaussianWeight = 0.0547 * (1.0 - distanceFromCenterColor);
    gaussianWeightTotal += gaussianWeight;
    sum += sampleColor * gaussianWeight;

    sampleColor = texture(inputImageTexture, blurCoordinates[7]);
    distanceFromCenterColor = min(distance(centerColor, sampleColor) * distanceNormaliztionFactor, 1.0);
    gaussianWeight = 0.0683 * (1.0 - distanceFromCenterColor);
    gaussianWeightTotal += gaussianWeight;
    sum += sampleColor * gaussianWeight;

    sampleColor = texture(inputImageTexture, blurCoordinates[8]);
    distanceFromCenterColor = min(distance(centerColor, sampleColor) * distanceNormaliztionFactor, 1.0);
    gaussianWeight = 0.0547 * (1.0 - distanceFromCenterColor);
    gaussianWeightTotal += gaussianWeight;
    sum += sampleColor * gaussianWeight;

    sampleColor = texture(inputImageTexture, blurCoordinates[9]);
    distanceFromCenterColor = min(distance(centerColor, sampleColor) * distanceNormaliztionFactor, 1.0);
    gaussianWeight = 0.0281 * (1.0 - distanceFromCenterColor);
    gaussianWeightTotal += gaussianWeight;
    sum += sampleColor * gaussianWeight;

    sampleColor = texture(inputImageTexture, blurCoordinates[10]);
    distanceFromCenterColor = min(distance(centerColor, sampleColor) * distanceNormaliztionFactor, 1.0);
    gaussianWeight = 0.0351 * (1.0 - distanceFromCenterColor);
    gaussianWeightTotal += gaussianWeight;
    sum += sampleColor * gaussianWeight;

    sampleColor = texture(inputImageTexture, blurCoordinates[11]);
    distanceFromCenterColor = min(distance(centerColor, sampleColor) * distanceNormaliztionFactor, 1.0);
    gaussianWeight = 0.0683 * (1.0 - distanceFromCenterColor);
    gaussianWeightTotal += gaussianWeight;
    sum += sampleColor * gaussianWeight;

    sampleColor = texture(inputImageTexture, blurCoordinates[12]);
    distanceFromCenterColor = min(distance(centerColor, sampleColor) * distanceNormaliztionFactor, 1.0);
    gaussianWeight = 0.0953 * (1.0 - distanceFromCenterColor);
    gaussianWeightTotal += gaussianWeight;
    sum += sampleColor * gaussianWeight;

    sampleColor = texture(inputImageTexture, blurCoordinates[13]);
    distanceFromCenterColor = min(distance(centerColor, sampleColor) * distanceNormaliztionFactor, 1.0);
    gaussianWeight = 0.0683 * (1.0 - distanceFromCenterColor);
    gaussianWeightTotal += gaussianWeight;
    sum += sampleColor * gaussianWeight;

    sampleColor = texture(inputImageTexture, blurCoordinates[14]);
    distanceFromCenterColor = min(distance(centerColor, sampleColor) * distanceNormaliztionFactor, 1.0);
    gaussianWeight = 0.0351 * (1.0 - distanceFromCenterColor);
    gaussianWeightTotal += gaussianWeight;
    sum += sampleColor * gaussianWeight;

    sampleColor = texture(inputImageTexture, blurCoordinates[15]);
    distanceFromCenterColor = min(distance(centerColor, sampleColor) * distanceNormaliztionFactor, 1.0);
    gaussianWeight = 0.0281 * (1.0 - distanceFromCenterColor);
    gaussianWeightTotal += gaussianWeight;
    sum += sampleColor * gaussianWeight;

    sampleColor = texture(inputImageTexture, blurCoordinates[16]);
    distanceFromCenterColor = min(distance(centerColor, sampleColor) * distanceNormaliztionFactor, 1.0);
    gaussianWeight = 0.0547 * (1.0 - distanceFromCenterColor);
    gaussianWeightTotal += gaussianWeight;
    sum += sampleColor * gaussianWeight;

    sampleColor = texture(inputImageTexture, blurCoordinates[17]);
    distanceFromCenterColor = min(distance(centerColor, sampleColor) * distanceNormaliztionFactor, 1.0);
    gaussianWeight = 0.0683 * (1.0 - distanceFromCenterColor);
    gaussianWeightTotal += gaussianWeight;
    sum += sampleColor * gaussianWeight;

    sampleColor = texture(inputImageTexture, blurCoordinates[18]);
    distanceFromCenterColor = min(distance(centerColor, sampleColor) * distanceNormaliztionFactor, 1.0);
    gaussianWeight = 0.0547 * (1.0 - distanceFromCenterColor);
    gaussianWeightTotal += gaussianWeight;
    sum += sampleColor * gaussianWeight;

    sampleColor = texture(inputImageTexture, blurCoordinates[19]);
    distanceFromCenterColor = min(distance(centerColor, sampleColor) * distanceNormaliztionFactor, 1.0);
    gaussianWeight = 0.0281 * (1.0 - distanceFromCenterColor);
    gaussianWeightTotal += gaussianWeight;
    sum += sampleColor * gaussianWeight;

    sampleColor = texture(inputImageTexture, blurCoordinates[20]);
    distanceFromCenterColor = min(distance(centerColor, sampleColor) * distanceNormaliztionFactor, 1.0);
    gaussianWeight = 0.0244 * (1.0 - distanceFromCenterColor);
    gaussianWeightTotal += gaussianWeight;
    sum += sampleColor * gaussianWeight;

    sampleColor = texture(inputImageTexture, blurCoordinates[21]);
    distanceFromCenterColor = min(distance(centerColor, sampleColor) * distanceNormaliztionFactor, 1.0);
    gaussianWeight = 0.0281 * (1.0 - distanceFromCenterColor);
    gaussianWeightTotal += gaussianWeight;
    sum += sampleColor * gaussianWeight;

    sampleColor = texture(inputImageTexture, blurCoordinates[22]);
    distanceFromCenterColor = min(distance(centerColor, sampleColor) * distanceNormaliztionFactor, 1.0);
    gaussianWeight = 0.0351 * (1.0 - distanceFromCenterColor);
    gaussianWeightTotal += gaussianWeight;
    sum += sampleColor * gaussianWeight;

    sampleColor = texture(inputImageTexture, blurCoordinates[23]);
    distanceFromCenterColor = min(distance(centerColor, sampleColor) * distanceNormaliztionFactor, 1.0);
    gaussianWeight = 0.0381 * (1.0 - distanceFromCenterColor);
    gaussianWeightTotal += gaussianWeight;
    sum += sampleColor * gaussianWeight;

    sampleColor = texture(inputImageTexture, blurCoordinates[24]);
    distanceFromCenterColor = min(distance(centerColor, sampleColor) * distanceNormaliztionFactor, 1.0);
    gaussianWeight = 0.0244 * (1.0 - distanceFromCenterColor);
    gaussianWeightTotal += gaussianWeight;
    sum += sampleColor * gaussianWeight;

    outFragColor = sum / gaussianWeightTotal;
}

