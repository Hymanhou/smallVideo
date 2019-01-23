package com.hyuan.smallvideo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.camera2.*;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.util.Size;

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
public class MyCamera {
    private final static String TAG = "MyCamera";
    private CameraManager cameraManager;
    private CameraDevice cameraInstance;
    private CameraCaptureSession captureSession;
    private ImageReader imageReader;
    private int cameraFacing = CameraCharacteristics.LENS_FACING_FRONT;
    private int viewWidth;
    private int viewHeight;
    private OnPreviewListener onPreviewListener;

    public MyCamera(Activity activity){
        cameraManager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
    }

    public void onResume(int width, int height) {
        viewWidth = width;
        viewHeight = height;
        setUpCamera();
    }

    @SuppressLint("MissingPermission")
    private void setUpCamera(){
        String cameraId = chooseCamera();
        try {
            cameraManager.openCamera(cameraId, new CameraDeviceCallback(), null);
        } catch (CameraAccessException e){
            Log.e(TAG, "access camera error", e);
            e.printStackTrace();
        }
    }

    private String chooseCamera() {
        try {
            String[] cameraIdArray = cameraManager.getCameraIdList();
            for (int i = 0; i < cameraIdArray.length; i++) {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraIdArray[i]);
                if (characteristics.get(CameraCharacteristics.LENS_FACING) == cameraFacing) {
                    return cameraIdArray[i];
                }
            }
        } catch (CameraAccessException e){
            Log.e(TAG, "access camera error", e);
            e.printStackTrace();
        }
        return null;
    }

    private void startCaptureSession(){
        Size size = chooseOptimalSize();
        imageReader = ImageReader.newInstance(size.getWidth(), size.getHeight(), ImageFormat.YUV_420_888, 2);
        imageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
                Image image = reader.acquireNextImage();
                if (null != onPreviewListener) {
                    onPreviewListener.onPreviewFrame(image.gene);
                }
            }
        }, null);
    }

    private Size chooseOptimalSize() {
        if (viewHeight == 0 || viewWidth == 0) {
            return new Size(0, 0);
        }
        String cameraId = chooseCamera();
        if (null == cameraId) {
            return new Size(0, 0);
        }
        Size bestSize = new Size(480, 640);
        try {
            StreamConfigurationMap  configurationMap = cameraManager
                    .getCameraCharacteristics(cameraId)
                    .get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            Size[] outputSizeArray = configurationMap.getOutputSizes(ImageFormat.YUV_420_888);

            int orientation = getCameraOrientation();
            int maxPreviewWidth = viewWidth;
            int maxPreviewHeight = viewHeight;
            if (orientation == 90 || orientation == 270) {
                maxPreviewWidth = viewHeight;
                maxPreviewHeight = viewWidth;
            }
            long maxProduct = 0;
            for (Size size: outputSizeArray) {
                if (size.getWidth() < maxPreviewWidth / 2 && size.getHeight() < maxPreviewHeight / 2) {
                    if (size.getHeight() * size.getWidth() >  maxProduct) {
                        maxProduct = size.getHeight() * size.getWidth();
                        bestSize = new Size(size.getWidth(), size.getHeight());
                    }
                }
            }
            return bestSize;
        } catch (CameraAccessException e) {
            Log.e(TAG, "access camera error", e);
        }
        return bestSize;
    }

    public void onPause() {

    }

    public void switchCamera() {

    }

    public int getCameraOrientation() {
        return 0;
    }

    public boolean hasMultipleCamera() {
        return false;
    }

    public void setOnPreviewListener(OnPreviewListener listener){

    }

    private class CameraDeviceCallback extends CameraDevice.StateCallback{
        @Override
        public void onOpened(CameraDevice camera) {
            cameraInstance = camera;
            startCaptureSession();
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            camera.close();
            cameraInstance = null;
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            camera.close();
            cameraInstance = null;
        }
    }

    private class CaptureStateCallback extends CameraCaptureSession.StateCallback {
        @Override
        public void onConfigured(CameraCaptureSession session) {
            if (cameraInstance == null){
                return;
            }
            captureSession = session;
            try {
                CaptureRequest.Builder builder = cameraInstance.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                builder.addTarget(imageReader.getSurface());
                captureSession.setRepeatingRequest(builder.build(), null, null);
            } catch (CameraAccessException e) {
                Log.e(TAG, "access camera error", e);
                e.printStackTrace();
            }

        }

        @Override
        public void onConfigureFailed(CameraCaptureSession session) {
            Log.e(TAG, "onConfigureFailed camera error");
        }
    }
}
