using System;
using System.Diagnostics;
using System.IO;

namespace YJWebcam.Desktop.Services
{
    public class VirtualCamController
    {
        private const string DLL_NAME = "YJWebcamVirtualCam.dll";

        public bool IsDriverInstalled()
        {
            string dllPath = Path.Combine(AppDomain.CurrentDomain.BaseDirectory, DLL_NAME);
            return File.Exists(dllPath);
        }

        public bool RegisterVirtualCameraDriver()
        {
            string dllPath = Path.Combine(AppDomain.CurrentDomain.BaseDirectory, DLL_NAME);
            if (!File.Exists(dllPath)) return false;

            return ExecuteRegsvr32($"/s \"{dllPath}\"");
        }

        public bool UnregisterVirtualCameraDriver()
        {
            string dllPath = Path.Combine(AppDomain.CurrentDomain.BaseDirectory, DLL_NAME);
            if (!File.Exists(dllPath)) return false;

            return ExecuteRegsvr32($"/u /s \"{dllPath}\"");
        }

        private bool ExecuteRegsvr32(string arguments)
        {
            try
            {
                var psi = new ProcessStartInfo
                {
                    FileName = "regsvr32.exe",
                    Arguments = arguments,
                    UseShellExecute = true,
                    Verb = "runas" // Elevate as admin
                };
                using var process = Process.Start(psi);
                process?.WaitForExit(5000);
                return process?.ExitCode == 0;
            }
            catch (Exception ex)
            {
                Debug.WriteLine($"Regsvr32 execution failed: {ex.Message}");
                return false;
            }
        }
    }
}
