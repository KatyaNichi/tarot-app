import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.superapp.luneartarot.R
import com.superapp.luneartarot.data.CardRepository
import com.superapp.luneartarot.data.TarotCard
import com.superapp.luneartarot.viewmodel.SettingsViewModel
import kotlin.random.Random

@Composable
fun YesOrNoScreen(
    cardRepository: CardRepository,
    settingsViewModel: SettingsViewModel = viewModel()
) {
    var tapCount by remember { mutableStateOf(0) }
    var selectedCard by remember { mutableStateOf<TarotCard?>(null) }
    var showAnswer by remember { mutableStateOf(false) }
    var isUpright by remember { mutableStateOf(true) }
    val context = LocalContext.current

    val isVibrationEnabled by settingsViewModel.isVibrationEnabled.collectAsState()

    val tangerineBold = FontFamily(Font(R.font.almendra_italic))

    val animatedAlpha by animateFloatAsState(
        targetValue = if (showAnswer) 1f else 0f,
        animationSpec = tween(durationMillis = 1000)
    )

    var cardScale by remember { mutableStateOf(1f) }
    val cardScaleAnimation by animateFloatAsState(
        targetValue = cardScale,
        animationSpec = spring(dampingRatio = 0.4f, stiffness = 400f)
    )

    fun vibrate() {
        if (isVibrationEnabled) {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(50)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background image
        Image(
            painter = painterResource(id = R.drawable.background_dark),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
        )


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                if (!showAnswer)
                    stringResource(id = R.string.ask_question)
                else
                    selectedCard?.name ?: "",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontFamily = tangerineBold,
                    fontSize = 32.sp
                ),
                textAlign = TextAlign.Center,
                color = Color.White,
                modifier = Modifier.padding(top = 32.dp, bottom = 16.dp)
            )

            Box(
                modifier = Modifier
                    .size(240.dp)
                    .scale(cardScaleAnimation)
            ) {
                if (!showAnswer) {
                    Image(
                        painter = painterResource(id = R.drawable.card_back1),
                        contentDescription = "Tap this card",
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                vibrate()
                                cardScale = 0.9f
                                tapCount++
                                if (tapCount >= 7) {
                                    selectedCard = cardRepository.getAllCards().random()
                                    isUpright = Random.nextBoolean()
                                    showAnswer = true
                                }
                            }
                    )
                } else {
                    selectedCard?.let { card ->
                        val imageResId = remember(card.imageName) {
                            context.resources.getIdentifier(card.imageName, "drawable", context.packageName)
                        }
                        if (imageResId != 0) {
                            Image(
                                painter = painterResource(id = imageResId),
                                contentDescription = card.name,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .alpha(animatedAlpha)
                            )
                        } else {
                            // Fallback for when the image resource is not found
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .alpha(animatedAlpha)
                            ) {
                                Text(
                                    "Image not found",
                                    color = Color.White,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        }
                    }
                }
            }

            if (!showAnswer) {
                // Progress indicator
                LinearProgressIndicator(
                    progress = tapCount / 7f,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .width(200.dp),
                    color = Color(0xFF1D3450)
                )
            } else {
                Text(
                    stringResource(id = R.string.answer, if (isUpright) stringResource(id = R.string.yes) else stringResource(id = R.string.no)),
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color(0xFF1B263B),
                    modifier = Modifier.padding(top = 8.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(
                        stringResource(id = R.string.ask_new_question),
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontFamily = tangerineBold,
                            fontSize = 38.sp
                        ),
                        fontSize = 18.sp,
                        modifier = Modifier
                            .padding(bottom = 32.dp)
                            .clickable {
                                tapCount = 0
                                selectedCard = null
                                showAnswer = false
                                cardScale = 1f
                            }
                    )
                }
            }
        }
    }

    LaunchedEffect(cardScale) {
        if (cardScale < 1f) {
            kotlinx.coroutines.delay(100)
            cardScale = 1f
        }
    }
}