using System;
using System.Security.Cryptography;
using System.Text;

namespace YJWebcam.Desktop.Services
{
    public class LicenseService
    {
        private const string EntropyKey = "YJWebcamProtectedLicenseEntropy2026";

        public bool ValidateAndSaveLicenseKey(string licenseKey)
        {
            if (string.IsNullOrWhiteSpace(licenseKey) || licenseKey.Length < 16)
            {
                return false;
            }

            try
            {
                // Encrypt license key using Windows DPAPI (ProtectedData)
                byte[] entropy = Encoding.UTF8.GetBytes(EntropyKey);
                byte[] dataToEncrypt = Encoding.UTF8.GetBytes(licenseKey.Trim());
                byte[] encryptedData = ProtectedData.Protect(dataToEncrypt, entropy, DataProtectionScope.CurrentUser);

                string appDataDir = Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData);
                string licenseFilePath = System.IO.Path.Combine(appDataDir, "YJWebcam", "license.dat");

                System.IO.Directory.CreateDirectory(System.IO.Path.GetDirectoryName(licenseFilePath)!);
                System.IO.File.WriteAllBytes(licenseFilePath, encryptedData);
                return true;
            }
            catch (Exception ex)
            {
                System.Diagnostics.Debug.WriteLine($"License save error: {ex.Message}");
                return false;
            }
        }

        public bool IsProLicensed()
        {
            try
            {
                string appDataDir = Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData);
                string licenseFilePath = System.IO.Path.Combine(appDataDir, "YJWebcam", "license.dat");

                if (!System.IO.File.Exists(licenseFilePath)) return false;

                byte[] encryptedData = System.IO.File.ReadAllBytes(licenseFilePath);
                byte[] entropy = Encoding.UTF8.GetBytes(EntropyKey);
                byte[] decryptedData = ProtectedData.Unprotect(encryptedData, entropy, DataProtectionScope.CurrentUser);

                string key = Encoding.UTF8.GetString(decryptedData);
                return !string.IsNullOrWhiteSpace(key) && key.StartsWith("YJPRO-");
            }
            catch
            {
                return false;
            }
        }
    }
}
