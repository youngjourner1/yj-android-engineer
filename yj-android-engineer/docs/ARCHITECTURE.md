# YJ Webcam System Architecture Guide

## Overview
YJ Webcam is built using SOLID Clean Architecture principles across Android (Kotlin/Compose), Windows Desktop (C#/WinUI 3), and Native Driver C++ components.

---

## 1. Android Architecture (Clean Architecture + MVVM)

```
                       +-----------------------------+
                       |     Presentation Layer      |
                       |  Jetpack Compose / ViewModels|
                       +--------------+--------------+
                                      |
                                      v
                       +-----------------------------+
                       |        Domain Layer         |
                       |  UseCases / Domain Models   |
                       +--------------+--------------+
                                      |
                                      v
                       +-----------------------------+
                       |         Data Layer          |
                       | CameraX / WebRTC / Room / DS|
                       +-----------------------------+
```

### Layer Responsibilities
- **Core**: Theme, Color palette, Coroutine Dispatchers, Crypto utilities (`EncryptionManager`), and result wrappers.
- **Domain**: Pure Kotlin interfaces and business rules (`StreamVideoUseCase`, `ApplyAiEffectUseCase`, `PairDeviceUseCase`). Zero framework dependencies.
- **Data**:
  - `CameraXController`: Interacts with Android Camera2/CameraX API, manages lens switching, focus metering, exposure, and frame outputs.
  - `AiFrameProcessor`: Intercepts raw YUV frames to run MediaPipe / MLKit segmentation masks for background blur, background removal, auto-framing, and low-light boost before GPU drawing.
  - `WebRTCManager`: Controls WebRTC PeerConnection, ICE candidate negotiation, and hardware-accelerated H.264 / HEVC video encoders.
  - `TcpSocketServer`: High-speed binary framing engine for USB ADB socket forwarding.
  - `AppDatabase`: Room DB storing trusted paired devices, session history, and error logs.
- **Presentation**: Reactive Jetpack Compose UI backed by Hilt injected `ViewModel` instances using `StateFlow` and `SharedFlow`.

---

## 2. Windows Desktop & Virtual Camera Driver Architecture

```
+-------------------------------------------------------------------+
|                     WinUI 3 Desktop Application                   |
|                     (C# .NET 8 / CommunityToolkit)                |
+---------------------------------+---------------------------------+
                                  |
                                  v
+-------------------------------------------------------------------+
|               Shared Memory / Ring Buffer IPC Service             |
|                  ("Global\YJWebcamSharedFrameBuffer")             |
+---------------------------------+---------------------------------+
                                  |
                                  v
+-------------------------------------------------------------------+
|             C++ MediaFoundation Virtual Camera DLL                |
|             (Registered as System Video Capture Source)           |
+-------------------------------------------------------------------+
```

### Virtual Camera Pipeline
1. The **C# WinUI 3 App** receives video frames (either via WebRTC or USB TCP receiver).
2. Frames are decoded to NV12 / YUY2 raw pixel buffers.
3. Decoded frames are written into a high-performance memory-mapped circular buffer shared memory segment (`Global\YJWebcamSharedFrameBuffer`) synchronized via Windows `EventWaitHandle` primitives.
4. The native **C++ Virtual Camera DLL (`YJWebcamVirtualCam.dll`)** registers an `IMFMediaSource` / DirectShow filter with Windows Media Foundation system.
5. Windows multimedia framework queries `YJWebcamVirtualCam.dll` to read raw frames from the shared buffer and presents "YJ Virtual Camera" to OBS, Zoom, Meet, and Teams as a physical USB webcam.

---

## 3. Security & Cryptography Model

- **ECDH P-256 Key Exchange**: Performed over TLS-like handshake when establishing control channel.
- **AES-256-GCM Frame Encryption**: Video frame payloads and audio packets are encrypted with dynamic IV counters.
- **PIN & QR Pairing**: QR codes embed temporary nonces and public key fingerprints to prevent Man-in-the-Middle (MITM) attacks on public Wi-Fi networks.

---

## 4. JY Color Engine & Live LUT Engine Architecture

```
Camera Input -> Color Space Conv -> Exposure -> WB -> Tone Mapping -> Primary Color -> Curves -> 3D LUT -> Intensity Blend -> Secondary -> Effects -> Video Encoder / Virtual Cam
```

### Key Modules:
- **`CubeLutParser`**: Parses 17x17x17, 33x33x33, and 65x65x65 `.cube` files with float validation, header inspection, and NaN/Infinity safety checks.
- **`GlColorEngine` (Android)**: Real-time GPU OpenGL ES 3.0 texture processor running 14-stage GLSL fragment shader for exposure, color balance, 3D LUT lookup, intensity blend (0-100%), and video scopes histogram calculation.
- **`ColorEngineService` (Windows)**: High-performance C# desktop color engine supporting dual Mobile Mode vs Desktop Mode execution with real-time telemetry metrics.
- **`EntitlementManager`**: Subscription tier feature gating (Free vs Pro) supporting offline access for local imported LUTs.

