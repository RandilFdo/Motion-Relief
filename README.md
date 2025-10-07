# Motion Relief

A gentle visual overlay app designed to help reduce motion discomfort while using your Android device. Motion Relief adds subtle, animated periphery dots that can provide visual stability during scrolling, gaming, and other motion-heavy activities.

![WhatsApp Image 2025-09-08 at 20 21 06_6cfb90a9](https://github.com/user-attachments/assets/697839d1-6f0e-4f9a-b30b-d0af1cd51463)

<img width="1024" height="1024" alt="98f0c241-423a-45fa-ab56-1d1c80759ba9" src="https://github.com/user-attachments/assets/d0664796-a92d-4053-afa0-fbe6526a8447" />

## Features

- **Dual Overlay Modes**: Foreground overlay or accessibility service
- **Customizable Visuals**: Adjust size, speed, and color (black, white, or both)
- **Quick Access**: Toggle via Quick Settings tile
- **Privacy-First**: No data collection, no internet access required
- **Sensor-Based Animation**: Real-time motion sensor processing for responsive effects
- **Cross-App Support**: Works over any app, video, or game

## How It Works

Motion Relief renders a periphery overlay with animated dots that respond to device movement. The stable visual reference can help reduce visual-vestibular mismatch, a common trigger of motion discomfort.

## Installation

1. Download the APK from [releases](https://github.com/RandilFdo/Motion-Relief/releases)
2. Enable "Install from unknown sources" in Android settings
3. Install the APK
4. Grant overlay permission when prompted

## Usage

1. Open Motion Relief
2. Choose your preferred overlay mode (Foreground or Accessibility)
3. Grant the required permissions
4. Tap "Start" to enable the overlay
5. Adjust size and speed in Settings to your preference
6. Use the Quick Settings tile for easy toggling

## Permissions

- **SYSTEM_ALERT_WINDOW**: Draw overlay over other apps
- **ACCESSIBILITY_SERVICE**: Optional mode for overlay rendering (does not read accessibility content)
- **FOREGROUND_SERVICE**: Keep overlay running reliably
- **POST_NOTIFICATIONS**: Display service notification
- **VIBRATE**: Light haptic feedback
- **WAKE_LOCK**: Maintain service responsiveness
- **HIGH_SAMPLING_RATE_SENSORS**: Read motion sensors for animation

## Privacy

- **No data collection or sharing**
- **No internet access**
- **On-device processing only**
- Local settings stored in app cache only

## Building from Source

### Prerequisites

- Android Studio Arctic Fox or later
- Android SDK 21+ (Android 5.0)
- Kotlin 1.8+

### Build Steps

1. Clone the repository:
   ```bash
   git clone https://github.com/RandilFdo/Motion-Relief.git
   cd Motion-Relief
   ```

2. Open in Android Studio

3. Build the project:
   ```bash
   ./gradlew assembleDebug
   ```

4. Install on device:
   ```bash
   ./gradlew installDebug
   ```

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Disclaimer

**USE AT YOUR OWN RISK.**

Important:

* This app is not a substitute for professional medical advice, diagnosis, or treatment. Always seek the advice of your physician or other qualified health provider with any questions you may have regarding a medical condition.
* If you experience any adverse effects while using this app, discontinue use immediately and consult a healthcare professional.
* The developer of this app makes no guarantees or warranties, express or implied, regarding its safety, effectiveness, or suitability for any particular purpose.

By using this app, you acknowledge and accept that you do so at your own risk.

## Support

For issues, feature requests, or questions, please [open an issue](https://github.com/RandilFdo/Motion-Relief/issues) on GitHub.
