# OptiLearn - Build & Debug Guide

## ✅ Build Success!

The project should now build successfully. Here's what was fixed:

### Fixed Issues:
1. ✅ **compileSdk** syntax corrected (was using invalid syntax)
2. ✅ **Removed Compose** dependencies (we're using traditional Views)
3. ✅ **Added missing dependencies**: CardView, RecyclerView, ViewModel, LiveData
4. ✅ **Fixed onBackPressed()** deprecation warning
5. ✅ **Enabled ViewBinding** instead of Compose

## 🚀 Running the App

### In Android Studio:
1. Click **Sync Project with Gradle Files**
2. Select a device/emulator
3. Click **Run** (Shift+F10)

### Common Crash Causes & Fixes:

#### 1. **Database Initialization Error**
**Symptom**: App crashes immediately on launch
**Fix**: The database will auto-initialize on first launch. If issues persist:
```kotlin
// Room database is configured to pre-populate on first run
// Check logcat for: "Cannot find implementation for AppDatabase"
```

#### 2. **Activity Not Found**
**Symptom**: `ActivityNotFoundException`
**Fix**: Verify AndroidManifest.xml has all activities registered ✅ (Already done)

#### 3. **View Not Found**
**Symptom**: `NullPointerException` or `findViewById returns null`
**Fix**: Check layout file names match exactly:
- activity_main_menu.xml ✅
- activity_quest_map.xml ✅
- activity_quiz.xml ✅
- activity_result.xml ✅

#### 4. **ViewModel Creation Error**
**Symptom**: Cannot create ViewModel
**Fix**: Ensure lifecycle dependencies are added ✅ (Already done)

## 📱 Testing the App

### Test Flow:
1. **Launch App** → See Main Menu with stats
2. **Click "Play" or "Quest Map"** → See 15 levels (Level 1 unlocked)
3. **Click Level 1** → Start quiz with 5 questions
4. **Answer questions** → See feedback and explanations
5. **Complete quiz** → See results screen
6. **Score ≥80%** → Next level unlocks automatically
7. **Score 100%** → Earn 1 OptiHint

### Key Features to Test:
- ✅ Level progression (1-15)
- ✅ Score calculation
- ✅ OptiHint system
- ✅ Badge display
- ✅ Progress tracking
- ✅ Navigation flow

## 🐛 Debugging Commands

### Check Logcat:
```bash
adb logcat | findstr "OptiLearn"
```

### View Database:
```bash
adb shell
run-as com.lokixcz.optilearn
cd databases
ls -la
```

### Clear App Data (Reset Progress):
```bash
adb shell pm clear com.lokixcz.optilearn
```

## 📊 Expected App Behavior

### On First Launch:
1. Database creates automatically
2. 150 questions pre-loaded (10 per level x 15 levels)
3. All 15 levels populated
4. Level 1 unlocked
5. User progress initialized (0 score, 0 hints)

### After Playing Level 1:
1. Questions randomized (5 out of 10)
2. Score calculated
3. If pass (≥80%): Level 2 unlocks
4. If perfect (100%): Earn 1 OptiHint
5. Progress saved to database

## 🎮 Game Flow

```
Main Menu
    ├── Play (goes to Quest Map)
    ├── Quest Map (select level)
    ├── Trophy Room (not yet implemented)
    └── Settings (not yet implemented)

Quest Map
    └── Click Level → Quiz

Quiz (5 questions)
    ├── Answer questions
    ├── Use OptiHint (if available)
    ├── See explanations
    └── Finish → Results

Results
    ├── View score & badge
    ├── Next Level (if passed)
    ├── Retry Level
    └── Back to Quest Map
```

## 🔧 If App Still Crashes

1. **Check Android Studio Logcat** for error stack trace
2. **Verify minimum SDK**: Device should be Android 8.0 (API 26) or higher
3. **Rebuild project**: Build → Rebuild Project
4. **Invalidate caches**: File → Invalidate Caches / Restart
5. **Check database**: Ensure Room is creating properly

## 📝 Next Steps

Once app is running successfully:
- [ ] Add Trophy Room UI
- [ ] Add Settings screen
- [ ] Add confetti animations
- [ ] Add sound effects
- [ ] Add certificate screen for Level 15 completion
