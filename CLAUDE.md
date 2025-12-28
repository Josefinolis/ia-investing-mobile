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

Development (Android Emulator):
```kotlin
buildConfigField("String", "API_BASE_URL", "\"http://10.0.2.2:8000\"")
```

Production:
```kotlin
buildConfigField("String", "API_BASE_URL", "\"https://your-api-domain.com\"")
```

## Building

```bash
# Debug build
./gradlew assembleDebug

# Install on device
./gradlew installDebug

# Run tests
./gradlew test
```

## Dependencies

- Compose BOM 2024.02.00
- Hilt 2.48
- Retrofit 2.9.0
- OkHttp 4.12.0
- Navigation Compose 2.7.6
- Lifecycle 2.7.0

## Notes

- Requires Android SDK 26+ (Android 8.0)
- Uses cleartext traffic for development (http://10.0.2.2:8000)
- Backend must be running on host machine port 8000
