package com.lib.lokdroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.lib.lokdroid.core.LoKdroid
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
import com.lib.lokdroid.navigation.AppNavGraph
import com.lib.lokdroid.navigation.Screen
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

        logI(message = "some message")

        /** custom implementation */
//        LoKdroid.initialize(
//            minLevel = Level.Debug,
//            logger = { level: Level, tag: String, message: String -> /** your logic */ },
//            formatter = { message -> "return formatted message: $message" },
//            tagProvider = { "custom tag" },
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
                    val navHostController = rememberNavController()
                    AppNavGraph(
                        navHostController = navHostController,
                        singleLogDemonstration = {
                            SingleLoggingScreen(
                                data = data,
                                toMultipleLoggingScreen = {
                                    navHostController.navigate(Screen.MultipleLogging())
                                }
                            )
                        },
                        multipleLogDemonstration = {
                            MultipleLoggingScreen(data = data)
                        }
                    )

                }
            }
        }
    }
}