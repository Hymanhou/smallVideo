package com.hyuan.smallvideo;

import android.Manifest;
import android.app.ActivityManager;
import android.content.pm.ActivityInfo;
import android.content.pm.ConfigurationInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import com.hyuan.smallvideo.utils.NativeTool;
import com.hyuan.smallvideo.utils.PermissionUtils;

import java.nio.IntBuffer;

public class MainActivity extends AppCompatActivity {

    private WxCameraView cameraView = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (!checkGLES30()) {
            Log.e("cameraView", "es3.0 not supported.");
            finish();
        }
        PermissionUtils.askPermission(this, new String[]{Manifest.permission.CAMERA,Manifest
                .permission.WRITE_EXTERNAL_STORAGE}, 1);
        cameraView = new WxCameraView(this);
        setContentView(cameraView);
    }

    private boolean checkGLES30() {
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        ConfigurationInfo info = activityManager.getDeviceConfigurationInfo();
        return info.reqGlEsVersion >= 0x30000;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraView.onPause();
    }
}
