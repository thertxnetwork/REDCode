# Quick Reference: Creating Releases

This is a quick guide for creating new releases of REDCode Android Editor.

## Creating a New Release

### Step 1: Prepare the Release

1. **Update Version Information** (optional - auto-incremented by CI/CD)
   - Version code: Auto-incremented based on commit count
   - Version name: Taken from git tag

2. **Test Thoroughly**
   ```bash
   # Run all tests
   ./gradlew test
   
   # Build and test locally
   ./gradlew assembleDebug
   ./gradlew assembleRelease
   ```

3. **Update CHANGELOG** (if you maintain one)
   - Document new features
   - Document bug fixes
   - Document breaking changes

### Step 2: Create and Push Tag

Use semantic versioning: `vMAJOR.MINOR.PATCH`

```bash
# Create a version tag
git tag v1.0.0

# Or with annotation (recommended)
git tag -a v1.0.0 -m "Release version 1.0.0"

# Push the tag to GitHub
git push origin v1.0.0
```

**Semantic Versioning Guide:**
- `v1.0.0` → `v2.0.0`: Major version (breaking changes)
- `v1.0.0` → `v1.1.0`: Minor version (new features, backwards compatible)
- `v1.0.0` → `v1.0.1`: Patch version (bug fixes)

### Step 3: Wait for CI/CD

The GitHub Actions workflow will automatically:
1. Run tests
2. Build signed APKs (if keystore configured)
3. Create GitHub release
4. Attach APK files
5. Generate release notes

**Monitor Progress:**
- Go to Actions tab: `https://github.com/thertxnetwork/REDCode/actions`
- Click on the running workflow
- Watch the progress

### Step 4: Verify Release

Once workflow completes:

1. **Go to Releases page**: `https://github.com/thertxnetwork/REDCode/releases`
2. **Find your release**: Should be at the top
3. **Verify Assets**: Should include:
   - `REDCode-vX.X.X-debug.apk`
   - `REDCode-vX.X.X-release.apk` (or `-unsigned.apk`)
   - `build-info.txt`
4. **Check Release Notes**: Auto-generated from commits

### Step 5: Test the APK

Download and test the release APK:

```bash
# Download from releases page
wget https://github.com/thertxnetwork/REDCode/releases/download/v1.0.0/REDCode-v1.0.0-release.apk

# Install on connected device
adb install REDCode-v1.0.0-release.apk

# Or manually transfer and install
```

---

## Quick Commands

### List all tags:
```bash
git tag -l
```

### Delete a tag (if you made a mistake):
```bash
# Delete locally
git tag -d v1.0.0

# Delete remotely
git push origin :refs/tags/v1.0.0
```

### Create a pre-release:
```bash
git tag v1.0.0-beta
git push origin v1.0.0-beta
```

### View tag details:
```bash
git show v1.0.0
```

---

## Version Examples

### First Release
```bash
git tag v1.0.0
git push origin v1.0.0
```

### Bug Fix Release
```bash
git tag v1.0.1
git push origin v1.0.1
```

### Feature Release
```bash
git tag v1.1.0
git push origin v1.1.0
```

### Major Release
```bash
git tag v2.0.0
git push origin v2.0.0
```

### Beta/Pre-release
```bash
git tag v1.1.0-beta.1
git push origin v1.1.0-beta.1
```

---

## Troubleshooting

### Release Not Created

**Problem**: Tag pushed but no release created

**Check:**
1. Go to Actions tab and check workflow status
2. Look for errors in workflow logs
3. Verify tag format is correct (`v*.*.*`)

### APK Not Signed

**Problem**: Release APK shows as unsigned

**Check:**
1. Verify GitHub secrets are configured
2. Check workflow logs for signing errors
3. See [KEYSTORE_SETUP.md](KEYSTORE_SETUP.md) for setup

### Wrong Version in APK

**Problem**: APK has wrong version number

**Solution:**
- Version is taken from tag name
- Delete and recreate tag with correct version

---

## Release Checklist

Before creating a release:

- [ ] All tests passing
- [ ] Code reviewed and merged to main
- [ ] CHANGELOG updated (if applicable)
- [ ] Version number decided (semantic versioning)
- [ ] Keystore secrets configured (for signed releases)
- [ ] No breaking changes without major version bump

After creating release:

- [ ] Workflow completed successfully
- [ ] Release visible on Releases page
- [ ] APK files attached and downloadable
- [ ] Release notes accurate
- [ ] APK tested on device
- [ ] Announced to users (if applicable)

---

## Automation Tips

### Auto-increment Patch Version

If you want to auto-increment patch versions:

```bash
# Get latest tag
LATEST_TAG=$(git describe --tags --abbrev=0)

# Extract version parts
VERSION=${LATEST_TAG#v}
IFS='.' read -ra PARTS <<< "$VERSION"
MAJOR=${PARTS[0]}
MINOR=${PARTS[1]}
PATCH=${PARTS[2]}

# Increment patch
NEW_PATCH=$((PATCH + 1))
NEW_VERSION="v${MAJOR}.${MINOR}.${NEW_PATCH}"

# Create and push
git tag $NEW_VERSION
git push origin $NEW_VERSION
```

### Script for Quick Releases

Save this as `release.sh`:

```bash
#!/bin/bash
if [ -z "$1" ]; then
    echo "Usage: ./release.sh v1.0.0"
    exit 1
fi

VERSION=$1

echo "Creating release $VERSION"
git tag -a $VERSION -m "Release $VERSION"
git push origin $VERSION
echo "Done! Check Actions tab for build progress."
```

Make it executable:
```bash
chmod +x release.sh
```

Use it:
```bash
./release.sh v1.0.0
```

---

## See Also

- [CICD_SETUP.md](CICD_SETUP.md) - Complete CI/CD documentation
- [KEYSTORE_SETUP.md](KEYSTORE_SETUP.md) - Keystore setup guide
- [GitHub Releases Documentation](https://docs.github.com/en/repositories/releasing-projects-on-github)
- [Semantic Versioning](https://semver.org/)
