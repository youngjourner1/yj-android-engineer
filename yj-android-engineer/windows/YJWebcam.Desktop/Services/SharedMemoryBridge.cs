using System;
using System.IO.MemoryMappedFiles;
using System.Threading;

namespace YJWebcam.Desktop.Services
{
    public class SharedMemoryBridge : IDisposable
    {
        private const string MAP_NAME = "Global\\YJWebcamSharedFrameBuffer";
        private const string EVENT_NAME = "Global\\YJWebcamFrameReadyEvent";
        private const int BUFFER_SIZE = 1920 * 1080 * 4 + 32; // Header + 1080p frame buffer

        private MemoryMappedFile? _mmf;
        private MemoryMappedViewAccessor? _accessor;
        private EventWaitHandle? _frameReadyEvent;
        private bool _initialized;

        public void Initialize()
        {
            try
            {
                _mmf = MemoryMappedFile.CreateOrOpen(MAP_NAME, BUFFER_SIZE);
                _accessor = _mmf.CreateViewAccessor();
                _frameReadyEvent = new EventWaitHandle(false, EventResetMode.AutoReset, EVENT_NAME);
                _initialized = true;
            }
            catch (Exception ex)
            {
                System.Diagnostics.Debug.WriteLine($"SharedMemoryBridge init error: {ex.Message}");
            }
        }

        public void WriteFrame(byte[] frameData, int width, int height, long timestampUs)
        {
            if (!_initialized || _accessor == null || _frameReadyEvent == null) return;

            try
            {
                // Write Header (Width, Height, Timestamp, DataSize)
                _accessor.Write(0, width);
                _accessor.Write(4, height);
                _accessor.Write(8, timestampUs);
                _accessor.Write(16, frameData.Length);

                // Write Pixel Payload
                _accessor.WriteArray(32, frameData, 0, Math.Min(frameData.Length, BUFFER_SIZE - 32));

                // Signal Virtual Camera DLL that a new frame is ready
                _frameReadyEvent.Set();
            }
            catch (Exception ex)
            {
                System.Diagnostics.Debug.WriteLine($"Error writing shared memory frame: {ex.Message}");
            }
        }

        public void Dispose()
        {
            _accessor?.Dispose();
            _mmf?.Dispose();
            _frameReadyEvent?.Dispose();
        }
    }
}
