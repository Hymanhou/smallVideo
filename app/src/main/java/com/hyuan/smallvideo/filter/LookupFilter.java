package com.hyuan.smallvideo.filter;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES30;
import android.opengl.GLES30;
import android.opengl.GLUtils;

import java.io.IOException;

public class LookupFilter extends AffectFilter {
    private int maskImageLoc;
    private int intensityLoc;

    private int[] maskTexture = new int[1];
    private Bitmap bitmap;

    private float intensityF;

    public LookupFilter(Resources resources){
        super(resources, "lookup.vert", "lookup.frag");
    }

    @Override
    public void onInit() {
        super.onInit();
        int promgramId = super.getProgram();
        maskImageLoc = GLES30.glGetUniformLocation(promgramId,"maskTexture");
        intensityLoc = GLES30.glGetUniformLocation(promgramId,"intensity");

        GLES30.glGenTextures(1, maskTexture, 0);
        for (int i = 0; i < 1; i++) {
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, maskTexture[i]);
            GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA, 512, 512,
                    0, GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, null);
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER,GLES30.GL_NEAREST);
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER,GLES30.GL_LINEAR);
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S,GLES30.GL_CLAMP_TO_EDGE);
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T,GLES30.GL_CLAMP_TO_EDGE);
        }
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,0);
    }

    public void setIntensity(float value){
        this.intensityF = value;
    }

    public void setMaskImage(String mask, Resources resources){
        try {
            bitmap=BitmapFactory.decodeStream(resources.getAssets().open(mask));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDrawArraysPre() {
        super.onDrawArraysPre();
        GLES30.glUniform1f(intensityLoc, intensityF);
        if(maskTexture[0]!=0){
            GLES30.glActiveTexture(GLES30.GL_TEXTURE0 + getUniformTexture() + 1);
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,maskTexture[0]);
            if(bitmap != null && !bitmap.isRecycled()){
                GLUtils.texImage2D(GLES30.GL_TEXTURE_2D,0, bitmap,0);
                bitmap.recycle();
            }
            GLES30.glUniform1i(maskImageLoc,getUniformTexture() + 1);
        }
    }
}
