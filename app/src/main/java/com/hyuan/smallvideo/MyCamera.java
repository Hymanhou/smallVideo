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
import android.view.Surface;
import com.hyuan.smallvideo.utils.ImageUtil;

import java.util.ArrayList;
import java.util.List;

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
    private Activity activity;

    public MyCamera(Activity activity){
        this.activity = activity;
        cameraManager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
    }

    public void onResume(int width, int height) {
        viewWidth = width;
        viewHeight = height;
        setUpCamera();
    }

    public void onPause() {
        releaseCamera();
    }

    private void releaseCamera() {
        imageReader.close();
        cameraInstance.close();
        captureSession.close();
        imageReader = null;
        cameraInstance = null;
        captureSession = null;
    }

    public void switchCamera() {
        if (cameraFacing == CameraCharacteristics.LENS_FACING_FRONT) {
            cameraFacing = CameraCharacteristics.LENS_FACING_BACK;
        } else {
            cameraFacing = CameraCharacteristics.LENS_FACING_FRONT;
        }
        releaseCamera();
        setUpCamera();
    }

    public int getCameraOrientation() {
        int degrees = 0;
        switch (activity.getWindowManager().getDefaultDisplay().getRotation()) {
            case Surface.ROTATION_0:{
                degrees = 0;
                break;
            }
            case Surface.ROTATION_90:{
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
        String cameraId = chooseCamera();
        if (cameraId == null){
            return 0;
        }
        try {
            CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
            int orinetation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
            if (cameraFacing == CameraCharacteristics.LENS_FACING_FRONT) {
                return orinetation + degrees % 360;
            }
            return orinetation - degrees % 360;
        } catch (CameraAccessException e) {
            Log.e(TAG, "access camera error.", e);
        }
        return 0;
    }

    public boolean hasMultipleCamera() {
        try {
            return cameraManager.getCameraIdList().length > 1;
        } catch (CameraAccessException e) {
            return false;
        }
    }

    public void setOnPreviewListener(OnPreviewListener listener){
        onPreviewListener = listener;
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
                if (null != onPreviewListener && image != null) {
                    onPreviewListener.onPreviewFrame(ImageUtil.generateNV21Data(image)
                            , image.getWidth(), image.getHeight());
                    image.close();
                }
            }
        }, null);

        try {
            List<Surface> surfaceList = new ArrayList<>();
            surfaceList.add(imageReader.getSurface());
            cameraInstance.createCaptureSession(
                    surfaceList,
                    new CaptureStateCallback(),
                    null);
        } catch (CameraAccessException e) {
            Log.e(TAG, "camera access error", e);
        }
    }

    private Size chooseOptimalSize() {
        if (viewHeight == 0 || viewWidth == 0) {
            return new Size(0, 0);
        }
        String cameraId = chooseCamera();
        if (null == cameraId) {
            return new Size(0, 0);
        }
        int bestWidth = 480;
        int bestHeight = 640;
        try {
            StreamConfigurationMap  configurationMap = cameraManager
                    .getCameraCharacteristics(cameraId)
                    .get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            Size[] outputSizeArray = configurationMap.getOutputSizes(ImageFormat.YUV_420_888);

            long minProduct = Integer.MAX_VALUE;

            for (Size size: outputSizeArray) {
                long product = Math.abs((viewHeight - size.getHeight()) * (viewWidth - size.getWidth()));
                if (product < minProduct) {
                    minProduct = product;
                    bestHeight = size.getHeight();
                    bestWidth = size.getWidth();
                }
            }
        } catch (CameraAccessException e) {
            Log.e(TAG, "access camera error", e);
        }
        return new Size(bestWidth, bestHeight);
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
