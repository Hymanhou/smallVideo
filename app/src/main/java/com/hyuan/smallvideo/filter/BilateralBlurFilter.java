/**
 * @author wysaid
 * @mail admin@wysaid.org
 */

package com.hyuan.smallvideo.filter;

import android.content.res.Resources;
import android.opengl.GLES20;
import com.hyuan.smallvideo.utils.ShaderUtil;


public class BilateralBlurFilter extends AffectFilter {
    public static final String BILATERAL_VERTEX_SHADER = "" +
            "#version 300 es\n" +
            "layout(location=0) in vec4 position;\n" +
            "layout(location=1) in vec4 inputTextureCoordinate;\n" +
            "out vec2 textureCoordinate;\n" +
            "const int GAUSSIAN_SAMPLES = 9;\n" +
            "out vec2 blurCoordinates[GAUSSIAN_SAMPLES];\n" +

            "uniform vec2 singleStepOffset;\n" +
            "void main()\n" +
            "{\n" +
            "	gl_Position = position;\n" +
            "	textureCoordinate = inputTextureCoordinate.xy;\n" +

            "	int multiplier = 0;\n" +
            "	vec2 blurStep;\n" +

            "	for (int i = 0; i < GAUSSIAN_SAMPLES; i++)\n" +
            "	{\n" +
            "		multiplier = (i - ((GAUSSIAN_SAMPLES - 1) / 2));\n" +

            "		blurStep = float(multiplier) * singleStepOffset;\n" +
            "		blurCoordinates[i] = inputTextureCoordinate.xy + blurStep;\n" +
            "	}\n" +
            "}";

    public static final String BILATERAL_FRAGMENT_SHADER = "" +
            " #version 300 es\n" +
            " precision mediump float;\n" +
            " uniform sampler2D inputImageTexture;\n" +

            " const lowp int GAUSSIAN_SAMPLES = 9;\n" +

            " in highp vec2 textureCoordinate;\n" +
            " in highp vec2 blurCoordinates[GAUSSIAN_SAMPLES];\n" +

            " uniform mediump float distanceNormalizationFactor;\n" +
            " out vec4 fragColor;\n" +
            " void main()\n" +
            " {\n" +
            "     lowp vec4 centralColor;\n" +
            "     lowp float gaussianWeightTotal;\n" +
            "     lowp vec4 sum;\n" +
            "     lowp vec4 sampleColor;\n" +
            "     lowp float distanceFromCentralColor;\n" +
            "     lowp float gaussianWeight;\n" +
            "     \n" +
            "     centralColor = texture(inputImageTexture, blurCoordinates[4]);\n" +
            "     gaussianWeightTotal = 0.18;\n" +
            "     sum = centralColor * 0.18;\n" +
            "     \n" +
            "     sampleColor = texture(inputImageTexture, blurCoordinates[0]);\n" +
            "     distanceFromCentralColor = min(distance(centralColor, sampleColor) * distanceNormalizationFactor, 1.0);\n" +
            "     gaussianWeight = 0.05 * (1.0 - distanceFromCentralColor);\n" +
            "     gaussianWeightTotal += gaussianWeight;\n" +
            "     sum += sampleColor * gaussianWeight;\n" +

            "     sampleColor = texture(inputImageTexture, blurCoordinates[1]);\n" +
            "     distanceFromCentralColor = min(distance(centralColor, sampleColor) * distanceNormalizationFactor, 1.0);\n" +
            "     gaussianWeight = 0.09 * (1.0 - distanceFromCentralColor);\n" +
            "     gaussianWeightTotal += gaussianWeight;\n" +
            "     sum += sampleColor * gaussianWeight;\n" +

            "     sampleColor = texture(inputImageTexture, blurCoordinates[2]);\n" +
            "     distanceFromCentralColor = min(distance(centralColor, sampleColor) * distanceNormalizationFactor, 1.0);\n" +
            "     gaussianWeight = 0.12 * (1.0 - distanceFromCentralColor);\n" +
            "     gaussianWeightTotal += gaussianWeight;\n" +
            "     sum += sampleColor * gaussianWeight;\n" +

            "     sampleColor = texture(inputImageTexture, blurCoordinates[3]);\n" +
            "     distanceFromCentralColor = min(distance(centralColor, sampleColor) * distanceNormalizationFactor, 1.0);\n" +
            "     gaussianWeight = 0.15 * (1.0 - distanceFromCentralColor);\n" +
            "     gaussianWeightTotal += gaussianWeight;\n" +
            "     sum += sampleColor * gaussianWeight;\n" +

            "     sampleColor = texture(inputImageTexture, blurCoordinates[5]);\n" +
            "     distanceFromCentralColor = min(distance(centralColor, sampleColor) * distanceNormalizationFactor, 1.0);\n" +
            "     gaussianWeight = 0.15 * (1.0 - distanceFromCentralColor);\n" +
            "     gaussianWeightTotal += gaussianWeight;\n" +
            "     sum += sampleColor * gaussianWeight;\n" +

            "     sampleColor = texture(inputImageTexture, blurCoordinates[6]);\n" +
            "     distanceFromCentralColor = min(distance(centralColor, sampleColor) * distanceNormalizationFactor, 1.0);\n" +
            "     gaussianWeight = 0.12 * (1.0 - distanceFromCentralColor);\n" +
            "     gaussianWeightTotal += gaussianWeight;\n" +
            "     sum += sampleColor * gaussianWeight;\n" +

            "     sampleColor = texture(inputImageTexture, blurCoordinates[7]);\n" +
            "     distanceFromCentralColor = min(distance(centralColor, sampleColor) * distanceNormalizationFactor, 1.0);\n" +
            "     gaussianWeight = 0.09 * (1.0 - distanceFromCentralColor);\n" +
            "     gaussianWeightTotal += gaussianWeight;\n" +
            "     sum += sampleColor * gaussianWeight;\n" +

            "     sampleColor = texture(inputImageTexture, blurCoordinates[8]);\n" +
            "     distanceFromCentralColor = min(distance(centralColor, sampleColor) * distanceNormalizationFactor, 1.0);\n" +
            "     gaussianWeight = 0.05 * (1.0 - distanceFromCentralColor);\n" +
            "     gaussianWeightTotal += gaussianWeight;\n" +
            "     sum += sampleColor * gaussianWeight;\n" +
            "     fragColor = sum / gaussianWeightTotal;\n" +
            " }";

    private float distanceNormalizationFactor;
    private int disFactorLocation;
    private int singleStepOffsetLocation;

    public BilateralBlurFilter() {
        this(8.0f);
    }

    public BilateralBlurFilter(Resources resources) {
        super(resources, "blur_vertex.glsl", "blur_fragment.glsl");
        this.distanceNormalizationFactor = 8.0f;
    }

    public BilateralBlurFilter(final float distanceNormalizationFactor) {
        super(BILATERAL_VERTEX_SHADER, BILATERAL_FRAGMENT_SHADER);
        this.distanceNormalizationFactor = distanceNormalizationFactor;
    }

    @Override
    public void onInit() {
        super.onInit();
        disFactorLocation = GLES20.glGetUniformLocation(getProgram(), "distanceNormalizationFactor");
        singleStepOffsetLocation = GLES20.glGetUniformLocation(getProgram(), "singleStepOffset");
    }

    @Override
    public void onInitialized() {
        super.onInitialized();
        setDistanceNormalizationFactor(distanceNormalizationFactor);
    }

    public void setDistanceNormalizationFactor(final float newValue) {
        distanceNormalizationFactor = newValue;
        setFloat(disFactorLocation, newValue);
    }

    private void setTexelSize(final float w, final float h) {
        setFloatVec2(singleStepOffsetLocation, new float[]{2.0f / w, 2.0f / h});
    }

    @Override
    public void onOutputSizeChanged(final int width, final int height) {
        super.onOutputSizeChanged(width, height);
        setTexelSize(width, height);
    }
}
