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
import org.simpleframework.xml.ElementList
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

    suspend fun sendMessages(userKey: String, msg: Message): String {
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
            callbacks.fallback()

            val pastes = getPastes(userKey)

            for (paste in pastes) {
                val message = getPasteMessage(userKey, paste) ?: continue
                deletePaste(userKey, paste)
                callbacks.callback(message)
            }
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

    private suspend fun getPasteMessage(userKey: String, paste: Paste): Message? = try {
        if (paste.title != title) {
            null
        }
        var resp: String = client.post {
            url {
                path("api/api_raw.php")
            }
            setBody(
                "api_dev_key=${devKey}&api_user_key=${userKey}&api_paste_key=${paste.key}&api_option=show_paste"
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
                "api_dev_key=${devKey}&api_paste_key=${paste.key}&api_user_key=${userKey}&api_option=delete"
            )
        }
    }

    @org.simpleframework.xml.Root(name = "root", strict = false)
    private class Root {
        @field:ElementList(name = "paste", required = false, inline = true)
        lateinit var pastes: List<Paste>
    }

    @org.simpleframework.xml.Root(name = "paste", strict = false)
    private class Paste {
        @field:Element(name = "paste_key", required = false)
        var key: String? = null

        @field:Element(name = "paste_date", required = false)
        var date: String? = null

        @field:Element(name = "paste_title", required = false)
        var title: String? = null
    }

    private val serializer: Serializer = Persister()

    private suspend fun getPastes(userKey: String): List<Paste> {
        var text: String = client.post {
            url {
                path("api/api_post.php")
            }
            setBody(
                "api_dev_key=${devKey}&api_user_key=${userKey}&api_option=list&api_results_limit=1000"
            )
        }.body()
        text = "<root>\n$text\n</root>"
        var pastes = serializer.read(Root::class.java, text)
        return pastes.pastes
    }
}