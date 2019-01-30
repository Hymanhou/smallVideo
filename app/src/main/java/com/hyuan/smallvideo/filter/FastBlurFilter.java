/**
 * @author wysaid
 * @mail admin@wysaid.org
 */

package com.hyuan.smallvideo.filter;

import android.content.res.Resources;
import android.opengl.GLES30;


public class FastBlurFilter extends AffectFilter {
    
    private int singleStepOffsetLocation;
    
    public FastBlurFilter(Resources resources) {
        super(resources, "fast_blur_vertex.glsl", "fast_blur_fragment.glsl");
    }
    
    @Override
    public void onInit() {
        super.onInit();
        singleStepOffsetLocation = GLES30.glGetUniformLocation(getProgram(), "singleStepOffset");
    }

    @Override
    public void onInitialized() {
        super.onInitialized();
    }

    private void setTexelSize(final float w, final float h) {
        setFloatVec2(singleStepOffsetLocation, new float[]{1.0f / w, 1.0f / h});
    }

    @Override
    public void onOutputSizeChanged(final int width, final int height) {
        super.onOutputSizeChanged(width, height);
        setTexelSize(width, height);
    }
}
