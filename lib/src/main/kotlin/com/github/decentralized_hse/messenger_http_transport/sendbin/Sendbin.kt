package com.github.decentralized_hse.messenger_http_transport.sendbin

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.simpleframework.xml.Element
import org.simpleframework.xml.Root
import org.simpleframework.xml.Serializer
import org.simpleframework.xml.core.Persister

class Sendbin(
    private val devKey: String,
    private val engine: HttpClientEngine = CIO.create(),
    private val settings: PastebinSettings = PastebinSettings(),
    private val httpClientSettings: HttpClientConfig<*>.() -> Unit = {}
) {
    private val client = HttpClient(engine) {
        expectSuccess = true
        defaultRequest {
            url {
                protocol = settings.protocol
                host = settings.host
            }
            contentType(ContentType.Application.FormUrlEncoded)
        }
    }.apply { httpClientSettings }

    @Serializable
    data class Message(val from: String, val payload: String) {}

    companion object {
        const val title = "[THIS IS TITLE BY SENDBIN PROTOCOL DO NOT TOUCH THIS]"
    }

    suspend fun send(msg: Message, userKey: String): String {
        return client.post {
            url {
                path("api/api_post.php")
            }
            setBody(
                "api_dev_key=${devKey}&api_paste_code=${Json.encodeToString(msg)}&" + "api_paste_private=2&api_paste_name=${title}&api_paste_expire_date=10M&" + "api_user_key=${userKey}&api_option=paste"
            )
        }.body()
    }

    class Callbacks(
        val callback: suspend (Message) -> Unit,
        val fallback: suspend () -> Unit = {},
        val errors: suspend (Exception) -> Unit = {}
    ) {
    }

    suspend fun listen(
        userKey: String, callbacks: Callbacks
    ) {
        do try {
            val pastes = getPastes(userKey)

            for (paste in pastes) {
                val message = getPasteMessage(userKey, paste) ?: continue
                deletePaste(userKey, paste)
                callbacks.callback(message)
            }

            callbacks.fallback()
        } catch (ex: Exception) {
            callbacks.errors(ex)
        } while (true);
    }

    /* Only one key can be active at the same time for the same user. This key does not expire,
     * unless a new one is generated. We recommend creating just one, then caching that key locally
     * as it does not expire.
     */
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

    @Root(strict = false, name = "Paste")
    private data class Paste(
        @field:Element(name = "paste_key", required = false)
        val pasteKey: String?,
        @field:Element(name = "paste_date", required = false)
        val pasteDate: String?,
        @field:Element(name = "paste_title", required = false)
        val pasteTitle: String?,
        @field:Element(name = "paste_size", required = false)
        val pasteSize: String?,
        @field:Element(name = "paste_expire_date", required = false)
        val pasteExpireDate: String?,
        @field:Element(name = "paste_private", required = false)
        val pastePrivate: String?,
        @field:Element(name = "paste_format_long", required = false)
        val pastFormatLong: String?,
        @field:Element(name = "paste_format_short", required = false)
        val pasteFormatShort: String?,
        @field:Element(name = "paste_url", required = false)
        val pasteUrl: String?,
        @field:Element(name = "paste_hits", required = false)
        val pasteHits: String?
    ) {}


    private suspend fun getPastes(userKey: String): List<Paste> {
        val text: String = client.post {
            url {
                path("api/api_post.php")
            }
            setBody(
                "api_dev_key=${devKey}&api_user_key=${userKey}&api_option=list&api_results_limit=1000"
            )
        }.body()
        val paste = serializer.read(Paste::class.java, text)
        println(paste.pasteTitle)
        return listOf(paste)
    }

    private val serializer: Serializer = Persister()
    private suspend fun getPasteMessage(userKey: String, paste: Paste): Message? = try {
        if (paste.pasteTitle != title) {
            null
        }
        var resp: String = client.post {
            url {
                path("api/api_raw.php")
            }
            setBody(
                "api_dev_key=${devKey}&api_user_key=${userKey}&api_paste_key=${paste.pasteKey}"
            )
        }.body()
        var message = Json.decodeFromString<Message>(resp)
        message
    } catch (ex: Exception) {
        null
    }

    private suspend fun deletePaste(userKey: String, paste: Paste) {
        client.post {
            url {
                path("api/api_post.php")
            }
            setBody(
                "api_dev_key=${devKey}&api_paste_key=${paste.pasteKey}&api_user_key=${userKey}&api_option=delete"
            )
        }
    }
}