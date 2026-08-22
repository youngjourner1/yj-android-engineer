# YJ Webcam Communication Protocol Specification (v1.0)

## Overview
The YJ Webcam Communication Protocol defines the network, framing, security, and signaling mechanisms used between the Android Phone app (Sender/Server) and the Windows Desktop Application / Virtual Camera driver (Receiver/Client).

The protocol supports dual transport modes:
1. **Network Wi-Fi Mode**: WebRTC Media Streams over UDP with WebSocket control signaling + fallback low-latency RTSP/TCP socket.
2. **Wired USB ADB Tunnel Mode**: High-throughput Direct TCP socket tunnel (`127.0.0.1:9876`) over `adb forward tcp:9876 tcp:9876`.

---

## 1. Network Discovery & Pairing Protocol

### 1.1 Service Discovery (mDNS / DNS-SD)
- **Service Name**: `_yjwebcam._tcp.local.`
- **TXTT Records**:
  - `name`: Human-readable device name (e.g., `Samsung Galaxy S24`)
  - `ver`: Protocol version (`1.0`)
  - `port`: WebSocket control server port (`9876`)
  - `fps_max`: Maximum supported FPS (`60`)
  - `hdr`: Supported (`true`/`false`)
  - `paired`: Requires PIN/QR authentication (`true`)

### 1.2 Handshake & ECDH Encryption Setup
All control and frame metadata payloads are encrypted using **AES-256-GCM** after performing an ECDH (P-256 curve) key exchange.

1. **Client Handshake Request** (`0x01`):
   ```json
   {
     "msg_type": "HANDSHAKE_REQ",
     "client_id": "WIN-DESKTOP-9942",
     "ecdh_pub_key": "<Base64 encoded ECDH P-256 Public Key>",
     "client_version": "1.0.0"
   }
   ```
2. **Server Handshake Response** (`0x02`):
   ```json
   {
     "msg_type": "HANDSHAKE_RESP",
     "status": "OK",
     "server_ecdh_pub_key": "<Base64 encoded ECDH P-256 Public Key>",
     "salt": "<Base64 16 bytes>",
     "auth_required": true
   }
   ```
3. **Session Key Derivation**:
   - `SharedSecret = ECDH(ClientPrivateKey, ServerPublicKey)`
   - `AESKey = HKDF-SHA256(SharedSecret, salt, info="YJWebcamv1")`

---

## 2. Frame Transport Protocol (Binary Stream Format)

For USB direct mode or raw fallback TCP streaming, video frames are transmitted using a 32-byte header followed by the payload.

### Frame Packet Binary Layout
```
 0                   1                   2                   3
 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
|                     Magic Header ('YJW1')                     |
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
|  Frame Type   | Codec (H264/HEVC) |  Flags (IDR/Keyframe)     |
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
|                         Sequence Number                       |
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
|                    Timestamp (Microseconds)                   |
|                                                               |
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
|                         Width (uint16)  |   Height (uint16)   |
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
|                       Payload Size (uint32)                   |
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
|                   AES-256-GCM IV (12 bytes)                   |
|                                                               |
|                                                               |
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
|                                                               |
|                        Payload Data...                        |
|                                                               |
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
|                     GCM Auth Tag (16 bytes)                   |
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
```

---

## 3. Remote Control Protocol RPC Messages

Remote control requests sent from Windows Desktop to Android Camera:

### 3.1 Switch Camera
```json
{
  "jsonrpc": "2.0",
  "method": "setCameraLens",
  "params": { "lens": "BACK_ULTRA_WIDE" }, // FRONT, BACK_MAIN, BACK_ULTRA_WIDE, BACK_TELEPHOTO
  "id": 101
}
```

### 3.2 Adjust Exposure & Focus
```json
{
  "jsonrpc": "2.0",
  "method": "setFocusExposure",
  "params": {
    "tap_x": 0.45,
    "tap_y": 0.60,
    "exposure_compensation": +2
  },
  "id": 102
}
```

### 3.3 Set AI Effects
```json
{
  "jsonrpc": "2.0",
  "method": "setAiEffect",
  "params": {
    "background_blur": true,
    "blur_radius": 15,
    "auto_framing": true,
    "portrait_enhancement": true,
    "low_light_boost": true
  },
  "id": 103
}
```

### 3.4 Color Engine & Live LUT Control
```json
{
  "jsonrpc": "2.0",
  "method": "setColorEngineConfig",
  "params": {
    "exposure": 0.25,
    "contrast": 1.10,
    "saturation": 1.05,
    "temperature": 0.10,
    "tint": -0.05,
    "selected_lut_id": "lut_teal_orange",
    "lut_intensity": 0.85,
    "processing_mode": "MOBILE_MODE"
  },
  "id": 104
}
```

---

## 4. Diagnostics & Stat Metrics Protocol
Sent every 1000ms from Server to Client:
```json
{
  "msg_type": "DIAGNOSTICS_TICK",
  "current_fps": 59.8,
  "dropped_frames": 2,
  "bitrate_kbps": 12500,
  "latency_ms": 18.4,
  "battery_level": 88,
  "thermal_status": "NORMAL",
  "encoder_cpu_usage": 4.2
}
```
