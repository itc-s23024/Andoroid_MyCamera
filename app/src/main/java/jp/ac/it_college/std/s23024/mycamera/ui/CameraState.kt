package jp.ac.it_college.std.s23024.mycamera.ui

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import java.text.SimpleDateFormat
import java.util.Locale

data class CameraState(
    val context: Context,
    val cameraProviderFuture: ListenableFuture<ProcessCameraProvider>,
    val lifecycleOwner: LifecycleOwner,
    val imageCapture: ImageCapture
) {
    companion object {
        const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }
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

    fun takePhoto() {
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contextValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/CameraX-Image")
        }

        val outputOptions = ImageCapture.OutputFileOptions.Builder(
            context.contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contextValues
        ).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val msg = "Phone capture succeeded: ${outputFileResults.savedUri}"
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    Log.d("Camera", msg)
                    Intent(Intent.ACTION_SEND).also { share ->
                        share.type = "image/*"
                        share.putExtra(Intent.EXTRA_STREAM, outputFileResults.savedUri)
                        context.startActivity(
                            Intent.createChooser(share, "Share to")
                        )
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    val msg = "Photo capture failed ${exception.message}"
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    Log.e("Camera", msg, exception)
                }
            }
        )
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