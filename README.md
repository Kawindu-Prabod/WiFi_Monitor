# WiFi Monitor

WiFi Monitor is a simple Android application that monitors your device's WiFi connectivity status and displays information about the current connection.
This was mainly made because if WiFi suddenly disconnected while in full screen mode, it will not show as user have to pull down the notification centre to check it. So, this will inform the user via Toast message if WiFi is disconnected. This can help some user's ensure mobile data is not charged when WiFi is no longer available.

## Features

- **Real-time WiFi Status:** See whether your device is connected to a WiFi network.
- **Network SSID:** Displays the name of the connected WiFi network (SSID).
- **Available Networks:** If not connected, it shows a list of the top 5 available WiFi networks with the strongest signal.
- **Background Notifications:** Get notified with a Toast message when your WiFi connects or disconnects, even when the app is in the background.
- **Permission Handling:** Gracefully requests necessary permissions to access WiFi and location data.

## How to Build and Run

1.  Clone the repository or import the project into Android Studio.
2.  Build the project using Gradle.
3.  Run the app on an Android device or emulator.

## Permissions

The app requires the following permissions to function correctly:

- `ACCESS_NETWORK_STATE`: To check the network connectivity state.
- `ACCESS_WIFI_STATE`: To access information about the WiFi connection.
- `ACCESS_FINE_LOCATION`: To scan for available WiFi networks.
