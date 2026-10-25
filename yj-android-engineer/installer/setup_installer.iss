[Setup]
AppName=YJ Webcam
AppVersion=1.0.0
DefaultDirName={autopf}\YJWebcam
DefaultGroupName=YJ Webcam
OutputBaseFilename=YJWebcam_Setup
Compression=lzma
SolidCompression=yes
PrivilegesRequired=admin

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags: unchecked

[Files]
Source: "..\windows\YJWebcam.Desktop\bin\Release\net8.0-windows10.0.19041.0\publish\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs
Source: "..\windows\YJWebcam.VirtualCamera\bin\Release\YJWebcamVirtualCam.dll"; DestDir: "{sys}"; Flags: regserver 32bit; Check: Is64BitInstallMode
Source: "..\windows\YJWebcam.VirtualCamera\bin\x64\Release\YJWebcamVirtualCam.dll"; DestDir: "{syswow64}"; Flags: regserver 64bit; Check: Is64BitInstallMode

[Icons]
Name: "{autoprograms}\YJ Webcam"; Filename: "{app}\YJWebcam.Desktop.exe"
Name: "{autodesktop}\YJ Webcam"; Filename: "{app}\YJWebcam.Desktop.exe"; Tasks: desktopicon

[Run]
Filename: "{app}\YJWebcam.Desktop.exe"; Description: "{cm:LaunchProgram,YJ Webcam}"; Flags: nowait postinstall skipifsilent
