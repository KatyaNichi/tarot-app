package com.superapp.luneartarot.data

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random
import org.json.JSONObject
import java.nio.charset.Charset

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "card_of_day")

data class TarotCard(
    val id: Int,
    val name: String,
    val arcana: String,
    val uprightMeanings: String,
    val reversedMeanings: String,
    val keywords: List<String>,
    val keywordsReversed: List<String>,
    val imageName: String,
    val element: String,
    val yesOrNo: String
)

fun loadTarotData(context: Context): List<TarotCard> {
    val currentLanguage = context.resources.configuration.locales[0].language
    val language = when (currentLanguage) {
        "ru", "sv" -> currentLanguage
        else -> "en"
    }

    val json = context.assets.open("tarot_data.json").bufferedReader().use { it.readText() }
    val jsonObject = JSONObject(json)
    val cardsArray = jsonObject.getJSONArray("cards")

    return List(cardsArray.length()) { i ->
        val card = cardsArray.getJSONObject(i)
        TarotCard(
            id = card.getInt("id"),
            name = card.getJSONObject("name").getString(language),
            arcana = card.getJSONObject("arcana").getString(language),
            uprightMeanings = card.getJSONObject("meaning").getString(language),
            reversedMeanings = card.getJSONObject("meaningReversed").getString(language),
            keywords = card.getJSONObject("keywords").getJSONArray(language).let {
                List(it.length()) { j -> it.getString(j) }
            },
            keywordsReversed = card.getJSONObject("keywordsReversed").getJSONArray(language).let {
                List(it.length()) { j -> it.getString(j) }
            },
            imageName = card.getString("imageName"),
            element = card.getJSONObject("element").getString(language),
            yesOrNo = card.getJSONObject("yesOrNo").getString(language)
        )
    }
}
data class CardOfDay(
    val card: TarotCard,
    val isUpright: Boolean
)


class CardRepository(private val context: Context) {
    private val cardOfDayKey = stringPreferencesKey("card_of_day")
    private val cardOrientationKey = stringPreferencesKey("card_orientation")
    private val dateKey = stringPreferencesKey("date")
    private val cards = loadTarotData(context)

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Calendar.getInstance().time)
    }

    suspend fun getCardOfDay(): CardOfDay {
        val today = getCurrentDate()
        val storedDate = context.dataStore.data.map { it[dateKey] }.first()
        val storedCardId = context.dataStore.data.map { it[cardOfDayKey] }.first()
        val storedOrientation = context.dataStore.data.map { it[cardOrientationKey] }.first()

        return if (storedDate == today && storedCardId != null && storedOrientation != null) {
            CardOfDay(
                card = cards.first { it.id.toString() == storedCardId },
                isUpright = storedOrientation.toBoolean()
            )
        } else {
            val randomCard = cards.random()
            val isUpright = Random.nextBoolean()
            context.dataStore.edit { prefs ->
                prefs[dateKey] = today
                prefs[cardOfDayKey] = randomCard.id.toString()
                prefs[cardOrientationKey] = isUpright.toString()
            }
            CardOfDay(randomCard, isUpright)
        }
    }

    fun getCardById(id: Int): TarotCard {
        return cards.first { it.id == id }
    }
    fun getRandomCard(): CardOfDay {
        val randomCard = cards.random()
        val isUpright = Random.nextBoolean()
        Log.d("CardRepository", "Random card: ${randomCard.name}, isUpright: $isUpright")
        return CardOfDay(randomCard, isUpright)
    }
    fun getAllCards(): List<TarotCard> {
        return cards
    }
}