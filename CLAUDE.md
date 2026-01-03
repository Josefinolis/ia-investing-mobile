# IA Trading Mobile - Android App

Native Android app for visualizing market sentiment data from the IA Trading API.

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
│   │   └── Models.kt          # Data classes (snake_case with @SerializedName)
│   └── repository/
│       ├── TradingRepository.kt
│       └── SettingsRepository.kt
├── di/
│   └── NetworkModule.kt       # Hilt DI module
├── ui/
│   ├── theme/                 # Material 3 theming
│   ├── navigation/
│   │   └── NavGraph.kt        # Navigation routes
│   ├── screens/
│   │   ├── dashboard/         # Ticker list
│   │   ├── detail/            # Ticker news & sentiment
│   │   ├── addticker/         # Add new ticker
│   │   └── settings/          # App settings
│   └── components/
│       ├── TickerCard.kt
│       ├── NewsItemCard.kt
│       ├── SentimentBadge.kt
│       └── ApiStatusBanner.kt
```

## Screens

### Dashboard
- List of followed tickers with sentiment scores
- Pull-to-refresh
- FAB to add ticker
- Tap to view details
- API status banner (shows rate limit warnings)

### Ticker Detail
- Sentiment summary card (score, signal, confidence)
- News list with analysis status badges
- **Fetch News** button - downloads new articles
- **Analyze** button - triggers AI sentiment analysis
- Shows pending/analyzed counts

### Add Ticker
- Ticker symbol input (uppercase letters and numbers)
- Optional company name
- Real-time validation

### Settings
- API endpoint configuration
- Theme preferences

## API Configuration

The app uses **product flavors** for different environments:

| Flavor | Target | API URL |
|--------|--------|---------|
| `emulator` | Android Emulator | `http://10.0.2.2:8080` |
| `device` | Physical Device | `http://195.20.235.94` |

## Building

### For Android Emulator (local API)
```bash
./gradlew installEmulatorDebug
```

### For Physical Device (production API)
```bash
./gradlew installDeviceDebug
```

### Production Release
```bash
./gradlew assembleRelease
```

### Other Commands
```bash
./gradlew test              # Run tests
./gradlew clean             # Clean build
./gradlew lint              # Run lint checks
```

## Build Variants (Android Studio)

1. Open **Build > Select Build Variant**
2. Choose:
   - `emulatorDebug` - Emulator + local API
   - `deviceDebug` - Physical device + production API

## API Integration

The app expects the API to return JSON with **snake_case** field names:

```json
{
  "ticker": "AAPL",
  "added_at": "2026-01-03T10:00:00",
  "is_active": true,
  "sentiment": {
    "normalized_score": 0.5,
    "sentiment_label": "Positive",
    "positive_count": 10
  }
}
```

### Endpoints Used

| Endpoint | Screen |
|----------|--------|
| `GET /api/tickers` | Dashboard |
| `POST /api/tickers` | Add Ticker |
| `DELETE /api/tickers/{ticker}` | Dashboard |
| `GET /api/tickers/{ticker}` | Detail |
| `GET /api/tickers/{ticker}/news` | Detail |
| `POST /api/tickers/{ticker}/fetch` | Detail |
| `POST /api/tickers/{ticker}/analyze` | Detail |
| `GET /api/status` | Dashboard, Detail |

## Dependencies

| Library | Version | Purpose |
|---------|---------|---------|
| Compose BOM | 2024.02.00 | UI framework |
| Hilt | 2.48 | Dependency injection |
| Retrofit | 2.9.0 | HTTP client |
| OkHttp | 4.12.0 | HTTP client |
| Navigation Compose | 2.7.6 | Navigation |
| Lifecycle | 2.7.0 | ViewModel, State |

## Requirements

- Android SDK 26+ (Android 8.0 Oreo)
- Kotlin 1.9+
- JDK 17+

## Network Security

- `emulator` flavor: Allows cleartext HTTP (for local development)
- `device` flavor: Uses HTTP to production VPS (should migrate to HTTPS)

## Production

- **Backend API**: http://195.20.235.94 (Kotlin/Spring Boot)
- **Repository**: https://github.com/Josefinolis/ia-investing-mobile
