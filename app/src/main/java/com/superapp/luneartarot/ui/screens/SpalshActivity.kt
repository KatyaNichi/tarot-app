import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.superapp.luneartarot.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onSplashScreenFinish: () -> Unit) {
    var isAnimationFinished by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isAnimationFinished) 1.05f else 1f,
        animationSpec = tween(
            durationMillis = 4000,
            easing = LinearEasing
        ),
        finishedListener = { onSplashScreenFinish() }, label = ""
    )


    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val painter = painterResource(id = R.drawable.splash_screen1)
        val imageRatio = painter.intrinsicSize.width / painter.intrinsicSize.height
        val screenRatio = maxWidth / maxHeight

        val imageModifier = if (screenRatio > imageRatio) {
            Modifier.fillMaxWidth()
        } else {
            Modifier.fillMaxHeight()
        }

        Image(
            painter = painter,
            contentDescription = "Splash Image",
            contentScale = ContentScale.Crop,
            modifier = imageModifier
                .scale(scale)
                .align(Alignment.Center)
        )
    }

    LaunchedEffect(key1 = true) {
        delay(100) // Small delay to ensure animation starts
        isAnimationFinished = true
    }
}