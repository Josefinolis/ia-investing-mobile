# Settings Screen Implementation

## Overview
Implemented a Settings screen that allows users to configure the API base URL at runtime using DataStore for persistence.

## Files Created/Modified

### New Files
1. **SettingsRepository.kt** (`/app/src/main/java/com/iatrading/mobile/data/repository/SettingsRepository.kt`)
   - Manages API base URL preferences using DataStore
   - Provides Flow of current API URL
   - Methods: `setApiBaseUrl()`, `resetToDefaultUrl()`, `getDefaultBaseUrl()`

2. **SettingsViewModel.kt** (`/app/src/main/java/com/iatrading/mobile/ui/screens/settings/SettingsViewModel.kt`)
   - Manages Settings screen state
   - Validates URL input (must start with http:// or https://)
   - Handles save/reset operations

3. **SettingsScreen.kt** (`/app/src/main/java/com/iatrading/mobile/ui/screens/settings/SettingsScreen.kt`)
   - Material 3 themed Settings UI
   - Displays current URL and default URL
   - Text field for custom URL input
   - Save and Reset buttons
   - Shows success/error messages via Snackbar

### Modified Files
1. **build.gradle.kts**
   - Added DataStore dependency: `androidx.datastore:datastore-preferences:1.0.0`

2. **NetworkModule.kt**
   - Updated to provide `SettingsRepository` as singleton
   - Modified `provideRetrofit()` to read base URL from DataStore
   - Uses `runBlocking` to read URL during DI initialization

3. **NavGraph.kt**
   - Added `Settings` screen route
   - Updated `DashboardScreen` composable to accept `onSettingsClick` parameter
   - Added Settings screen navigation

4. **DashboardScreen.kt**
   - Added `onSettingsClick` parameter
   - Added Settings icon button to TopAppBar
   - Import for `Icons.Default.Settings`

5. **strings.xml**
   - Added 12 new string resources for Settings screen

## Features

### 1. Dynamic API Base URL Configuration
- Users can set a custom API base URL at runtime
- Settings persist across app restarts using DataStore
- Falls back to BuildConfig.API_BASE_URL if no custom URL is set

### 2. URL Validation
- Ensures URL starts with `http://` or `https://`
- Removes trailing slashes
- Shows clear error messages for invalid input

### 3. Reset to Default
- One-click reset to BuildConfig default URL
- Clears custom URL from DataStore

### 4. Visual Feedback
- Shows current active URL
- Shows default URL for reference
- Indicates when a custom URL is active
- Success/error messages via Snackbar
- Loading states during save/reset operations

### 5. Material 3 Design
- Follows existing app theme
- Color-coded cards for different information types
- Responsive layout with proper spacing
- Accessible with content descriptions

## Usage

### For Users
1. Open the app and navigate to Dashboard
2. Tap the Settings icon (gear) in the top-right corner
3. Enter a custom API URL (e.g., `http://192.168.1.100:8000`)
4. Tap "Save URL"
5. **Restart the app** for changes to take effect
6. To revert: Tap "Reset to Default" button

### For Developers
```kotlin
// Access SettingsRepository anywhere via Hilt injection
@Inject lateinit var settingsRepository: SettingsRepository

// Read current URL as Flow
settingsRepository.apiBaseUrl.collect { url ->
    println("Current API URL: $url")
}

// Set custom URL
settingsRepository.setApiBaseUrl("https://api.example.com")

// Reset to default
settingsRepository.resetToDefaultUrl()

// Get default URL
val defaultUrl = settingsRepository.getDefaultBaseUrl()
```

## Technical Details

### DataStore Preferences
- Uses Preferences DataStore (not Proto DataStore)
- Key: `"api_base_url"` (String)
- Location: `/data/data/com.iatrading.mobile/files/datastore/settings.preferences_pb`

### Dependency Injection
- `SettingsRepository` is provided as `@Singleton` in `NetworkModule`
- Retrofit instance reads URL during initialization via `runBlocking`
- This approach ensures the URL is available when Retrofit is created

### Important Notes
1. **App Restart Required**: Changes to the API URL require a full app restart because Retrofit is initialized as a singleton at app startup
2. **BuildConfig Fallback**: If no custom URL is set, the app uses `BuildConfig.API_BASE_URL` from the selected build flavor (emulator/device)
3. **Validation**: URLs must start with `http://` or `https://` - the app will show an error otherwise

## Testing

### Test Scenarios
1. **Set custom URL**: Enter valid URL and save
2. **Invalid URL**: Try URLs without protocol (should show error)
3. **Reset**: Click reset and verify URL returns to default
4. **Persistence**: Set URL, restart app, verify URL persists
5. **Empty URL**: Try to save empty URL (should show error)

### Build Variants
The default URL differs by build flavor:
- **emulatorDebug**: `http://10.0.2.2:8000` (local backend)
- **deviceDebug**: `https://ia-investing.onrender.com` (production)

Custom URLs override both flavors.

## Future Enhancements (Optional)
- Add URL validation to check if backend is reachable
- Add "Test Connection" button
- Support for additional settings (timeouts, API keys, etc.)
- Dynamic Retrofit reinitialization without app restart (advanced)
- Settings export/import functionality
