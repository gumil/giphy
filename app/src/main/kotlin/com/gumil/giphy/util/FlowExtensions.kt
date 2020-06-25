package com.gumil.giphy.util

import dev.gumil.kaskade.Action
import dev.gumil.kaskade.Kaskade
import dev.gumil.kaskade.State
import dev.gumil.kaskade.flow.SavedValueHolder
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * @return [StateFlow] of states from Kaskade.
 */
fun <A : Action, S : State> Kaskade<A, S>.stateFlow(initialState: S): StateFlow<S> =
    createStateFlow(MutableStateFlow(initialState))

/**
 * Creates a [DamStateFlow] that saves emissions.
 *
 * @return [StateFlow] of states from Kaskade.
 */
fun <A : Action, S : State> Kaskade<A, S>.stateDamFlow(initialState: S): StateFlow<S> =
    createStateFlow(DamStateFlow(initialState))

private fun <A : Action, S : State, F : MutableStateFlow<S>> Kaskade<A, S>.createStateFlow(
    state: F
): F {
    onStateChanged = {
        state.value = it
    }
    return state
}

/**
 * Dam version of [StateFlow].
 *
 * Emits only the last value in savedValueHolder. This is due to the limitation
 * of the StateFlow api.
 *
 * @see dev.gumil.kaskade.flow.DamEmitter
 */
class DamStateFlow<T: Any>(
    initialState: T
): MutableStateFlow<T> {

    private val stateFlow = MutableStateFlow(initialState)
    private val savedValueHolder = SavedValueHolder<T>()

    @InternalCoroutinesApi
    override suspend fun collect(collector: FlowCollector<T>) {
        savedValueHolder.savedValues.values.lastOrNull()?.let {
            stateFlow.value = it
        }
        stateFlow.collect(collector)
    }

    override var value: T
        get() = stateFlow.value
        set(value) {
            savedValueHolder.saveValue(value)
            stateFlow.value = value
        }
}
