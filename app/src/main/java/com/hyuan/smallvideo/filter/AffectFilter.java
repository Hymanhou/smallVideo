package com.hyuan.smallvideo.filter;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.PointF;
import android.opengl.GLES30;
import com.hyuan.smallvideo.utils.ShaderUtil;

import java.io.InputStream;
import java.nio.FloatBuffer;
import java.util.LinkedList;

public class AffectFilter {
    public static final String NO_FILTER_VERTEX_SHADER = "" +
            "#version 300 es\n" +
            "layout(location=0) in vec4 position;\n" +
            "layout(location=1) in vec4 inputTextureCoordinate;\n" +
            "out vec2 textureCoordinate;\n" +
            "void main()\n" +
            "{\n" +
            "    gl_Position = position;\n" +
            "    textureCoordinate = inputTextureCoordinate.xy;\n" +
            "}";
    public static final String NO_FILTER_FRAGMENT_SHADER = "" +
            "#version 300 es\n" +
            "precision mediump float;\n" +
            "in highp vec2 textureCoordinate;\n" +
            "uniform sampler2D inputImageTexture;\n" +
            "out vec4 fragColor;\n" +
            "void main()\n" +
            "{\n" +
            "     fragColor = texture(inputImageTexture, textureCoordinate);\n" +
            "}";

    private final LinkedList<Runnable> runOnDraw;
    private final String vertexShader;
    private final String fragmentShader;
    private int glProgId;
    private int glAttribPosition = 0;
    private int glUniformTexture;
    private int glAttribTextureCoordinate = 1;
    private int outputWidth;
    private int outputHeight;
    private boolean isInitialized;

    public AffectFilter() {
        this(NO_FILTER_VERTEX_SHADER, NO_FILTER_FRAGMENT_SHADER);
    }

    public AffectFilter(Resources res, String vertexFile, String fragmentFile) {
        this.vertexShader = ShaderUtil.loadShaderSource(vertexFile, res);
        this.fragmentShader = ShaderUtil.loadShaderSource(fragmentFile, res);
        runOnDraw = new LinkedList<>();
    }

    public AffectFilter(final String vertexShader, final String fragmentShader) {
        runOnDraw = new LinkedList<>();
        this.vertexShader = vertexShader;
        this.fragmentShader = fragmentShader;
    }

    private final void init() {
        onInit();
        onInitialized();
    }

    public void onInit() {
        glProgId = ShaderUtil.createShaderProgram(vertexShader, fragmentShader);
        glUniformTexture = GLES30.glGetUniformLocation(glProgId, "inputImageTexture");
        isInitialized = true;
    }

    public void onInitialized() {
    }

    public void ifNeedInit() {
        if (!isInitialized) init();
    }

    public final void destroy() {
        isInitialized = false;
        GLES30.glDeleteProgram(glProgId);
        onDestroy();
    }

    public void onDestroy() {
    }

    public void onOutputSizeChanged(final int width, final int height) {
        outputWidth = width;
        outputHeight = height;
    }

    public void onDraw(final int textureId, final FloatBuffer cubeBuffer,
                       final FloatBuffer textureBuffer) {
        GLES30.glUseProgram(glProgId);
        runPendingOnDrawTasks();
        if (!isInitialized) {
            return;
        }

        cubeBuffer.position(0);
        GLES30.glVertexAttribPointer(glAttribPosition, 2, GLES30.GL_FLOAT, false, 0, cubeBuffer);
        GLES30.glEnableVertexAttribArray(glAttribPosition);
        textureBuffer.position(0);
        GLES30.glVertexAttribPointer(glAttribTextureCoordinate, 2, GLES30.GL_FLOAT, false, 0,
                textureBuffer);
        GLES30.glEnableVertexAttribArray(glAttribTextureCoordinate);
        if (textureId != -1) {
            GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId);
            GLES30.glUniform1i(glUniformTexture, 0);
        }
        onDrawArraysPre();
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, 4);
        GLES30.glDisableVertexAttribArray(glAttribPosition);
        GLES30.glDisableVertexAttribArray(glAttribTextureCoordinate);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);
    }

    protected void onDrawArraysPre() {
    }

    protected void runPendingOnDrawTasks() {
        while (!runOnDraw.isEmpty()) {
            runOnDraw.removeFirst().run();
        }
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    public int getOutputWidth() {
        return outputWidth;
    }

    public int getOutputHeight() {
        return outputHeight;
    }

    public int getProgram() {
        return glProgId;
    }

    public int getAttribPosition() {
        return glAttribPosition;
    }

    public int getAttribTextureCoordinate() {
        return glAttribTextureCoordinate;
    }

    public int getUniformTexture() {
        return glUniformTexture;
    }

    protected void setInteger(final int location, final int intValue) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                ifNeedInit();
                GLES30.glUniform1i(location, intValue);
            }
        });
    }

    protected void setFloat(final int location, final float floatValue) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                ifNeedInit();
                GLES30.glUniform1f(location, floatValue);
            }
        });
    }

    protected void setFloatVec2(final int location, final float[] arrayValue) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                ifNeedInit();
                GLES30.glUniform2fv(location, 1, FloatBuffer.wrap(arrayValue));
            }
        });
    }

    protected void setFloatVec3(final int location, final float[] arrayValue) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                ifNeedInit();
                GLES30.glUniform3fv(location, 1, FloatBuffer.wrap(arrayValue));
            }
        });
    }

    protected void setFloatVec4(final int location, final float[] arrayValue) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                ifNeedInit();
                GLES30.glUniform4fv(location, 1, FloatBuffer.wrap(arrayValue));
            }
        });
    }

    protected void setFloatArray(final int location, final float[] arrayValue) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                ifNeedInit();
                GLES30.glUniform1fv(location, arrayValue.length, FloatBuffer.wrap(arrayValue));
            }
        });
    }

    protected void setPoint(final int location, final PointF point) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                ifNeedInit();
                float[] vec2 = new float[2];
                vec2[0] = point.x;
                vec2[1] = point.y;
                GLES30.glUniform2fv(location, 1, vec2, 0);
            }
        });
    }

    protected void setUniformMatrix3f(final int location, final float[] matrix) {
        runOnDraw(new Runnable() {

            @Override
            public void run() {
                ifNeedInit();
                GLES30.glUniformMatrix3fv(location, 1, false, matrix, 0);
            }
        });
    }

    protected void setUniformMatrix4f(final int location, final float[] matrix) {
        runOnDraw(new Runnable() {

            @Override
            public void run() {
                ifNeedInit();
                GLES30.glUniformMatrix4fv(location, 1, false, matrix, 0);
            }
        });
    }

    protected void runOnDraw(final Runnable runnable) {
        synchronized (runOnDraw) {
            runOnDraw.addLast(runnable);
        }
    }

    public static String loadShader(String file, Context context) {
        try {
            AssetManager assetManager = context.getAssets();
            InputStream ims = assetManager.open(file);

            String re = convertStreamToString(ims);
            ims.close();
            return re;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
