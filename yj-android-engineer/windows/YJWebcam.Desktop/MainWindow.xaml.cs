using Microsoft.UI.Xaml.Controls;
using YJWebcam.Desktop.Views;

namespace YJWebcam.Desktop
{
    public partial class MainWindow : Window
    {
        public MainWindow()
        {
            this.InitializeComponent();
            ContentFrame.Navigate(typeof(DashboardPage));
        }

        private void NavView_SelectionChanged(NavigationView sender, NavigationViewSelectionChangedEventArgs args)
        {
            if (args.SelectedItemContainer is NavigationViewItem item)
            {
                switch (item.Tag.ToString())
                {
                    case "dashboard":
                        ContentFrame.Navigate(typeof(DashboardPage));
                        break;
                    case "settings":
                        ContentFrame.Navigate(typeof(SettingsPage));
                        break;
                    case "diagnostics":
                        ContentFrame.Navigate(typeof(DiagnosticsPage));
                        break;
                    case "pairing":
                        ContentFrame.Navigate(typeof(PairingPage));
                        break;
                }
            }
        }
    }
}
