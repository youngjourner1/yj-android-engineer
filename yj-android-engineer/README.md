# YJ Webcam 🎥⚡
> **Transform your Android phone into an ultra-low-latency, professional PC webcam with real-time GPU color grading and live 3D LUT support.**

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Platform: Android 10+](https://img.shields.io/badge/Platform-Android%2010%2B-green.svg)](https://developer.android.com)
[![Platform: Windows 10/11](https://img.shields.io/badge/Platform-Windows%2010%2F11-0078D6.svg)](https://microsoft.com)
[![CI Status](https://img.shields.io/badge/Build-Passing-brightgreen.svg)](.github/workflows/ci.yml)

---

## 🎨 JY Color Engine & Live LUT Engine

YJ Webcam features the **JY Color Engine**, a professional real-time color-grading engine processing live video before outputting to virtual webcams:

- 🎬 **.CUBE 3D LUT Support**: Full support for 17x17x17, 33x33x33, and 65x65x65 `.cube` 3D LUT files with instant validation and NaN/Infinity safety protection.
- 🎛️ **Full Color Controls**: Exposure EV (-3 to +3), Contrast, Saturation, Vibrance, Temperature, Tint, Gamma, Lift, Gain, and Offset.
- ⚡ **GPU Accelerated Pipeline**: Deterministic 14-stage OpenGL ES 3.0 (Android) and Direct3D (Windows) shader processing with <1ms frame overhead.
- 📊 **Professional Video Scopes**: Live Luma/RGB Histograms, Waveform monitors, and Vectorscopes.
- 🔄 **Dual Processing Modes**: Toggle between **Mobile Mode** (Phone GPU processing) and **Desktop Mode** (PC Processing).
- 💎 **Entitlement & Tier System**: Pro entitlement architecture with offline local library access.

---

## 🚀 Key Features

- 🏎️ **Ultra-Low Latency**: <25ms latency with H.264/HEVC hardware acceleration via WebRTC and Direct USB ADB socket tunneling.
- 📸 **Complete Camera Control**: Support for Multi-camera setup (Ultra-wide, Front, Telephoto), manual focus, exposure compensation, tap-to-focus, white balance, zoom up to 10x, and flash toggle.
- 🤖 **On-Device AI Suite**:
  - Real-time background blur & background replacement
  - AI Face Tracking & Dynamic Auto-Framing
  - Portrait Beauty enhancement & Low-light noise reduction
- 🎙️ **Studio Audio Engine**: High-fidelity microphone streaming with active noise suppression, echo cancellation, automatic gain control, and delay correction.
- 🔌 **Dual Connection Modes**: Auto mDNS discovery over Wi-Fi, QR code pairing, or zero-latency wired USB connection.
- 💻 **Universal Windows Virtual Camera**: Appears as a native DirectShow / MediaFoundation hardware camera visible to **OBS Studio, Zoom, Microsoft Teams, Discord, Google Meet, Skype, and Web Browsers**.
- 📊 **Real-time Diagnostics**: Dropped frame monitor, latency graphs, thermal metrics, and bitrate control panel.

---

## 📁 Project Architecture Overview

```
YJ-Webcam Root
├── android/            # Android Studio Clean Architecture Kotlin App (Compose + CameraX + WebRTC + Hilt + GlColorEngine)
├── windows/            # WinUI 3 Desktop App (C# / .NET 8) + C++ MediaFoundation Virtual Camera Driver + ColorEngineService
├── protocol/           # AES-256-GCM Encrypted Binary & JSON-RPC Protocol Spec
├── installer/          # Inno Setup Windows Installer Script
├── scripts/            # PowerShell Build & Test Automation Scripts
└── docs/               # System Architecture, API Specs, and Installation Guides
```

---

## ⚙️ Quick Start

### 1. Build Android App
```powershell
cd android
$env:JAVA_HOME="C:\Program Files\Android\Android Studio\jbr"
.\gradlew assembleRelease
```
Install the generated `app-release.apk` on your Android 10+ phone.

### 2. Build Windows Application & Driver
```powershell
cd scripts
.\build_windows.ps1
```

### 3. Pair Devices
1. Launch **YJ Webcam** on your Android phone.
2. Open **YJ Webcam Desktop** on Windows.
3. Scan the QR code shown on screen or connect via USB cable.
4. Select your preferred color preset or LUT in the **JY Color Engine** panel.
5. Open OBS or Zoom, select **YJ Virtual Camera** as your video capture device!

---

## 📚 Documentation & Guidelines

- 🏛️ [Architecture Guide](docs/ARCHITECTURE.md)
- 🛠️ [Developer Setup & Build Guide](docs/DEVELOPER_GUIDE.md)
- 🔌 [API & Protocol Specification](protocol/PROTOCOL_SPEC.md)
- 💻 [Installation & Driver Registration Guide](docs/INSTALLATION_GUIDE.md)
- 🤝 [Contribution Guidelines](docs/CONTRIBUTING.md)
- 🛡️ [Security Policy](SECURITY.md)
- 📜 [Code of Conduct](CODE_OF_CONDUCT.md)
- 📜 [License](LICENSE)

---

## 📜 License
Distributed under the MIT License. See [LICENSE](LICENSE) for more information.
