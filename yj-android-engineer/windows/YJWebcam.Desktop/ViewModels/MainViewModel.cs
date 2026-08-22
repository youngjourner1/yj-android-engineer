using CommunityToolkit.Mvvm.ComponentModel;
using CommunityToolkit.Mvvm.Input;
using YJWebcam.Desktop.Models;
using YJWebcam.Desktop.Services;

namespace YJWebcam.Desktop.ViewModels
{
    public partial class MainViewModel : ObservableObject
    {
        private readonly SharedMemoryBridge _sharedMemoryBridge;
        private readonly TcpReceiverService _tcpReceiverService;
        private readonly VirtualCamController _virtualCamController;

        [ObservableProperty]
        private DeviceInfo _activeDevice = new();

        [ObservableProperty]
        private StreamStats _stats = new();

        [ObservableProperty]
        private bool _isDriverRegistered;

        public MainViewModel()
        {
            _sharedMemoryBridge = new SharedMemoryBridge();
            _sharedMemoryBridge.Initialize();

            _tcpReceiverService = new TcpReceiverService(_sharedMemoryBridge);
            _tcpReceiverService.OnStatsUpdated += (fps, bitrate) =>
            {
                Stats.CurrentFps = fps;
                Stats.BitrateKbps = bitrate;
                OnPropertyChanged(nameof(Stats));
            };

            _virtualCamController = new VirtualCamController();
            IsDriverRegistered = _virtualCamController.IsDriverInstalled();
        }

        [RelayCommand]
        private async Task ConnectAsync()
        {
            ActiveDevice.State = ConnectionState.Connecting;
            OnPropertyChanged(nameof(ActiveDevice));

            await _tcpReceiverService.StartReceiverAsync(ActiveDevice.IpAddress, ActiveDevice.Port);

            ActiveDevice.State = ConnectionState.Streaming;
            OnPropertyChanged(nameof(ActiveDevice));
        }

        [RelayCommand]
        private void Disconnect()
        {
            _tcpReceiverService.StopReceiver();
            ActiveDevice.State = ConnectionState.Disconnected;
            OnPropertyChanged(nameof(ActiveDevice));
        }

        [RelayCommand]
        private void ToggleDriverRegistration()
        {
            if (IsDriverRegistered)
            {
                _virtualCamController.UnregisterVirtualCameraDriver();
                IsDriverRegistered = false;
            }
            else
            {
                IsDriverRegistered = _virtualCamController.RegisterVirtualCameraDriver();
            }
        }
    }
}
