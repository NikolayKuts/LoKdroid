package com.lib.lokdroid.data.default_implementation

actual object TagProvider {

    actual fun getTag(): String {
        val element = getTargetReferenceStackTraceElement() ?: return "????"

        return element.getShortClassName()
    }
}
