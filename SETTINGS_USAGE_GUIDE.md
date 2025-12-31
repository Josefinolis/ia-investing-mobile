# Settings Screen - User Guide

## How to Configure API Base URL

### Quick Start

1. **Open Settings**
   - Launch the IA Trading app
   - From the Dashboard (Watchlist screen), tap the **Settings icon** (⚙️) in the top-right corner

2. **View Current Configuration**
   - The Settings screen shows:
     - **Current API URL**: The URL currently being used
     - **Default API URL**: The build-time default URL
     - **Custom URL Active** badge (if using a custom URL)

3. **Set a Custom URL**
   - Scroll to "Set Custom URL" section
   - Enter your API base URL (e.g., `http://192.168.1.100:8000`)
   - Tap **Save URL** button
   - You'll see a success message: "Settings saved successfully"

4. **Apply Changes**
   - **Important**: Close the app completely (swipe away from recent apps)
   - Reopen the app
   - The new URL will now be active

5. **Reset to Default**
   - Open Settings
   - Tap **Reset to Default** button
   - The app will revert to using the build-time default URL

## Common Use Cases

### Use Case 1: Connect to Local Development Server

**Scenario**: You're running the ia_trading backend on your local machine and want to test the mobile app.

**For Android Emulator**:
```
1. Start your backend: cd /home/os_uis/projects/ia_trading && uvicorn api.main:app --host 0.0.0.0 --port 8000
2. Open Settings in the app
3. Enter: http://10.0.2.2:8000
4. Save and restart the app
```

**For Physical Device (same WiFi network)**:
```
1. Find your computer's local IP (e.g., 192.168.1.100)
2. Start backend with --host 0.0.0.0
3. Open Settings in the app
4. Enter: http://192.168.1.100:8000
5. Save and restart the app
```

### Use Case 2: Connect to Staging/Production Server

**Scenario**: Test against a remote backend environment.

```
1. Open Settings in the app
2. Enter: https://your-staging-server.com
3. Save and restart the app
```

### Use Case 3: Switch Between Environments

**Scenario**: Quickly switch between development and production.

```
Development:
- Open Settings → Enter http://10.0.2.2:8000 → Save → Restart

Production:
- Open Settings → Tap "Reset to Default" → Restart
  (Uses production URL from build flavor)
```

## URL Format Requirements

### Valid URLs
```
✅ http://10.0.2.2:8000
✅ http://192.168.1.100:8000
✅ https://api.example.com
✅ https://ia-investing.onrender.com
✅ http://localhost:8000  (works on physical device if backend is on same device)
```

### Invalid URLs
```
❌ 10.0.2.2:8000              (Missing protocol)
❌ www.example.com            (Missing protocol)
❌ http://10.0.2.2:8000/      (Trailing slash - will be auto-removed)
❌ http://10.0.2.2:8000/api   (Should be base URL only, not endpoint)
❌ (empty)                     (URL required)
```

### Notes
- Protocol (`http://` or `https://`) is **required**
- Port numbers are optional (defaults to 80 for HTTP, 443 for HTTPS)
- Trailing slashes are automatically removed
- Do NOT include `/api` or other paths - enter the base URL only

## Troubleshooting

### Problem: Changes Don't Take Effect

**Solution**: Make sure you completely restart the app
```
1. Open Android Recent Apps (square button or swipe up gesture)
2. Swipe away the IA Trading app
3. Reopen the app from launcher
```

Why? The Retrofit HTTP client is initialized when the app starts. Changing settings only updates DataStore; the Retrofit instance needs to be recreated by restarting the app.

### Problem: "Network error" or "Connection refused"

**Possible Causes**:
1. **Backend not running**: Start your backend server
2. **Wrong IP address**: Double-check your computer's IP (emulator: use 10.0.2.2)
3. **Firewall blocking**: Allow port 8000 through your firewall
4. **Wrong port**: Verify backend is running on the port you entered
5. **Not on same network**: Physical device must be on same WiFi as backend

**Debug Steps**:
```
1. Check backend is running: curl http://localhost:8000
2. Check IP is correct: hostname -I (Linux/Mac) or ipconfig (Windows)
3. Test from device browser: Open http://your-ip:8000 in mobile browser
4. Check Settings shows correct URL: Open Settings → Verify "Current API URL"
```

### Problem: App Shows Default URL After Setting Custom URL

**Possible Causes**:
1. Settings didn't save (check for error message)
2. DataStore write failed (check app permissions)
3. App cache cleared (re-enter custom URL)

**Solution**:
```
1. Open Settings
2. Check if "Custom URL is active" badge is shown
3. If not, re-enter the URL and save again
4. Restart the app
```

### Problem: Invalid URL Error

**Symptoms**: Red error text below the URL input field

**Solutions**:
- "URL cannot be empty": Enter a URL
- "URL must start with http:// or https://": Add protocol prefix
- "Failed to save": Check app permissions, try again

## Build Flavors vs Custom URL

### Default URLs by Build Flavor

| Build Flavor | Default URL | Purpose |
|--------------|-------------|---------|
| emulatorDebug | `http://10.0.2.2:8000` | Local dev on emulator |
| deviceDebug | `https://ia-investing.onrender.com` | Production on physical device |

### Custom URL Priority

```
Custom URL (if set)
     ↓
Takes precedence over build flavor default
     ↓
Persists across app restarts
     ↓
Reset to Default → Uses build flavor default
```

**Example**:
```
Build Flavor: deviceDebug (default: https://ia-investing.onrender.com)
Custom URL set: http://192.168.1.100:8000

App uses: http://192.168.1.100:8000 (custom URL wins)

After reset:
App uses: https://ia-investing.onrender.com (back to default)
```

## Testing Your Configuration

### 1. Verify URL is Active
```
1. Open Settings
2. Check "Current API URL" matches what you entered
3. Look for "Custom URL is active" badge
```

### 2. Test API Connection
```
1. Go back to Dashboard
2. Pull to refresh (swipe down)
3. If tickers load → Connection successful ✅
4. If error appears → Check backend and URL ❌
```

### 3. View Network Logs (Developer)
```
1. Connect device via USB debugging
2. Run: adb logcat | grep "OkHttp"
3. Look for HTTP requests to verify URL being used
```

## Security Considerations

### HTTP vs HTTPS

**Development (HTTP)**:
```
✅ Use for local development (10.0.2.2, 192.168.x.x, localhost)
✅ Faster for testing (no SSL overhead)
⚠️  Only on trusted networks
```

**Production (HTTPS)**:
```
✅ Always use HTTPS for production/staging
✅ Encrypts all data in transit
✅ Required for public/untrusted networks
⚠️  Requires valid SSL certificate
```

### Network Security Config

The app allows cleartext (HTTP) traffic in debug builds for local development. Production builds should use HTTPS.

## Tips & Best Practices

1. **Save Your URLs**: Keep a note of URLs you frequently use
2. **Test Connection**: After changing URL, test with a simple refresh
3. **Reset if Unsure**: Use "Reset to Default" if you're unsure what URL is active
4. **Restart App**: Always restart after changing URL
5. **Check Backend Logs**: Monitor backend console for incoming requests

## Advanced: Multiple Environments

If you frequently switch between environments, consider:

1. **Keep a reference list**:
   ```
   Local Dev:   http://10.0.2.2:8000
   Staging:     https://staging.example.com
   Production:  https://ia-investing.onrender.com
   ```

2. **Quick switch workflow**:
   ```
   Settings → Paste URL → Save → Restart (< 10 seconds)
   ```

3. **Default fallback**:
   ```
   Use build flavors for your most common environment
   Use custom URL for temporary testing
   ```

## Support

If you encounter issues:

1. Check this guide's Troubleshooting section
2. Verify backend is running and accessible
3. Try resetting to default and restarting
4. Check app permissions (storage, network)
5. Clear app data and reconfigure (last resort)

## Examples

### Example 1: Emulator to Local Backend
```
Computer: Running ia_trading on localhost:8000
Emulator: Android Emulator on same machine

Settings Configuration:
- URL: http://10.0.2.2:8000
- Why: 10.0.2.2 is the emulator's way to reach host's localhost
- Result: App connects to local backend
```

### Example 2: Phone to Computer on Same WiFi
```
Computer: IP 192.168.1.150, running backend on port 8000
Phone: Connected to same WiFi network

Settings Configuration:
- URL: http://192.168.1.150:8000
- Why: Direct connection over local network
- Result: App connects to local backend from phone
```

### Example 3: Production Deployment
```
Backend: Deployed at https://ia-investing.onrender.com
App: deviceDebug build flavor

Settings Configuration:
- No custom URL needed (uses default)
- OR: Reset to Default
- Result: App connects to production API
```
