
package com.hyuan.smallvideo.filter;

import android.annotation.SuppressLint;
import android.opengl.GLES30;
import android.util.Log;
import com.hyuan.smallvideo.utils.Rotation;
import com.hyuan.smallvideo.utils.TextureRotationUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import static com.hyuan.smallvideo.ImageRender.CUBE;
import static com.hyuan.smallvideo.utils.TextureRotationUtil.TEXTURE_NO_ROTATION;

/**
 * Resembles a filter that consists of multiple filters applied after each
 * other.
 */
public class AffectFilterGroup extends AffectFilter {
    private final static String TAG = "AffectFilterGroup";

    private List<AffectFilter> filters;
    private List<AffectFilter> mergedFilters;
    private int[] frameBuffers;
    private int[] frameBufferTextures;

    private final FloatBuffer glCubeBuffer;
    private final FloatBuffer glTextureBuffer;
    private final FloatBuffer glTextureFlipBuffer;

    /**
     * Instantiates a new AffectFilterGroup with no filters.
     */
    public AffectFilterGroup() {
        this(null);
    }

    /**
     * Instantiates a new AffectFilterGroup with the given filters.
     *
     * @param filters the filters which represent this filter
     */
    public AffectFilterGroup(List<AffectFilter> filters) {
        this.filters = filters;
        if (this.filters == null) {
            this.filters = new ArrayList<>();
        } else {
            updateMergedFilters();
        }

        glCubeBuffer = ByteBuffer.allocateDirect(CUBE.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        glCubeBuffer.put(CUBE).position(0);

        glTextureBuffer = ByteBuffer.allocateDirect(TEXTURE_NO_ROTATION.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        glTextureBuffer.put(TEXTURE_NO_ROTATION).position(0);

        float[] flipTexture = TextureRotationUtil.getRotation(Rotation.NORMAL, false, true);
        glTextureFlipBuffer = ByteBuffer.allocateDirect(flipTexture.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        glTextureFlipBuffer.put(flipTexture).position(0);
    }

    public void addFilter(AffectFilter aFilter) {
        if (aFilter == null) {
            return;
        }
        filters.add(aFilter);
        updateMergedFilters();
    }

    /*
     * (non-Javadoc)
     * @see jp.co.cyberagent.android.gpuimage.filter.AffectFilter#onInit()
     */
    @Override
    public void onInit() {
        super.onInit();
        for (AffectFilter filter : filters) {
            filter.ifNeedInit();
        }
    }

    /*
     * (non-Javadoc)
     * @see jp.co.cyberagent.android.gpuimage.filter.AffectFilter#onDestroy()
     */
    @Override
    public void onDestroy() {
        destroyFramebuffers();
        for (AffectFilter filter : filters) {
            filter.destroy();
        }
        super.onDestroy();
    }

    private void destroyFramebuffers() {
        if (frameBufferTextures != null) {
            GLES30.glDeleteTextures(frameBufferTextures.length, frameBufferTextures, 0);
            frameBufferTextures = null;
        }
        if (frameBuffers != null) {
            GLES30.glDeleteFramebuffers(frameBuffers.length, frameBuffers, 0);
            frameBuffers = null;
        }
    }

    @Override
    public void onOutputSizeChanged(final int width, final int height) {
        Log.e(TAG, "output size changed");
        if (frameBuffers != null) {
            destroyFramebuffers();
        }

        int size = filters.size();
        for (int i = 0; i < size; i++) {
            filters.get(i).onOutputSizeChanged(width, height);
        }

        if (mergedFilters != null && mergedFilters.size() > 0) {
            size = mergedFilters.size();
            frameBuffers = new int[size - 1];
            frameBufferTextures = new int[size - 1];

            for (int i = 0; i < size - 1; i++) {
                GLES30.glGenFramebuffers(1, frameBuffers, i);
                GLES30.glGenTextures(1, frameBufferTextures, i);
                GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, frameBufferTextures[i]);
                GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA, width, height, 0,
                        GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, null);
                GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
                        GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
                GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
                        GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
                GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
                        GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
                GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
                        GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);

                GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, frameBuffers[i]);
                GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0,
                        GLES30.GL_TEXTURE_2D, frameBufferTextures[i], 0);

                GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);
                GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see jp.co.cyberagent.android.gpuimage.filter.AffectFilter#onDraw(int,
     * java.nio.FloatBuffer, java.nio.FloatBuffer)
     */
    @SuppressLint("WrongCall")
    @Override
    public void onDraw(final int textureId, final FloatBuffer cubeBuffer,
                       final FloatBuffer textureBuffer) {
        runPendingOnDrawTasks();
        if (!isInitialized() || frameBuffers == null || frameBufferTextures == null) {
            return;
        }
        if (mergedFilters != null) {
            int size = mergedFilters.size();
            int previousTexture = textureId;
            for (int i = 0; i < size; i++) {
                AffectFilter filter = mergedFilters.get(i);
                boolean isNotLast = i < size - 1;
                if (isNotLast) {
                    GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, frameBuffers[i]);
                    GLES30.glClearColor(0, 0, 0, 0);
                }

                if (i == 0) {
                    filter.onDraw(previousTexture, cubeBuffer, textureBuffer);
                } else if (i == size - 1) {
                    filter.onDraw(previousTexture, glCubeBuffer, (size % 2 == 0) ? glTextureFlipBuffer : glTextureBuffer);
                } else {
                    filter.onDraw(previousTexture, glCubeBuffer, glTextureBuffer);
                }

                if (isNotLast) {
                    GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);
                    previousTexture = frameBufferTextures[i];
                }
            }
        }
    }

    /**
     * Gets the filters.
     *
     * @return the filters
     */
    public List<AffectFilter> getFilters() {
        return filters;
    }

    public List<AffectFilter> getMergedFilters() {
        return mergedFilters;
    }

    public void updateMergedFilters() {
        if (filters == null) {
            return;
        }

        if (mergedFilters == null) {
            mergedFilters = new ArrayList<>();
        } else {
            mergedFilters.clear();
        }

        List<AffectFilter> filters;
        for (AffectFilter filter : this.filters) {
            if (filter instanceof AffectFilterGroup) {
                ((AffectFilterGroup) filter).updateMergedFilters();
                filters = ((AffectFilterGroup) filter).getMergedFilters();
                if (filters == null || filters.isEmpty())
                    continue;
                mergedFilters.addAll(filters);
                continue;
            }
            mergedFilters.add(filter);
        }
    }

    public void clearFilters(){
        for (AffectFilter filter : filters) {
            filter.destroy();
        }
        filters.clear();
        destroyFramebuffers();
    }
}
