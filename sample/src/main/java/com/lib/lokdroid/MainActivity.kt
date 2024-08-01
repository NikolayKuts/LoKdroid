package com.lib.lokdroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.lib.lokdroid.core.LoKdroid
import com.lib.lokdroid.core.log
import com.lib.lokdroid.core.logD
import com.lib.lokdroid.core.logE
import com.lib.lokdroid.core.logI
import com.lib.lokdroid.core.logV
import com.lib.lokdroid.core.logW
import com.lib.lokdroid.data.default_implementation.logger.ConsoleAndFileLogger
import com.lib.lokdroid.data.default_implementation.logger.FileLogger
import com.lib.lokdroid.data.default_implementation.logger.RemoteLogger
import com.lib.lokdroid.domain.LogBuilder
import com.lib.lokdroid.domain.model.Level
import com.lib.lokdroid.ui.theme.LoKdroidTheme


class MainActivity : ComponentActivity() {

    private val data = listOf(
        "first",
        "second",
        "third",
        "forth",
        "fifth",
        "sixth",
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LoKdroid.initialize(
            /** uncomment this to check logging to file */
//            logger = FileLogger(context = application)
            /** uncomment this to check logging to file and console */
//            logger = ConsoleAndFileLogger(fileLogger = FileLogger(context = application))
            /** uncomment this to check logging to remote */
//            logger = RemoteLogger.getInstance(url = "https://echo.free.beeceptor.com")
        )

        /** custom implementation */
//        LoKdroid.initialize(
//            minLevel = Level.Debug,
//            logger = { level: Level, tag: String, message: String -> /** your logic */ },
//            formatter = { message -> "return formatted message: $message" },
//            tag = "custom tag",
//            logBuilderProvider = {
//                /** provide your custom LogBuilder */
//                object : LogBuilder {
//
//                    override fun build(): String {
//                        return "build your string"
//                    }
//
//                    override fun message(value: Any) {
//                        /** use this block to build multiple log */
//                    }
//                }
//            }
//        )

        setContent {
            LoKdroidTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    MainScreen(
                        data = data,
                        onVerboseClick = { logV(message = it) },
                        onDebugClick = { logD(message = it) },
                        onInfoClick = { logI(message = it) },
                        onWarnClick = { logW(message = it) },
                        onErrorClick = { logE(message = it) },
                        onMultipleVerboseLogClick = { invokeMultipleLog(level = Level.Verbose) },
                        onMultipleDebugLogClick = { invokeMultipleLog(level = Level.Debug) },
                        onMultipleInfoLogClick = { invokeMultipleLog(level = Level.Info) },
                        onMultipleWarnLogClick = { invokeMultipleLog(level = Level.Warn) },
                        onMultipleErrorLogClick = { invokeMultipleLog(level = Level.Error) },
                    )
                }
            }
        }
    }

    private fun invokeMultipleLog(level: Level) {
        log(level = level) {
            message("Multiple log")
            data.forEach { message(value = it) }
        }
    }
}