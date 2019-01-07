package com.hyuan.smallvideo;

import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.view.Surface;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class WxCamera {
    private Camera mCamera;
    private Camera.Size mPreviewSize;
    private Camera.Size mPictureSize;

    public int openCamera(int width) {
        try {
            mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
        } catch (RuntimeException e) {
            return -1;
        }
        Camera.Parameters parameters = mCamera.getParameters();
        mPictureSize = getBestSize(parameters.getSupportedPictureSizes(), width, 1.778f);
        mPreviewSize = getBestSize(parameters.getSupportedPreviewSizes(), width, 1.778f);
        parameters.setPictureSize(mPictureSize.width, mPictureSize.height);
        parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
        parameters.setPictureFormat(ImageFormat.NV21);
        parameters.setPreviewFormat(ImageFormat.NV21);
        mCamera.setParameters(parameters);
        //mCamera.setDisplayOrientation(90);
        return 0;
    }

    public void setSurfaceTexture(SurfaceTexture surfaceTexture) {
        try {
            mCamera.setPreviewTexture(surfaceTexture);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    public void setPreviewCallback(Camera.PreviewCallback callback) {
        mCamera.setPreviewCallback(callback);
    }

    public void startPreview() {
        mCamera.startPreview();
    }

    public void closeCamera() {
        mCamera.release();
        mCamera = null;
    }

    private Camera.Size getBestSize(List<Camera.Size> sizeList, int width, float rate){
        Collections.sort(sizeList, new SizeCompare());
        for (Camera.Size size: sizeList) {
            if (size.height >= width) {
                float rate0 = (float)size.width / (float)size.height;
                if (Math.abs(rate - rate0) < 0.03){
                    return size;
                }
            }
        }
        return sizeList.get(0);
    }

    private class SizeCompare implements Comparator<Camera.Size> {
        @Override
        public int compare(Camera.Size o1, Camera.Size o2) {
            return o1.height - o2.height;
        }
    }
}
