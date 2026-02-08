# WiFi Monitor

WiFi Monitor is a real-time Android application designed to track and display WiFi connectivity status. It provides users with immediate feedback on their network connection, ensuring they are aware of state changes such as disconnections or network switches.

This application is particularly useful for users who need to ensure they are connected to WiFi to avoid unwanted mobile data usage, especially when using full-screen apps where the status bar is hidden.

## Features

- **Real-time Status Monitoring:** Instantly detects changes in WiFi connectivity (Connected, Disconnected, Enabled, Disabled).
- **Network Information:** Displays the SSID of the currently connected WiFi network.
- **Available Networks:** Scans and lists the top available WiFi networks sorted by signal strength when not connected.
- **Background Alerts:** Uses a `BroadcastReceiver` to provide Toast messages for WiFi state changes even when the app is in the background.
- **Theme Support:** Includes support for both Light and Dark modes with adaptive launcher icons.
- **Splash Screen:** Integrated `androidx.core:core-splashscreen` for a smooth launch experience.

## Technical Details

The project is built using modern Android development practices:

- **Language:** Kotlin
- **Build System:** Gradle with Kotlin DSL (`build.gradle.kts`)
- **UI:** Android Views (XML layouts) with Jetpack Compose dependencies included.
- **Key Components:**
    - **`MainActivity`**: Manages the main UI using `status_screen.xml`, handles runtime permissions, and initiates WiFi scans.
    - **`WifiStateReceiver`**: A `BroadcastReceiver` that listens for connectivity and state changes to trigger notifications.

## Permissions

To function correctly, the app requires the following permissions defined in `AndroidManifest.xml`:

- `android.permission.ACCESS_NETWORK_STATE`: To check if the device has an active network connection.
- `android.permission.ACCESS_WIFI_STATE`: To read information about the current WiFi network (SSID, BSSID, etc.).
- `android.permission.ACCESS_FINE_LOCATION`: Required on Android 8.1+ to access WiFi SSID and scan results.

## Getting Started

1.  **Clone the repository:** Import the project into Android Studio.
2.  **Build:** Sync the project with Gradle files.
3.  **Run:** Deploy the app to an Android device or emulator.

*Note: Location services must be enabled on the device for WiFi scanning to work correctly on newer Android versions.*
