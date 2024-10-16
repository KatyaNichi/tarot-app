package com.superapp.luneartarot.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.superapp.luneartarot.R
import com.superapp.luneartarot.MainActivity
import com.superapp.luneartarot.data.CardRepository
import com.superapp.luneartarot.data.local.PreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import java.util.*

class DailyCardNotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val cardRepository = CardRepository(context)
    private val preferencesManager = PreferencesManager(context)

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            Log.d("DailyCardNotificationWorker", "Worker is executing")
            val isNotificationEnabled = preferencesManager.isNotificationEnabled.first()
            if (isNotificationEnabled) {
                val cardOfDay = cardRepository.getCardOfDay()
                showNotification(applicationContext, cardOfDay.card.name)
            } else {
                Log.d("DailyCardNotificationWorker", "Notifications are disabled")
            }
            Result.success()
        } catch (e: Exception) {
            Log.e("DailyCardNotificationWorker", "Error in work: ${e.message}")
            Result.failure()
        }
    }

    companion object {
        private const val CHANNEL_ID = "daily_card_channel"
        private const val NOTIFICATION_ID = 1
        private const val WORK_NAME = "daily_card_notification"

        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .build()

            // Calculate the delay to 9:00 AM the next day
            val currentTime = System.currentTimeMillis()
            val calendar = Calendar.getInstance().apply {
                if (get(Calendar.HOUR_OF_DAY) >= 9) {
                    // If it's already past 9 AM today, schedule for 9 AM tomorrow
                    add(Calendar.DAY_OF_YEAR, 1)
                }
                set(Calendar.HOUR_OF_DAY, 9)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }
            val initialDelay = calendar.timeInMillis - currentTime

            // Schedule the periodic work with the initial delay
            val dailyWorkRequest = PeriodicWorkRequestBuilder<DailyCardNotificationWorker>(
                1, TimeUnit.DAYS
            )
                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS) // Set initial delay to trigger at 9 AM
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                dailyWorkRequest
            )
        }



        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }

        private fun showNotification(context: Context, cardName: String) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    context.getString(R.string.daily_card_notification_title), // Use the string resource for channel name
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                notificationManager.createNotificationChannel(channel)
            }

            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            val notificationTitle = context.getString(R.string.daily_card_notification_title)
            // Create the notification content using the dynamic card name
            val notificationContent = context.getString(R.string.daily_card_notification_content, cardName)

            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.lunear_logot)
                .setContentTitle(notificationTitle)
                .setContentText(notificationContent) // Set the dynamic content here
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build()

            notificationManager.notify(NOTIFICATION_ID, notification)
        }

    }
}