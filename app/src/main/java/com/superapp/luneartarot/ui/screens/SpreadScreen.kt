package com.superapp.luneartarot.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.superapp.luneartarot.data.CardRepository

@Composable
fun SpreadScreen(cardRepository: CardRepository) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Yes or No Screen")
        // TODO: Implement the Yes/No functionality here
    }
}