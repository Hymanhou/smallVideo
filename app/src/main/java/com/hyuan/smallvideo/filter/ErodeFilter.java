package com.hyuan.smallvideo.filter;

import android.content.res.Resources;

public class ErodeFilter extends AffectFilter {
    public ErodeFilter(Resources resources) {
        super(resources, "fast_blur_vertex.glsl", "erode_fragment.glsl");
    }
}
