package com.gumil.giphy

import android.app.Application
import android.os.Build.VERSION.SDK_INT
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import dagger.hilt.DefineComponent
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
internal class App : Application(), ImageLoaderFactory {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            initializeDebugTools()
        }
    }

    private fun initializeDebugTools() {
        Timber.plant(Timber.DebugTree())
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .componentRegistry {
                if (SDK_INT >= 28) {
                    add(ImageDecoderDecoder())
                } else {
                    add(GifDecoder())
                }
            }
            .build()
    }
}
