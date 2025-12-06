# GitHub Actions CI/CD Setup Guide

This guide explains how to set up and use the automated build and release system for REDCode Android Editor.

## Overview

The GitHub Actions workflow provides:
- ✅ Automated testing (lint and unit tests)
- ✅ Automatic APK builds (debug and release)
- ✅ APK signing for releases
- ✅ Artifact upload with version tagging
- ✅ Automatic GitHub releases on version tags
- ✅ Build versioning based on git commits
- ✅ ProGuard mapping file retention

## Workflow Triggers

The workflow runs automatically on:
- **Push to branches**: `main`, `master`, `develop`, or `copilot/add-android-code-editor`
- **Pull Requests**: To `main` or `master` branches
- **Tag pushes**: Version tags like `v1.0.0`, `v1.1.0`, etc.
- **Manual trigger**: Via GitHub Actions UI (workflow_dispatch)

## Workflow Jobs

### 1. Test Job
- Runs lint checks
- Executes unit tests
- Uploads test and lint results as artifacts

### 2. Build Job
- Builds debug APK
- Builds release APK (signed if credentials available)
- Automatically increments version based on commit count
- Uploads APKs and mapping files as artifacts

### 3. Release Job (only on tags)
- Creates GitHub release
- Attaches APK files
- Generates release notes from commit history
- Includes build information

---

## Setting Up APK Signing

### Step 1: Generate Keystore

If you don't have a keystore, generate one:

```bash
keytool -genkey -v -keystore release-keystore.jks \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias redcode-release-key
```

**You'll be prompted for:**
- Keystore password (remember this!)
- Key password (can be same as keystore password)
- Your name, organization, location, etc.

**Keep this file secure and never commit it to git!**

### Step 2: Encode Keystore to Base64

```bash
# On Linux/macOS
base64 release-keystore.jks > keystore-base64.txt

# On Windows (PowerShell)
[Convert]::ToBase64String([IO.File]::ReadAllBytes("release-keystore.jks")) > keystore-base64.txt
```

### Step 3: Add Secrets to GitHub

1. Go to your repository on GitHub
2. Navigate to **Settings** → **Secrets and variables** → **Actions**
3. Click **New repository secret**
4. Add the following secrets:

| Secret Name | Description | Example |
|-------------|-------------|---------|
| `KEYSTORE_FILE` | Base64-encoded keystore content | (paste content from keystore-base64.txt) |
| `KEYSTORE_PASSWORD` | Keystore password | your_keystore_password |
| `KEY_ALIAS` | Key alias name | redcode-release-key |
| `KEY_PASSWORD` | Key password | your_key_password |

**Important Notes:**
- Don't include quotes around the values
- The `KEYSTORE_FILE` should be the entire base64 string (will be very long)
- Keep these secrets secure and never share them

### Step 4: Verify Setup

After adding secrets:
1. Push a commit to `main` branch
2. Go to **Actions** tab
3. Watch the workflow run
4. Check if release APK is signed (download and verify)

---

## Version Management

### Automatic Versioning

The workflow automatically manages versions:

- **Version Code**: Auto-incremented based on commit count
- **Version Name**: 
  - For tags: Uses tag name (e.g., `v1.0.0` → `1.0.0`)
  - For commits: `1.0.<commit_count>-<short_sha>` (e.g., `1.0.42-abc1234`)

### Creating Releases

To create a new release:

```bash
# Create and push a version tag
git tag v1.0.0
git push origin v1.0.0
```

Or use semantic versioning:
```bash
git tag v1.1.0  # Minor version
git tag v2.0.0  # Major version
git tag v1.0.1  # Patch version
```

The workflow will automatically:
1. Build signed APKs
2. Create a GitHub release
3. Attach APK files
4. Generate release notes

---

## Downloading APKs

### From Actions Tab

For any build:
1. Go to **Actions** tab
2. Click on a workflow run
3. Scroll to **Artifacts** section
4. Download APK artifacts:
   - `REDCode-debug-vX.X.X` - Debug APK
   - `REDCode-release-vX.X.X` - Release APK (signed if configured)
   - `build-info-vX.X.X` - Build information

### From Releases Page

For tagged releases:
1. Go to **Releases** section (or `/releases` URL)
2. Find the release version
3. Download APK from **Assets** section

---

## Build Status Badge

Add this badge to your README.md to show build status:

```markdown
![Android CI/CD](https://github.com/thertxnetwork/REDCode/actions/workflows/android-build.yml/badge.svg)
```

Or for specific branch:
```markdown
![Android CI/CD](https://github.com/thertxnetwork/REDCode/actions/workflows/android-build.yml/badge.svg?branch=main)
```

---

## Testing Locally

To test the build configuration locally (without signing):

```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK (unsigned)
./gradlew assembleRelease

# Run tests
./gradlew testDebugUnitTest

# Run lint
./gradlew lintDebug
```

To test with signing locally:

```bash
./gradlew assembleRelease \
  -Pandroid.injected.signing.store.file=/path/to/your/keystore.jks \
  -Pandroid.injected.signing.store.password=your_password \
  -Pandroid.injected.signing.key.alias=your_alias \
  -Pandroid.injected.signing.key.password=your_key_password
```

---

## Troubleshooting

### Build Fails with "Keystore not found"

**Problem**: The workflow can't find or decode the keystore.

**Solution**:
1. Verify `KEYSTORE_FILE` secret is set correctly
2. Ensure the base64 encoding is correct
3. Check that the secret doesn't have extra spaces or line breaks

### APK is unsigned

**Problem**: Release APK shows as "unsigned" in filename.

**Solution**:
1. Ensure all four secrets are added (`KEYSTORE_FILE`, `KEYSTORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD`)
2. Check the workflow logs for signing errors
3. Verify keystore password and key password are correct

### Version not updating

**Problem**: Version stays at 1.0 despite multiple commits.

**Solution**:
- Version auto-increments are based on commit count
- For custom versions, create and push tags
- Check workflow logs for version extraction step

### Tests failing

**Problem**: Test job fails preventing build.

**Solution**:
1. Run tests locally: `./gradlew testDebugUnitTest`
2. Fix failing tests
3. Commit and push changes
4. Tests marked as `continue-on-error: true` won't block builds

### Artifact not found

**Problem**: Can't download APK artifacts.

**Solution**:
1. Check if workflow completed successfully
2. Artifacts are retained for 30 days only
3. Look in the specific workflow run's Artifacts section

---

## Security Best Practices

1. **Never commit keystore files** - Add `*.jks`, `*.keystore` to `.gitignore`
2. **Use strong passwords** - Minimum 8 characters, mix of characters
3. **Rotate keys periodically** - Update secrets when needed
4. **Limit secret access** - Only repository admins should have access
5. **Use different keystores** - Separate keystores for debug and release
6. **Backup keystore safely** - Store securely offline (you cannot recover it!)

---

## Advanced Configuration

### Custom Build Variants

To add custom build variants (e.g., staging, beta):

1. Update `app/build.gradle.kts`:
```kotlin
buildTypes {
    create("staging") {
        initWith(getByName("release"))
        applicationIdSuffix = ".staging"
        versionNameSuffix = "-staging"
    }
}
```

2. Update workflow to build staging variant:
```yaml
- name: Build Staging APK
  run: ./gradlew assembleStaging --stacktrace
```

### Custom Version Naming

To customize version names, modify the "Extract version info" step in the workflow:

```yaml
- name: Extract version info
  run: |
    VERSION="MyApp-$(date +'%Y%m%d')"
    echo "version=$VERSION" >> $GITHUB_OUTPUT
```

### Adding More Tests

To add instrumentation tests or other checks:

```yaml
- name: Run Instrumentation Tests
  uses: reactivecircus/android-emulator-runner@v2
  with:
    api-level: 29
    script: ./gradlew connectedCheck
```

---

## Workflow Files

- **Main workflow**: `.github/workflows/android-build.yml`
- **Build configuration**: `app/build.gradle.kts`
- **ProGuard rules**: `app/proguard-rules.pro`

---

## Support

For issues with the CI/CD setup:
1. Check workflow logs in Actions tab
2. Review this documentation
3. Check common issues in Troubleshooting section
4. Open an issue on GitHub

---

## Summary

✅ **Automated Testing** - Lint and unit tests on every commit  
✅ **Automatic Builds** - Debug and release APKs generated automatically  
✅ **Signed Releases** - Properly signed APKs for production  
✅ **Version Management** - Automatic versioning from git  
✅ **GitHub Releases** - Automatic releases on tags  
✅ **Artifact Retention** - 30-day retention for APKs, 90 days for mapping files

The workflow is production-ready and follows Android development best practices.
