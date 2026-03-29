package com.lib.lokdroid.model

/**
 * Supported file formats for Android file-based logging.
 *
 * @property value The file extension used for the stored log file.
 */
enum class FileFormat(val value: String) {
    /** Plain text log file format. */
    Txt(value = "txt"),
    /** Structured logcat-export format compatible with Android Studio log import. */
    Logcat(value = "logcat")
}
