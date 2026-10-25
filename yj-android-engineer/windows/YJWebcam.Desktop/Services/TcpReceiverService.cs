using System;
using System.IO;
using System.Net.Sockets;
using System.Threading;
using System.Threading.Tasks;

namespace YJWebcam.Desktop.Services
{
    public class TcpReceiverService
    {
        private readonly SharedMemoryBridge _sharedMemoryBridge;
        private TcpClient? _client;
        private CancellationTokenSource? _cts;
        private bool _isReceiving;

        public event Action<float, long>? OnStatsUpdated;

        public TcpReceiverService(SharedMemoryBridge sharedMemoryBridge)
        {
            _sharedMemoryBridge = sharedMemoryBridge;
        }

        public async Task StartReceiverAsync(string host = "127.0.0.1", int port = 9876)
        {
            _cts = new CancellationTokenSource();
            _isReceiving = true;

            await Task.Run(async () =>
            {
                int retryAttempt = 0;
                while (_isReceiving && !_cts.Token.IsCancellationRequested)
                {
                    try
                    {
                        _client = new TcpClient();
                        await _client.ConnectAsync(host, port);
                        retryAttempt = 0; // Reset on successful connect
                        using var stream = _client.GetStream();

                        byte[] headerBuffer = new byte[32];
                        long frameCount = 0;
                        var startTime = DateTime.UtcNow;

                        while (_client.Connected && _isReceiving)
                        {
                            int read = await ReadExactAsync(stream, headerBuffer, 0, 32, _cts.Token);
                            if (read < 32) break;

                            // Magic Header 'YJW1' check (0x594A5731)
                            if (headerBuffer[0] != 0x59 || headerBuffer[1] != 0x4A ||
                                headerBuffer[2] != 0x57 || headerBuffer[3] != 0x31)
                            {
                                continue;
                            }

                            int width = (headerBuffer[20] << 8) | headerBuffer[21];
                            int height = (headerBuffer[22] << 8) | headerBuffer[23];
                            int payloadSize = (headerBuffer[24] << 24) | (headerBuffer[25] << 16) |
                                             (headerBuffer[26] << 8) | headerBuffer[27];

                            byte[] payload = new byte[payloadSize];
                            int payloadRead = await ReadExactAsync(stream, payload, 0, payloadSize, _cts.Token);
                            if (payloadRead < payloadSize) break;

                            frameCount++;
                            _sharedMemoryBridge.WriteFrame(payload, width, height, DateTime.UtcNow.Ticks);

                            var elapsed = (DateTime.UtcNow - startTime).TotalSeconds;
                            if (elapsed >= 1.0)
                            {
                                float fps = (float)(frameCount / elapsed);
                                OnStatsUpdated?.Invoke(fps, payloadSize * 8L * (long)fps / 1000L);
                                frameCount = 0;
                                startTime = DateTime.UtcNow;
                            }
                        }
                    }
                    catch (Exception ex)
                    {
                        // Exponential backoff: max delay 10 seconds
                        int retryDelay = Math.Min(10000, 2000 * (int)Math.Pow(2, Math.Min(5, retryAttempt++)));
                        await Task.Delay(retryDelay, _cts.Token);
                    }
                }
            }, _cts.Token);
        }

        private async Task<int> ReadExactAsync(Stream stream, byte[] buffer, int offset, int count, CancellationToken ct)
        {
            int totalRead = 0;
            while (totalRead < count)
            {
                int read = await stream.ReadAsync(buffer, offset + totalRead, count - totalRead, ct);
                if (read == 0) break;
                totalRead += read;
            }
            return totalRead;
        }

        public void StopReceiver()
        {
            _isReceiving = false;
            _cts?.Cancel();
            _client?.Close();
        }
    }
}
