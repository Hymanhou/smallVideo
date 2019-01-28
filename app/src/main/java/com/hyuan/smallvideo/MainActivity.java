package com.hyuan.smallvideo;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;
import com.hyuan.smallvideo.filter.AffectFilterGroup;
import com.hyuan.smallvideo.filter.BeautyFilter;
import com.hyuan.smallvideo.filter.GrayFilter;
import com.hyuan.smallvideo.filter.LookupFilter;
import com.hyuan.smallvideo.utils.PermissionUtils;
import com.hyuan.smallvideo.utils.Rotation;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainActivity extends AppCompatActivity {

    private MyCamera myCamera;
    private CameraPreviewView cameraPreviewView;

    private MyGLSurfaceView glSurfaceView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PermissionUtils.askPermission(this, new String[]{Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE}, 10);
        setContentView(R.layout.activity_main);

        AffectFilterGroup filterGroup = new AffectFilterGroup();

        LookupFilter lookupFilter = new LookupFilter(getResources());
        lookupFilter.setMaskImage("purity.png", getResources());
        lookupFilter.setIntensity(0.2f);
        BeautyFilter beautyFilter = new BeautyFilter(this.getResources());

        filterGroup.addFilter(lookupFilter);
        filterGroup.addFilter(beautyFilter);

        cameraPreviewView = findViewById(R.id.camera_preview);
        myCamera = new MyCamera(this);
        myCamera.setOnPreviewListener(new OnPreviewListener() {
            @Override
            public void onPreviewFrame(byte[] data, int width, int height) {
                cameraPreviewView.updatePreviewFrame(data, width, height);
            }
        });
        cameraPreviewView.setRotation(getRotaion(myCamera.getCameraOrientation()));
        cameraPreviewView.setFilter(filterGroup);
    }

    private Rotation getRotaion(int orientation) {
        switch (orientation) {
            case 90:{
                return Rotation.ROTATION_90;
            }
            case 180:{
                return Rotation.ROTATION_180;
            }
            case 270:{
                return Rotation.ROTATION_270;
            }
        }
        return Rotation.NORMAL;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (cameraPreviewView.isLaidOut() && cameraPreviewView.isLayoutRequested()){
            myCamera.onResume(cameraPreviewView.getWidth(), cameraPreviewView.getHeight());
        } else {
            cameraPreviewView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                           int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    cameraPreviewView.removeOnLayoutChangeListener(this);
                    myCamera.onResume(cameraPreviewView.getWidth(), cameraPreviewView.getHeight());
                }
            });

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //myCamera.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 10 && grantResults.length == 2
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
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
