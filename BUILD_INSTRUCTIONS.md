# Building REDCode Editor - Comprehensive Guide

## Current Build Status

⚠️ **IMPORTANT**: The APK cannot be built in the current environment due to network restrictions that block access to Google's Maven repository (`dl.google.com`). This is required for downloading the Android Gradle Plugin (AGP) and related dependencies.

## Requirements for Successful Build

### Essential Requirements
1. **Network Access to Google Maven**
   - `https://maven.google.com` or `https://dl.google.com/dl/android/maven2/`
   - This is the ONLY source for Android Gradle Plugin
   - Most corporate networks and some regions block this

2. **Development Tools**
   - Android Studio (Arctic Fox or newer) - **RECOMMENDED**
   - OR: Command-line with:
     - JDK 17
     - Android SDK (API 34)
     - Gradle 8.9+ (included via wrapper)

3. **System Requirements**
   - Operating System: Windows, macOS, or Linux
   - RAM: At least 8GB (16GB recommended for Android Studio)
   - Disk Space: At least 10GB free

## Build Methods

### Method 1: Android Studio (Recommended)

This is the easiest and most reliable method.

1. **Install Android Studio**
   - Download from: https://developer.android.com/studio
   - Install with default settings
   - Ensure Android SDK API 34 is installed

2. **Open Project**
   ```
   - Launch Android Studio
   - Select "Open an Existing Project"
   - Navigate to the REDCode directory
   - Click "Open"
   ```

3. **Sync Gradle**
   - Android Studio will automatically sync Gradle
   - Wait for "Gradle sync finished" message
   - This may take several minutes on first run

4. **Build APK**
   ```
   - Menu → Build → Build Bundle(s) / APK(s) → Build APK(s)
   - OR: Click the hammer icon in toolbar
   - Wait for build to complete
   - APK location will be shown in notification
   ```

5. **Locate APK**
   ```
   Debug APK: app/build/outputs/apk/debug/app-debug.apk
   Release APK: app/build/outputs/apk/release/app-release-unsigned.apk
   ```

### Method 2: Command Line (Advanced)

For developers who prefer command-line tools.

#### Prerequisites Setup

**On Windows:**
```batch
# Set JAVA_HOME
set JAVA_HOME=C:\Program Files\Java\jdk-17
set PATH=%JAVA_HOME%\bin;%PATH%

# Set ANDROID_HOME
set ANDROID_HOME=C:\Users\YourUsername\AppData\Local\Android\Sdk
set PATH=%ANDROID_HOME%\platform-tools;%ANDROID_HOME%\build-tools\34.0.0;%PATH%
```

**On macOS/Linux:**
```bash
# Set JAVA_HOME
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home
export PATH=$JAVA_HOME/bin:$PATH

# Set ANDROID_HOME
export ANDROID_HOME=$HOME/Library/Android/sdk  # macOS
export ANDROID_HOME=$HOME/Android/Sdk          # Linux
export PATH=$ANDROID_HOME/platform-tools:$ANDROID_HOME/build-tools/34.0.0:$PATH
```

#### Build Commands

1. **Navigate to Project**
   ```bash
   cd REDCode
   ```

2. **Make Gradle Wrapper Executable** (Linux/macOS only)
   ```bash
   chmod +x gradlew
   ```

3. **Clean Previous Builds**
   ```bash
   ./gradlew clean          # Linux/macOS
   gradlew.bat clean        # Windows
   ```

4. **Build Debug APK**
   ```bash
   ./gradlew assembleDebug          # Linux/macOS
   gradlew.bat assembleDebug        # Windows
   ```

5. **Build Release APK**
   ```bash
   ./gradlew assembleRelease          # Linux/macOS
   gradlew.bat assembleRelease        # Windows
   ```

6. **Install on Connected Device**
   ```bash
   ./gradlew installDebug          # Linux/macOS
   gradlew.bat installDebug        # Windows
   ```

### Method 3: Using Pre-built APK (Easiest for Users)

If you're not a developer and just want to use the app:

1. Download the pre-built APK from the Releases page
2. Transfer to your Android device
3. Install directly (see Installation Guide below)

## Troubleshooting Build Issues

### Issue 1: Cannot Access Google Maven

**Symptoms:**
```
Could not GET 'https://dl.google.com/...'
dl.google.com: No address associated with hostname
```

**Solutions:**
1. **Check Internet Connection**
   - Ensure you have active internet
   - Try accessing https://maven.google.com in browser

2. **Use VPN**
   - If in China or restricted region
   - Connect to VPN with access to Google services

3. **Check Corporate Firewall**
   - Request IT to whitelist `maven.google.com` and `dl.google.com`
   - OR: Build from home network

4. **Use Android Studio**
   - Android Studio handles proxy settings better
   - Go to Settings → Appearance & Behavior → System Settings → HTTP Proxy
   - Configure proxy if needed

### Issue 2: Gradle Sync Failed

**Symptoms:**
```
Gradle sync failed: Could not resolve all dependencies
```

**Solutions:**
1. **Clear Gradle Cache**
   ```bash
   ./gradlew clean --no-daemon
   rm -rf ~/.gradle/caches/
   ./gradlew assembleDebug
   ```

2. **Check Gradle Version**
   ```bash
   ./gradlew --version
   ```
   Should be 8.9 or higher

3. **Update Gradle Wrapper**
   ```bash
   ./gradlew wrapper --gradle-version=8.9
   ```

### Issue 3: Java Version Mismatch

**Symptoms:**
```
Unsupported Java version / Java version mismatch
```

**Solution:**
```bash
# Check Java version
java -version
javac -version

# Should show Java 17
# If not, install JDK 17 and set JAVA_HOME
```

### Issue 4: Android SDK Not Found

**Symptoms:**
```
SDK location not found. Define location with sdk.dir in the local.properties file
```

**Solution:**
Create `local.properties` in project root:
```properties
# On Windows:
sdk.dir=C\:\\Users\\YourUsername\\AppData\\Local\\Android\\Sdk

# On macOS:
sdk.dir=/Users/YourUsername/Library/Android/sdk

# On Linux:
sdk.dir=/home/YourUsername/Android/Sdk
```

### Issue 5: Build Takes Too Long

**Solutions:**
1. **Enable Gradle Daemon**
   ```bash
   # In gradle.properties
   org.gradle.daemon=true
   org.gradle.parallel=true
   org.gradle.caching=true
   ```

2. **Increase Memory**
   ```bash
   # In gradle.properties
   org.gradle.jvmargs=-Xmx4096m -XX:MaxMetaspaceSize=512m
   ```

3. **Use Offline Mode** (after first successful build)
   ```bash
   ./gradlew assembleDebug --offline
   ```

## Build Variants

### Debug Build
- **Purpose**: Development and testing
- **Features**: 
  - Debuggable
  - Not minified
  - Larger APK size
  - Faster build time
- **Command**: `./gradlew assembleDebug`
- **Output**: `app/build/outputs/apk/debug/app-debug.apk`

### Release Build
- **Purpose**: Production distribution
- **Features**:
  - Optimized with ProGuard
  - Minified code
  - Smaller APK size
  - Not debuggable
- **Command**: `./gradlew assembleRelease`
- **Output**: `app/build/outputs/apk/release/app-release-unsigned.apk`
- **Note**: Needs signing for distribution

## Signing Release APK

For distribution outside this project:

### Generate Keystore
```bash
keytool -genkey -v -keystore redcode.keystore -alias redcode -keyalg RSA -keysize 2048 -validity 10000
```

### Configure Signing in build.gradle.kts
```kotlin
android {
    signingConfigs {
        create("release") {
            storeFile = file("redcode.keystore")
            storePassword = "your_password"
            keyAlias = "redcode"
            keyPassword = "your_password"
        }
    }
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            // ... other config
        }
    }
}
```

### Build Signed Release
```bash
./gradlew assembleRelease
```

## APK Installation Guide

### For Developers
1. **Enable USB Debugging** on Android device
2. **Connect via USB**
3. **Install via ADB**
   ```bash
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```

### For End Users
1. **Enable Unknown Sources**
   - Android 8.0+: Settings → Apps → Special Access → Install Unknown Apps
   - Older Android: Settings → Security → Unknown Sources

2. **Transfer APK**
   - Via USB, Bluetooth, or download

3. **Install**
   - Tap APK file
   - Follow prompts
   - Grant permissions

## Continuous Integration (CI/CD)

### GitHub Actions Example
```yaml
name: Android CI

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v3
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
    - name: Grant execute permission for gradlew
      run: chmod +x gradlew
    - name: Build with Gradle
      run: ./gradlew assembleDebug
    - name: Upload APK
      uses: actions/upload-artifact@v3
      with:
        name: app-debug
        path: app/build/outputs/apk/debug/app-debug.apk
```

## Performance Optimization

### Speed Up Builds
1. Use Gradle daemon
2. Enable parallel builds
3. Enable caching
4. Use `--offline` mode when possible
5. Increase heap size for Gradle

### Reduce APK Size
1. Enable ProGuard/R8 in release builds (already configured)
2. Remove unused resources
3. Use WebP instead of PNG for images
4. Enable app bundles (AAB) instead of APK

## Environment-Specific Notes

### Building in Restricted Networks
If Google Maven is blocked:
1. Use Android Studio with proxy settings
2. Build on a different network
3. Use mobile hotspot temporarily
4. Request IT department to whitelist required domains

### Building Without Internet
After first successful build with internet:
```bash
./gradlew assembleDebug --offline
```

This uses cached dependencies.

## Getting Help

If you encounter issues not covered here:

1. **Check Android Studio Logs**
   - View → Tool Windows → Build
   - Read full error messages

2. **Gradle Output**
   ```bash
   ./gradlew assembleDebug --stacktrace --info
   ```

3. **GitHub Issues**
   - https://github.com/thertxnetwork/REDCode/issues
   - Include error messages and logs

4. **Android Developer Documentation**
   - https://developer.android.com/studio/build

## Summary

- **Easiest**: Use Android Studio
- **For Devs**: Command-line with proper network access
- **Current Environment**: Cannot build due to Google Maven access restriction
- **Solution**: Build in Android Studio or environment with proper internet access

---

Last Updated: 2024-12-06
