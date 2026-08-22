# YJ Webcam API & Control Specification

## WebSockets / JSON-RPC Command Reference

### 1. `setCameraLens`
Changes the active phone camera sensor.
- **Parameters**:
  - `lens` (string): `"FRONT"`, `"BACK_MAIN"`, `"BACK_ULTRA_WIDE"`, `"BACK_TELEPHOTO"`
- **Response**: `{"status": "SUCCESS", "current_lens": "BACK_ULTRA_WIDE"}`

### 2. `setZoomRatio`
Adjusts optical/digital camera zoom level.
- **Parameters**:
  - `zoom_ratio` (float): `1.0` to `10.0`
- **Response**: `{"status": "SUCCESS", "applied_zoom": 2.5}`

### 3. `setTorchMode`
Enables or disables camera LED flash / torch.
- **Parameters**:
  - `enabled` (boolean): `true` / `false`

### 4. `setAiEffect`
Configures real-time AI background and portrait processing filters.
- **Parameters**:
  - `background_blur` (boolean)
  - `background_blur_strength` (int 1-100)
  - `auto_framing` (boolean)
  - `portrait_beauty` (boolean)
  - `low_light_boost` (boolean)

### 5. `setAudioConfig`
Configures audio capture settings.
- **Parameters**:
  - `sample_rate` (int): `44100` or `48000`
  - `channels` (int): `1` (Mono) or `2` (Stereo)
  - `noise_suppression` (boolean)
  - `echo_cancellation` (boolean)
  - `gain` (float): `0.5` to `3.0`
