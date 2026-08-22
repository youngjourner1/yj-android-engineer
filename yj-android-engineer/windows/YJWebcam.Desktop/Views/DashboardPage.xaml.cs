using Microsoft.UI.Xaml.Controls;
using YJWebcam.Desktop.ViewModels;

namespace YJWebcam.Desktop.Views
{
    public partial class DashboardPage : Page
    {
        public MainViewModel ViewModel { get; } = new MainViewModel();

        public DashboardPage()
        {
            this.InitializeComponent();
            this.DataContext = ViewModel;
        }
    }
}
