package com.hyuan.smallvideo;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import com.hyuan.smallvideo.filter.AffectFilter;
import com.hyuan.smallvideo.filter.AffectFilterGroup;
import com.hyuan.smallvideo.utils.Rotation;

//为相机提供预览的view
//CameraPreviewView只是一个容器布局，
// 内部是通过surfaceview来实现，见init函数，surfaceView被添加到groupView中
public class CameraPreviewView extends FrameLayout{

    private Context context;
    private ImageRender imageRender;
    private GLSurfaceView surfaceView;
    private AffectFilter filter;
    private float ratio;

    public CameraPreviewView(Context context) {
        super(context);
    }

    public CameraPreviewView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        if (configurationInfo.reqGlEsVersion >= 0x30000){
            filter = new AffectFilter();
            imageRender = new ImageRender(filter);
            surfaceView = new ImageGLSurfaceView(context, attrs);
            surfaceView.setEGLContextClientVersion(3);
            surfaceView.setRenderer(imageRender);
            surfaceView.getHolder().setFormat(PixelFormat.RGBA_8888);
            surfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
            addView(surfaceView);
        } else {
            throw new IllegalStateException("OpenGL ES3.0 is not supported!");
        }
    }

    public void setRatio(float ratio) {
        this.ratio = ratio;
        surfaceView.requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //根据缩放重新测量预览窗口大小
        //缩放后画面不能超过屏幕的宽和高
        if (ratio != 0.0f) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = MeasureSpec.getSize(heightMeasureSpec);

            int newHeight;
            int newWidth;
            //ratio指的是宽高比，widht/height = ratio
            //当前的宽高比存ratio_now在两种情况，
            //1：ratio_now > ratio,说明宽度太宽，那么需要根据高度和ratio来重新计算宽度，这时候宽度必然不会超过屏幕宽度
            //2：ratio_now < ratio,说明高度太高，需要根据宽度和ratio重新计算高度，这样高度也不会超过屏幕高度
            if (width / ratio < height) {
                newWidth = width;
                newHeight = Math.round(width / ratio);
            } else {
                newHeight = height;
                newWidth = Math.round(height * ratio);
            }

            int newWidhtSpec = MeasureSpec.makeMeasureSpec(newWidth, MeasureSpec.EXACTLY);
            int newHeightSpec = MeasureSpec.makeMeasureSpec(newHeight, MeasureSpec.EXACTLY);
            super.onMeasure(newWidhtSpec, newHeightSpec);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }


    public void setFilter(AffectFilter filter) {
        this.filter = filter;
        imageRender.setFilter(filter);
        //设置特效过滤器后马上刷新画面
        surfaceView.requestRender();
    }

    public void updatePreviewFrame(byte[] data, int width, int height) {
        imageRender.updatePreviewFrame(data, width, height);
        surfaceView.requestRender();
    }

    public void setRotation(Rotation rotation) {
        imageRender.setRotation(rotation);
        surfaceView.requestRender();
    }

    private class ImageGLSurfaceView extends GLSurfaceView {
        public ImageGLSurfaceView(Context context) {
            super(context);
        }

        public ImageGLSurfaceView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }
    }

}
