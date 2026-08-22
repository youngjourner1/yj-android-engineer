# YJ Webcam Developer Guide

## Development Environment Requirements

### Android
- **Android Studio**: Ladybug / Hedgehog or newer (2024.1+)
- **JDK**: Java 17 LTS or Java 21
- **Android SDK**: Min SDK 29 (Android 10), Target SDK 34/35
- **Gradle**: 8.7+

### Windows
- **OS**: Windows 10 (19041+) or Windows 11
- **IDE**: Visual Studio 2022 (v17.8+) with workloads:
  - `.NET Desktop Development` (.NET 8 SDK)
  - `Desktop development with C++` (MSVC v143 compiler, Windows 11 SDK 10.0.22621.0)
  - `Windows Application Development` (WinUI 3 / App SDK)

---

## Building the Android Project

```powershell
# Navigate to android root
cd android

# Build Debug APK
.\gradlew assembleDebug

# Run Unit Tests
.\gradlew testDebugUnitTest

# Install on connected device via ADB
.\gradlew installDebug
```

---

## Building the Windows Desktop & Virtual Camera Driver

```powershell
# Using the automated powershell script
cd scripts
.\build_windows.ps1
```

Or manually via Visual Studio:
1. Open `windows/YJWebcam.sln` in Visual Studio 2022.
2. Select Configuration: `Release` | Platform: `x64`.
3. Right click `YJWebcam.Desktop` project and click **Build**.
4. Right click `YJWebcam.VirtualCamera` project and click **Build**.

---

## Code Style & Architecture Best Practices

- **Kotlin**: Enforce Kotlin Style Guide via ktlint. All state components must use Immutable `StateFlow` and Hilt dependency injection.
- **C# / WinUI 3**: Follow standard .NET naming conventions and use `CommunityToolkit.Mvvm` attributes (`[ObservableProperty]`, `[RelayCommand]`).
- **C++**: Follow Modern C++ (C++20) guidelines with smart pointers (`std::unique_ptr`, `ComPtr<T>`) for zero memory leaks.
