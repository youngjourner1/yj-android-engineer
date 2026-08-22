# Security Policy

## Supported Versions

| Version | Supported          |
| ------- | ------------------ |
| 1.0.x   | :white_check_mark: |
| < 1.0   | :x:                |

## Reporting a Vulnerability

The YJ Webcam development team takes security seriously. If you discover a security vulnerability in this project, please follow these steps:

1. **Do NOT open a public GitHub issue** for security vulnerabilities.
2. Email your findings to **security@youngjourner.com** with details about:
   - Affected components (Android App, Windows Client, Protocol, Virtual Camera DLL)
   - Step-by-step reproduction instructions
   - Potential impact of the issue
3. We will acknowledge receipt of your vulnerability report within **48 hours** and provide periodic updates on resolution progress.

## Encryption & Communication Security

YJ Webcam uses:
- **ECDH P-256 Key Exchange** for establishing session secrets.
- **AES-256-GCM** authenticated encryption for control streams and direct video frame data.
- **PIN/QR Authentication** to prevent unauthorized access across local networks.
