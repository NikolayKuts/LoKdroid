package com.lib.lokdroid.core

import com.lib.lokdroid.data.default_implementation.FormatterBuilder
import com.lib.lokdroid.domain.model.Level
import kotlin.test.BeforeTest
import kotlin.test.Test

class AndroidCallSiteResolutionTest {

    @BeforeTest
    fun setUp() {
        LoKdroid.initialize(
            logger = { level, tag, message ->
                println("$tag\t[$level] ${level.toSquare()} $message")
            },
            formatter = FormatterBuilder()
                .withPointer()
                .space()
                .withLineReference()
                .space()
                .message()
                .build()
        )
    }

    @Test
    fun `prints companion accepted factory builder log`() {
        AndroidCompanionFactoryOwner.acceptFactory {
            log {
                "android companion accepted factory header"()
                "android companion accepted factory detail"(I)
            }
        }
    }

    @Test
    fun `prints top level android entry point log`() {
        androidTopLevelEntryPointLikeFunction()
    }

    @Test
    fun `prints user wrapper with log prefix`() {
        logUserDefinedAndroidWrapperFunction()
    }

    @Test
    fun `prints nested class plain log`() {
        AndroidNestedCaller().emitPlain()
    }

    @Test
    fun `prints enum plain log`() {
        AndroidEnumCaller.First.emitPlain()
    }

    @Test
    fun `prints singleton plain log`() {
        AndroidSingletonCaller.emitPlain()
    }

    @Test
    fun `prints companion builder log`() {
        AndroidCompanionOwner.emitBuilder()
    }

    @Test
    fun `prints init block logs`() {
        AndroidInitBlockCaller()
    }

    @Test
    fun `prints extension builder log`() {
        AndroidExtensionReceiver().emitFromExtension()
    }
}

private fun androidTopLevelEntryPointLikeFunction() {
    log {
        "android top level entry point header"()
        "android top level entry point detail"(I)
    }
}

private fun logUserDefinedAndroidWrapperFunction() {
    log {
        "android user-defined wrapper with log prefix header"()
        "android user-defined wrapper with log prefix detail"(I)
    }
}

private class AndroidNestedCaller {
    fun emitPlain() {
        logD(message = "android nested class plain")
    }
}

private enum class AndroidEnumCaller {
    First,
    Second,
    ;

    fun emitPlain() {
        logV(message = "android enum $name plain")
    }
}

private object AndroidSingletonCaller {
    fun emitPlain() {
        logI(message = "android singleton plain")
    }
}

private class AndroidCompanionOwner {
    companion object {
        fun emitBuilder() {
            logD {
                "android companion builder header"()
                "android companion builder detail"(W)
            }
        }
    }
}

private object AndroidCompanionFactoryOwner {
    fun acceptFactory(block: () -> Unit) {
        block()
    }
}

private class AndroidInitBlockCaller {
    init {
        logI(message = "android init block plain")
        logI {
            "android init block builder header"()
            "android init block builder detail"(D)
        }
    }
}

private class AndroidExtensionReceiver

private fun AndroidExtensionReceiver.emitFromExtension() {
    log {
        "android extension builder header"()
        "android extension builder detail"(E)
    }
}

private fun Level.toSquare(): String = when (this) {
    Level.Verbose -> "⬜"
    Level.Debug -> "🟦"
    Level.Info -> "🟩"
    Level.Warn -> "🟨"
    Level.Error -> "🟥"
}
