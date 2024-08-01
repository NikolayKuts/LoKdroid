package com.lib.lokdroid.data.default_implementation.logger.logcat

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Process
import com.lib.lokdroid.data.default_implementation.logger.logcat.entities.LogHeader
import com.lib.lokdroid.data.default_implementation.logger.logcat.entities.LogcatContent
import com.lib.lokdroid.data.default_implementation.logger.logcat.entities.LogcatMessage
import com.lib.lokdroid.data.default_implementation.logger.logcat.entities.Timestamp
import com.lib.lokdroid.domain.Logger
import com.lib.lokdroid.domain.model.Level
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.io.RandomAccessFile

/**
 * A [Logger] implementation that writes log messages to a file in a structured JSON format,
 * allowing import in new versions of Android Studio via the Logcat command-line tool
 *
 * @property logFile The file where log messages will be written.
 * @property context The context used to retrieve application-specific information.
 */
internal class LogcatLogger(private val logFile: File, private val context: Context) : Logger {

    companion object {

        /**
         * The mode used to access the log file.
         */
        private const val ACCESS_FILE_MODE = "rw"

        /**
         * The offset used for message insertion in the log file.
         */
        private const val MESSAGE_INSERTION_OFFSET = 6

        /**
         * The indentation used for pretty printing JSON.
         */
        private const val PRETTY_PRINT_INDENT = "  "
    }

    /**
     * The content of the logcat log.
     */
    private val logcatContent: LogcatContent = LogcatContent()

    /**
     * Flag indicating whether any message has been logged.
     */
    private var amyMessageLogged = false

    @OptIn(ExperimentalSerializationApi::class)
    private val json = Json {
        prettyPrint = true
        prettyPrintIndent = PRETTY_PRINT_INDENT
    }

    init {
        writeLogcatContentToFile(content = logcatContent)
    }

    /**
     * Logs a message with the specified [level], [tag], and [message].
     *
     * This function constructs a log header using the provided log level, tag, and message.
     * It gathers the process ID (PID), thread ID (TID), application ID, and process name,
     * along with the current timestamp. A logcat message is then created with this header
     * and the provided message. The logcat message is subsequently written to the log file.
     *
     * @param level The level of the log message.
     * @param tag The tag associated with the log message.
     * @param message The actual message to be logged.
     */
    override fun log(level: Level, tag: String, message: String) {
        val logHeader = LogHeader(
            logLevel = level.name.uppercase(),
            pid = Process.myPid(),
            tid = Process.myTid(),
            applicationId = context.packageName,
            processName = getProcessName(),
            tag = tag,
            timestamp = getTimestamp(currentTime = System.currentTimeMillis())
        )

        val logcatMessage = LogcatMessage(header = logHeader, message = message)

        writeNewMessage(logcatMessage = logcatMessage)
    }

    /**
     * Writes the initial logcat content to the file.
     *
     * @param content The initial content to write to the log file.
     */
    private fun writeLogcatContentToFile(content: LogcatContent) {
        try {
            FileWriter(logFile, false).use { writer ->
                writer.write(json.encodeToString<LogcatContent>(content))
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * Retrieves the process name of the current application.
     *
     * @return The process name of the current application.
     */
    private fun getProcessName(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Application.getProcessName()
        } else {
            val activityManager =
                context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val myPid = Process.myPid()

            return activityManager.runningAppProcesses
                .firstOrNull { processInfo -> processInfo.pid == myPid }
                ?.processName
                ?: "Process name not found"
        }
    }

    /**
     * Writes a new logcat message to the file.
     *
     * @param logcatMessage The logcat message to write to the file.
     */
    private fun writeNewMessage(logcatMessage: LogcatMessage) {
        try {
            if (amyMessageLogged) {
                RandomAccessFile(logFile, ACCESS_FILE_MODE).use { file ->
                    val content = ByteArray(file.length().toInt())

                    file.seek(0)
                    file.readFully(content)

                    val contentString = String(content)
                    val positionToInsert = contentString.lastIndex - MESSAGE_INSERTION_OFFSET
                    val formatedMessage = formatLogcatMessage(json.encodeToString(logcatMessage))

                    file.seek(positionToInsert.toLong())
                    file.writeBytes(formatedMessage)
                }
            } else {
                FileWriter(logFile, false).use { writer ->
                    val updatedMessages = logcatContent.logcatMessages.toMutableList()
                        .apply { add(logcatMessage) }
                    val updatedContent = logcatContent.copy(logcatMessages = updatedMessages)

                    writer.write(json.encodeToString(updatedContent))
                    amyMessageLogged = true
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * Retrieves the current timestamp.
     *
     * @param currentTime The current time in milliseconds.
     * @return The timestamp object.
     */
    private fun getTimestamp(currentTime: Long): Timestamp = Timestamp(
        seconds = currentTime / 1000,
        nanos = (currentTime % 1000).toInt() * 1000000
    )

    /**
     * Formats the logcat message with proper indentation.
     *
     * @param message The logcat message to format.
     * @return The formatted logcat message.
     */
    private fun formatLogcatMessage(message: String): String {
        val formatedMessage = message.lines().joinToString("\n") { "    $it" }
        return "},\n$formatedMessage\n  ]\n}"
    }
}