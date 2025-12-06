# REDCode - Android Code Editor

A professional Android code editor application built with Rosemoe's Sora Editor library, supporting Python, HTML, CSS, JavaScript, and TypeScript.

## ⚠️ Build Status

**Current Status:** Project structure created, but APK build is blocked due to network restrictions.

### Build Issue

The project cannot currently build APK files because:
- The Google Maven repository (`dl.google.com`) is not accessible in the current environment
- The Android Gradle Plugin (AGP) is only available from Google's Maven repository
- Alternative mirrors and proxies are also blocked

### Workaround

To build this project successfully, you need:

1. **Access to Google Maven Repository**: Ensure your network/environment can reach `https://maven.google.com` or `https://dl.google.com/dl/android/maven2/`
2. **Android Studio**: Open this project in Android Studio which handles repository access better
3. **VPN/Proxy**: If in a restricted network, use a VPN or proxy that allows access to Google services

## Features

### Implemented Features
- ✅ Sora Editor integration with syntax highlighting
- ✅ Material Design 3 UI components
- ✅ Dark and light theme support (automatic)
- ✅ Tabbed interface for multiple files
- ✅ File management (open, save, create, close)
- ✅ Status bar showing cursor position and language mode
- ✅ Line numbers and code formatting
- ✅ Modern Material You design language

### Planned Features (Not Yet Implemented)
- Language Server Protocol (LSP) integration
- Real-time auto-complete and suggestions
- Linting and diagnostics
- Search and replace functionality
- File explorer/tree view
- Advanced editor features (bracket matching, auto-indentation)
- Settings panel with preferences

## Project Structure

```
REDCode/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── kotlin/com/redcode/editor/
│   │   │   │   ├── model/
│   │   │   │   │   └── EditorFile.kt          # Data model for editor files
│   │   │   │   ├── ui/
│   │   │   │   │   ├── MainActivity.kt         # Main activity with tabs
│   │   │   │   │   ├── EditorFragment.kt       # Individual editor instance
│   │   │   │   │   └── SettingsActivity.kt     # Settings screen
│   │   │   │   └── utils/
│   │   │   │       └── FileManager.kt          # File I/O operations
│   │   │   ├── res/
│   │   │   │   ├── layout/                      # XML layouts
│   │   │   │   ├── values/                      # Strings, colors, themes
│   │   │   │   └── drawable/                    # Icons and drawables
│   │   │   └── AndroidManifest.xml
│   │   └── build.gradle.kts
│   └── proguard-rules.pro
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

## Technical Details

### Dependencies
- **Sora Editor**: `io.github.rosemoe.sora-editor:editor:0.23.4`
- **Language Support**: `io.github.rosemoe.sora-editor:language-textmate:0.23.4`
- **AndroidX**: Core, AppCompat, Material Design 3, Lifecycle, Compose
- **Coroutines**: For async operations
- **Document File**: For Storage Access Framework

### Requirements
- **Minimum SDK**: Android 7.0 (API 24)
- **Target SDK**: Android 14 (API 34)
- **Compile SDK**: Android 14 (API 34)
- **Language**: Kotlin
- **Build System**: Gradle 8.9 with Kotlin DSL

## Building from Source

### Prerequisites
1. **Android Studio**: Arctic Fox or newer
2. **JDK**: Version 17
3. **Android SDK**: API 34 installed
4. **Network Access**: To Google Maven repository

### Build Steps

1. **Clone the Repository**
```bash
git clone https://github.com/thertxnetwork/REDCode.git
cd REDCode
```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an Existing Project"
   - Navigate to the cloned REDCode directory
   - Wait for Gradle sync to complete

3. **Build Debug APK**
```bash
./gradlew assembleDebug
```

The APK will be located at: `app/build/outputs/apk/debug/app-debug.apk`

4. **Build Release APK**
```bash
./gradlew assembleRelease
```

The APK will be located at: `app/build/outputs/apk/release/app-release.apk`

### Build with Gradle (Command Line)

If you have proper network access:

```bash
# Clean build
./gradlew clean

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Install on connected device
./gradlew installDebug
```

## Installation

### Installing APK on Android Device

1. **Enable Unknown Sources**
   - Go to Settings → Security
   - Enable "Install from Unknown Sources" or "Install Unknown Apps"
   - Select your file manager or browser

2. **Transfer APK**
   - Download the APK to your Android device
   - Or transfer via USB/ADB

3. **Install**
   - Open the APK file
   - Tap "Install"
   - Grant necessary permissions when prompted

### Permissions Required
- **Storage Access**: To read and write files
- **Internet**: For future features (LSP, updates)

## Usage

1. **Open Files**
   - Tap the menu icon (three lines) in the top-left
   - Select "Open File"
   - Choose a file from your device

2. **Create New File**
   - Tap the floating "+" button
   - Start editing in a new tab

3. **Save Files**
   - Tap the menu icon
   - Select "Save File"
   - Choose location and filename

4. **Multiple Tabs**
   - Files open in separate tabs
   - Swipe left/right to switch between files
   - Tap "Close Tab" from menu to close current file

5. **Settings**
   - Access from menu
   - Configure line numbers, word wrap, auto-complete

## Architecture

### MVVM Pattern
- **Model**: `EditorFile` data class
- **View**: Activities and Fragments
- **ViewModel**: (To be implemented for advanced features)

### Key Components

#### MainActivity
- Manages tab layout and ViewPager2
- Handles file operations (open, save, close)
- Coordinates between fragments
- Updates status bar

#### EditorFragment
- Hosts Sora Editor widget
- Manages individual file editing
- Tracks cursor position and file state
- Handles text change events

#### FileManager
- Abstracts file I/O operations
- Uses Storage Access Framework
- Supports content:// URIs

## Supported Languages

- **Python** (`.py`)
- **JavaScript** (`.js`)
- **TypeScript** (`.ts`)
- **HTML** (`.html`)
- **CSS** (`.css`)
- **Plain Text** (`.txt`)

Language is auto-detected based on file extension.

## Themes

- **Dark Theme**: Enabled automatically when system dark mode is on
- **Light Theme**: Default theme
- Themes use Material Design 3 color system

## Known Issues

1. **Build Blocked**: Cannot build APK due to Google Maven repository access restrictions
2. **LSP Not Implemented**: Language Server Protocol features are planned but not yet implemented
3. **Limited Auto-Complete**: Basic syntax highlighting only, no intelligent completions yet
4. **No File Explorer**: Must use system file picker

## Roadmap

### Phase 1 (Current)
- [x] Basic project structure
- [x] Sora Editor integration
- [x] File management
- [x] Multi-tab support
- [ ] Build successful APK

### Phase 2 (Planned)
- [ ] LSP client implementation
- [ ] Auto-complete with LSP
- [ ] Real-time diagnostics and linting
- [ ] Search and replace
- [ ] File tree explorer

### Phase 3 (Future)
- [ ] Git integration
- [ ] Terminal emulator
- [ ] Plugin system
- [ ] Cloud storage support
- [ ] Collaborative editing

## Contributing

Contributions are welcome! Please feel free to submit pull requests or open issues.

## License

See the [LICENSE](LICENSE) file for details.

## Acknowledgments

- **Rosemoe's Sora Editor**: The core editor component
- **Material Design 3**: UI design system
- **Android Jetpack**: Modern Android development libraries

## Support

For issues, questions, or contributions, please visit:
- GitHub Issues: https://github.com/thertxnetwork/REDCode/issues

---

**Note**: This project is currently in development. The APK build is blocked in certain environments due to network restrictions. For the best development experience, use Android Studio with proper internet access.
