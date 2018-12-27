package com.hyuan.smallvideo;

import android.content.Context;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class WxCameraView extends GLSurfaceView implements GLSurfaceView.Renderer {

    private Square square;
    private VAORenderer vaoRenderer;

    public WxCameraView(Context context){
        this(context, null);
    }

    public WxCameraView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setEGLContextClientVersion(3);
        setRenderer(this);
        setRenderMode(RENDERMODE_CONTINUOUSLY);
//        setRenderMode(RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //square = new Square(getContext());
        vaoRenderer = new VAORenderer(getContext());
        GLES30.glClearColor(1, 1, 1, 0.5f);
        GLES30.glDisable(GLES30.GL_CULL_FACE);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES30.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);
        //square.draw();
        vaoRenderer.onDrawFrame(gl);
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
