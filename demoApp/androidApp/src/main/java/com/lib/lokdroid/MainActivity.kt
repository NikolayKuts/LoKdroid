package com.lib.lokdroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.lib.lokdroid.core.LoKdroid
import com.lib.lokdroid.core.logI
import com.lib.lokdroid.core.logV
import com.lib.lokdroid.data.default_implementation.FormatterBuilder
import com.lib.lokdroid.demoapp.sharedui.SharedUiDemoApp
import kotlinx.coroutines.flow.flow


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LoKdroid.initialize(
            /** uncomment this to check logging to file */
//            logger = FileLogger(context = application)
            /** uncomment this to check logging to file and console */
//            logger = ConsoleAndFileLogger(fileLogger = FileLogger(context = application))
            /** uncomment this to check logging to remote */
//            logger = RemoteLogger.getInstance(url = "https://echo.free.beeceptor.com")
            /** uncomment this to check building Formatter */
//            formatter = FormatterBuilder()
//                .withPointer()
//                .space()
//                .withLineReference()
//                .space()
//                .message()
//                .space()
//                .custom(text = "some custom text")
//                .build()
        )

        logI(message = "some message")

        /** custom implementation */
//        LoKdroid.initialize(
//            minLevel = Level.Debug,
//            logger = { level: Level, tag: String, message: String -> /** your logic */ },
//            formatter = { message -> "return formatted message: $message" }, /** or use FormatterBuilder */
//            tagProvider = { "custom tag" },
//            messageBuilderFactory = {
//                /** provide your custom IMessageBuilder */
//                object : com.lib.lokdroid.domain.IMessageBuilder {
//
//                    override fun build(): String {
//                        return "build your string"
//                    }
//
//                    override operator fun String.invoke() {
//                        /** use this block to build multiple log lines */
//                    }
//                }
//            }
//        )

        logV {
            "some Error"(E)
            "some Info"(I)
            "some Debug"(D)
            "some Verbose"(V)
            "some Warn"(W)
        }

        setContent {
            SharedUiDemoApp()
        }

        flow {
            emit(1)
            emit(2)
            emit(3)
        }.collectWhenStarted(lifecycleOwner = this@MainActivity) {
            logV(it)
        }
    }
}
