package com.hyuan.smallvideo;

public interface CameraLoader {
    void onResume(int width, int height);
    void onPause();
    void switchCamera();
    int getCameraOrientation();
    boolean hasMultipleCamera();
    void setOnPreviewListener(OnPreviewListener listener);
}
