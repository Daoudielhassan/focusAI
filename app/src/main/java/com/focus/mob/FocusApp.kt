package com.focus.mob

import android.app.Application
import com.focus.mob.BuildConfig
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.focus.mob.worker.FocusReminderWorker
import com.focus.mob.utils.NotificationHelper
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class FocusApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Timber: only log in debug builds
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        NotificationHelper.createAllChannels(this)
        scheduleFocusReminder()
        
        Timber.i("FocusAI started ⚡")
    }

    private fun scheduleFocusReminder() {
        val workRequest = PeriodicWorkRequestBuilder<FocusReminderWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(12, TimeUnit.HOURS)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "focus_reminder",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}
