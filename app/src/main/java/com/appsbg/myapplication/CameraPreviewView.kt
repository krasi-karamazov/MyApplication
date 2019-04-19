package com.appsbg.myapplication

import android.content.Context
import android.graphics.PixelFormat
import android.hardware.Camera
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.TextureView
import android.hardware.Camera.PreviewCallback


@Suppress("DEPRECATION")
class CameraPreviewView(ctx: Context, attr: AttributeSet): SurfaceView(ctx, attr), SurfaceHolder.Callback {

    companion object {
        private const val AUTO_FOCUS_DELAY: Int = 250
    }

    private lateinit var camera: Camera
    private var surfaceHolder: SurfaceHolder

    init {
        surfaceHolder = holder
        surfaceHolder.addCallback(this)
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_GPU)
        surfaceHolder.setFormat(PixelFormat.RGB_332)
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        val parameters = camera.parameters
        parameters.setPreviewSize(80, 60)
        camera.parameters = parameters

        camera.startPreview()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        camera.stopPreview()
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        camera = Camera.open()
        val params: Camera.Parameters = camera.parameters
        params.setPictureSize(80, 60)
        params.colorEffect = Camera.Parameters.EFFECT_NONE
        params.jpegQuality = 20
        params.previewFrameRate = 1
        params.setPreviewFpsRange(5, 10)
        val mSupportedPreviewSizes = params.getSupportedPreviewSizes()
        val previewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height)

        previewSize?.width?.let { params.setPreviewSize(it, previewSize.height) }
        camera.parameters = params

        camera.setPreviewDisplay(surfaceHolder)
        camera.setPreviewCallback { _, _ ->
            this.invalidate()
        }
    }

    private fun getOptimalPreviewSize(sizes:List<Camera.Size>, w: Int, h: Int): Camera.Size?  {
        val ASPECT_TOLERANCE: Double = 0.1
        val targetRatio: Double = (h / w).toDouble()

        if (sizes == null)
            return null

        var optimalSize: Camera.Size? = null
        var minDiff: Double = Double.MAX_VALUE

        val targetHeight: Int = h;
        val iterator = sizes.iterator()
        iterator.forEach {
            val ratio: Double = it.height / it.width.toDouble()
            if(Math.abs(it.height - targetHeight) > minDiff) {
                optimalSize = it
                minDiff = Math.abs(it.height - targetHeight).toDouble()
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE
            val iterator = sizes.iterator()
            iterator.forEach {
                if (Math.abs(it.height - targetHeight) < minDiff) {
                    optimalSize = it
                    minDiff = Math.abs(it.height - targetHeight).toDouble()
                }
            }
        }

        return optimalSize
    }

}