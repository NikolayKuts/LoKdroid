package com.lib.lokdroid.data.default_implementation

import com.lib.lokdroid.domain.model.Level

/**
 * Maps a log [Level] to the square emoji used by the default console loggers.
 */
internal fun Level.toEmoji(): String = when (this) {
    Level.Verbose -> "⬜"
    Level.Debug -> "🟦"
    Level.Info -> "🟩"
    Level.Warn -> "🟨"
    Level.Error -> "🟥"
}

/**
 * Maps a nullable log [Level] to its emoji representation, returning an empty string for `null`.
 */
internal fun Level?.toEmojiOrEmpty(): String = this?.toEmoji().orEmpty()
