package com.gumil.giphy.network

import com.gumil.giphy.network.repository.GiphyRepository
import com.gumil.giphy.network.repository.Repository
import dagger.Component
import dagger.Module
import dagger.Provides
import org.koin.core.qualifier.named
import org.koin.dsl.module
import javax.inject.Scope

const val NAME_API_KEY = "apiKey"

fun createNetworkModule(isDebug: Boolean) = module {
    single<Repository> { GiphyRepository(ApiFactory.createGiphyApi(isDebug), get(named(NAME_API_KEY))) }
}

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class NetworkScope

@Module
internal class NetworkModule(
    private val apiKey: String,
    private val isDebug: Boolean
) {

    @Provides
    @NetworkScope
    fun provideRepository(): Repository = GiphyRepository(ApiFactory.createGiphyApi(isDebug), apiKey)
}

@NetworkScope
@Component(modules = [NetworkModule::class])
interface DataComponent {
    fun repository(): Repository
}

object DataDiBuilder {
    fun build(apiKey: String, isDebug: Boolean) = DaggerDataComponent.builder()
        .networkModule(NetworkModule(apiKey, isDebug))
        .build()
}