package com.github.decentralized_hse.messenger_http_transport.api2ch

import io.ktor.http.*

class Settings {
    companion object {
        val CHAN_PROTOCOL = URLProtocol.HTTPS
        const val CHAN_HOST = "2ch.hk"
    }
}