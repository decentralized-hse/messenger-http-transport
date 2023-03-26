package com.github.decentralized_hse.messenger_http_transport.listener

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.decentralized_hse.messenger_http_transport.sendbin.Sendbin
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class Listener : CliktCommand(printHelpOnEmptyArgs = true) {
    private val devKey by option(
        "-d", "--dev-key",
        help = "The pastebin dev key for API",
        envvar = "SENDBIN_DEV_KEY"
    ).required()
    private val userKey by option(
        "-u", "--user-key",
        help = "The pastebin user key for user pastes management",
        envvar = "SENDBIN_USER_KEY"
    ).required()

    override fun run() = runBlocking {
        val sendbin = Sendbin(devKey)
        sendbin.listen(userKey, Sendbin.Callbacks(
            { message ->
                println("From: ${message.from}, ${message.payload}")
            },
            {
                delay(10000)
            },
            { ex ->
                ex.printStackTrace()
            }
        ))
    }
}

fun main(args: Array<String>) = Listener().main(args)