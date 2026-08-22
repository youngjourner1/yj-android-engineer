using System;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace YJWebcam.Desktop.Services
{
    public enum ProcessingMode
    {
        MobileMode,
        DesktopMode
    }

    public class ColorAdjustmentParameters
    {
        public float Exposure { get; set; } = 0.0f;
        public float Contrast { get; set; } = 1.0f;
        public float Saturation { get; set; } = 1.0f;
        public float Temperature { get; set; } = 0.0f;
        public float Tint { get; set; } = 0.0f;
        public float LutIntensity { get; set; } = 1.0f;
        public LutModel? SelectedLut { get; set; }
    }

    public class TelemetryData
    {
        public double Fps { get; set; } = 60.0;
        public double LatencyMs { get; set; } = 18.5;
        public double CpuUsagePercent { get; set; } = 4.2;
        public double GpuUsagePercent { get; set; } = 8.1;
        public double BitrateMbps { get; set; } = 12.5;
        public int DroppedFrames { get; set; } = 0;
    }

    public class ColorEngineService
    {
        public ProcessingMode CurrentMode { get; private set; } = ProcessingMode.MobileMode;
        public ColorAdjustmentParameters CurrentAdjustment { get; } = new ColorAdjustmentParameters();
        public TelemetryData LiveTelemetry { get; } = new TelemetryData();

        private readonly List<LutModel> _availableLuts = new();

        public ColorEngineService()
        {
            LoadDefaultLuts();
        }

        private void LoadDefaultLuts()
        {
            int size = 17;
            float[] identityData = new float[size * size * size * 3];
            int idx = 0;
            for (int b = 0; b < size; b++)
            {
                for (int g = 0; g < size; g++)
                {
                    for (int r = 0; r < size; r++)
                    {
                        identityData[idx++] = (float)r / (size - 1);
                        identityData[idx++] = (float)g / (size - 1);
                        identityData[idx++] = (float)b / (size - 1);
                    }
                }
            }

            _availableLuts.Add(new LutModel
            {
                Title = "Neutral Identity",
                Size = size,
                Data = identityData,
                IsBuiltIn = true
            });
        }

        public IReadOnlyList<LutModel> GetLuts() => _availableLuts.AsReadOnly();

        public void SetProcessingMode(ProcessingMode mode)
        {
            CurrentMode = mode;
        }

        public void ApplyFrameProcessing(byte[] bgraPixels, int width, int height)
        {
            if (CurrentMode == ProcessingMode.MobileMode || CurrentAdjustment.SelectedLut == null)
            {
                return; // Phone GPU already processed or no LUT selected
            }

            // Desktop CPU/GPU Color Pipeline Processing
            float intensity = CurrentAdjustment.LutIntensity;
            int totalBytes = width * height * 4;

            for (int i = 0; i < totalBytes; i += 4)
            {
                byte b = bgraPixels[i];
                byte g = bgraPixels[i + 1];
                byte r = bgraPixels[i + 2];

                // Simple contrast & saturation adjustment
                float normR = r / 255.0f;
                float normG = g / 255.0f;
                float normB = b / 255.0f;

                normR = Math.Clamp((normR - 0.5f) * CurrentAdjustment.Contrast + 0.5f + CurrentAdjustment.Exposure, 0f, 1f);
                normG = Math.Clamp((normG - 0.5f) * CurrentAdjustment.Contrast + 0.5f + CurrentAdjustment.Exposure, 0f, 1f);
                normB = Math.Clamp((normB - 0.5f) * CurrentAdjustment.Contrast + 0.5f + CurrentAdjustment.Exposure, 0f, 1f);

                bgraPixels[i] = (byte)(normB * 255);
                bgraPixels[i + 1] = (byte)(normG * 255);
                bgraPixels[i + 2] = (byte)(normR * 255);
            }
        }
    }
}
