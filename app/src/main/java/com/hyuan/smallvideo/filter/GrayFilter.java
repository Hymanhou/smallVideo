package com.hyuan.smallvideo.filter;

public class GrayFilter extends AffectFilter {
    private static final String FRAGMENT_SHADER = "precision mediump float;\n" +
            "varying vec2 textureCoordinate;\n" +
            "uniform sampler2D inputImageTexture;\n" +
            "void main() {\n" +
            "    vec4 color=texture2D(inputImageTexture, textureCoordinate);\n" +
            "    float rgb=color.g;\n" +
            "    vec4 c=vec4(rgb,rgb,rgb,color.a);\n" +
            "    gl_FragColor = c;\n" +
            "}";

    public GrayFilter() {
        super(NO_FILTER_VERTEX_SHADER, FRAGMENT_SHADER);
    }
}
