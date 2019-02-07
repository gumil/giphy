package com.gumil.giphy

import android.app.Application
import com.gumil.giphy.detail.GiphyDetailViewModel
import com.gumil.giphy.list.GiphyListViewModel
import com.gumil.giphy.network.NAME_API_KEY
import com.gumil.giphy.network.createNetworkModule
import com.squareup.leakcanary.LeakCanary
import org.koin.core.context.startKoin
import org.koin.dsl.module
import timber.log.Timber

internal class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            modules(
                module {
                    single(NAME_API_KEY) { BuildConfig.API_KEY }
                },
                createNetworkModule(BuildConfig.DEBUG),
                GiphyListViewModel.createModule(),
                GiphyDetailViewModel.creatModule()
            )
        }

        if (BuildConfig.DEBUG) {
            initializeDebugTools()
        }
    }

    private fun initializeDebugTools() {
        Timber.plant(Timber.DebugTree())

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        LeakCanary.install(this)
    }
}