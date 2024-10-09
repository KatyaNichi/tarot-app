package com.superapp.luneartarot.ui.screens

import android.graphics.ImageDecoder
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.superapp.luneartarot.R
import com.superapp.luneartarot.data.CardRepository
import com.superapp.luneartarot.data.TarotCard
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.IntOffset
import coil.compose.rememberAsyncImagePainter
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import coil.size.Size
import kotlin.random.Random

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun YesOrNoScreen(
    cardRepository: CardRepository,
    onCardClick: (TarotCard) -> Unit
) {
    var selectedCard by remember { mutableStateOf<TarotCard?>(null) }
    var showAnswer by remember { mutableStateOf(false) }
    var showDeck by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Load the GIF using ImageDecoder
    val gifBitmap = remember {
        val gifResourceId = R.raw.gifmagic
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.resources, gifResourceId)
            ImageDecoder.decodeBitmap(source)
        } else {
            // For older versions, you might need to use a different method or a library like Glide
            null
        }
    }
    val gifResourceId = R.raw.gifmagic

    Box(modifier = Modifier.fillMaxSize()) {
        // Background image
        Image(
            painter = painterResource(id = R.drawable.background_image),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(80.dp))

            Text(
                "Think of a yes-or-no question",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (!showDeck) {
                // Animated GIF using Coil
                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(context)
                            .decoderFactory(ImageDecoderDecoder.Factory())
                            .data(gifResourceId)
                            .size(Size.ORIGINAL)
                            .build()
                    ),
                    contentDescription = "Magic Animation",
                    modifier = Modifier
                        .size(200.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            if (!showDeck) {
                Text(
                    "I'm ready to pick a card",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        textDecoration = TextDecoration.Underline
                    ),
                    modifier = Modifier
                        .clickable { showDeck = true }
                        .padding(vertical = 16.dp),
                    textAlign = TextAlign.Center
                )
            }

            if (showDeck) {
              //  Spacer(modifier = Modifier.height(300.dp))
                ChaoticCardSpread(
                    cards = cardRepository.getAllCards(),
                    onCardSelected = { card ->
                        selectedCard = card
                        showAnswer = true
                        onCardClick(card)
                    }
                )
            }

            if (showAnswer && selectedCard != null) {
                val imageResId = context.resources.getIdentifier(selectedCard!!.imageName, "drawable", context.packageName)
                Image(
                    painter = painterResource(id = imageResId),
                    contentDescription = selectedCard!!.name,
                    modifier = Modifier
                        .size(200.dp)
                        .padding(bottom = 16.dp)
                )

                Text(
                    selectedCard!!.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    "Answer: ${selectedCard!!.answer}",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.Green,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                Button(
                    onClick = {
                        selectedCard = null
                        showAnswer = false
                        showDeck = false
                    }
                ) {
                    Text("Ask Another Question")
                }
            }
        }
    }
}


@Composable
fun CardWheel(
    cards: List<TarotCard>,
    onCardSelected: (TarotCard) -> Unit
) {
    var rotationState by remember { mutableFloatStateOf(0f) }
    val animatedRotation = remember { Animatable(0f) }
    val radius = 375.dp
    val cardWidthDp = 200.dp
    val cardHeightDp = 280.dp
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        // Snap to the current rotation when starting a new drag
                        scope.launch {
                            animatedRotation.snapTo(rotationState)
                        }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        // Update rotation based on horizontal drag
                        // Negative factor to make drag right turn wheel right
                        rotationState -= dragAmount.x * 0.5f
                        scope.launch {
                            animatedRotation.snapTo(rotationState)
                        }
                    },
                    onDragEnd = {
                        // Add inertia effect when drag ends
                        scope.launch {
                            animatedRotation.animateDecay(
                                initialVelocity = -animatedRotation.velocity, // Negative to maintain drag direction
                                animationSpec = exponentialDecay(frictionMultiplier = 1f)
                            ) {
                                rotationState = value
                            }
                        }
                    }
                )
            }
    ) {
        Box(
            modifier = Modifier
                .size(radius * 2)
                .align(Alignment.Center)
        ) {
            cards.forEachIndexed { index, card ->
                val angle = (index * 360f / cards.size) + animatedRotation.value
                val radians = Math.toRadians(angle.toDouble())

                val radiusPx = with(density) { radius.toPx() }
                val xOffset = (radiusPx * cos(radians)).toFloat()
                val yOffset = (radiusPx * sin(radians)).toFloat()

                with(density) {
                    Image(
                        painter = painterResource(id = R.drawable.card_back),
                        contentDescription = card.name,
                        modifier = Modifier
                            .size(cardWidthDp, cardHeightDp)
                            .offset(x = xOffset.toDp(), y = yOffset.toDp())
                            .clickable(
                                onClick = {
                                    onCardSelected(card)
                                }
                            )
                            .rotate(angle + 90f)
                            .align(Alignment.Center),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    }
}

@Composable
fun ChaoticCardSpread(
    cards: List<TarotCard>,
    onCardSelected: (TarotCard) -> Unit
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val maxWidth = constraints.maxWidth
        val maxHeight = constraints.maxHeight

        cards.forEach { card ->
            var offsetX by remember { mutableStateOf(Random.nextInt(0, maxWidth).toFloat()) }
            var offsetY by remember { mutableStateOf(Random.nextInt(0, maxHeight).toFloat()) }
            var rotation by remember { mutableStateOf(Random.nextFloat() * 360f) }

            Image(
                painter = painterResource(id = R.drawable.card_back),
                contentDescription = card.name,
                modifier = Modifier
                    .size(width = 100.dp, height = 150.dp)
                    .offset { IntOffset(offsetX.toInt(), offsetY.toInt()) }
                    .rotate(rotation)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                onCardSelected(card)
                            }
                        )
                    },
                contentScale = ContentScale.Fit
            )
        }
    }
}





