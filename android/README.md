# PY-OS Cloud Android

Native Android Java edition of PY-OS Cloud.

## Build

The project uses Android Gradle Plugin 8.7.3, Gradle 8.9, Java 17, and Android SDK 35.

From the repository root:

```sh
gradle --no-daemon -p android :app:assembleDebug
```

APK output:

`android/app/build/outputs/apk/debug/app-debug.apk`

## GitHub Actions

The workflow at `.github/workflows/android-apk.yml` builds an APK when Android source changes or when manually dispatched. The APK is uploaded as a workflow artifact. Push a version tag such as `v0.4.0` to also attach the APK to a GitHub Release.

The terminal in this initial native edition provides a small Python-inspired command set; it is not yet a Python interpreter. The existing Python server/browser edition remains separate and unchanged.
