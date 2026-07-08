# My HMSE

An Android app to control and monitor SMS-based security alarm systems — arm/disarm your device, manage zones, configure timers, and view command history, all from your phone.

## Features

- **Arm / Disarm** your alarm system remotely via SMS commands
- **Zone management** — enable or disable individual zones
- **Auto timers** and **entry/exit timers** configuration
- **Sounder timing** settings
- **Device selection & settings** for managing multiple devices
- **Command history** — track sent commands and device responses
- **Saved user details** and notes per device
- **Firebase Authentication** for user login
- **Local storage** with Room database (device data, history, notes)

## Tech Stack

- **Language:** Java
- **Platform:** Android (min SDK 24, target SDK 34, compile SDK 35)
- **Database:** Room (local persistence)
- **Auth:** Firebase Authentication
- **UI:** AndroidX, Material Components, ConstraintLayout, ViewPager/Fragments

## Permissions

The app requires the following permissions:

- `SEND_SMS` — to send control commands to the alarm device
- `INTERNET` / `ACCESS_NETWORK_STATE` — for Firebase Authentication and connectivity checks

## Getting Started

### Prerequisites

- [Android Studio](https://developer.android.com/studio) (latest stable version)
- JDK 11
- A Firebase project with `google-services.json` configured (place it in `app/`)

### Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/<your-username>/<repo-name>.git
   ```
2. Open the project in Android Studio.
3. Add your own `app/google-services.json` file (see [Firebase setup](https://firebase.google.com/docs/android/setup)) — this file is required for Firebase Auth and is **not** committed to the repo for security reasons.
4. Sync Gradle and run the app on an emulator or physical device.

### Build from command line

```bash
./gradlew assembleDebug
```

The generated APK will be located at `app/build/outputs/apk/debug/`.

## Project Structure

```
newapp/
├── app/
│   ├── src/main/java/com/spark/newapp2/   # App source code (activities, fragments, adapters)
│   ├── src/main/java/com/spark/newapp2/database/  # Room database entities & DAOs
│   ├── src/main/res/                      # Layouts, drawables, strings, themes
│   └── src/main/AndroidManifest.xml
├── build.gradle.kts
├── settings.gradle.kts
└── gradle/
```



