namespace YJWebcam.Desktop.Models
{
    public enum ConnectionState
    {
        Disconnected,
        Connecting,
        Connected,
        Streaming,
        Error
    }

    public class DeviceInfo
    {
        public string Name { get; set; } = string.Empty;
        public string IpAddress { get; set; } = "127.0.0.1";
        public int Port { get; set; } = 9876;
        public bool IsUsbConnected { get; set; }
        public ConnectionState State { get; set; } = ConnectionState.Disconnected;
    }

    public class StreamStats
    {
        public float CurrentFps { get; set; }
        public float TargetFps { get; set; } = 60.0f;
        public long BitrateKbps { get; set; }
        public float LatencyMs { get; set; }
        public long DroppedFrames { get; set; }
    }
}
