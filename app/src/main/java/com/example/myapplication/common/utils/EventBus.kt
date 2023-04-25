package com.example.myapplication.common.utils

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consume
import kotlinx.coroutines.channels.consumeEach
import java.util.*


@OptIn(ExperimentalCoroutinesApi::class)
object EventBus {

    val channel = BroadcastChannel<Any>(1)

    fun publish(event: Any) = GlobalScope.launch { channel.send(event) }

    inline fun <reified TYPE> subscribe(
        alSubscription: ArrayList<ReceiveChannel<*>>,
        crossinline block: (TYPE) -> Unit
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            channel.openSubscription().consume {
                this.consumeEach {
                    alSubscription.add(this)
                    if(it is TYPE) block(it as TYPE)
                }
            }
        }
    }
}