package com.hyuan.smallvideo

import android.content.Context
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CameraMetadata
import android.os.Build
import android.view.TextureView

class HyCamera {
    private lateinit var context:Context
    private lateinit var mCameraId:String

    constructor(context: Context) {
        this.context = context
    }

    fun checkCameraLevel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            
        }
    }

    fun prepare() {
        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        for (cameraId in cameraManager.cameraIdList) {
            val cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId)
            if (cameraCharacteristics[CameraCharacteristics.LENS_FACING] == CameraMetadata.LENS_FACING_FRONT){
                mCameraId = cameraId
                val map = cameraCharacteristics[]
                break
            }
        }


    }
}