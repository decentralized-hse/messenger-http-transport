package com.github.decentralized_hse.messenger_http_transport.api2ch

import io.ktor.client.engine.mock.*
import io.ktor.http.*
import io.ktor.utils.io.*
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class CaptchaTest {

    companion object {
        const val CAPTCHA_ID = "7ca721f2b8a63310cc750c5b400ea548860bb04ec9a042ceadaeb8aa5e2cb2f2"
        const val CAPTCHA_TYPE = "2chcaptcha"
    }

    //    private val mockEngine = MockEngine { _ ->
//        respond(
//                content = ByteReadChannel(
//                        """{"id":"$CAPTCHA_ID","input":"numeric","result":1,"type":"$CAPTCHA_TYPE"}"""
//                ),
//                status = HttpStatusCode.OK,
//                headers = headersOf(
//                        HttpHeaders.ContentType,
//                        "application/json")
//        )
//    }
    private val mockEngine = MockEngine { request ->
        when (val path = request.url.pathSegments.joinToString("/")) {
            "api/captcha/2chcaptcha/id" -> respond(
                content = ByteReadChannel(
                    """{"id":"$CAPTCHA_ID","input":"numeric","result":1,"type":"$CAPTCHA_TYPE"}"""
                ),
                status = HttpStatusCode.OK,
                headers = headersOf(
                    HttpHeaders.ContentType,
                    "application/json"
                )
            )

            "/api/captcha/2chcaptcha/show" -> {
                val par = request.url.parameters
                if (par.contains("id") && par["id"] == CAPTCHA_ID) {
                    respond(
                        content = ByteReadChannel(
                            "<image>"
                        ),
                        status = HttpStatusCode.OK,
                        headers = headersOf(
                            HttpHeaders.ContentType,
                            "image/png"
                        )
                    )
                } else {
                    respond(
                        """{"status": "404", "message_error": "404"}""",
                        status = HttpStatusCode.NotFound,
                        headers = headersOf(
                            HttpHeaders.ContentType,
                            "application/json"
                        )
                    )
                }
            }

            else -> {
                respond(
                    """{"status": "400", "message_error": "$path"}""",
                    status = HttpStatusCode.BadRequest,
                    headers = headersOf(
                        HttpHeaders.ContentType,
                        "application/json"
                    )
                )
            }
        }
    }

    @Test
    fun getCaptchaTest() {
        runBlocking {
            val captchaHelper = CaptchaHelper(mockEngine)
            assertEquals(Captcha(CAPTCHA_TYPE, CAPTCHA_ID), captchaHelper.getCaptcha())
            captchaHelper.close()
        }
    }

    @Test
    fun getImageTest() {
        runBlocking {
            val captchaHelper = CaptchaHelper(mockEngine)
            val captcha = captchaHelper.getCaptcha()
            assertEquals(captcha.url().toString(), "https://2ch.hk/api/captcha/$CAPTCHA_TYPE/show?id=$CAPTCHA_ID")
            assertEquals(Captcha(CAPTCHA_TYPE, CAPTCHA_ID), captcha)
            assertEquals(captchaHelper.getImage(captcha).readText(), "<image>")
            captchaHelper.close()
        }
    }
}

