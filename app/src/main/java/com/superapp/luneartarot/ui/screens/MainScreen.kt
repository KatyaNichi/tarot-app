package com.superapp.luneartarot.ui.screens

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.superapp.luneartarot.R
import com.superapp.luneartarot.data.CardOfDay
import com.superapp.luneartarot.data.CardRepository
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.Font

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(
    onCardOfDayClick: (CardOfDay) -> Unit,
    cardRepository: CardRepository
) {

    val almendraFont = FontFamily(
        Font(R.font.almendra_bold, FontWeight.Bold)
    )
    val almendraFontItalic = FontFamily(
        Font(R.font.almendra_italic, FontWeight.Bold)
    )
    var cardOfDay by remember { mutableStateOf<CardOfDay?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                cardOfDay = cardRepository.getCardOfDay()
                //cardOfDay = cardRepository.getRandomCard()
                isLoading = false
            } catch (e: Exception) {
                errorMessage = "Error loading card: ${e.message}"
                isLoading = false
            }
        }
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
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = stringResource(id = R.string.welcome_message),
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontFamily = almendraFont,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            )
            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontFamily = almendraFontItalic,
                    fontSize = 40.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            )

            cardOfDay?.let { cardOfDayInfo ->
                Text(
                    text = stringResource(id = R.string.card_of_the_day),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontFamily = almendraFont,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.padding(top = 28.dp, bottom = 12.dp)
                )
                val imageResId = context.resources.getIdentifier(cardOfDayInfo.card.imageName, "drawable", context.packageName)
                Image(
                    painter = painterResource(id = imageResId),
                    contentDescription = cardOfDayInfo.card.name,
                    modifier = Modifier
                        .size(250.dp)
                        .rotate(if (cardOfDayInfo.isUpright) 0f else 180f)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() }, // Manage interactions
                            indication = null // Disable ripple effect
                        ) {
                            onCardOfDayClick(cardOfDayInfo)
                        }
                )

                Text(
                    cardOfDayInfo.card.name,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontFamily = almendraFont,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.padding(top = 16.dp)
                )
                Text(
                    text = stringResource(id = R.string.tap_card_instruction),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.White,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.padding(top = 16.dp)
                )
            } ?: CircularProgressIndicator(color = Color.White)
        }
    }
}