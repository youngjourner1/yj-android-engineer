# YJ Webcam Installation & Windows Driver Registration Guide

## System Requirements

- **Android Phone**: Android 10 (API level 29) or newer with working camera and Wi-Fi / USB debugging enabled.
- **Windows PC**: Windows 10 x64 (Build 19041+) or Windows 11 x64.

---

## Installation Steps

### Option 1: Automated Installer (Recommended)
1. Download `YJWebcam_Setup_v1.0.0.exe` from Releases.
2. Run the installer. It will automatically:
   - Install **YJ Webcam Desktop App** to `C:\Program Files\YJWebcam\`
   - Register `YJWebcamVirtualCam.dll` COM DirectShow / MediaFoundation source with Windows System Registry.
   - Install ADB driver helpers for USB connection.
3. Install `YJWebcam.apk` on your Android phone.

### Option 2: Manual Driver Registration (Developers)
If building from source:
1. Open PowerShell as Administrator.
2. Navigate to build output directory:
   ```powershell
   cd windows\x64\Release
   ```
3. Register the C++ Virtual Camera DLL:
   ```powershell
   regsvr32.exe YJWebcamVirtualCam.dll
   ```
4. Verify registration in Device Manager or OBS Studio video input device list.
