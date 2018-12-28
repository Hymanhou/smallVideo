package com.hyuan.smallvideo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.media.Image;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.util.AttributeSet;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.io.IOException;

public class WxCameraView extends GLSurfaceView implements GLSurfaceView.Renderer {
    private WxCamera wxCamera;
    private SurfaceTexture surfaceTexture;

    private int program;
    private int[] textureIds = new int[1];
    private float[] imageTexMatrix = new float[16];
    private int imageMatrixLoc;

    public WxCameraView(Context context){
        this(context, null);
    }

    public WxCameraView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setEGLContextClientVersion(3);
        setRenderer(this);
        //setRenderMode(RENDERMODE_CONTINUOUSLY);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
        wxCamera = new WxCamera();
        wxCamera.openCamera(720);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES30.glClearColor(1, 1, 1, 0.5f);
        GLES30.glGenTextures(1, textureIds, 0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureIds[0]);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);

        program = ShaderUtil.createShaderProgram("texture2d_vertex.glsl", "texture2d_fragment.glsl",
                getContext().getResources());
        imageMatrixLoc = GLES30.glGetUniformLocation(program, "a_texMatrix");

        surfaceTexture = new SurfaceTexture(textureIds[0]);
        surfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
            @Override
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                requestRender();
            }
        });
        wxCamera.setSurfaceTexture(surfaceTexture);
        wxCamera.startPreview();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES30.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);
        if (surfaceTexture != null){
            surfaceTexture.updateTexImage();
            surfaceTexture.getTransformMatrix(imageTexMatrix);
            GLES30.glUniformMatrix4fv(imageMatrixLoc,1, false, imageTexMatrix, 0);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
