package com.focus.mob.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.focus.mob.R
import com.focus.mob.ui.main.HomeActivity

object NotificationHelper {

    const val CHANNEL_REMINDER = "focus_reminder_channel"
    const val CHANNEL_SESSION  = "focus_session_channel"
    const val CHANNEL_GOAL     = "focus_goal_channel"

    private const val NOTIF_ID_SESSION = 1002
    private const val NOTIF_ID_GOAL    = 1003

    // ─── Crée tous les canaux de notification ─────────────────────
    fun createAllChannels(context: Context) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        listOf(
            NotificationChannel(
                CHANNEL_REMINDER,
                "Rappels quotidiens",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = "Rappel pour démarrer une session de concentration" },

            NotificationChannel(
                CHANNEL_SESSION,
                "Sessions terminées",
                NotificationManager.IMPORTANCE_HIGH
            ).apply { description = "Notification de fin de session de concentration" },

            NotificationChannel(
                CHANNEL_GOAL,
                "Objectif atteint",
                NotificationManager.IMPORTANCE_HIGH
            ).apply { description = "Vous avez atteint votre objectif quotidien de concentration" },
        ).forEach { nm.createNotificationChannel(it) }
    }

    // ─── Vérifie si l'utilisateur a activé les notifications ──────
    fun areNotificationsEnabled(context: Context): Boolean {
        val prefs = context.getSharedPreferences("LuminaPrefs", Context.MODE_PRIVATE)
        return prefs.getBoolean("smart_notifications", true)
    }

    // ─── Notif : session complète ──────────────────────────────────
    fun sendSessionCompleteNotification(context: Context, minutes: Int) {
        if (!areNotificationsEnabled(context)) return

        val intent = Intent(context, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_SESSION)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Session terminée 🎉")
            .setContentText("Bravo ! Vous avez complété $minutes minutes de concentration.")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Bravo ! Vous venez de terminer $minutes minutes de concentration intense. Lumina est fière de vous ✨")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(NOTIF_ID_SESSION, notification)
    }

    // ─── Notif : objectif quotidien atteint ───────────────────────
    fun sendDailyGoalReachedNotification(context: Context, goalHours: Int) {
        if (!areNotificationsEnabled(context)) return

        val intent = Intent(context, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 1, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_GOAL)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Objectif atteint ! ⭐")
            .setContentText("Vous avez atteint votre objectif de ${goalHours}h de concentration aujourd'hui !")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Incroyable ! Vous avez atteint votre objectif de ${goalHours}h de concentration aujourd'hui. Continuez comme ça ! 🚀")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(NOTIF_ID_GOAL, notification)
    }
}
