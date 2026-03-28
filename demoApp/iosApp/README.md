This directory is reserved for the native iOS demo host app.

It is intentionally not part of the Gradle build.

Shared Compose UI lives in `:sharedUI`, while the future iOS host
should be created as a platform-specific Xcode app that embeds Kotlin output
from shared modules as needed.
