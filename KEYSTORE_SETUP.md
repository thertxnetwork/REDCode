# Keystore Setup Guide for Android APK Signing

This guide provides detailed instructions for generating, managing, and securing your Android keystore for APK signing.

## What is a Keystore?

A keystore is a binary file that contains your app's private key and certificate. It's used to digitally sign your APK files, proving that updates come from you. **This is required for publishing apps on Google Play Store.**

⚠️ **CRITICAL**: Lose your keystore = lose ability to update your app on Play Store!

---

## Generating a Keystore

### Using Command Line (Recommended)

#### On Linux/macOS/Windows (Git Bash):

```bash
keytool -genkey -v -keystore release-keystore.jks \
  -alias redcode-release-key \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000
```

#### On Windows (Command Prompt):

```cmd
keytool -genkey -v -keystore release-keystore.jks ^
  -alias redcode-release-key ^
  -keyalg RSA ^
  -keysize 2048 ^
  -validity 10000
```

### Interactive Prompts

You'll be asked for:

1. **Keystore password**: Choose a strong password (min 6 chars)
2. **Key password**: Can be same as keystore password
3. **Name**: Your full name or organization name
4. **Organizational Unit**: Your department (e.g., "Development")
5. **Organization**: Your company/organization name
6. **City/Locality**: Your city
7. **State/Province**: Your state or province
8. **Country Code**: Two-letter country code (e.g., "US", "UK")

### Example Session

```
Enter keystore password: ********
Re-enter new password: ********
What is your first and last name?
  [Unknown]:  John Doe
What is the name of your organizational unit?
  [Unknown]:  Development
What is the name of your organization?
  [Unknown]:  REDCode Team
What is the name of your City or Locality?
  [Unknown]:  San Francisco
What is the name of your State or Province?
  [Unknown]:  California
What is the two-letter country code for this unit?
  [Unknown]:  US
Is CN=John Doe, OU=Development, O=REDCode Team, L=San Francisco, ST=California, C=US correct?
  [no]:  yes

Generating 2,048 bit RSA key pair and self-signed certificate (SHA256withRSA) with a validity of 10,000 days
        for: CN=John Doe, OU=Development, O=REDCode Team, L=San Francisco, ST=California, C=US
[Storing release-keystore.jks]
```

---

## Keystore Parameters Explained

| Parameter | Description | Recommendation |
|-----------|-------------|----------------|
| `-keystore` | Filename for keystore | Use descriptive name like `release-keystore.jks` |
| `-alias` | Alias for the key | Use app-specific alias like `redcode-release-key` |
| `-keyalg` | Encryption algorithm | Use `RSA` (industry standard) |
| `-keysize` | Key size in bits | Use `2048` minimum (4096 for extra security) |
| `-validity` | Days the key is valid | Use `10000` (about 27 years) |

---

## Encoding Keystore for GitHub Secrets

GitHub Secrets can't store binary files directly, so we encode them as base64.

### On Linux/macOS:

```bash
base64 release-keystore.jks > keystore-base64.txt
```

Or in one line:
```bash
base64 -i release-keystore.jks | tr -d '\n' > keystore-base64.txt
```

### On Windows (PowerShell):

```powershell
[Convert]::ToBase64String([IO.File]::ReadAllBytes("release-keystore.jks")) | Out-File -Encoding ASCII keystore-base64.txt
```

### On Windows (Git Bash):

```bash
base64 -w 0 release-keystore.jks > keystore-base64.txt
```

The file `keystore-base64.txt` will contain a long string like:
```
/u3+7QAAAAIAAAABAAAAAQAUcmVkY29kZS1yZWxlYXNlLWtleQAA...
```

**This entire string** (without line breaks) goes into the `KEYSTORE_FILE` GitHub secret.

---

## Adding Secrets to GitHub

### Step-by-Step:

1. **Go to Your Repository**
   - Open your repository on GitHub
   - Example: `https://github.com/thertxnetwork/REDCode`

2. **Navigate to Secrets**
   - Click on **Settings** tab
   - In the left sidebar, expand **Secrets and variables**
   - Click **Actions**

3. **Add Each Secret**
   - Click **New repository secret** button
   - Enter secret name and value
   - Click **Add secret**

### Required Secrets:

#### 1. KEYSTORE_FILE
- **Name**: `KEYSTORE_FILE`
- **Value**: Paste the entire content of `keystore-base64.txt`
- **Note**: This will be very long (1000+ characters)

#### 2. KEYSTORE_PASSWORD
- **Name**: `KEYSTORE_PASSWORD`
- **Value**: Your keystore password
- **Example**: `MyStrongPassword123!`

#### 3. KEY_ALIAS
- **Name**: `KEY_ALIAS`
- **Value**: The alias you used when creating keystore
- **Example**: `redcode-release-key`

#### 4. KEY_PASSWORD
- **Name**: `KEY_PASSWORD`
- **Value**: Your key password (often same as keystore password)
- **Example**: `MyStrongPassword123!`

---

## Verifying Your Keystore

### Check Keystore Information:

```bash
keytool -list -v -keystore release-keystore.jks -alias redcode-release-key
```

You'll be prompted for the keystore password, then see information like:

```
Alias name: redcode-release-key
Creation date: Dec 6, 2024
Entry type: PrivateKeyEntry
Certificate chain length: 1
Certificate[1]:
Owner: CN=John Doe, OU=Development, O=REDCode Team, L=San Francisco, ST=California, C=US
Issuer: CN=John Doe, OU=Development, O=REDCode Team, L=San Francisco, ST=California, C=US
Serial number: 1a2b3c4d
Valid from: Fri Dec 06 12:00:00 PST 2024 until: Mon Apr 02 12:00:00 PDT 2052
Certificate fingerprints:
     SHA1: AB:CD:EF:12:34:56:78:90:AB:CD:EF:12:34:56:78:90:AB:CD:EF:12
     SHA256: 12:34:56:78:90:AB:CD:EF:12:34:56:78:90:AB:CD:EF:12:34:56:78:90:AB:CD:EF:12:34:56:78:90:AB:CD
Signature algorithm name: SHA256withRSA
```

### Verify Base64 Encoding:

Test that decoding works:

```bash
# Decode the base64 file
base64 -d keystore-base64.txt > test-keystore.jks

# Verify it's the same
keytool -list -keystore test-keystore.jks
```

If successful, you'll be prompted for password and see keystore details.

---

## Security Best Practices

### 🔒 DO:
- ✅ Use strong passwords (min 12 characters, mixed case, numbers, symbols)
- ✅ Store keystore in multiple secure locations (encrypted cloud, USB drive)
- ✅ Keep passwords in a password manager
- ✅ Use different keystores for debug and release builds
- ✅ Add `*.jks`, `*.keystore`, `keystore-base64.txt` to `.gitignore`
- ✅ Limit access to keystore to essential personnel only
- ✅ Use GitHub's environment secrets for additional protection
- ✅ Enable two-factor authentication on your GitHub account
- ✅ Regularly backup your keystore

### 🚫 DON'T:
- ❌ Never commit keystore to version control (git)
- ❌ Never share keystore password via email or chat
- ❌ Never use simple passwords like "password123"
- ❌ Never store keystore in public cloud storage unencrypted
- ❌ Never use the same keystore for multiple apps
- ❌ Never generate keystores on shared/public computers
- ❌ Never store passwords in plain text files in the repository

---

## Backup Strategy

### Recommended Backup Locations:

1. **Primary**: Encrypted external drive (kept physically secure)
2. **Secondary**: Encrypted cloud storage (Google Drive, Dropbox with encryption)
3. **Tertiary**: USB drive in safe deposit box

### What to Backup:

Create a secure archive containing:
```
keystore-backup/
├── release-keystore.jks          # The keystore file
├── keystore-info.txt             # Passwords and alias (encrypted!)
├── certificate-fingerprints.txt  # SHA1 and SHA256 fingerprints
└── google-play-console-key.json  # If using Play App Signing
```

### Encrypting Your Backup:

Using 7-Zip (Windows):
```cmd
7z a -p -mhe=on keystore-backup.7z keystore-backup/
```

Using zip with password (Linux/macOS):
```bash
zip -er keystore-backup.zip keystore-backup/
```

Using GPG:
```bash
tar czf - keystore-backup/ | gpg -c > keystore-backup.tar.gz.gpg
```

---

## Keystore for Different Build Types

### Debug Keystore (Auto-generated)

Android Studio creates a debug keystore automatically at:
- **Linux/macOS**: `~/.android/debug.keystore`
- **Windows**: `C:\Users\<username>\.android\debug.keystore`

**Default credentials:**
- Password: `android`
- Alias: `androiddebugkey`
- Key password: `android`

**Note**: Debug keystores should NEVER be used for release builds!

### Release Keystore (Custom)

Follow this guide to create a custom release keystore. Use strong passwords and keep it secure.

---

## Updating GitHub Secrets

If you need to update secrets (e.g., changed password, new keystore):

1. Go to repository **Settings** → **Secrets and variables** → **Actions**
2. Find the secret you want to update
3. Click the **Update** button
4. Enter the new value
5. Click **Update secret**

---

## Testing Your Setup

### Test Signing Locally:

```bash
./gradlew assembleRelease \
  -Pandroid.injected.signing.store.file=$(pwd)/release-keystore.jks \
  -Pandroid.injected.signing.store.password=YOUR_PASSWORD \
  -Pandroid.injected.signing.key.alias=redcode-release-key \
  -Pandroid.injected.signing.key.password=YOUR_KEY_PASSWORD
```

### Verify Signed APK:

```bash
# Check APK signature
jarsigner -verify -verbose -certs app/build/outputs/apk/release/app-release.apk

# If signed correctly, you'll see:
# jar verified.
```

Or use apksigner (from Android SDK):
```bash
apksigner verify --verbose app/build/outputs/apk/release/app-release.apk
```

---

## Troubleshooting

### "Keystore was tampered with, or password was incorrect"

**Cause**: Wrong password or corrupted keystore

**Solution**:
1. Double-check password (case-sensitive)
2. Try the key password if different from keystore password
3. Verify keystore file isn't corrupted

### "Alias does not exist"

**Cause**: Wrong alias name

**Solution**:
1. List all aliases: `keytool -list -keystore release-keystore.jks`
2. Use the correct alias name

### "Keystore file does not exist"

**Cause**: Wrong file path

**Solution**:
1. Verify file exists: `ls -la release-keystore.jks`
2. Use absolute path
3. Check file permissions

### Base64 decoding fails in GitHub Actions

**Cause**: Extra whitespace or line breaks in base64 string

**Solution**:
1. Re-encode without line breaks: `base64 -w 0 keystore.jks`
2. Ensure no spaces at start/end when pasting into GitHub secret

---

## Recovery

### If You Lose Your Keystore:

**For apps not yet published:**
- Generate a new keystore
- Update GitHub secrets
- Sign new APKs with new keystore

**For apps already published on Google Play:**
- **You cannot update the app** with a new keystore
- Options:
  1. If using Play App Signing: Google has backup (you can reset)
  2. If not: Must publish as a new app (lose all users/ratings)
  
**Prevention**: Always maintain backups! This cannot be stressed enough.

---

## Google Play App Signing

Google Play offers managed app signing where Google stores your upload key and manages the signing key. This is recommended for new apps as it provides:
- Key protection by Google
- Ability to reset upload key if lost
- Additional security

To use:
1. Enroll in Play App Signing when publishing
2. Upload your keystore to Google Play Console
3. Use upload key (can be different from signing key)

---

## Summary Checklist

Before using your keystore in CI/CD:

- [ ] Keystore generated with strong parameters
- [ ] Passwords are strong and documented (encrypted)
- [ ] Keystore encoded to base64 correctly
- [ ] All four GitHub secrets added
- [ ] Keystore tested locally
- [ ] Keystore backed up in multiple secure locations
- [ ] `*.jks` and `*.keystore` added to `.gitignore`
- [ ] Team members know keystore backup locations
- [ ] Password stored in password manager

---

## Additional Resources

- [Android Developer Guide - Sign Your App](https://developer.android.com/studio/publish/app-signing)
- [Keytool Documentation](https://docs.oracle.com/javase/8/docs/technotes/tools/unix/keytool.html)
- [Google Play App Signing](https://support.google.com/googleplay/android-developer/answer/9842756)

---

**Remember**: Your keystore is the identity of your app. Treat it like your most valuable credential!
