package com.hyuan.smallvideo;

import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import com.hyuan.smallvideo.filter.AffectFilter;
import com.hyuan.smallvideo.utils.NativeTool;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.LinkedList;
import java.util.Queue;

public class ImageRender implements GLSurfaceView.Renderer {
    //屏幕矩形
    private static final float SQUARE[] = {
            -1.0f, -1.0f,
            1.0f, -1.0f,
            -1.0f, 1.0f,
            1.0f, 1.0f
    };
    //纹理坐标需要延X轴和Y轴进行翻转
    //延X轴翻转是因为纹理坐标原点在左下角，而屏幕坐标在右上角
    //延Y轴进行翻转是因为镜头捕获到的左右方向和实际的是相反的
    private float[] TEXTURE = {
            1.0f, 1.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f
    };
    private int glTextureId = -1;
    private FloatBuffer glSquareBuffer;
    private FloatBuffer glTextureBuffer;
    private IntBuffer imageBuff;
    private Queue<Runnable> runOnDraw;
    private AffectFilter filter;

    public ImageRender(final AffectFilter filter){
        this.filter = filter;
        runOnDraw = new LinkedList<>();

        glSquareBuffer = ByteBuffer.allocateDirect(SQUARE.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        glSquareBuffer.put(SQUARE).position(0);

        glTextureBuffer = ByteBuffer.allocateDirect(TEXTURE.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        glTextureBuffer.put(TEXTURE).position(0);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0, 0, 0, 1);
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        filter.ifNeedInit();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        synchronized (runOnDraw) {
            while (!runOnDraw.isEmpty()){
                runOnDraw.poll().run();
            }
            filter.onDraw(glTextureId, glSquareBuffer, glSquareBuffer);
        }
    }

    //主要是进行纹理加载
    //把相机传送过来的图片绑定到纹理上，以备特效过滤器进行渲染
    public void updatePreviewFrame(final byte[] data, final int width, final int height) {
        if (imageBuff == null){
            imageBuff = IntBuffer.allocate(width * height);
        }
        //必须处于队列第一步，保证纹理已经预先加载好，否则渲染找不到纹理
        if (runOnDraw.isEmpty()) {
            synchronized (runOnDraw) {
                runOnDraw.add(new Runnable() {
                    @Override
                    public void run() {
                        NativeTool.YUVtoRBGA(data, width, height, imageBuff.array());
                        if (glTextureId == -1) {
                            int[] textureIds = new int[1];
                            GLES20.glGenTextures(1, textureIds, 0);
                            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIds[0]);
                            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
                            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
                            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
                            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
                            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0, GLES20.GL_RGBA,
                                    GLES20.GL_UNSIGNED_BYTE, imageBuff);
                            glTextureId = textureIds[0];
                        } else {
                            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, glTextureId);
                            GLES20.glTexSubImage2D(GLES20.GL_TEXTURE_2D, 0, 0, 0, width, height, GLES20.GL_RGBA,
                                    GLES20.GL_UNSIGNED_BYTE, imageBuff);
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

    public void setFilter(final AffectFilter filter) {
        synchronized (runOnDraw) {
            runOnDraw.add(new Runnable() {
                @Override
                public void run() {
                    final AffectFilter oldFilter = ImageRender.this.filter;
                    ImageRender.this.filter = filter;
                    if (oldFilter != null) {
                        oldFilter.destroy();
                    }
                    ImageRender.this.filter.ifNeedInit();
                    GLES20.glUseProgram(ImageRender.this.filter.getProgram());
                }
            });
        }
    }
}
