# My HMSE

An Android app to control and monitor SMS-based (GSM) security alarm systems — arm/disarm your device, manage zones, configure timers, and view command history, all from your phone.

Built to work with [HMSE Technologies](https://hmsetech.com/) GSM-based wired alarm control panels, used to secure homes, offices, and industries.

![Platform](https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white)
![Language](https://img.shields.io/badge/Language-Java-orange?logo=java)
![Database](https://img.shields.io/badge/Database-Room-blue)

## Features

- **Arm / Disarm** your alarm system remotely via SMS commands
- **Zone management** — enable or disable individual zones
- **Auto timers** and **entry/exit timers** configuration
- **Sounder timing** settings
- **Device selection & settings** for managing multiple devices
- **Command history** — track sent commands and device responses
- **Saved user details** and notes per device
- **Local storage** with Room database (device data, history, notes)

## Tech Stack

- **Language:** Java
- **Platform:** Android (min SDK 24, target SDK 34, compile SDK 35)
- **Database:** Room (local persistence — devices, command history, notes)
- **UI:** AndroidX, Material Components, ConstraintLayout, ViewPager/Fragments

## Permissions

The app requires the following permission:

| Permission | Purpose |
|---|---|
| `SEND_SMS` | Send control commands (arm/disarm, zone toggles, timer settings) to the GSM alarm control panel |

## Getting Started

### Prerequisites

- [Android Studio](https://developer.android.com/studio) (latest stable version)
- JDK 11

### Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/Isha1942/MY-HMSE-App.git
   ```
2. Open the project in Android Studio.
3. Sync Gradle and run the app on an emulator or physical device.

### Build from command line

```bash
./gradlew assembleDebug
```

The generated APK will be located at `app/build/outputs/apk/debug/`.

## Project Structure

```
newapp/
├── app/
│   ├── src/main/java/com/spark/newapp2/          # App source code (activities, fragments, adapters)
│   ├── src/main/java/com/spark/newapp2/database/ # Room database entities & DAOs
│   ├── src/main/res/                              # Layouts, drawables, strings, themes
│   └── src/main/AndroidManifest.xml
├── build.gradle.kts
├── settings.gradle.kts
└── gradle/
```

## Author

**Ishwari Korbu**
[GitHub](https://github.com/Isha1942)

## License

© 2026 Ishwari Korbu. All Rights Reserved.

This code is publicly visible for portfolio/interview purposes only.
Copying, redistribution, or commercial use of this code or the
associated Play Store app without explicit written permission is prohibited.
