package com.github.decentralized_hse.messenger_http_transport.client

import com.github.decentralized_hse.messenger_http_transport.api2ch.CaptchaHelper

suspend fun main(args: Array<String>) {
    val captchaHelper = CaptchaHelper()
    println(captchaHelper.getCaptcha())
}