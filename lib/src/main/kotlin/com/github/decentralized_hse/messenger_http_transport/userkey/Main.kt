package com.github.decentralized_hse.messenger_http_transport.userkey

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.decentralized_hse.messenger_http_transport.sendbin.Sendbin
import com.github.decentralized_hse.messenger_http_transport.sendbin.UserInfo
import kotlinx.coroutines.runBlocking

class UserKey : CliktCommand(printHelpOnEmptyArgs = true) {
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

    override fun run() = runBlocking {
        val sendbin = Sendbin(devKey)
        val userKey = sendbin.getUserKey(UserInfo(login, password))
        echo("User key: $userKey")
    }
}

fun main(args: Array<String>) = UserKey().main(args)