package jp.ac.it_college.std.s23024.mycamera.ui

import android.content.Context
import androidx.camera.core.Camera
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun PreviewCamera(
    modifier: Modifier = Modifier,
    createPreviewCamera: (Context) -> PreviewView
) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { ctx ->
            createPreviewCamera(ctx)
        }
    )
}