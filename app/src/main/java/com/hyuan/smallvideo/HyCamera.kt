package com.hyuan.smallvideo

import android.content.Context
import android.hardware.Camera
import android.os.Build
import android.util.Size

class HyCamera {
    private lateinit var mCamera:Camera;

    fun openCamera(width:Int, height:Int){
        mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT)
        var cameraParameters:Camera.Parameters  = mCamera.parameters
        val pictureSizeList = cameraParameters.supportedPictureSizes
        val previewSize = cameraParameters.supportedPreviewSizes

        cameraParameters.setPictureSize()
        mCamera.parameters = cameraParameters
    }

    fun getBestSize(sizeList:List<Camera.Size>, targetWidth:Int):Camera.Size{
        var bestSize:Camera.Size? = null
        if (bestSize != null){
        }
    }
}