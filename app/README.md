# SlitherSync

A Snake game where your real-world steps (Google Fit) propel the snake forward and your device heading steers it. Built with Kotlin + Jetpack Compose.

## Tech and Versions
- Android Studio Narwhal (stable)
- JDK 17 (Temurin)
- Gradle wrapper 8.11.1
- AGP 8.9.0
- Kotlin 1.9.25
- Compose BOM 2025.08.00, Compose Compiler 1.5.15
- Compile/Target SDK 35, Min SDK 23
- Google Fit: play-services-fitness 21.3.0, Auth 21.3.0

## Setup
1. Open in Android Studio (Narwhal) with JDK 17.
2. Sync Gradle. Build with `assembleDebug`.
3. Create a Google Cloud project and enable Fitness API + Google Sign-In.
4. Configure OAuth consent and create Android OAuth client with package `com.malabarmatrix.slithersync` and your SHA-1.
5. Run on a device. Grant ACTIVITY_RECOGNITION permission and sign in.

## Emulator step simulation
- Use the in-app debug overlay to simulate steps if Fit data is unavailable.

## Privacy & Play Store
This app reads fitness/step data. Provide a Privacy Policy, complete Data Safety and Health Apps declarations in Play Console before publishing.

## Migration note
Google Fit is migrating toward Android Health/Health Connect. `StepRepository` is abstracted for future provider swaps.

## Troubleshooting
- OAuth errors: confirm SHA-1 and package name match your Cloud Console credentials.
- Permission denied: ensure ACTIVITY_RECOGNITION is granted on Android 10+.
