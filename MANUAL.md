# VulnaBank — run steps

Use the maintained `main` branch. Open this directory as the Gradle project.

## Setup and build

Use Android Studio Rabbit 2 (2026.2.2) or newer, Android SDK platform 37 and build-tools 37.0.0. Use the project's Gradle daemon JVM criteria, which select JetBrains Runtime 25 from Studio's bundled JBR; no separate JDK installation is needed. The app requires Android 11 (API 30) and targets Android 17 (API 37). Java/Kotlin compilation targets Java 25.

For a shell build, select your installed JDK through `JAVA_HOME` if needed:

```sh
./gradlew assembleDebug
```

## Install and run

```sh
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n defdev.labs.droid.vulnabank/.LoginActivity
```

Launch **VulnaBank**, register a four-character password on first start and log in with it afterwards. The course server address is supplied in `lab.properties`; normal course use needs no local configuration.

## Another server

Only for a different instructor setup: create `lab.local.properties` next to `lab.properties` with your own `lab.endpoint=` value, then rebuild. The file is ignored by Git.

After updating the source, sync, rebuild and reinstall before using the app.
