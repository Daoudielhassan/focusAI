package com.focus.mob.di

import android.content.Context
import com.focus.mob.data.AppDatabase
import com.focus.mob.data.SessionDao
import com.focus.mob.network.RadioBrowserApi
import com.focus.mob.network.RetrofitClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    fun provideSessionDao(database: AppDatabase): SessionDao {
        return database.sessionDao()
    }

    @Provides
    @Singleton
    fun provideRadioBrowserApi(): RadioBrowserApi {
        return RetrofitClient.radioBrowserApi
    }
}
