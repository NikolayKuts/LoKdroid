package com.lib.lokdroid.core

import com.lib.lokdroid.data.default_implementation.FormatterBuilder
import com.lib.lokdroid.domain.model.Level
import kotlin.test.BeforeTest
import kotlin.test.Test
import platform.UIKit.UIViewController

/**
 * iOS smoke scaffold for caller-location coverage.
 *
 * These tests intentionally contain no assertions. Their only job is to exercise the logging API
 * from many Kotlin call shapes so future iterations can attach precise caller-info assertions on top.
 */
class IosLoggingCallSiteSmokeTest {

    @BeforeTest
    fun setUp() {
        LoKdroid.initialize(
            formatter = FormatterBuilder().withPointer()
                .space()
                .withLineReference()
                .space()
                .message()
                .build()
        )
    }

    @Test
    fun `logs from direct local and top level contexts`() {
        log(message = "direct generic call", level = Level.Info)
        logI(message = "direct shortcut call")

        fun localPlainFunction() {
            logW(message = "local function plain")
        }

        fun localBuilderFunction() {
            log(level = Level.Warn) {
                "local function builder header"()
                "local function builder detail"(E)
            }
        }

        fun String.emitFromLocalExtension() {
            log(message = "local extension plain $this", level = Level.Info)
            log(level = Level.Info) {
                "local extension builder $this"()
                "local extension builder detail $this"(W)
            }
        }

        val lambdaCaller = {
            logD(message = "lambda plain")
        }

        val builderLambdaCaller = {
            logI {
                "lambda builder header"()
                "lambda builder detail"(W)
            }
        }

        val anonymousFunctionCaller = fun() {
            logE(message = "anonymous function plain")
        }

        val nestedLambdaCaller = {
            {
                logV {
                    "nested lambda builder header"()
                    "nested lambda builder detail"(D)
                }
            }.invoke()
        }

        class LocalConstructorLambdaCaller(
            private val plainEmitter: () -> Unit,
            private val builderEmitter: () -> Unit,
        ) {
            fun emit() {
                plainEmitter()
                builderEmitter()
            }
        }

        val localAnonymousObject = object {
            fun emitPlain() {
                logI(message = "local anonymous object plain")
            }

            fun emitBuilder() {
                logI {
                    "local anonymous object builder header"()
                    "local anonymous object builder detail"(E)
                }
            }
        }

        localPlainFunction()
        localBuilderFunction()
        "local extension receiver".emitFromLocalExtension()
        lambdaCaller()
        builderLambdaCaller()
        anonymousFunctionCaller()
        nestedLambdaCaller()
        LocalConstructorLambdaCaller(
            plainEmitter = { logI(message = "local class constructor lambda plain") },
            builderEmitter = {
                log(level = Level.Debug) {
                    "local class constructor lambda builder header"()
                    "local class constructor lambda builder detail"(I)
                }
            },
        ).emit()
        localAnonymousObject.emitPlain()
        localAnonymousObject.emitBuilder()
        topLevelPlainFunction()
        topLevelBuilderFunction()
    }

    @Test
    fun `logs from nested classes inner classes anonymous objects and enums`() {
        NestedCaller().emitPlain()
        NestedCaller().emitBuilder()

        val outer = OuterCaller()
        outer.InnerCaller().emitPlain()
        outer.InnerCaller().emitBuilder()

        val anonymousEmitter = object : FixtureEmitter {
            override fun emitPlain() {
                logW(message = "anonymous interface object plain")
            }

            override fun emitBuilder() {
                logW {
                    "anonymous interface object builder header"()
                    "anonymous interface object builder detail"(D)
                }
            }
        }

        val localAnonymousObject = object {
            fun emit() {
                log(message = "anonymous object generic", level = Level.Info)
            }
        }

        anonymousEmitter.emitPlain()
        anonymousEmitter.emitBuilder()
        localAnonymousObject.emit()
        EnumCaller.First.emitPlain()
        EnumCaller.Second.emitBuilder()
    }

    @Test
    fun `logs from singleton companion and factory style entry points`() {
        SingletonCaller.emitPlain()
        SingletonCaller.emitBuilder()
        SingletonCaller.acceptLambda {
            logI(message = "singleton accepted lambda plain")
        }
        SingletonCaller.acceptLambda {
            log(level = Level.Error) {
                "singleton accepted lambda builder header"()
                "singleton accepted lambda builder detail"(W)
            }
        }
        SingletonCaller.withReturnedLambda().invoke()
        SingletonCaller.withReturnedBuilderLambda().invoke()
        SingletonCaller.createConstructorLambdaCaller(
            plainEmitter = { logW(message = "singleton factory constructor lambda plain") },
            builderEmitter = {
                log(level = Level.Warn) {
                    "singleton factory constructor lambda builder header"()
                    "singleton factory constructor lambda builder detail"(I)
                }
            },
        ).emit()

        CompanionOwner.emitPlain()
        CompanionOwner.emitBuilder()
        CompanionOwner.acceptLambda {
            logD(message = "companion accepted lambda plain")
        }
        CompanionOwner.acceptLambda {
            logI {
                "companion accepted lambda builder header"()
                "companion accepted lambda builder detail"(E)
            }
        }
        CompanionOwner.createConstructorLambdaCaller(
            plainEmitter = { logW(message = "companion factory constructor lambda plain") },
            builderEmitter = {
                log(level = Level.Warn) {
                    "companion factory constructor lambda builder header"()
                    "companion factory constructor lambda builder detail"(I)
                }
            },
        ).emit()
    }

    @Test
    fun `logs from constructors accessors invoke operators and extension functions`() {
        InitBlockCaller()
        SecondaryConstructorCaller(label = "secondary constructor")
        ConstructorLambdaCaller(
            plainEmitter = { logV(message = "constructor lambda plain") },
            builderEmitter = {
                logV {
                    "constructor lambda builder header"()
                    "constructor lambda builder detail"(D)
                }
            },
        ).emit()
        ParameterizedLambdaCaller(
            plainEmitter = { label ->
                log(message = "$label plain", level = Level.Info)
            },
            builderEmitter = { label ->
                log(level = Level.Info) {
                    "$label builder header"()
                    "$label builder detail"(W)
                }
            },
        ).emit("parameterized constructor lambda")

        val accessorCaller = AccessorCaller()
        accessorCaller.exercise()
        accessorCaller.emitFromExtensionFunction()

        SingletonStateCarrier.eagerValue
        SingletonStateCarrier.lazyValue
        SingletonStateCarrier.computedValue
        SingletonStateCarrier()
    }

    @Test
    fun `logs from wrappers factories fun interfaces and collection lambdas`() {
        regularWrapper {
            logI(message = "regular wrapper plain")
        }
        regularWrapper {
            logI {
                "regular wrapper builder header"()
                "regular wrapper builder detail"(W)
            }
        }

        inlineWrapper {
            logD(message = "inline wrapper plain")
        }
        inlineWrapper {
            log(level = Level.Debug) {
                "inline wrapper builder header"()
                "inline wrapper builder detail"(E)
            }
        }

        returningLambdaWrapper {
            logE(message = "returned lambda plain")
        }.invoke()
        returningLambdaWrapper {
            logE {
                "returned lambda builder header"()
                "returned lambda builder detail"(I)
            }
        }.invoke()

        val samPlain: EmitterAction = EmitterAction {
            logW(message = "fun interface plain")
        }
        val samBuilder: EmitterAction = EmitterAction {
            log(level = Level.Warn) {
                "fun interface builder header"()
                "fun interface builder detail"(D)
            }
        }

        samPlain.emit()
        samBuilder.emit()

        listOf("first", "second").forEach { label ->
            log(message = "collection forEach plain $label", level = Level.Info)
        }
        listOf("alpha", "beta").forEach { label ->
            logI {
                "collection forEach builder $label"()
                "collection forEach builder detail $label"(W)
            }
        }

        buildEmitter("factory lambda").invoke()
        buildBuilderEmitter("factory builder lambda").invoke()
    }

    @Test
    fun `logs from objc interop like entry points and UIViewController return types`() {
        val representableLike = UIKitRepresentableLike()
        val initialController = UIViewController()

        iosAppEntryPointLikeFunction()
        iosExpressionBodyEntryPointLikeFunction()
        topLevelObjcParameterAndReturnType(initialController)
        topLevelAdditionalObjcReturnType()
        returnedObjcFactory().invoke()
        returnedObjcBuilderFactory().invoke()

        representableLike.makeUIViewController()
        representableLike.updateUIViewController(initialController)

        ObjcConstructorLambdaOwner(
            plainFactory = {
                logI(message = "objc constructor lambda plain")
                UIViewController()
            },
            builderFactory = {
                log {
                    "objc constructor lambda builder header"()
                    "objc constructor lambda builder detail"(W)
                }
                UIViewController()
            },
        ).makeViewController()

        ObjcCompanionOwner.makeViewController()
        ObjcCompanionOwner.acceptFactory {
            log {
                "objc companion accepted factory header"()
                "objc companion accepted factory detail"(I)
            }
            UIViewController()
        }

        ObjcSingletonOwner.makeViewController()
        ObjcSingletonOwner.acceptFactory {
            logI(message = "objc singleton accepted factory")
            UIViewController()
        }

        initialController.logAndReturnSelf()
        initialController.logBuilderAndReturnSelf()
    }
}
