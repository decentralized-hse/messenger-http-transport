package com.github.decentralized_hse.messenger_http_transport.sender

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.decentralized_hse.messenger_http_transport.sendbin.Sendbin
import kotlinx.coroutines.runBlocking
import kotlin.system.exitProcess

class Sender : CliktCommand(printHelpOnEmptyArgs = true) {
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
    private val from by option(
        "-f",
        "--from",
        help = "Meta field indicating the sender of the message",
        envvar = "SENDBIN_FROM"
    ).required()

    override fun run() = runBlocking {
        val sendbin = Sendbin(devKey)
        while (true) {
            val payload = prompt("Message to send") ?: exitProcess(0)
            println(sendbin.send(Sendbin.Message(from, payload), userKey))
        }
    }
}

fun main(args: Array<String>) = Sender().main(args)