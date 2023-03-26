package com.github.decentralized_hse.messenger_http_transport.sendbin

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Sendbin(
    private val devKey: String,
    private val engine: HttpClientEngine = CIO.create(),
    private val settings: PastebinSettings = PastebinSettings()
) {
    private val client = HttpClient(engine) {
        expectSuccess = true
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.ALL
        }
        defaultRequest {
            url {
                protocol = settings.protocol
                host = settings.host
            }
            contentType(ContentType.Application.FormUrlEncoded)
        }

    }

    @Serializable
    data class Message(val from: String, val payload: String) {
    }

    companion object {
        const val title = "[THIS IS TITLE BY SENDBIN PROTOCOL DO NOT TOUCH THIS]"
    }

    suspend fun send(msg: Message, userKey: String): String {
        return client.post {
            url {
                path("api/api_post.php")
            }
            setBody(
                "api_dev_key=${devKey}&api_paste_code=${Json.encodeToString(msg)}&" +
                        "api_paste_private=2&api_paste_name=${title}&api_paste_expire_date=10M&" +
                        "api_user_key=${userKey}&api_option=paste"
            )
        }.body()
    }

//    fun listen(user: UserInfo, ctx: String, callback: (Message)) {
//        launch {
//            delay(100l)
//        }
//    }

    //  Only one key can be active at the same time for the same user. This key does not expire,
    //  unless a new one is generated. We recommend creating just one, then caching that key locally
    //  as it does not expire.
    suspend fun getUserKey(
        user: UserInfo
    ): String {
        return client.post {
            url {
                path("api/api_login.php")
            }
            setBody("api_dev_key=${devKey}&api_user_name=${user.login}&api_user_password=${user.password}")
        }.body()
    }
}