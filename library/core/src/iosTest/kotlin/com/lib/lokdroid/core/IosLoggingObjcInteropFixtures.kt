package com.lib.lokdroid.core

import platform.UIKit.UIViewController

internal fun logUserDefinedWrapperFunction() {
    log {
        "user-defined wrapper with log prefix header"()
        "user-defined wrapper with log prefix detail"(I)
    }
}

internal fun iosAppEntryPointLikeFunction(): UIViewController {
    log {
        "ios app entry point init"()
        "UIViewController return type"(I)
    }

    return UIViewController()
}

internal fun iosExpressionBodyEntryPointLikeFunction(): UIViewController = run {
    logI(message = "ios expression body UIViewController return type")

    UIViewController()
}

internal fun topLevelObjcParameterAndReturnType(
    controller: UIViewController,
): UIViewController {
    log {
        "top level objc parameter and return type"()
        "UIViewController parameter"(W)
    }

    return controller
}

internal fun topLevelAdditionalObjcReturnType(): UIViewController {
    log(level = com.lib.lokdroid.domain.model.Level.Info) {
        "top level additional objc return type"()
        "UIViewController repeated return type"(D)
    }

    return UIViewController()
}

internal fun returnedObjcFactory(): () -> UIViewController = {
    logE(message = "returned objc factory lambda")
    UIViewController()
}

internal fun returnedObjcBuilderFactory(): () -> UIViewController = {
    logW {
        "returned objc builder factory header"()
        "returned objc builder factory detail"(I)
    }
    UIViewController()
}

internal class UIKitRepresentableLike {

    fun makeUIViewController(): UIViewController {
        log {
            "makeUIViewController"()
            "representable like"(I)
        }

        return UIViewController()
    }

    fun updateUIViewController(controller: UIViewController) {
        logI(message = "updateUIViewController ${controller.hash}")
        logI {
            "updateUIViewController builder header"()
            "updateUIViewController builder detail"(W)
        }
    }
}

internal class ObjcConstructorLambdaOwner(
    private val plainFactory: () -> UIViewController,
    private val builderFactory: () -> UIViewController,
) {
    init {
        plainFactory()
    }

    fun makeViewController(): UIViewController = builderFactory()
}

internal class ObjcCompanionOwner {
    companion object {
        fun makeViewController(): UIViewController {
            log {
                "companion make UIViewController"()
                "objc companion"(I)
            }

            return UIViewController()
        }

        fun acceptFactory(factory: () -> UIViewController): UIViewController = factory()
    }
}

internal object ObjcSingletonOwner {

    fun makeViewController(): UIViewController {
        log {
            "singleton make UIViewController"()
            "objc singleton"(I)
        }

        return UIViewController()
    }

    fun acceptFactory(factory: () -> UIViewController): UIViewController = factory()
}

internal fun UIViewController.logAndReturnSelf(): UIViewController {
    logI(message = "UIViewController extension return self")

    return this
}

internal fun UIViewController.logBuilderAndReturnSelf(): UIViewController {
    log {
        "UIViewController extension builder return self"()
        "extension builder"(I)
    }

    return this
}
