package com.github.decentralized_hse.messenger_http_transport.sender

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.decentralized_hse.messenger_http_transport.sendbin.Sendbin
import com.github.decentralized_hse.messenger_http_transport.sendbin.UserInfo
import io.ktor.client.engine.cio.*
import kotlinx.coroutines.runBlocking

class Sender : CliktCommand(printHelpOnEmptyArgs = true) {
    private val devKey by option(
        "-d", "--dev-key",
        help = "The pastebin dev key for API",
        envvar = "SENDBIN_DEV_KEY"
    ).required()
    private val login by option(
        "-l", "--login",
        help = "The pastebin login of the user to whom the message is sent",
        envvar = "SENDBIN_LOGIN"
    ).required()
    private val password by option(
        "-p", "--password",
        help = "The pastebin password of the user to whom the message is sent",
        envvar = "SENDBIN_PASSWORD"
    ).required()
    private val from by option(
        "-f",
        "--from",
        help = "Meta field indicating the sender of the message",
        envvar = "SENDBIN_FROM"
    ).required()
    private val payload by option(
        "-pl",
        "--payload",
        help = "The message payload",
        envvar = "SENDBIN_PAYLOAD"
    ).required()

    override fun run() {
        runBlocking {
            val sendbin = Sendbin(devKey, CIO.create())
            println(sendbin.send(Sendbin.Message("chopik", "Hello"), sendbin.getUserKey(UserInfo(login, password))))
        }
    }
}

fun main(args: Array<String>) = Sender().main(args)