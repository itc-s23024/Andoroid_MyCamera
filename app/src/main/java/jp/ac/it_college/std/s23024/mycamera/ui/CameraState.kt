package jp.ac.it_college.std.s23024.mycamera.ui

import android.content.Context
import android.util.Log
import androidx.camera.core.CameraProvider
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.Future

data class CameraState(
    val context: Context,
    val cameraProviderFuture: ListenableFuture<ProcessCameraProvider>,
    val lifecycleOwner: LifecycleOwner,
    val imageCapture: ImageCapture
) {
    fun stateCamera(ctx: Context): PreviewView {
        val previewView = PreviewView(ctx).apply {
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
        }
        val executor = ContextCompat.getMainExecutor(ctx)
        cameraProviderFuture.addListener(
            {
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageCapture
                    )
                } catch (e: Exception) {
                    Log.e("CameraPreView", "Use case binding failed", e)
                }
            },
            executor
        )
        return previewView
    }
}

@Composable
fun rememberCameraState(
    context: Context = LocalContext.current,
    cameraProviderFuture: ListenableFuture<ProcessCameraProvider> = ProcessCameraProvider.getInstance(
        context
    ),
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    imageCapture: ImageCapture = ImageCapture.Builder().build()
) = remember(context, cameraProviderFuture, lifecycleOwner) {
    CameraState(
        context = context,
        cameraProviderFuture = cameraProviderFuture,
        lifecycleOwner = lifecycleOwner,
        imageCapture = imageCapture
    )
}