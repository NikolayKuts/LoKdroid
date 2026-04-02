package com.lib.lokdroid.core

import com.lib.lokdroid.data.default_implementation.FormatterBuilder
import kotlin.test.BeforeTest
import kotlin.test.Test
import platform.UIKit.UIViewController

class IosCallSiteResolutionTest {

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
        ObjcCompanionOwner.acceptFactory {
            log {
                "objc companion accepted factory header"()
                "objc companion accepted factory detail"(I)
            }
            UIViewController()
        }
    }

    @Test
    fun `prints top level UIViewController entry point log`() {
        iosAppEntryPointLikeFunction()
    }

    @Test
    fun `prints user wrapper with log prefix`() {
        logUserDefinedWrapperFunction()
    }

    @Test
    fun `prints nested class plain log`() {
        NestedCaller().emitPlain()
    }

    @Test
    fun `prints enum plain log`() {
        EnumCaller.First.emitPlain()
    }

    @Test
    fun `prints singleton plain log`() {
        SingletonCaller.emitPlain()
    }

    @Test
    fun `prints companion builder log`() {
        CompanionOwner.emitBuilder()
    }

    @Test
    fun `prints init block logs`() {
        InitBlockCaller()
    }
}
