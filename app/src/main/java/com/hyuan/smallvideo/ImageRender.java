package com.hyuan.smallvideo;

import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.util.Log;
import com.hyuan.smallvideo.filter.AffectFilter;
import com.hyuan.smallvideo.utils.NativeTool;
import com.hyuan.smallvideo.utils.Rotation;
import com.hyuan.smallvideo.utils.TextureRotationUtil;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.LinkedList;
import java.util.Queue;

public class ImageRender implements GLSurfaceView.Renderer {
    private static final String TAG = "ImageRender";
    //屏幕矩形
    public static final float CUBE[] = {
            -1.0f, -1.0f,
            1.0f, -1.0f,
            -1.0f, 1.0f,
            1.0f, 1.0f,
    };

    private int glTextureId = -1;
    private FloatBuffer glCubeBuffer;
    private FloatBuffer glTextureBuffer;
    private IntBuffer imageBuff;
    private Queue<Runnable> runOnDraw;
    private AffectFilter filter;

    private int outputWidth;
    private int outputHeight;
    private int imageWidth;
    private int imageHeight;
    private Rotation rotation;
    private boolean flipHorizontal;
    private boolean flipVertical;

    public ImageRender(final AffectFilter filter){
        this.filter = filter;
        runOnDraw = new LinkedList<>();

        glCubeBuffer = ByteBuffer.allocateDirect(CUBE.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        glCubeBuffer.put(CUBE).position(0);

        glTextureBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_NO_ROTATION.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();

        setRotation(Rotation.NORMAL, false, true);
    }

    public void setRotationCamera(final Rotation rotation, final boolean flipHorizontal,
                                  final boolean flipVertical) {
        setRotation(rotation, flipVertical, flipHorizontal);
    }

    public void setRotation(final Rotation rotation) {
        this.rotation = rotation;
        adjustImageScaling();
    }

    public void setRotation(final Rotation rotation,
                            final boolean flipHorizontal, final boolean flipVertical) {
        this.flipHorizontal = flipHorizontal;
        this.flipVertical = flipVertical;
        setRotation(rotation);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES30.glClearColor(0, 0, 0, 1);
        GLES30.glDisable(GLES30.GL_DEPTH_TEST);
        filter.ifNeedInit();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        outputHeight = height;
        outputWidth = width;
        GLES30.glViewport(0, 0, width, height);
        GLES30.glUseProgram(filter.getProgram());
        filter.onOutputSizeChanged(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);
        runAll(runOnDraw);
        filter.onDraw(glTextureId, glCubeBuffer, glTextureBuffer);
    }

    //主要是进行纹理加载
    //把相机传送过来的图片绑定到纹理上，以备特效过滤器进行渲染
    public void updatePreviewFrame(final byte[] data, final int width, final int height) {
        if (imageBuff == null){
            imageBuff = IntBuffer.allocate(width * height);
        }
        //必须处于队列第一步，保证纹理已经预先加载好，否则渲染找不到纹理
        if (runOnDraw.isEmpty()) {
            runOnDraw(new Runnable() {
                @Override
                public void run() {
                    NativeTool.YUVtoRBGA(data, width, height, imageBuff.array());
                    if (glTextureId == -1) {
                        int[] textureIds = new int[1];
                        GLES30.glGenTextures(1, textureIds, 0);
                        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureIds[0]);
                        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST);
                        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
                        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_REPEAT);
                        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_REPEAT);
                        GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA, width, height, 0, GLES30.GL_RGBA,
                                GLES30.GL_UNSIGNED_BYTE, imageBuff);
                        glTextureId = textureIds[0];
                    } else {
                        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, glTextureId);
                        GLES30.glTexSubImage2D(GLES30.GL_TEXTURE_2D, 0, 0, 0, width, height, GLES30.GL_RGBA,
                                GLES30.GL_UNSIGNED_BYTE, imageBuff);
                    }

                    if (imageWidth != width) {
                        imageWidth = width;
                        imageHeight = height;
                        adjustImageScaling();
                    }
                }
            });
        }
    }

    public void setFilter(final AffectFilter filter) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                final AffectFilter oldFilter = ImageRender.this.filter;
                ImageRender.this.filter = filter;
                if (oldFilter != null) {
                    oldFilter.destroy();
                }
                ImageRender.this.filter.ifNeedInit();
                GLES30.glUseProgram(ImageRender.this.filter.getProgram());
                ImageRender.this.filter.onOutputSizeChanged(outputWidth, outputHeight);
            }
        });
    }

    private void runOnDraw(final Runnable runnable) {
        synchronized (runOnDraw) {
            runOnDraw.add(runnable);
        }
    }

    private void runAll(Queue<Runnable> queue) {
        synchronized (queue) {
            while (!queue.isEmpty()) {
                queue.poll().run();
            }
        }
    }

    private void adjustImageScaling() {
        float outputWidth = this.outputWidth;
        float outputHeight = this.outputHeight;
        if (rotation == Rotation.ROTATION_270 || rotation == Rotation.ROTATION_90) {
            outputWidth = this.outputHeight;
            outputHeight = this.outputWidth;
        }

        float ratio1 = outputWidth / imageWidth;
        float ratio2 = outputHeight / imageHeight;
        float ratioMax = Math.max(ratio1, ratio2);
        int imageWidthNew = Math.round(imageWidth * ratioMax);
        int imageHeightNew = Math.round(imageHeight * ratioMax);

        float ratioWidth = imageWidthNew / outputWidth;
        float ratioHeight = imageHeightNew / outputHeight;

        float[] cube = CUBE;
        float[] textureCords = TextureRotationUtil.getRotation(rotation, flipHorizontal, flipVertical);
        float distHorizontal = (1 - 1 / ratioWidth) / 2;
        float distVertical = (1 - 1 / ratioHeight) / 2;
        textureCords = new float[]{
                addDistance(textureCords[0], distHorizontal), addDistance(textureCords[1], distVertical),
                addDistance(textureCords[2], distHorizontal), addDistance(textureCords[3], distVertical),
                addDistance(textureCords[4], distHorizontal), addDistance(textureCords[5], distVertical),
                addDistance(textureCords[6], distHorizontal), addDistance(textureCords[7], distVertical),
        };

        glCubeBuffer.clear();
        glCubeBuffer.put(cube).position(0);
        glTextureBuffer.clear();
        glTextureBuffer.put(textureCords).position(0);
    }

    private float addDistance(float coordinate, float distance) {
        return coordinate == 0.0f ? distance : 1 - distance;
    }
}
