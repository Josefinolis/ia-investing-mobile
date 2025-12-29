# IA Trading Mobile - Android App

## Project Overview

Native Android app for visualizing market sentiment data from the ia_trading backend API.

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose with Material 3
- **Architecture**: MVVM + Repository pattern
- **DI**: Hilt
- **Networking**: Retrofit + OkHttp
- **Async**: Kotlin Coroutines + Flow

## Project Structure

```
app/src/main/java/com/iatrading/mobile/
├── TradingApplication.kt      # Hilt Application
├── MainActivity.kt            # Single Activity
├── data/
│   ├── api/
│   │   └── TradingApi.kt      # Retrofit interface
│   ├── model/
│   │   └── Models.kt          # Data classes
│   └── repository/
│       └── TradingRepository.kt
├── di/
│   └── NetworkModule.kt       # Hilt DI module
├── ui/
│   ├── theme/                 # Material 3 theming
│   ├── navigation/
│   │   └── NavGraph.kt        # Navigation routes
│   ├── screens/
│   │   ├── dashboard/         # Ticker list
│   │   ├── detail/            # Ticker news & sentiment
│   │   └── addticker/         # Add new ticker
│   └── components/
│       ├── TickerCard.kt
│       ├── NewsItemCard.kt
│       └── SentimentBadge.kt
```

## Screens

### Dashboard
- List of followed tickers
- Sentiment score and signal for each
- Pull-to-refresh
- FAB to add ticker
- Swipe to delete (pending)

### Ticker Detail
- Sentiment summary card
- News list with status badges
- Filter by pending/analyzed
- Manual refresh button

### Add Ticker
- Ticker symbol input (uppercase letters only)
- Optional company name
- Validation feedback

## API Configuration

The app uses **product flavors** to handle different deployment targets:

| Flavor | Target | API URL |
|--------|--------|---------|
| `emulator` | Android Emulator | `http://10.0.2.2:8000` |
| `device` | Physical Device | Auto-detected host IP |

The `device` flavor automatically detects your computer's local IP at build time using `hostname -I`.

## Building

### For Android Emulator
```bash
# Build and install
./gradlew installEmulatorDebug

# Or just build the APK
./gradlew assembleEmulatorDebug
```

### For Physical Device (USB Debugging)
```bash
# Build and install - uses auto-detected host IP
./gradlew installDeviceDebug

# Or just build the APK
./gradlew assembleDeviceDebug
```

### Production Release
```bash
# Uses https://your-api-domain.com (configure in build.gradle.kts)
./gradlew assembleRelease
```

### Other Commands
```bash
# Run tests
./gradlew test

# Clean build
./gradlew clean

# List all build variants
./gradlew tasks --group=build
```

### Build Variants in Android Studio
1. Open **Build > Select Build Variant**
2. Choose from:
   - `emulatorDebug` - For emulator testing
   - `deviceDebug` - For physical device testing
   - `emulatorRelease` / `deviceRelease` - Release builds

## Dependencies

- Compose BOM 2024.02.00
- Hilt 2.48
- Retrofit 2.9.0
- OkHttp 4.12.0
- Navigation Compose 2.7.6
- Lifecycle 2.7.0

## Notes

- Requires Android SDK 26+ (Android 8.0)
- Uses cleartext traffic for development (HTTP)
- Backend (`ia_trading`) must be running on host machine port 8000:
  ```bash
  cd /home/os_uis/projects/ia_trading
  source venv/bin/activate
  uvicorn api.main:app --host 0.0.0.0 --port 8000
  ```
- Use `device` flavor for physical devices (auto-detects host IP)
- Use `emulator` flavor for Android Emulator (uses 10.0.2.2)
