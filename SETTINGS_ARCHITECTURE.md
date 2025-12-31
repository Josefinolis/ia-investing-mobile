# Settings Feature Architecture

## Component Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                         Presentation Layer                       │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌──────────────────┐              ┌────────────────────┐      │
│  │ DashboardScreen  │              │  SettingsScreen    │      │
│  │                  │  navigates   │                    │      │
│  │ - Settings Icon  │─────────────>│ - URL Input        │      │
│  │   in TopAppBar   │              │ - Save Button      │      │
│  └──────────────────┘              │ - Reset Button     │      │
│                                     └──────────┬─────────┘      │
│                                                │                 │
│                                                │ observes        │
│                                                │ StateFlow       │
│                                                v                 │
│                                     ┌────────────────────┐      │
│                                     │ SettingsViewModel  │      │
│                                     │                    │      │
│                                     │ - uiState          │      │
│                                     │ - saveUrl()        │      │
│                                     │ - resetToDefault() │      │
│                                     └──────────┬─────────┘      │
│                                                │                 │
└────────────────────────────────────────────────┼─────────────────┘
                                                 │
                                                 │ injects
                                                 │ (Hilt)
                                                 v
┌─────────────────────────────────────────────────────────────────┐
│                           Data Layer                             │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │              SettingsRepository (@Singleton)             │  │
│  │                                                          │  │
│  │  - apiBaseUrl: Flow<String>                             │  │
│  │  - isCustomUrlSet: Flow<Boolean>                        │  │
│  │  + setApiBaseUrl(url: String)                           │  │
│  │  + resetToDefaultUrl()                                  │  │
│  │  + getDefaultBaseUrl(): String                          │  │
│  │                                                          │  │
│  └────────────────────┬───────────────────┬─────────────────┘  │
│                       │                   │                    │
│                       │                   │ provides           │
│                       │ reads/writes      │ to DI              │
│                       v                   v                    │
│              ┌─────────────────┐  ┌─────────────────┐         │
│              │   DataStore     │  │  NetworkModule  │         │
│              │   Preferences   │  │                 │         │
│              │                 │  │  - Retrofit     │         │
│              │ - api_base_url  │  │  - OkHttpClient │         │
│              │   (String)      │  │  - TradingApi   │         │
│              └─────────────────┘  └─────────────────┘         │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

## Data Flow

### 1. Initial App Launch
```
App Start
   │
   ├─> NetworkModule.provideSettingsRepository()
   │       │
   │       └─> Creates SettingsRepository with Context
   │
   └─> NetworkModule.provideRetrofit()
           │
           ├─> Injects SettingsRepository
           │
           ├─> runBlocking {
           │       settingsRepository.apiBaseUrl.first()
           │   }
           │       │
           │       └─> Reads from DataStore
           │           │
           │           ├─> If custom URL exists: Returns custom URL
           │           └─> If not: Returns BuildConfig.API_BASE_URL
           │
           └─> Creates Retrofit with base URL + "/"
```

### 2. User Sets Custom URL
```
User clicks Settings icon
   │
   └─> Navigates to SettingsScreen
           │
           ├─> ViewModel loads current state from SettingsRepository
           │   (observes apiBaseUrl Flow)
           │
           └─> User enters URL and clicks Save
                   │
                   └─> SettingsViewModel.saveUrl()
                           │
                           ├─> Validates URL format
                           │
                           └─> SettingsRepository.setApiBaseUrl(url)
                                   │
                                   └─> DataStore.edit { preferences[KEY] = url }
                                           │
                                           └─> Persists to disk
                                                   │
                                                   └─> Flow emits new value
                                                           │
                                                           └─> UI updates to show custom URL active
```

### 3. User Resets to Default
```
User clicks "Reset to Default"
   │
   └─> SettingsViewModel.resetToDefault()
           │
           └─> SettingsRepository.resetToDefaultUrl()
                   │
                   └─> DataStore.edit { preferences.remove(KEY) }
                           │
                           └─> Flow emits BuildConfig.API_BASE_URL
                                   │
                                   └─> UI updates to show default URL
```

## State Management

### SettingsUiState
```kotlin
data class SettingsUiState(
    val currentUrl: String = "",         // Active URL (from DataStore or BuildConfig)
    val inputUrl: String = "",           // User input in text field
    val defaultUrl: String = "",         // BuildConfig.API_BASE_URL
    val isCustomUrlSet: Boolean = false, // Whether custom URL is active
    val isSaving: Boolean = false,       // Loading state during save/reset
    val saveSuccess: Boolean = false,    // Trigger success message
    val error: String? = null            // Error message to display
)
```

### State Transitions
```
Initial State
   │
   ├─> ViewModel.loadSettings()
   │       │
   │       └─> Combines apiBaseUrl and isCustomUrlSet flows
   │               │
   │               └─> Updates uiState with current/default/isCustom
   │
   ├─> User types in text field
   │       │
   │       └─> ViewModel.updateInputUrl(url)
   │               │
   │               └─> Updates uiState.inputUrl, clears error/success
   │
   ├─> User clicks Save
   │       │
   │       ├─> Set isSaving = true
   │       ├─> Validate URL
   │       │   ├─> Invalid: Set error message
   │       │   └─> Valid: Call repository.setApiBaseUrl()
   │       │           ├─> Success: Set saveSuccess = true
   │       │           └─> Error: Set error message
   │       └─> Set isSaving = false
   │
   └─> User clicks Reset
           │
           ├─> Set isSaving = true
           ├─> Call repository.resetToDefaultUrl()
           │   ├─> Success: Set saveSuccess = true, clear inputUrl
           │   └─> Error: Set error message
           └─> Set isSaving = false
```

## Dependency Injection (Hilt)

### NetworkModule
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides @Singleton
    fun provideSettingsRepository(context: Context): SettingsRepository

    @Provides @Singleton
    fun provideOkHttpClient(): OkHttpClient

    @Provides @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        settingsRepository: SettingsRepository  // Dependency
    ): Retrofit

    @Provides @Singleton
    fun provideTradingApi(retrofit: Retrofit): TradingApi
}
```

### Injection Points
1. **SettingsRepository** → NetworkModule (for Retrofit)
2. **SettingsRepository** → SettingsViewModel (for UI)
3. **TradingRepository** → ViewModels (uses TradingApi)

## Persistence Layer

### DataStore Structure
```
File: /data/data/com.iatrading.mobile/files/datastore/settings.preferences_pb

Content (Protocol Buffer):
┌────────────────────────┐
│ Preferences            │
├────────────────────────┤
│ "api_base_url": String │  ← Only present if custom URL is set
└────────────────────────┘
```

### DataStore Flow Pattern
```kotlin
// Read as Flow (reactive)
val apiBaseUrl: Flow<String> = dataStore.data.map { preferences ->
    preferences[API_BASE_URL_KEY] ?: BuildConfig.API_BASE_URL
}

// Write (suspend function)
suspend fun setApiBaseUrl(url: String) {
    dataStore.edit { preferences ->
        preferences[API_BASE_URL_KEY] = url
    }
}

// Delete (suspend function)
suspend fun resetToDefaultUrl() {
    dataStore.edit { preferences ->
        preferences.remove(API_BASE_URL_KEY)
    }
}
```

## Navigation Flow

```
┌────────────────┐
│ DashboardScreen│
│ (start dest)   │
└───┬────────────┘
    │
    ├─> Ticker clicked ──> TickerDetailScreen
    │
    ├─> Add FAB clicked ──> AddTickerScreen
    │
    └─> Settings icon clicked
            │
            v
    ┌────────────────┐
    │ SettingsScreen │
    └───┬────────────┘
        │
        └─> Back button ──> Pop back to Dashboard
```

## Thread Safety

1. **DataStore**: Thread-safe by design (uses coroutines)
2. **Retrofit**: Created once at app start (singleton)
3. **SettingsRepository**: Singleton, all operations suspend functions
4. **ViewModel**: StateFlow ensures thread-safe UI updates

## Error Handling

```
User Input
   │
   └─> SettingsViewModel.saveUrl()
           │
           ├─> Empty URL → "URL cannot be empty"
           ├─> No protocol → "URL must start with http:// or https://"
           ├─> Valid URL → Try to save
           │       ├─> Success → Show success snackbar
           │       └─> Exception → "Failed to save: {message}"
           │
           └─> All errors shown in UI (Text below TextField)
```

## Performance Considerations

1. **Lazy DataStore initialization**: Only reads when needed
2. **Flow-based reactivity**: UI updates automatically when settings change
3. **Singleton Retrofit**: Created once, reused throughout app
4. **runBlocking in DI**: Acceptable as it runs once at app startup
5. **Minimal I/O**: Only writes to DataStore when user saves/resets

## Build Variants Impact

| Flavor | BuildConfig.API_BASE_URL | Custom URL Behavior |
|--------|--------------------------|---------------------|
| emulator | `http://10.0.2.2:8000` | Overrides emulator default |
| device | `https://ia-investing.onrender.com` | Overrides device default |

Custom URLs work across all flavors and persist across flavor switches.
