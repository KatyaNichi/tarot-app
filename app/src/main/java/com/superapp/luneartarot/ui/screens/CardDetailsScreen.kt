package com.superapp.luneartarot.ui.screens

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import com.superapp.luneartarot.R
import com.superapp.luneartarot.data.CardOfDay
import java.io.File
import java.io.FileOutputStream

@Composable
fun CardDetailScreen(cardOfDay: CardOfDay) {
    val context = LocalContext.current
    val imageResId = context.resources.getIdentifier(cardOfDay.card.imageName, "drawable", context.packageName)
    val isUpright = cardOfDay.isUpright
    val description = if (isUpright) cardOfDay.card.uprightDescription else cardOfDay.card.reversedDescription
    val meanings = if (isUpright) cardOfDay.card.uprightMeanings else cardOfDay.card.reversedMeanings

    Box(modifier = Modifier.fillMaxSize()) {
        // Background image
        Image(
            painter = painterResource(id = R.drawable.card_details_background),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
        Image(
            painter = painterResource(id = imageResId),
            contentDescription = cardOfDay.card.name,
            modifier = Modifier
                .size(400.dp)
                .padding(bottom = 16.dp)
                .rotate(if (isUpright) 0f else 180f)
        )

        Text(
            text = cardOfDay.card.name,
            color = Color.White,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = if (isUpright) "Upright" else "Reversed",
            color = Color.White,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0x80000000))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = description,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text(
                    text = "Key Meanings:",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                meanings.forEach { meaning ->
                    Text(
                        text = "• $meaning",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
            }
        }
    }
        IconButton(
            onClick = {
                Log.d("CardDetailScreen", "Share icon clicked")
                try {
                    shareCard(context, cardOfDay)
                } catch (e: Exception) {
                    Log.e("CardDetailScreen", "Error calling shareCard", e)
                }
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Share,
                contentDescription = "Share",
                tint = Color.White
            )
        }
    }
}

fun shareCard(context: Context, cardOfDay: CardOfDay) {
    val isUpright = cardOfDay.isUpright
    val orientation = if (isUpright) "Upright" else "Reversed"
    val shareText = buildString {
        appendLine("My Tarot Card of the Day:")
        appendLine("${cardOfDay.card.name} ($orientation)")
        appendLine()
        appendLine(if (isUpright) cardOfDay.card.uprightDescription else cardOfDay.card.reversedDescription)
        appendLine()
        appendLine("Key Meanings:")
        (if (isUpright) cardOfDay.card.uprightMeanings else cardOfDay.card.reversedMeanings).forEach {
            appendLine("• $it")
        }
        appendLine()
        appendLine("Shared from Lunear Tarot App")
    }
    // Get the image resource
    val imageResId = context.resources.getIdentifier(cardOfDay.card.imageName, "drawable", context.packageName)
    val imageDrawable = ContextCompat.getDrawable(context, imageResId)
    val bitmap = (imageDrawable as BitmapDrawable).bitmap

    // Create a file to share
    val imagesFolder = File(context.cacheDir, "images")
    imagesFolder.mkdirs()
    val file = File(imagesFolder, "shared_image.png")
    val stream = FileOutputStream(file)
    bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
    stream.flush()
    stream.close()

    // Get the FileProvider URI
    val contentUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)


    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, shareText)
        putExtra(Intent.EXTRA_STREAM, contentUri)
        type = "image/*"
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    val shareIntent = Intent.createChooser(sendIntent, "Share Your Card of the Day")

    try {
        // Use context.startActivity() directly
        context.startActivity(shareIntent)
    } catch (e: Exception) {
        Log.e("ShareCard", "Error starting share intent", e)
        Toast.makeText(context, "Error sharing the card", Toast.LENGTH_LONG).show()
    }

    // Always show a Toast for debugging
    Toast.makeText(context, "Sharing: ${cardOfDay.card.name} ($orientation)", Toast.LENGTH_SHORT).show()
}

