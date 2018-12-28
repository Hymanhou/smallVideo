package com.hyuan.smallvideo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLUtils;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class PicturePreview {

    private int[] textures;
    private Bitmap bitmap;
    private int program;
    private int samplerLoc;
    private float[] verticesData = {
            -1.0f, 1.0f, 0.0f, // Position 0
            0.0f, 0.0f, // TexCoord 0
            -1.0f, -1.0f, 0.0f, // Position 1
            0.0f, 1.0f, // TexCoord 1
            1.0f, -1.0f, 0.0f, // Position 2
            1.0f, 1.0f, // TexCoord 2
            1.0f, 1.0f, 0.0f, // Position 3
            1.0f, 0.0f // TexCoord 3
    };
    private short[] indicesData = {
            0, 1, 2,
            0, 2, 3
    };
    private FloatBuffer verticesBuff;
    private ShortBuffer indicesBuff;

    public PicturePreview(String fileName, Context context) {
        ByteBuffer vb = ByteBuffer.allocateDirect(verticesData.length * 4);
        vb.order(ByteOrder.nativeOrder());
        verticesBuff = vb.asFloatBuffer();
        verticesBuff.put(verticesData);
        verticesBuff.position(0);

        ByteBuffer ib = ByteBuffer.allocateDirect(indicesData.length * 2);
        ib.order(ByteOrder.nativeOrder());
        indicesBuff = ib.asShortBuffer();
        indicesBuff.put(indicesData);
        indicesBuff.position(0);

        try {
            bitmap = BitmapFactory.decodeStream(context.getResources().getAssets().open(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        textures = new int[1];
        GLES30.glGenTextures(1, textures, 0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textures[0]);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0);

        program = ShaderUtil.createShaderProgram("texture2d_vertex.glsl", "texture2d_fragment.glsl",
                context.getResources());
        samplerLoc = GLES30.glGetUniformLocation(program, "s_texture");
    }

    public void draw() {
        GLES30.glDisable(GLES20.GL_CULL_FACE);

        GLES30.glUseProgram(program);

        GLES30.glEnableVertexAttribArray(0);
        verticesBuff.position(0);
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT,
                false, 4 * 5, verticesBuff);
        if (GLES30.glGetError() != 0) {
            Log.e("hyuan", GLES30.glGetError() + " ERROR");
        }
        verticesBuff.position(3);
        GLES30.glVertexAttribPointer(1, 2, GLES30.GL_FLOAT,
                false, 4 * 5, verticesBuff);

        GLES30.glEnableVertexAttribArray(1);

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textures[0]);
        GLES30.glUniform1i(samplerLoc, 0);

        GLES30.glDrawElements(GLES30.GL_TRIANGLES, 6, GLES30.GL_UNSIGNED_SHORT, indicesBuff);

        GLES30.glDisableVertexAttribArray(0);
    }
}
