package com.hyuan.smallvideo.filter;

public class GrayFilter extends AffectFilter {
    private static final String FRAGMENT_SHADER = "" +
            "#version 300 es\n" +
            "precision mediump float;\n" +
            "in vec2 textureCoordinate;\n" +
            "uniform sampler2D inputImageTexture;\n" +
            "out vec4 fragColor;\n" +
            "void main() {\n" +
            "    vec4 color=texture(inputImageTexture, textureCoordinate);\n" +
            "    float rgb=color.g;\n" +
            "    vec4 c=vec4(rgb,rgb,rgb,color.a);\n" +
            "    fragColor = c;\n" +
            "}";

    public GrayFilter() {
        super(NO_FILTER_VERTEX_SHADER, FRAGMENT_SHADER);
    }
}
