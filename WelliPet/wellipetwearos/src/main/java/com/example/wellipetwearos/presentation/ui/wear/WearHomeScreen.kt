// File: com/example/wellipetwearos/ui/wear/WearHomeScreen.kt
package com.example.wellipetwearos.presentation.ui.wear

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import coil.ImageLoader
import coil.decode.ImageDecoderDecoder
import androidx.compose.ui.platform.LocalContext
import com.example.wellipetwearos.R

@Composable
fun WearHomeScreen() {
    val context = LocalContext.current
    // 使用 ImageLoader 來解析 GIF
    val gifImageLoader = androidx.compose.runtime.remember {
        ImageLoader.Builder(context)
            .components { add(ImageDecoderDecoder.Factory()) }
            .build()
    }
    Box(modifier = Modifier.fillMaxSize()) {
        // 背景圖片
        AsyncImage(
            model = R.drawable.bg_park, // 請把適合 Wear OS 的背景圖片放在 res/drawable
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        // 中央顯示寵物 GIF
        AsyncImage(
            model = R.drawable.dog, // 請把適合手錶版的寵物 GIF 放在 res/drawable
            imageLoader = gifImageLoader,
            contentDescription = "Animated Pet",
            modifier = Modifier
                .align(Alignment.Center)
                .clip(CircleShape)
                .fillMaxSize(0.5f),
            contentScale = ContentScale.Crop
        )
    }
}
