package com.gumil.giphy.util

import android.content.Context
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

interface Cache {

    fun <T: Any> save(key: String, item: T)

    fun <T> get(key: String): T?
}

internal class DiskCache(
    private val context: Context
): Cache {

    override fun <T: Any> save(key: String, item: T) {
        ByteArrayOutputStream().use { stream ->
            ObjectOutputStream(stream).use {
                it.writeObject(item)
                it.flush()
            }

            context.openFileOutput(key, Context.MODE_PRIVATE).use {
                it.write(stream.toByteArray())
            }
        }
    }

    override fun <T> get(key: String): T? {
        return context.openFileInput(key).use { inputStream ->
            ByteArrayInputStream(inputStream.readBytes()).use { stream ->
                ObjectInputStream(stream).use {
                    try {
                        it.readObject() as T
                    } catch (e: ClassCastException) {
                        null
                    }
                }
            }
        }
    }
}