package com.lib.lokdroid.data.default_implementation.logger.txt

import com.lib.lokdroid.data.default_implementation.formatDate
import com.lib.lokdroid.domain.Logger
import com.lib.lokdroid.domain.model.Level
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException

/**
 * A [Logger] implementation that writes log messages to a text file.
 *
 * @property logFile The file where log messages will be written.
 */
internal class TxtLogger(private val logFile: File) : Logger {

    override fun log(level: Level, tag: String, message: String) {
        try {
            val date = formatDate(timestamp = System.currentTimeMillis())
            val fullMessage = "$date $level $tag $message"
            val bufferWriter = BufferedWriter(FileWriter(logFile, true))

            bufferWriter.use { buf ->
                buf.append(fullMessage)
                buf.newLine()
                buf.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}