package com.hyuan.smallvideo.filter;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES30;
import android.opengl.GLUtils;

import java.io.IOException;

public class BeautyFilter extends AffectFilter {

    private int glHaaCoef;
    private int glHmixCoef;
    private int glHiternum;
    private int glWidth;
    private int glHeight;

    private float aaCoef;
    private float mixCoef;
    private int iternum;

    private int width;
    private int height;

    public BeautyFilter(Resources res){
        super(res,"beauty.vert","beauty.frag");
    }

    @Override
    public void onInit() {
        super.onInit();
        int promgramId = super.getProgram();
        glHaaCoef=GLES30.glGetUniformLocation(promgramId,"aaCoef");
        glHmixCoef=GLES30.glGetUniformLocation(promgramId,"mixCoef");
        glHiternum=GLES30.glGetUniformLocation(promgramId,"iternum");
        glWidth=GLES30.glGetUniformLocation(promgramId,"mWidth");
        glHeight=GLES30.glGetUniformLocation(promgramId,"mHeight");
        setLevel(5);
    }

    public void setLevel(int level) {
        switch (level){
            case 1:
                a(1,0.19f,0.54f);
                break;
            case 2:
                a(2,0.29f,0.54f);
                break;
            case 3:
                a(3,0.17f,0.39f);
                break;
            case 4:
                a(3,0.25f,0.54f);
                break;
            case 5:
                a(4,0.13f,0.54f);
                break;
            case 6:
                a(4,0.19f,0.69f);
                break;
            default:
                a(0,0f,0f);
                break;
        }
    }

    private void a(int a,float b,float c){
        this.iternum=a;
        this.aaCoef=b;
        this.mixCoef=c;
    }

    @Override
    public void onOutputSizeChanged(int width, int height) {
        super.onOutputSizeChanged(width, height);
        this.width = width;
        this.height = height;
    }

    @Override
    protected void onDrawArraysPre() {
        super.onDrawArraysPre();
        GLES30.glUniform1i(glWidth,width);
        GLES30.glUniform1i(glHeight,height);
        GLES30.glUniform1f(glHaaCoef,aaCoef);
        GLES30.glUniform1f(glHmixCoef,mixCoef);
        GLES30.glUniform1i(glHiternum,iternum);
    }
}
