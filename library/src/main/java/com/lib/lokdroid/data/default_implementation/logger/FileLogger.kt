package com.lib.lokdroid.data.default_implementation.logger

import com.lib.lokdroid.data.default_implementation.formatDate
import com.lib.lokdroid.data.default_implementation.logger.FileLogger.Companion.DEFAULT_FILE_NAME
import com.lib.lokdroid.domain.Logger
import com.lib.lokdroid.domain.model.Level
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException

/**
 * A file-based logger that writes log messages to a specified file in an asynchronous manner.
 * This logger appends each log entry to the end of the file, formatting each entry with a timestamp,
 * log level, and tag.
 *
 * @param filePath The directory path where the log file will be created or exists.
 * @param fileName The name of the file to write logs to. Defaults to [DEFAULT_FILE_NAME].
 */

class FileLogger(filePath: String, fileName: String = DEFAULT_FILE_NAME) : Logger {

    companion object {

        private const val FILE_NAME_DATE_PATTERN = "dd.MM.yyyy_HH:mm:ss"
        private const val DEFAULT_FILE_NAME = "lokdroid"
    }

    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val mutex: Mutex = Mutex()
    private val file = File(buildFileName(filePath = filePath, fileName = fileName))

    /**
     * Logs a message with the specified level, tag, and content by writing it to a log file.
     * The operation is performed asynchronously to avoid blocking the caller's thread.
     *
     * @param level The severity level of the log message.
     * @param tag The tag associated with the log message.
     * @param message The message content to log.
     */

    override fun log(
        level: Level,
        tag: String,
        message: String,
    ) {
        logToFile(level = level, tag = tag, message = message)
    }

    /**
     * Logs a message to a file asynchronously, ensuring that the logging process does not block the main thread.
     * This method formats the message, attaches a timestamp and the log level, and writes to the log file, ensuring thread safety with a mutex.
     *
     * @param level The severity level of the log.
     * @param tag A string label used to identify the source of the log.
     * @param message The actual log message to be written to the file.
     */

    private fun logToFile(level: Level, tag: String, message: String) {
        scope.launch {
            createFileIfNeeded(file = file)

            try {
                val date = formatDate(timestamp = System.currentTimeMillis())
                val fullMessage = "$date $level $tag $message"
                val bufferWriter = BufferedWriter(FileWriter(file, true))

                mutex.withLock {
                    bufferWriter.use { buf ->
                        buf.append(fullMessage)
                        buf.newLine()
                        buf.close()
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Constructs a file name based on the provided path and the current date and time.
     * This method ensures that each log file is uniquely identified by its timestamp.
     *
     * @param filePath The path where the log file will be stored.
     * @param fileName The base name for the log file, which will be appended with a timestamp.
     * @return The fully constructed file name.
     */

    private fun buildFileName(filePath: String, fileName: String): String {
        val date = formatDate(
            timestamp = System.currentTimeMillis(),
            pattern = FILE_NAME_DATE_PATTERN
        )

        return "$filePath/${fileName}_$date.txt"
    }

    /**
     * Ensures that the log file exists before writing to it.
     * If the file does not exist, it attempts to create a new file.
     *
     * @param file The file to check and create if it does not exist.
     */

    private fun createFileIfNeeded(file: File) {
        if (file.exists()) return

        try {
            file.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}