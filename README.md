# VulnaBank

Android banking app used as examination material in the "Development security in Android" course. The trainer provides the exam instructions.

![Application overview](defdeveu_mockup.png)

## Requirements

- Android Studio Rabbit 2 (2026.2.2) or newer
- Android Studio's bundled JetBrains Runtime 25; no separate JDK installation required
- Android SDK platform 37 and build-tools 37.0.0
- A device or emulator running Android 11 (API 30) or newer
- Network access to the course server

The app targets Android 17 (API 37). Gradle uses the daemon JVM criteria in `gradle/gradle-daemon-jvm.properties`; Java/Kotlin compilation targets Java 25, with Kotlin 2.4.21.

## Build and run

Open this directory in Android Studio, sync and run the app, or use:

```sh
./gradlew assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

The maintained branch is `main`. See [MANUAL.md](MANUAL.md) for the run steps.

## Tests

```sh
./gradlew testDebugUnitTest
```

## Technology stack

- Kotlin, Jetpack Compose and Material 3
- Room for the local database
- Platform `HttpURLConnection` and `XmlPullParser` for server communication
- Kotlin coroutines

## Fonts and icons

The bundled variable Noto fonts and their upstream licenses are listed in `app/src/main/assets/licenses/fonts.txt`. The Material Symbols attribution is in `app/src/main/assets/licenses/material-symbols.txt`.

[![social image](assets/science-in-hd-ujY5eND8k44-unsplash.ksenia-edit-a.jpg)](https://github.com/defdeveu/vulnabankAndroid)

## Credits

- Implemented by Ferenc Sági (sagifer)
- Idea and specification by Zsombor Kovács (huobb0)
- Photo by 'Science in HD' used under the Unsplash License; derived work by Ksenia Kotelnikova; original: Seamans Guard, Stagg Field, University of Chicago...Credit: U.S. Department of Energy, Historian's Office
