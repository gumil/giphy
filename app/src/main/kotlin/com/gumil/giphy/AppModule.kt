package com.gumil.giphy

import android.app.Application
import com.gumil.giphy.network.createGiphyRepository
import com.gumil.giphy.network.repository.Repository
import com.gumil.giphy.util.Cache
import com.gumil.giphy.util.DiskCache
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
internal object AppModule {

    @Provides
    @Singleton
    fun provideRepository(): Repository = createGiphyRepository(BuildConfig.DEBUG, BuildConfig.API_KEY)

    @Provides
    @Singleton
    fun provideCache(application: Application): Cache = DiskCache(application)

    @Provides
    @Singleton
    fun ioDispatcher(): CoroutineDispatcher = Dispatchers.IO
}
