package com.lib.lokdroid.data.default_implementation.logger

import com.lib.lokdroid.data.default_implementation.formatDate
import com.lib.lokdroid.domain.Logger
import com.lib.lokdroid.domain.model.Level
import io.ktor.client.HttpClient
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * A singleton implementation of the [Logger] interface that sends log messages to a remote server.
 *
 * This logger uses Ktor for HTTP requests and Kotlin coroutines for asynchronous operations.
 * It posts log messages to a specified base URL.
 *
 * @property baseUrl The base URL to which log messages will be sent.
 */

class RemoteLogger private constructor(
    private val baseUrl: String,
) : Logger {

    companion object {

        private var instance: Logger? = null
        private val lock = Any()

        /**
         * Returns the singleton instance of [RemoteLogger].
         *
         * This method ensures thread-safe lazy initialization of the instance.
         *
         * @param url The base URL to which log messages will be sent.
         * @return The singleton instance of [RemoteLogger].
         */

        fun getInstance(url: String): Logger = synchronized(lock) {
            instance ?: RemoteLogger(baseUrl = url).also { instance = it }
        }
    }

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val client = HttpClient {
        defaultRequest { url(urlString = baseUrl) }
    }

    /**
     * Logs a message with the specified log level and tag.
     *
     * This method sends the log message to the remote server asynchronously.
     *
     * @param level The log level of the message.
     * @param tag A tag associated with the log message.
     * @param message The log message to be logged.
     */

    override fun log(level: Level, tag: String, message: String) {
        sendKtorPostRequest(level = level, tag = tag, message = message)
    }

    /**
     * Sends a POST request with the log message to the remote server.
     *
     * This method constructs the request body and sends it asynchronously using Ktor.
     *
     * @param level The log level of the message.
     * @param tag A tag associated with the log message.
     * @param message The log message to be logged.
     */

    private fun sendKtorPostRequest(level: Level, tag: String, message: String) {
        scope.launch {
            val requestBody = createRequestBody(level = level, tag = tag, message = message)

            client.post { setBody(requestBody) }
        }
    }

    /**
     * Creates the request body for the log message.
     *
     * This method formats the current timestamp and constructs the request body string.
     *
     * @param level The log level of the message.
     * @param tag A tag associated with the log message.
     * @param message The log message to be logged.
     * @return The formatted request body string.
     */

    private fun createRequestBody(
        level: Level,
        tag: String,
        message: String
    ): String {
        val formattedTimeStamp = formatDate(timestamp = System.currentTimeMillis())
        return "$formattedTimeStamp $level $tag $message"
    }
}