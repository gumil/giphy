package com.gumil.giphy

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

fun <T> Flow<T>.collectInTest(coroutineScope: CoroutineScope, action: (value: T) -> Unit): Job =
    onEach { action(it) }.launchIn(coroutineScope)
