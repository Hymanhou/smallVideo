package com.hyuan.smallvideo;

import android.content.Context;
import android.hardware.Camera;
import android.opengl.*;
import android.util.AttributeSet;
import com.hyuan.smallvideo.utils.NativeTool;
import com.hyuan.smallvideo.utils.ShaderUtil;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.nio.*;
import java.util.LinkedList;
import java.util.Queue;

public class WxCameraView extends GLSurfaceView implements GLSurfaceView.Renderer {
    private WxCamera wxCamera;
    private int textureId = -1;

    private int program_basic;
    private int samplerLoc;
    private int distanceFactor;

    private int program_beauty;
//    private float[] vertexesData = {
//            -1.0f, 1.0f, 0.0f,      //left top
//            0.0f, 1.0f,
//            -1.0f, -1.0f, 0.0f,     //left bottom
//            1.0f, 1.0f,
//            1.0f, -1.0f, 0.0f,      //right bottom
//            1.0f, 0.0f,
//            1.0f, 1.0f, 0.0f,       //right top
//            0.0f, 0.0f
//    };
    //flip
    private float[] vertexesData = {
            -1.0f, 1.0f, 0.0f,      //left top
            1.0f, 0.0f,
            -1.0f, -1.0f, 0.0f,     //left bottom
            0.0f, 0.0f,
            1.0f, -1.0f, 0.0f,      //right bottom
            0.0f, 1.0f,
            1.0f, 1.0f, 0.0f,       //right top
            1.0f, 1.0f
    };
    private short[] indicesData = {
            0, 1, 2,
            0, 2, 3
    };
    private FloatBuffer vertexBuff;
    private ShortBuffer indicesBuff;
    private IntBuffer imageBuff;
    private int vertexCoordLoc = 0;
    private int texCoordLoc = 1;
    private Queue<Runnable> runOnDraw;

    private int outputWidth;
    private int outputHeight;

    public WxCameraView(Context context){
        this(context, null);
    }

    public WxCameraView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        runOnDraw = new LinkedList<>();
        setEGLContextClientVersion(3);
        setRenderer(this);
        //setRenderMode(RENDERMODE_CONTINUOUSLY);
        //setRenderMode(RENDERMODE_WHEN_DIRTY);

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

        program_basic = ShaderUtil.createShaderProgram("camera_vertex.glsl", "camera_fragment.glsl",
                getContext().getResources());
        samplerLoc = GLES30.glGetUniformLocation(program_basic, "s_texture");
        wxCamera.openCamera(720);
        wxCamera.setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                int width = camera.getParameters().getPreviewSize().width;
                int height = camera.getParameters().getPreviewSize().height;
                if (outputHeight == 0 || outputWidth == 0) {
                    outputWidth = width;
                    outputHeight = height;
                }
                onPreviewFrameAvailable(data, width, height);
            }
        });
        wxCamera.startPreview();
    }

    private void onPreviewFrameAvailable(final byte[] data, final int width, final int height) {
        if (imageBuff == null){
            imageBuff = IntBuffer.allocate(width * height);
        }
        if (runOnDraw.isEmpty()) {
            synchronized (runOnDraw) {
                runOnDraw.add(new Runnable() {
                    @Override
                    public void run() {
                        NativeTool.YUVtoRBGA(data, width, height, imageBuff.array());
                        if (textureId == -1) {
                            int[] textureIds = new int[1];
                            GLES30.glGenTextures(1, textureIds, 0);
                            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureIds[0]);
                            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST);
                            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
                            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
                            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);
                            GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA, width, height, 0, GLES30.GL_RGBA,
                                    GLES30.GL_UNSIGNED_BYTE, imageBuff);
                            textureId = textureIds[0];
                        } else {
                            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId);
                            GLES30.glTexSubImage2D(GLES30.GL_TEXTURE_2D, 0, 0, 0, width, height, GLES30.GL_RGBA,
                                    GLES30.GL_UNSIGNED_BYTE, imageBuff);
                        }
                        try {
                            NativeTool.YUVtoRBGA(data, width, height, imageBuff.array());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES30.glViewport(0, 0, width, height);
    }

    private void drawBeauty() {
        if (program_beauty == 0) {
            program_beauty = ShaderUtil.createShaderProgram("beauty_vertex.glsl","beauty_fragment.glsl",
                    getResources());
            samplerLoc = GLES30.glGetUniformLocation(program_beauty, "inputImageTexture");
            distanceFactor = GLES30.glGetUniformLocation(program_beauty, "distanceNormaliztionFactor");
            int stepOffsetLoc = GLES30.glGetUniformLocation(program_beauty, "stepOffset");
            GLES30.glUniform2fv(stepOffsetLoc, 1, FloatBuffer.wrap(new float[]{1.0f/outputWidth, 1.0f/outputHeight}));
            GLES30.glUniform1f(distanceFactor, 0.001f);
        }
        GLES30.glUseProgram(program_beauty);
    }

    private void drawBasic() {
        GLES30.glUseProgram(program_basic);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);
        drawBeauty();
        int error = 0;
        synchronized (runOnDraw) {
            while (!runOnDraw.isEmpty()) {
                runOnDraw.poll().run();
            }
        }
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId);
        GLES30.glUniform1i(samplerLoc, 0);

        vertexBuff.position(0);
        GLES30.glEnableVertexAttribArray(vertexCoordLoc);
        GLES30.glVertexAttribPointer(vertexCoordLoc, 3, GLES30.GL_FLOAT, false, 5 * 4, vertexBuff);

        vertexBuff.position(3);
        GLES30.glEnableVertexAttribArray(texCoordLoc);
        GLES30.glVertexAttribPointer(texCoordLoc, 2, GLES30.GL_FLOAT, false, 5 * 4, vertexBuff);

        GLES30.glDrawElements(GLES30.GL_TRIANGLES, indicesData.length, GLES30.GL_UNSIGNED_SHORT, indicesBuff);

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
        wxCamera.startPreview();
    }
}
