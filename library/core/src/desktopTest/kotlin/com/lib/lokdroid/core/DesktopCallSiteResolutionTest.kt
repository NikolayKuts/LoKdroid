package com.lib.lokdroid.core

import com.lib.lokdroid.data.default_implementation.FormatterBuilder
import kotlin.test.BeforeTest
import kotlin.test.Test

class DesktopCallSiteResolutionTest {

    @BeforeTest
    fun setUp() {
        LoKdroid.initialize(
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
        DesktopCompanionFactoryOwner.acceptFactory {
            log {
                "desktop companion accepted factory header"()
                "desktop companion accepted factory detail"(I)
            }
        }
    }

    @Test
    fun `prints top level desktop entry point log`() {
        desktopTopLevelEntryPointLikeFunction()
    }

    @Test
    fun `prints user wrapper with log prefix`() {
        logUserDefinedDesktopWrapperFunction()
    }

    @Test
    fun `prints nested class plain log`() {
        DesktopNestedCaller().emitPlain()
    }

    @Test
    fun `prints enum plain log`() {
        DesktopEnumCaller.First.emitPlain()
    }

    @Test
    fun `prints singleton plain log`() {
        DesktopSingletonCaller.emitPlain()
    }

    @Test
    fun `prints companion builder log`() {
        DesktopCompanionOwner.emitBuilder()
    }

    @Test
    fun `prints init block logs`() {
        DesktopInitBlockCaller()
    }

    @Test
    fun `prints extension builder log`() {
        DesktopExtensionReceiver().emitFromExtension()
    }
}

private fun desktopTopLevelEntryPointLikeFunction() {
    log {
        "desktop top level entry point header"()
        "desktop top level entry point detail"(I)
    }
}

private fun logUserDefinedDesktopWrapperFunction() {
    log {
        "desktop user-defined wrapper with log prefix header"()
        "desktop user-defined wrapper with log prefix detail"(I)
    }
}

private class DesktopNestedCaller {
    fun emitPlain() {
        logD(message = "desktop nested class plain")
    }
}

private enum class DesktopEnumCaller {
    First,
    Second,
    ;

    fun emitPlain() {
        logV(message = "desktop enum $name plain")
    }
}

private object DesktopSingletonCaller {
    fun emitPlain() {
        logI(message = "desktop singleton plain")
    }
}

private class DesktopCompanionOwner {
    companion object {
        fun emitBuilder() {
            logD {
                "desktop companion builder header"()
                "desktop companion builder detail"(W)
            }
        }
    }
}

private object DesktopCompanionFactoryOwner {
    fun acceptFactory(block: () -> Unit) {
        block()
    }
}

private class DesktopInitBlockCaller {
    init {
        logI(message = "desktop init block plain")
        logI {
            "desktop init block builder header"()
            "desktop init block builder detail"(D)
        }
    }
}

private class DesktopExtensionReceiver

private fun DesktopExtensionReceiver.emitFromExtension() {
    log {
        "desktop extension builder header"()
        "desktop extension builder detail"(E)
    }
}
