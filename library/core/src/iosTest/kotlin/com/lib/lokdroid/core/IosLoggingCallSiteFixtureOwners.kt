package com.lib.lokdroid.core

import com.lib.lokdroid.domain.model.Level

internal fun topLevelPlainFunction() {
    logI(message = "top level plain")
}

internal fun topLevelBuilderFunction() {
    log(level = Level.Info) {
        "top level builder header"()
        "top level builder detail"(D)
    }
}

internal interface FixtureEmitter {
    fun emitPlain()
    fun emitBuilder()
}

internal class NestedCaller : FixtureEmitter {

    override fun emitPlain() {
        logD(message = "nested class plain")
    }

    override fun emitBuilder() {
        logD {
            "nested class builder header"()
            "nested class builder detail"(W)
        }
    }
}

internal class OuterCaller {

    inner class InnerCaller : FixtureEmitter {
        override fun emitPlain() {
            logW(message = "inner class plain")
        }

        override fun emitBuilder() {
            logW {
                "inner class builder header"()
                "inner class builder detail"(E)
            }
        }
    }
}

internal enum class EnumCaller {
    First,
    Second,
    ;

    fun emitPlain() {
        logV(message = "enum $name plain")
    }

    fun emitBuilder() {
        logV {
            "enum $name builder header"()
            "enum $name builder detail"(I)
        }
    }
}

internal object SingletonCaller {

    fun emitPlain() {
        logI(message = "singleton plain")
    }

    fun emitBuilder() {
        logI {
            "singleton builder header"()
            "singleton builder detail"(W)
        }
    }

    fun acceptLambda(block: () -> Unit) {
        block()
    }

    fun withReturnedLambda(): () -> Unit = {
        logE(message = "singleton returned lambda plain")
    }

    fun withReturnedBuilderLambda(): () -> Unit = {
        log(level = Level.Error) {
            "singleton returned lambda builder header"()
            "singleton returned lambda builder detail"(I)
        }
    }

    fun createConstructorLambdaCaller(
        plainEmitter: () -> Unit,
        builderEmitter: () -> Unit,
    ): ConstructorLambdaCaller = ConstructorLambdaCaller(
        plainEmitter = plainEmitter,
        builderEmitter = builderEmitter,
    )
}

internal class CompanionOwner {
    companion object {
        fun emitPlain() {
            logD(message = "companion plain")
        }

        fun emitBuilder() {
            logD {
                "companion builder header"()
                "companion builder detail"(W)
            }
        }

        fun acceptLambda(block: () -> Unit) {
            block()
        }

        fun createConstructorLambdaCaller(
            plainEmitter: () -> Unit,
            builderEmitter: () -> Unit,
        ): ConstructorLambdaCaller = ConstructorLambdaCaller(
            plainEmitter = plainEmitter,
            builderEmitter = builderEmitter,
        )
    }
}

internal class InitBlockCaller {
    init {
        logI(message = "init block plain")
        logI {
            "init block builder header"()
            "init block builder detail"(D)
        }
    }
}

internal class SecondaryConstructorCaller {
    constructor(label: String) {
        log(message = "$label plain", level = Level.Info)
        log(level = Level.Info) {
            "$label builder header"()
            "$label builder detail"(W)
        }
    }
}

internal class ConstructorLambdaCaller(
    private val plainEmitter: () -> Unit,
    private val builderEmitter: () -> Unit,
) {
    init {
        plainEmitter()
    }

    fun emit() {
        builderEmitter()
    }
}

internal class ParameterizedLambdaCaller(
    private val plainEmitter: (String) -> Unit,
    private val builderEmitter: (String) -> Unit,
) {
    fun emit(label: String) {
        plainEmitter(label)
        builderEmitter(label)
    }
}

internal class AccessorCaller {

    val eagerValue = run {
        logI(message = "accessor eager property plain")
        "eager"
    }

    val lazyValue by lazy {
        log(level = Level.Info) {
            "accessor lazy builder header"()
            "accessor lazy builder detail"(W)
        }
        "lazy"
    }

    val computedValue: String
        get() {
            logD(message = "accessor getter plain")
            log(level = Level.Debug) {
                "accessor getter builder header"()
                "accessor getter builder detail"(E)
            }
            return "computed"
        }

    operator fun invoke() {
        logW(message = "accessor invoke plain")
        logW {
            "accessor invoke builder header"()
            "accessor invoke builder detail"(I)
        }
    }

    fun exercise() {
        eagerValue
        lazyValue
        computedValue
        this()
    }
}

internal fun AccessorCaller.emitFromExtensionFunction() {
    logE(message = "accessor extension plain")
    logE {
        "accessor extension builder header"()
        "accessor extension builder detail"(D)
    }
}

internal object SingletonStateCarrier {
    init {
        logI(message = "singleton state init plain")
        logI {
            "singleton state init builder header"()
            "singleton state init builder detail"(D)
        }
    }

    val eagerValue = run {
        logV(message = "singleton state eager property plain")
        "singleton eager"
    }

    val lazyValue by lazy {
        log(level = Level.Verbose) {
            "singleton state lazy builder header"()
            "singleton state lazy builder detail"(W)
        }
        "singleton lazy"
    }

    val computedValue: String
        get() {
            logE(message = "singleton state getter plain")
            log(level = Level.Error) {
                "singleton state getter builder header"()
                "singleton state getter builder detail"(I)
            }
            return "singleton computed"
        }

    operator fun invoke() {
        log(level = Level.Error) {
            "singleton state invoke builder header"()
            "singleton state invoke builder detail"(W)
        }
    }
}

internal fun regularWrapper(block: () -> Unit) {
    block()
}

internal inline fun inlineWrapper(block: () -> Unit) {
    block()
}

internal fun returningLambdaWrapper(block: () -> Unit): () -> Unit = block

internal fun buildEmitter(label: String): () -> Unit = {
    log(message = "$label plain", level = Level.Info)
}

internal fun buildBuilderEmitter(label: String): () -> Unit = {
    log(level = Level.Info) {
        "$label builder header"()
        "$label builder detail"(W)
    }
}

internal fun interface EmitterAction {
    fun emit()
}
