package com.lib.lokdroid.data.default_implementation.logger

import android.util.Log
import com.lib.lokdroid.data.default_implementation.formatDate
import com.lib.lokdroid.domain.Logger
import com.lib.lokdroid.domain.model.Level
import io.ktor.client.HttpClient
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class RemoteLogger private constructor(
    private val baseUrl: String,
) : Logger {

    companion object {

        private var instance: Logger? = null
        private val lock = Any()

        fun getInstance(url: String): Logger = synchronized(lock) {
            instance ?: RemoteLogger(baseUrl = url).also { instance = it }
        }
    }

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val client = HttpClient {
        defaultRequest { url(urlString = baseUrl) }
    }

    override fun log(level: Level, tag: String, message: String) {
        sendKtorPostRequest(level = level, tag = tag, message = message)
    }

    private fun sendKtorPostRequest(level: Level, tag: String, message: String) {
        scope.launch {
            val requestBody = createRequestBody(level = level, tag = tag, message = message)

            // Only needed for demonstration
            val response = client.post { setBody(requestBody) }
            Log.i("", "*** response from Ktor-> ${response.bodyAsText()}")
        }
    }

    private fun createRequestBody(
        level: Level,
        tag: String,
        message: String
    ): String {
        val formattedTimeStamp = formatDate(timestamp = System.currentTimeMillis())
        return "$formattedTimeStamp $level $tag $message"
    }
}