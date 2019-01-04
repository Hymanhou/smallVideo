package com.hyuan.smallvideo;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.*;
import android.util.AttributeSet;
import android.util.Log;
import com.hyuan.smallvideo.utils.ShaderUtil;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class WxCameraView extends GLSurfaceView implements GLSurfaceView.Renderer {
    private WxCamera wxCamera;
    private SurfaceTexture surfaceTexture;

    private int program;
    private int[] textureIds = new int[1];
    private float[] transformMatrix = new float[16];
    private int transformMatrixLoc;
    private int samplerLoc;
    private float[] vertexesData = {
            -1.0f, 1.0f, 0.0f,
            0.0f, 1.0f,
            -1.0f, -1.0f, 0.0f,
            1.0f, 1.0f,
            1.0f, -1.0f, 0.0f,
            1.0f, 0.0f,
            1.0f, 1.0f, 0.0f,
            0.0f, 0.0f
    };
    private short[] indicesData = {
            0, 1, 2,
            0, 2, 3
    };
    private FloatBuffer vertexBuff;
    private ShortBuffer indicesBuff;
    private int vertexCoordLoc = 0;
    private int texCoordLoc = 1;

    public WxCameraView(Context context){
        this(context, null);
    }

    public WxCameraView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setEGLContextClientVersion(3);
        setRenderer(this);
        //setRenderMode(RENDERMODE_CONTINUOUSLY);
        setRenderMode(RENDERMODE_WHEN_DIRTY);

        ByteBuffer vb = ByteBuffer.allocateDirect(vertexesData.length * 4);
        vb.order(ByteOrder.nativeOrder());
        vertexBuff = vb.asFloatBuffer();
        vertexBuff.put(vertexesData);
        vertexBuff.position(0);

        ByteBuffer ib = ByteBuffer.allocateDirect(indicesData.length * 2);
        ib.order(ByteOrder.nativeOrder());
        indicesBuff = ib.asShortBuffer();
        indicesBuff.put(indicesData);
        indicesBuff.position(0);

        wxCamera = new WxCamera();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES30.glClearColor(1, 1, 1, 0.5f);
        GLES30.glGenTextures(1, textureIds, 0);
        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureIds[0]);
        GLES30.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST);
        GLES30.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);

        program = ShaderUtil.createShaderProgram("camera_vertex.glsl", "camera_fragment.glsl",
                getContext().getResources());
        transformMatrixLoc = GLES30.glGetUniformLocation(program, "u_texMatrix");
        samplerLoc = GLES30.glGetUniformLocation(program, "s_texture");

        surfaceTexture = new SurfaceTexture(textureIds[0]);
        surfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
            @Override
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                requestRender();
            }
        });


        wxCamera.openCamera(720);
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
        GLES30.glUseProgram(program);
        int error = 0;
        if (surfaceTexture != null){
            surfaceTexture.updateTexImage();
            surfaceTexture.getTransformMatrix(transformMatrix);
            GLES30.glUniformMatrix4fv(transformMatrixLoc,1, false, transformMatrix, 0);
            if ((error = GLES30.glGetError()) != GLES30.GL_NO_ERROR) {
                Log.e("hyuan","glUniformMatrix4fv error:" + error);
            }
        }
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureIds[0]);
        GLES30.glUniform1i(samplerLoc, 0);

        vertexBuff.position(0);
        GLES30.glEnableVertexAttribArray(vertexCoordLoc);
        if ((error = GLES30.glGetError()) != GLES30.GL_NO_ERROR) {
            Log.e("hyuan","glEnableVertexAttribArray error:" + error);
        }
        GLES30.glVertexAttribPointer(vertexCoordLoc, 3, GLES30.GL_FLOAT, false, 5 * 4, vertexBuff);
        if ((error = GLES30.glGetError()) != GLES30.GL_NO_ERROR) {
            Log.e("hyuan","glVertexAttribPointer error:" + error);
        }

        vertexBuff.position(3);
        GLES30.glEnableVertexAttribArray(texCoordLoc);
        GLES30.glVertexAttribPointer(texCoordLoc, 2, GLES30.GL_FLOAT, false, 5 * 4, vertexBuff);
        if (GLES30.glGetError() != GLES30.GL_NO_ERROR) {
            Log.e("hyuan","gl error:" + GLES30.glGetError());
        }

        //GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 3);
        GLES30.glDrawElements(GLES30.GL_TRIANGLES, indicesData.length, GLES30.GL_UNSIGNED_SHORT, indicesBuff);
        if (GLES30.glGetError() != GLES30.GL_NO_ERROR) {
            Log.e("hyuan","gl error:" + GLES30.glGetError());
        }

        GLES30.glDisableVertexAttribArray(vertexCoordLoc);
        GLES30.glDisableVertexAttribArray(texCoordLoc);
    }

    @Override
    public void onPause() {
        super.onPause();
        wxCamera.closeCamera();
    }

    @Override
    public void onResume() {
        super.onResume();
        wxCamera.openCamera(720);
        wxCamera.setSurfaceTexture(surfaceTexture);
        wxCamera.startPreview();
    }
}
