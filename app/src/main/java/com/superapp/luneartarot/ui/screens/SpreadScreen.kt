package com.superapp.luneartarot.ui.screens

import android.app.Activity
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.superapp.luneartarot.R
import com.superapp.luneartarot.data.CardRepository
import com.superapp.luneartarot.data.CardOfDay
import com.superapp.luneartarot.data.TarotCard
import kotlinx.coroutines.launch
import com.google.android.gms.ads.AdRequest
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

@Composable
fun SpreadScreen(cardRepository: CardRepository) {
    var pickedCards by remember { mutableStateOf(listOf<CardOfDay>()) }
    val allCards = remember { cardRepository.getAllCards() }
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var showAd by remember { mutableStateOf(false) }


    // Show video ad after picking 3 cards
    LaunchedEffect(pickedCards.size) {
        if (pickedCards.size == 3) {
            showAd = true
        }
    }


    LaunchedEffect(Unit) {
        val centerIndex = allCards.size / 2 - 1
        lazyListState.scrollToItem(centerIndex)
    }

    Box(modifier = Modifier.fillMaxSize()) {
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
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top row for picked cards
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                repeat(3) { index ->
                    Box(
                        modifier = Modifier
                            .width(80.dp)
                            .height(120.dp)
                            .background(Color.White.copy(alpha = 0.5f))
                    ) {
                        pickedCards.getOrNull(index)?.let { cardOfDay ->
                            Image(
                                painter = painterResource(id = getCardResourceId(cardOfDay.card.imageName)),
                                contentDescription = cardOfDay.card.name,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .rotate(if (cardOfDay.isUpright) 0f else 180f),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }

            if (pickedCards.size < 3) {
                CardPickingContent(
                    pickedCards = pickedCards,
                    allCards = allCards,
                    lazyListState = lazyListState,
                    onCardPicked = {
                        if (pickedCards.size < 3) {
                            val randomCard = cardRepository.getRandomCard()
                            pickedCards = pickedCards + randomCard
                            coroutineScope.launch {
                                lazyListState.animateScrollToItem(allCards.indexOf(randomCard.card))
                            }
                        }
                    }
                )
            } else {
                SpreadExplanationContent(pickedCards = pickedCards)
            }
        }

        if (showAd) {
            VideoAdComponent(onAdClosed = {
                showAd = false
            })
        }
    }
}

@Composable
fun VideoAdComponent(onAdClosed: () -> Unit) {
    val context = LocalContext.current
    var interstitialAd by remember { mutableStateOf<InterstitialAd?>(null) }

    LaunchedEffect(Unit) {
        loadInterstitialAd(context) { ad ->
            interstitialAd = ad
            ad?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    onAdClosed()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    interstitialAd = null
                    onAdClosed()
                }
            }
            ad?.show(context as Activity)
        }
    }
}
private fun loadInterstitialAd(context: Context, onAdLoaded: (InterstitialAd?) -> Unit) {
    InterstitialAd.load(
        context,
        "ca-app-pub-5041847512278032/6281792801",
        AdRequest.Builder().build(),
        object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(ad: InterstitialAd) {
                onAdLoaded(ad)
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                onAdLoaded(null)
            }
        }
    )
}

@Composable
fun CardPickingContent(
    pickedCards: List<CardOfDay>,
    allCards: List<TarotCard>,
    lazyListState: LazyListState,
    onCardPicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        // Instruction text
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 36.dp),
            contentAlignment = Alignment.Center
        ) {
            val instructionText = when {
                pickedCards.isEmpty() -> stringResource(R.string.pick_1_card)
                pickedCards.size == 1 -> stringResource(R.string.pick_2nd_card)
                pickedCards.size == 2 -> stringResource(R.string.pick_final_card)
                else -> stringResource(R.string.spread_complete)
            }
            val tangerineBold = FontFamily(Font(R.font.almendra_italic))
            Text(
                instructionText,
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontFamily = tangerineBold,
                    fontSize = 32.sp
                ),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Scrollable deck with arrows
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        ) {
            val canScrollBackward by remember {
                derivedStateOf { lazyListState.firstVisibleItemIndex > 0 }
            }
            val canScrollForward by remember {
                derivedStateOf { lazyListState.firstVisibleItemIndex < allCards.size - 1 }
            }

            // Left arrow
            if (canScrollBackward) {
                Image(
                    painter = painterResource(id = R.drawable.chevron_left),
                    contentDescription = stringResource(R.string.left_arrow),
                    colorFilter = ColorFilter.tint(Color.White),
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .size(64.dp)
                        .padding(start = 16.dp)
                        .zIndex(2f)
                )
            }

            // Cards
            LazyRow(
                state = lazyListState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 48.dp)
            ) {
                itemsIndexed(allCards) { index, card ->
                    if (card !in pickedCards.map { it.card }) {
                        val offset by animateDpAsState(
                            if (pickedCards.size < 3) 0.dp else 1000.dp
                        )
                        Image(
                            painter = painterResource(id = R.drawable.card_back1),
                            contentDescription = "Card back",
                            modifier = Modifier
                                .size(80.dp, 120.dp)
                                .offset(x = offset)
                                .padding(horizontal = 4.dp)
                                .clickable { onCardPicked() }
                        )
                    }
                }
            }

            // Right arrow
            if (canScrollForward) {
                Image(
                    painter = painterResource(id = R.drawable.chevron_right),
                    contentDescription = stringResource(R.string.right_arrow),
                    colorFilter = ColorFilter.tint(Color.White),
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(64.dp)
                        .padding(end = 16.dp)
                        .zIndex(2f)
                )
            }
        }
    }
}

@Composable
fun SpreadExplanationContent(pickedCards: List<CardOfDay>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            stringResource(R.string.your_3_card_spread),
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            itemsIndexed(pickedCards) { index, cardOfDay ->
                val position = when (index) {
                    0 -> stringResource(R.string.past)
                    1 -> stringResource(R.string.present)
                    2 -> stringResource(R.string.future)
                    else -> ""
                }

                Text(
                    position,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )

                Text(
                    "${cardOfDay.card.name} (${if (cardOfDay.isUpright) stringResource(R.string.upright) else stringResource(R.string.reversed)})",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    if (cardOfDay.isUpright) cardOfDay.card.uprightMeanings else cardOfDay.card.reversedMeanings,
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }
    }
}

// Helper function to get the resource ID for a card image
fun getCardResourceId(imageName: String): Int {
    return R.drawable::class.java.getField(imageName).getInt(null)
}