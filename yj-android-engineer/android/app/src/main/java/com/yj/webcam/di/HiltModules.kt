package com.yj.webcam.di

import android.content.Context
import androidx.room.Room
import com.yj.webcam.core.dispatchers.DefaultDispatcherProvider
import com.yj.webcam.core.dispatchers.DispatcherProvider
import com.yj.webcam.data.db.AppDatabase
import com.yj.webcam.data.db.DeviceDao
import com.yj.webcam.data.db.LogDao
import com.yj.webcam.data.repository.AudioRepositoryImpl
import com.yj.webcam.data.repository.CameraRepositoryImpl
import com.yj.webcam.data.repository.DeviceRepositoryImpl
import com.yj.webcam.data.repository.SettingsRepositoryImpl
import com.yj.webcam.data.repository.StreamingRepositoryImpl
import com.yj.webcam.domain.repository.AudioRepository
import com.yj.webcam.domain.repository.CameraRepository
import com.yj.webcam.domain.repository.DeviceRepository
import com.yj.webcam.domain.repository.SettingsRepository
import com.yj.webcam.domain.repository.StreamingRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCameraRepository(impl: CameraRepositoryImpl): CameraRepository

    @Binds
    @Singleton
    abstract fun bindStreamingRepository(impl: StreamingRepositoryImpl): StreamingRepository

    @Binds
    @Singleton
    abstract fun bindDeviceRepository(impl: DeviceRepositoryImpl): DeviceRepository

    @Binds
    @Singleton
    abstract fun bindAudioRepository(impl: AudioRepositoryImpl): AudioRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindLutRepository(impl: com.yj.webcam.data.repository.LutRepositoryImpl): com.yj.webcam.domain.repository.LutRepository
}

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDispatcherProvider(): DispatcherProvider = DefaultDispatcherProvider()

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "yj_webcam.db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideDeviceDao(database: AppDatabase): DeviceDao = database.deviceDao()

    @Provides
    fun provideLogDao(database: AppDatabase): LogDao = database.logDao()
}
