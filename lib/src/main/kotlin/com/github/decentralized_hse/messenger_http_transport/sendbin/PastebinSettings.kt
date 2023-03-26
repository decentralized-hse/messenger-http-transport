package com.github.decentralized_hse.messenger_http_transport.sendbin

import io.ktor.http.*

data class PastebinSettings(
    val protocol: URLProtocol = URLProtocol.HTTPS,
    val host: String = "pastebin.com",
) {
}