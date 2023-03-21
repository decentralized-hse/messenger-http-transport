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

    private val mockEngine = MockEngine { _ ->
        respond(
                content = ByteReadChannel(
                        """{"id":"$CAPTCHA_ID","input":"numeric","result":1,"type":"$CAPTCHA_TYPE"}"""
                ),
                status = HttpStatusCode.OK,
                headers = headersOf(
                        HttpHeaders.ContentType,
                        "application/json")
        )
    }

    @Test
    fun getCaptchaTest() {
        runBlocking {
            val captchaHelper = CaptchaHelper(mockEngine)
            assertEquals(Captcha(CAPTCHA_TYPE, CAPTCHA_ID), captchaHelper.getCaptcha())
        }
    }
}