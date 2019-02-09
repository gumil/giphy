package com.gumil.giphy.util

import java.lang.ClassCastException

interface Cache {

    fun <T: Any> save(key: String, item: T)

    fun <T> get(key: String): T?
}

internal class InMemoryCache: Cache {

    private val cache = mutableMapOf<String, Any>()

    override fun <T: Any> save(key: String, item: T) {
        cache[key] = item
    }

    override fun <T> get(key: String): T? {
        return try {
            cache[key] as T
        } catch (e: ClassCastException) {
            null
        }
    }
}