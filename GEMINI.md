# GEMINI.md - Mobile Controller Project

## Project Overview
This project is a **Mobile Controller** system that allows users to control their PC remotely via an Android smartphone. It consists of two main components:
1.  **Android Client:** A feature-rich application (Java) that acts as the controller (touchpad, keyboard, gestures, file manager, etc.).
2.  **Python Server:** A backend service running on the PC that receives commands from the mobile app and executes them (mouse movements, window management, file transfers, etc.).

The system uses custom UDP and TCP protocols for communication, supports QR-based pairing with AES encryption, and provides real-time PC status monitoring and live desktop previews.

## Architecture
- **Communication:**
    - **UDP (Port 5005):** Core commands (mouse/keyboard input, volume control).
    - **TCP (Port 5006):** File transfers and camera streaming.
    - **UDP Broadcast (Port 37020):** Server discovery.
    - **PC-to-Phone (Port 6000):** Reverse commands for PC-initiated actions.
- **Security:** QR-based pairing generates shared AES and HMAC keys for encrypted communication.
- **Server Core:** `python_server/server_1.py` manages session threads and command execution.

## Technologies
- **Python Backend:** `pyautogui` for automation, `socket` for networking, `opencv-python` for image processing, `customtkinter` for the optional server UI.
- **Android Frontend:** Java-based Android app with `ZXing` for QR pairing, custom layouts for touchpad/presenter modes, and background services for constant connectivity.

## Setup and Running

### Python Server
#### Prerequisites
Ensure you have Python 3.8+ installed. Install the following dependencies:
```bash
pip install pyautogui screen-brightness_control psutil pyperclip win10toast pystray Pillow customtkinter comtypes pycryptodome pycaw opencv-python numpy qrcode
```

#### Running the Server
Run the main server script:
```bash
cd python_server
python server_1.py
```
*(Optional: Use `watchdog.py` for auto-restarting the server on crash)*

### Android App
#### Building and Installing
The current repository contains the source code in a flattened structure under `MainActivity/`.
- **Package Name:** `com.prajwal.myfirstapp`
- **TODO:** Set up a standard Android Studio project structure (or use an existing `build.gradle` if found) to build the APK.
- **Minimum SDK:** Likely API 21+ (Android 5.0).

## Development Conventions
- **Commands:** Commands are sent as string-based protocols (e.g., `MOUSE_MOVE|x|y`).
- **File Storage:** Received files are stored in `python_server/Received_Files/`.
- **Logs:** Server logs are occasionally kept in `watchdog_log.txt`.
- **Dynamic Keys:** Pairing keys are stored in `python_server/pairing_keys.json` and should not be manually edited.

## Key Files
- `python_server/server_1.py`: Entry point for the PC server.
- `python_server/commands.py`: Logic for executing PC commands (PyAutoGUI).
- `python_server/config.py`: Global configuration and protocol settings.
- `MainActivity/MainActivity.java`: Main entry point for the Android app.
- `MainActivity/ConnectionManager.java`: Handles network communication on the Android side.
- `MainActivity/manifests/AndroidManifest.xml`: Android application configuration and permissions.
