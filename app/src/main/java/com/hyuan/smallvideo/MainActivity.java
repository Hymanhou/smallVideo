package com.hyuan.smallvideo;

import android.Manifest;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.SurfaceView;
import android.view.TextureView;

public class MainActivity extends AppCompatActivity {

    private WxCamera myCamera = null;
    private TextureView textureView = null;
    private int minWidth = 720;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PermissionUtils.askPermission(this,new String[]{Manifest.permission.CAMERA,Manifest
                .permission.WRITE_EXTERNAL_STORAGE},10);
//        setContentView(R.layout.activity_main);
        textureView = new TextureView(this);
        setContentView(textureView);
        myCamera = new WxCamera();

//        DisplayMetrics dm = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(dm);
//        screenHeight = dm.heightPixels;
//        screenWidth = dm.widthPixels;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myCamera.closeCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        myCamera.openCamera(minWidth);
        myCamera.setSurfaceTexture(textureView.getSurfaceTexture());
        myCamera.startPreview();
    }

}
