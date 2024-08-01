package com.lib.lokdroid.data.default_implementation.logger

import android.content.Context
import com.lib.lokdroid.data.default_implementation.formatDate
import com.lib.lokdroid.data.default_implementation.logger.FileLogger.Companion.DEFAULT_FILE_NAME
import com.lib.lokdroid.data.default_implementation.logger.logcat.LogcatLogger
import com.lib.lokdroid.data.default_implementation.logger.txt.TxtLogger
import com.lib.lokdroid.domain.Logger
import com.lib.lokdroid.domain.model.FileFormat
import com.lib.lokdroid.domain.model.Level
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File
import java.io.IOException

/**
 * A file-based logger that writes log messages to a specified file asynchronously.
 * This logger supports different file format.
 * Each entry is formatted with a timestamp, log level, and tag.
 *
 * @param context The context used to access the file directory and creating [LogcatLogger] instance.
 * @param filePath The directory path where the log file will be created or exists.
 * @param fileName The name of the file to write logs to. Defaults to [DEFAULT_FILE_NAME].
 * @param format The format in which logs will be written. Defaults to [FileFormat.Logcat].
 */
class FileLogger(
    context: Context,
    filePath: String = context.filesDir.path,
    fileName: String = DEFAULT_FILE_NAME,
    private val format: FileFormat = FileFormat.Logcat
) : Logger {

    companion object {

        private const val FILE_NAME_DATE_PATTERN = "dd.MM.yyyy_HH:mm:ss"
        private const val DEFAULT_FILE_NAME = "lokdroid"
    }

    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val mutex: Mutex = Mutex()
    private val builtFileName = buildFileName(
        filePath = filePath,
        fileName = fileName,
        fileFormat = format
    )
    private val file = File(builtFileName)
    private val txtLogger = TxtLogger(logFile = file)
    private val logcatLogger = LogcatLogger(logFile = file, context = context)

    init {
        createFileIfNeeded(file = file)
    }

    /**
     * Logs a message with the specified [level], [tag], and [message] by writing it to a log file.
     * The actual logging operation is delegated to a specific logger implementation based on the
     * [format] specified for this `FileLogger`. The logging operation is performed asynchronously
     * to avoid blocking the caller's thread.
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
        val logger = when (format) {
            FileFormat.Txt -> txtLogger
            FileFormat.Logcat -> logcatLogger
        }
        scope.launch {
            mutex.withLock {
                logger.log(level = level, tag = tag, message = message)
            }
        }
    }

    /**
     * Constructs a file name based on the provided path, base name, and the current date and time.
     * This method ensures that each log file is uniquely identified by its timestamp.
     *
     * @param filePath The path where the log file will be stored.
     * @param fileName The base name for the log file, which will be appended with a timestamp.
     * @param fileFormat The format in which logs will be written.
     * @return The fully constructed file name.
     */
    private fun buildFileName(
        filePath: String,
        fileName: String,
        fileFormat: FileFormat
    ): String {
        val date = formatDate(
            timestamp = System.currentTimeMillis(),
            pattern = FILE_NAME_DATE_PATTERN
        )

        return "$filePath/${fileName}_$date.${fileFormat.value}"
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