using System;
using System.Collections.Generic;
using System.Globalization;
using System.IO;

namespace YJWebcam.Desktop.Services
{
    public class LutModel
    {
        public string Id { get; set; } = Guid.NewGuid().ToString();
        public string Title { get; set; } = "Custom LUT";
        public int Size { get; set; }
        public float[] Data { get; set; } = Array.Empty<float>();
        public bool IsBuiltIn { get; set; }
    }

    public static class CubeParser
    {
        public static LutModel Parse(Stream stream, string defaultTitle = "Custom LUT")
        {
            using var reader = new StreamReader(stream);
            string title = defaultTitle;
            int lutSize = 0;
            var rawFloats = new List<float>(33 * 33 * 33 * 3);

            string? line;
            while ((line = reader.ReadLine()) != null)
            {
                var trimmed = line.Trim();
                if (string.IsNullOrWhiteSpace(trimmed) || trimmed.StartsWith("#"))
                    continue;

                if (trimmed.StartsWith("TITLE", StringComparison.OrdinalIgnoreCase))
                {
                    var parts = trimmed.Split('"', StringSplitOptions.RemoveEmptyEntries);
                    if (parts.Length > 1) title = parts[1].Trim();
                }
                else if (trimmed.StartsWith("LUT_3D_SIZE", StringComparison.OrdinalIgnoreCase))
                {
                    var parts = trimmed.Split((char[]?)null, StringSplitOptions.RemoveEmptyEntries);
                    if (parts.Length >= 2 && int.TryParse(parts[1], out int sz))
                    {
                        lutSize = sz;
                    }
                }
                else
                {
                    var parts = trimmed.Split((char[]?)null, StringSplitOptions.RemoveEmptyEntries);
                    if (parts.Length >= 3)
                    {
                        if (float.TryParse(parts[0], NumberStyles.Float, CultureInfo.InvariantCulture, out float r) &&
                            float.TryParse(parts[1], NumberStyles.Float, CultureInfo.InvariantCulture, out float g) &&
                            float.TryParse(parts[2], NumberStyles.Float, CultureInfo.InvariantCulture, out float b))
                        {
                            rawFloats.Add(Math.Clamp(float.IsNaN(r) ? 0f : r, 0f, 10f));
                            rawFloats.Add(Math.Clamp(float.IsNaN(g) ? 0f : g, 0f, 10f));
                            rawFloats.Add(Math.Clamp(float.IsNaN(b) ? 0f : b, 0f, 10f));
                        }
                    }
                }
            }

            if (lutSize == 0)
            {
                int totalRGB = rawFloats.Count / 3;
                int cubeRoot = (int)Math.Round(Math.Cbrt(totalRGB));
                if (cubeRoot * cubeRoot * cubeRoot == totalRGB)
                {
                    lutSize = cubeRoot;
                }
            }

            if (lutSize < 2 || lutSize > 65)
            {
                throw new InvalidDataException($"Unsupported LUT size: {lutSize}");
            }

            return new LutModel
            {
                Title = title,
                Size = lutSize,
                Data = rawFloats.ToArray()
            };
        }
    }
}
