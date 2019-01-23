package com.hyuan.smallvideo;

import android.Manifest;
import android.app.ActivityManager;
import android.content.pm.ActivityInfo;
import android.content.pm.ConfigurationInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.hyuan.smallvideo.filter.GrayFilter;
import com.hyuan.smallvideo.utils.NativeTool;
import com.hyuan.smallvideo.utils.PermissionUtils;

import java.nio.IntBuffer;

public class MainActivity extends AppCompatActivity {

    private WxCamera wxCamera;
    private CameraPreviewView cameraPreviewView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PermissionUtils.askPermission(this, new String[]{Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE}, 10);

        setContentView(R.layout.activity_main);

        GrayFilter filter = new GrayFilter();
        cameraPreviewView = findViewById(R.id.camera_preview);

        wxCamera = new WxCamera(this);
        wxCamera.setOnPreviewListener(new OnPreviewListener() {
            @Override
            public void onPreviewFrame(byte[] data, int width, int height) {
                cameraPreviewView.updatePreviewFrame(data, width, height);
            }
        });
        //cameraPreviewView.setFilter(filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (cameraPreviewView.isLaidOut() && cameraPreviewView.isLayoutRequested()){
            wxCamera.onResume(cameraPreviewView.getWidth(), cameraPreviewView.getHeight());
        } else {
            cameraPreviewView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                           int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    cameraPreviewView.removeOnLayoutChangeListener(this);
                    wxCamera.onResume(cameraPreviewView.getWidth(), cameraPreviewView.getHeight());
                }
            });

        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        wxCamera.onPause();
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
}
