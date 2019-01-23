package com.hyuan.smallvideo;

import android.app.Activity;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class WxCamera implements CameraLoader {
    private Camera mCamera;
    private int cameraFacing = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private Activity activity;
    private OnPreviewListener onPreviewListener;
    private int width;
    private int height;
    private TextureView textureView;

    public WxCamera(Activity activity){
        this.activity = activity;
        this.textureView = new TextureView(activity);
    }

    @Override
    public void onResume(int width, int height) {
        this.width = width;
        this.height = height;
        setUpCamera(width, height);
    }

    @Override
    public void onPause() {
        releaseCamera();
    }

    @Override
    public void switchCamera() {
        if (Camera.CameraInfo.CAMERA_FACING_BACK == cameraFacing) {
            cameraFacing = Camera.CameraInfo.CAMERA_FACING_FRONT;
        } else {
            cameraFacing = Camera.CameraInfo.CAMERA_FACING_BACK;
        }
        releaseCamera();
        setUpCamera(width, height);
    }

    @Override
    public int getCameraOrientation() {
        int degrees = 0;
        switch (activity.getWindowManager().getDefaultDisplay().getRotation()) {
            case Surface.ROTATION_0:{
                degrees = 0;
                break;
            }
            case Surface.ROTATION_90: {
                degrees = 90;
                break;
            }
            case Surface.ROTATION_180:{
                degrees = 180;
                break;
            }
            case Surface.ROTATION_270:{
                degrees = 270;
                break;
            }
        }

        if (cameraFacing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            degrees = (degrees + 90) % 360;
        } else {
            degrees = (90 - degrees) % 360;
        }

        return degrees;
    }

    @Override
    public boolean hasMultipleCamera() {
        return Camera.getNumberOfCameras() > 1;
    }

    @Override
    public void setOnPreviewListener(final OnPreviewListener listener) {
        onPreviewListener = listener;
    }

    public void setUpCamera(int width, int height) {
        int id = 0;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == cameraFacing){
                id = i;
                break;
            }
        }
        try {
            mCamera = Camera.open(id);
        } catch (RuntimeException e) {
            Log.e("hyuan", "Camera not found");
            return;
        }
        Camera.Parameters parameters = mCamera.getParameters();
        Camera.Size pictureSize = getBestSize(parameters.getSupportedPictureSizes(), width, height);
        Camera.Size previewSize = getBestSize(parameters.getSupportedPreviewSizes(), width, height);
        parameters.setPictureSize(pictureSize.width, pictureSize.height);
        parameters.setPreviewSize(previewSize.width, previewSize.height);
//        parameters.setPictureFormat(ImageFormat.NV21);
//        parameters.setPreviewFormat(ImageFormat.NV21);
        if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }
        mCamera.setParameters(parameters);
        //mCamera.setDisplayOrientation(90);

        mCamera.setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                Camera.Size size = camera.getParameters().getPreviewSize();
                onPreviewListener.onPreviewFrame(data, size.width, size.height);
            }
        });
        mCamera.startPreview();
    }

    private void releaseCamera() {
        mCamera.setPreviewCallback(null);
        mCamera.release();
        mCamera = null;
    }

    private Camera.Size getBestSize(List<Camera.Size> sizeList, int width, int height){
        Collections.sort(sizeList, new SizeCompare());
        List<Camera.Size> chooseList = new ArrayList<>();
        for (Camera.Size size: sizeList) {
            if (size.height <= size.width &&
                    size.height <= width &&
                    size.width <= height) {
                chooseList.add(size);
            }
        }
        if (chooseList.size() > 0) {
            return Collections.max(chooseList, new SizeCompare());
        }
        return sizeList.get(0);
    }

    private class SizeCompare implements Comparator<Camera.Size> {
        @Override
        public int compare(Camera.Size o1, Camera.Size o2) {
            return Long.signum(o1.height * o1.width - o2.height * o2.width);
        }
    }
}
