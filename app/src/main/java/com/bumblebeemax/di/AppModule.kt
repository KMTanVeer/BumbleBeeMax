package com.bumblebeemax.di

import android.content.Context
import androidx.room.Room
import androidx.work.WorkManager
import com.bumblebeemax.data.local.AppDatabase
import com.bumblebeemax.data.local.dao.*
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext ctx: Context): AppDatabase =
        Room.databaseBuilder(ctx, AppDatabase::class.java, "bumblebee.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides fun provideBatteryDao(db: AppDatabase): BatteryDao = db.batteryDao()
    @Provides fun provideLocationDao(db: AppDatabase): LocationDao = db.locationDao()
    @Provides fun provideAppUsageDao(db: AppDatabase): AppUsageDao = db.appUsageDao()
    @Provides fun provideGeofenceDao(db: AppDatabase): GeofenceDao = db.geofenceDao()
    @Provides fun provideHabitLogDao(db: AppDatabase): HabitLogDao = db.habitLogDao()
    @Provides fun provideCompanionEventDao(db: AppDatabase): CompanionEventDao = db.companionEventDao()

    @Singleton
    @Provides
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Singleton
    @Provides
    fun provideWorkManager(@ApplicationContext ctx: Context): WorkManager =
        WorkManager.getInstance(ctx)
}
