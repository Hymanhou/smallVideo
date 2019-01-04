package com.hyuan.smallvideo.utils;

import android.content.res.Resources;
import android.opengl.GLES30;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

public class ShaderUtil {
    public static String loadShaderSource(String fileName, Resources resources){
        StringBuilder stringBuilder = new StringBuilder();
        try {
            InputStream inputStream = resources.getAssets().open(fileName);
            byte[] data = new byte[1024];
            int len = 0;
            while ((len = inputStream.read(data, 0, 1024)) > 0) {
                stringBuilder.append(new String(data, 0, len));
            }
            return stringBuilder.toString();
        } catch (IOException e) {
            Log.e("hyuan", e.getMessage());
            e.printStackTrace();
        }
        return stringBuilder.toString().replace("\r\n", "\n");
    }

    public static int compileSrc(int shaderType, String src) {
        int shader = GLES30.glCreateShader(shaderType);
        if (shader == 0) {
            Log.e("hyuan", "Shader calls should be within a GL " +
                    "thread that is onSurfaceChanged(), onSurfaceCreated() or onDrawFrame()");
            return 0;
        }
        GLES30.glShaderSource(shader, src);
        GLES30.glCompileShader(shader);

        int[] compile = new int[1];
        GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compile, 0);
        if (compile[0] == 0){
            Log.e("hyuan", "compile shader failed.");
            String msg = GLES30.glGetShaderInfoLog(shader);
            Log.e("hyuan", "message:" + GLES30.glGetShaderInfoLog(shader));
            return 0;
        }
        return shader;
    }

    public static int createShaderProgram(String vertexFile, String fragmentFile, Resources resources) {
        String vertexSrc = loadShaderSource(vertexFile, resources);
        String fragmentSrc = loadShaderSource(fragmentFile, resources);
        int vertexShader = compileSrc(GLES30.GL_VERTEX_SHADER, vertexSrc);
        int fragmentShader = compileSrc(GLES30.GL_FRAGMENT_SHADER, fragmentSrc);

        int program = GLES30.glCreateProgram();
        if (program == 0) {
            Log.e("hyuan", "Shader calls should be within a GL " +
                    "thread that is onSurfaceChanged(), onSurfaceCreated() or onDrawFrame()");
            return 0;
        }

        GLES30.glAttachShader(program, vertexShader);
        GLES30.glAttachShader(program, fragmentShader);
        GLES30.glLinkProgram(program);

        int[] link = new int[1];
        GLES30.glGetProgramiv(program, GLES30.GL_LINK_STATUS, link, 0);
        if (link[0] == 0) {
            Log.e("hyuan", "link shader failed.");
            String msg = GLES30.glGetProgramInfoLog(program);
            Log.e("hyuan", "message:" + GLES30.glGetProgramInfoLog(program));
            return program;
        }
        return program;
    }

    public static int createShaderProgram(String vertexSrc, String fragmentSrc) {
        int vertexShader = compileSrc(GLES30.GL_VERTEX_SHADER, vertexSrc);
        int fragmentShader = compileSrc(GLES30.GL_FRAGMENT_SHADER, fragmentSrc);

        int program = GLES30.glCreateProgram();
        if (program == 0) {
            Log.e("hyuan", "Shader calls should be within a GL " +
                    "thread that is onSurfaceChanged(), onSurfaceCreated() or onDrawFrame()");
            return 0;
        }

        GLES30.glAttachShader(program, vertexShader);
        GLES30.glAttachShader(program, fragmentShader);
        GLES30.glLinkProgram(program);

        int[] link = new int[1];
        GLES30.glGetProgramiv(program, GLES30.GL_LINK_STATUS, link, 0);
        if (link[0] == 0) {
            Log.e("hyuan", "link shader failed.");
            Log.e("hyuan", "message:" + GLES30.glGetProgramInfoLog(program));
            return 0;
        }
        return program;
    }
}
