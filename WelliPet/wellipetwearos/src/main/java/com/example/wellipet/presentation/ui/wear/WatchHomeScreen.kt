package com.example.wellipet.presentation.ui.wear

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.ImageLoader
import coil.decode.ImageDecoderDecoder
import com.example.wellipet.R
import com.example.wellipet.presentation.model.PetGifMapper
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun WatchHomeScreen(
    viewModel: WatchHomeViewModel = viewModel()
) {
    val petStatus      by viewModel.petStatus.collectAsState()
    val petKey         by viewModel.selectedPet.collectAsState()         // String?
    val bgKey          by viewModel.selectedBackground.collectAsState()  // String?
    val selectedBadges by viewModel.selectedBadges.collectAsState()

    val ctx = LocalContext.current

    // 1) 背景资源：key -> id
    val bgRes = bgKey
        ?.let { key -> ctx.resources.getIdentifier(key, "drawable", ctx.packageName) }
        ?: R.drawable.bg_home

    // 2) 宠物动画：直接用 String key
    val gifRes = PetGifMapper.get(petKey, petStatus)

    val loader = remember {
        ImageLoader.Builder(ctx)
            .components { add(ImageDecoderDecoder.Factory()) }
            .build()
    }

    Box(Modifier.fillMaxSize()) {
        // 背景
        Image(
            painter = painterResource(id = bgRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // 底部徽章
        Row(
            Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(25.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            selectedBadges.take(3).forEach { badgeKey ->
                val badgeRes = ctx.resources
                    .getIdentifier(badgeKey, "drawable", ctx.packageName)
                    .takeIf { it != 0 } ?: return@forEach

                Image(
                    painter = painterResource(id = badgeRes),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        // 中间宠物
        Box(Modifier.fillMaxSize().padding(top = 50.dp), contentAlignment = Alignment.Center) {
            AsyncImage(
                model = gifRes,
                imageLoader = loader,
                contentDescription = "Pet",
                modifier = Modifier.size(150.dp),
                contentScale = ContentScale.Crop
            )
        }
    }
}
