package com.hyuan.smallvideo;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLES30;
import com.hyuan.smallvideo.utils.ShaderUtil;

import java.nio.*;

public class Square {

    private float vertexes[] = {
            0.5f, 0.5f, 0.0f,   // 右上角
            -0.5f, 0.5f, 0.0f,   // 左上角
            0.5f, -0.5f, 0.0f,  // 右下角

//            0.5f, -0.5f, 0.0f,  // 右下角
//            -0.5f, 0.5f, 0.0f,   // 左上角
            -0.5f, -0.5f, 0.0f // 左下角
    };

    private short indices[] = {
            0, 1, 2,// 第一个三角形
            2, 1, 3  // 第二个三角形
    };

    private FloatBuffer vertexesBuff;
    private ShortBuffer indicesBuff;
    private int shaderProgram;
    private int[] VBOIds = new int[2];
    private int[] VAOIds = new int[1];

    private final String VERTEX = "#version 300 es\n" +
            "layout(location=0) in vec4 a_position;\n" +
            "void main() {\n" +
            "    gl_Position = a_position;\n" +
            "}";

    private final String FRAGMENT = "#version 300 es\n" +
            "precision mediump float;\n" +
            "out vec4 fragColor;\n" +
            "void main() {\n" +
            "    fragColor = vec4(1.0, 0.0, 0.0, 1.0);\n" +
            "}\n";

    public Square(){
        ByteBuffer vb = ByteBuffer.allocateDirect(vertexes.length * 4);
        vb.order(ByteOrder.nativeOrder());
        vertexesBuff = vb.asFloatBuffer();
        vertexesBuff.put(vertexes);
        vertexesBuff.position(0);

        ByteBuffer ib = ByteBuffer.allocateDirect(indices.length * 2);
        ib.order(ByteOrder.nativeOrder());
        indicesBuff = ib.asShortBuffer();
        indicesBuff.put(indices);
        indicesBuff.position(0);

        shaderProgram = ShaderUtil.createShaderProgram(VERTEX, FRAGMENT);

        GLES30.glGenVertexArrays(1, VAOIds, 0);
        GLES30.glBindVertexArray(VAOIds[0]);

        GLES30.glGenBuffers(2, VBOIds, 0);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, VBOIds[0]);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, vertexes.length * 4, vertexesBuff, GLES30.GL_STATIC_DRAW);

        GLES30.glEnableVertexAttribArray(0);
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 3 * 4, 0);

        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, VBOIds[1]);
        GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER, indices.length * 2, indicesBuff, GLES30.GL_STATIC_DRAW);


        GLES30.glBindVertexArray(0);

    }

    public void draw() {
        GLES30.glClear(GLES20.GL_COLOR_BUFFER_BIT|GLES20.GL_DEPTH_BUFFER_BIT);
        //GLES30.glDisable(GLES30.GL_CULL_FACE);
        GLES30.glUseProgram(shaderProgram);
        GLES30.glBindVertexArray(VAOIds[0]);

        GLES30.glDrawElements(GLES30.GL_TRIANGLES, indices.length, GLES30.GL_UNSIGNED_SHORT, 0);

        GLES30.glBindVertexArray(0);

    }

}
