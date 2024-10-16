package jp.ac.it_college.std.s23024.mycamera.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun TakePhoto(
    modifier: Modifier = Modifier,
    takePhoto: () -> Unit = {}
) {
    IconButton(
        onClick = { takePhoto() },
        modifier = modifier
            .padding(16.dp)
            .size(70.dp),
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = Color.White,
            contentColor = Color.Red
        )
    ) {
        Icon(
            imageVector = Icons.Filled.AddAPhoto,
            contentDescription = null
        )
    }
}