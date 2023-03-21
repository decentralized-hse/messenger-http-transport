package com.github.decentralized_hse.messenger_http_transport.api2ch

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.*
import kotlinx.serialization.json.Json

data class Captcha(val type: String, val id: String) {
    var value: String = ""
}

class CaptchaHelper(engine: HttpClientEngine = CIO.create()) {
    private val client = HttpClient(engine) {
        expectSuccess = true
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }

    @Serializable
    private data class ChanCaptcha(val id: String, val input: String, val result: Long, val type: String)

    suspend fun getCaptcha(): Captcha {
        val chanCaptcha: ChanCaptcha = client.get {
            url {
                protocol = Settings.CHAN_PROTOCOL
                host = Settings.CHAN_HOST
                path("api/captcha/2chcaptcha/id")
            }
            contentType(ContentType.Application.Json)
        }.body()

        return Captcha(chanCaptcha.type, chanCaptcha.id)
    }
}