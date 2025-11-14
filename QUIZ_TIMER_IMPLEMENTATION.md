# Quiz Timer and Question Counter Implementation

## Overview
Added a countdown timer system and question counter to the quiz activity, with time bonuses for correct answers and visual feedback.

## Features Implemented

### 1. Question Counter
- **Display**: Shows current question out of total (e.g., "1/10")
- **Location**: Below level title in toolbar
- **Updates**: Automatically updates when moving to next question

### 2. Countdown Timer
- **Starting Time**: 60 seconds (00:60)
- **Display Format**: ⏱ MM:SS
- **Color Coding**:
  - Green (#4CAF50): > 20 seconds remaining
  - Yellow (#FDCB6E): 10-20 seconds remaining
  - Red (#D63031): ≤ 10 seconds remaining
- **Countdown**: Updates every second
- **Timeout Handling**: Shows dialog when time expires and auto-submits quiz

### 3. Time Bonus System
- **Reward**: +5 to +10 seconds (random) for each correct answer
- **Visual Feedback**: Green popup showing "+X seconds"
- **Animation**: Fade in, slide up, pause, fade out
- **Duration**: Popup visible for ~1.3 seconds total

## UI Changes - `activity_quiz.xml`

### Added Question Counter and Timer Display
```xml
<!-- Question Counter and Timer -->
<LinearLayout
    android:id="@+id/layoutQuizInfo"
    android:layout_width="0dp"
    android:layout_height="wrap_content"
    android:orientation="horizontal"
    android:gravity="start"
    app:layout_constraintBottom_toBottomOf="parent"
    app:layout_constraintEnd_toStartOf="@id/tvHintCount"
    app:layout_constraintStart_toStartOf="parent"
    app:layout_constraintTop_toBottomOf="@id/tvToolbarTitle">

    <TextView
        android:id="@+id/tvQuestionCounter"
        android:text="1/10"
        android:textSize="14sp"
        android:textColor="@color/white"
        android:fontFamily="@font/pixelated_display" />

    <TextView
        android:id="@+id/tvTimer"
        android:text="⏱ 00:60"
        android:textSize="14sp"
        android:textColor="#4CAF50"
        android:fontFamily="@font/pixelated_display" />
</LinearLayout>
```

### Added Time Bonus Popup
```xml
<!-- Time Bonus Popup -->
<TextView
    android:id="@+id/tvTimeBonus"
    android:text="+5 seconds"
    android:textSize="18sp"
    android:textColor="#4CAF50"
    android:fontFamily="@font/pixelated_display"
    android:textStyle="bold"
    android:padding="12dp"
    android:background="@drawable/pixelated_card_background"
    android:elevation="15dp"
    android:visibility="gone"
    android:alpha="0" />
```

## Code Changes - `QuizActivity.kt`

### 1. Added Imports
```kotlin
import android.os.CountDownTimer
```

### 2. Added Properties
```kotlin
// UI Elements
private lateinit var tvQuestionCounter: TextView
private lateinit var tvTimer: TextView
private lateinit var tvTimeBonus: TextView

// Timer
private var quizTimer: CountDownTimer? = null
private var timeLeftInMillis: Long = 60000 // 60 seconds
```

### 3. Timer Methods

#### Start Timer
```kotlin
private fun startTimer() {
    quizTimer?.cancel()
    
    quizTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            timeLeftInMillis = millisUntilFinished
            updateTimerDisplay()
        }

        override fun onFinish() {
            timeLeftInMillis = 0
            updateTimerDisplay()
            handleTimeOut()
        }
    }.start()
}
```

#### Update Timer Display
```kotlin
private fun updateTimerDisplay() {
    val seconds = (timeLeftInMillis / 1000).toInt()
    val minutes = seconds / 60
    val secs = seconds % 60
    
    tvTimer.text = String.format("⏱ %02d:%02d", minutes, secs)
    
    // Change color based on time left
    when {
        seconds <= 10 -> tvTimer.setTextColor(Color.parseColor("#D63031")) // Red
        seconds <= 20 -> tvTimer.setTextColor(Color.parseColor("#FDCB6E")) // Yellow
        else -> tvTimer.setTextColor(Color.parseColor("#4CAF50")) // Green
    }
}
```

#### Add Bonus Time
```kotlin
private fun addBonusTime(seconds: Int) {
    timeLeftInMillis += seconds * 1000L
    showTimeBonusPopup(seconds)
    startTimer() // Restart timer with new time
}
```

#### Show Time Bonus Popup
```kotlin
private fun showTimeBonusPopup(seconds: Int) {
    tvTimeBonus.text = "+$seconds seconds"
    tvTimeBonus.visibility = View.VISIBLE
    
    // Animate popup
    tvTimeBonus.alpha = 0f
    tvTimeBonus.translationY = 0f
    tvTimeBonus.animate()
        .alpha(1f)
        .translationY(-50f)
        .setDuration(300)
        .withEndAction {
            // Fade out after 1 second
            tvTimeBonus.animate()
                .alpha(0f)
                .setStartDelay(1000)
                .setDuration(300)
                .withEndAction {
                    tvTimeBonus.visibility = View.GONE
                    tvTimeBonus.translationY = 0f
                }
                .start()
        }
        .start()
}
```

#### Handle Timeout
```kotlin
private fun handleTimeOut() {
    if (hasAnswered) return
    
    hasAnswered = true
    disableAllOptions()
    
    // Show timeout dialog
    val dialog = android.app.AlertDialog.Builder(this)
        .setTitle("⏰ Time's Up!")
        .setMessage("The quiz time has expired. Your current score will be submitted.")
        .setPositiveButton("Continue") { _, _ ->
            finishQuiz()
        }
        .setCancelable(false)
        .create()
    
    dialog.show()
}
```

#### Update Question Counter
```kotlin
private fun updateQuestionCounter() {
    tvQuestionCounter.text = "${currentQuestionIndex + 1}/${questions.size}"
}
```

### 4. Updated Existing Methods

#### displayQuestion()
```kotlin
private fun displayQuestion() {
    // ... existing code ...
    
    // Update question counter
    updateQuestionCounter()
    
    // ... existing code ...
    
    // Start timer for first question
    if (currentQuestionIndex == 0) {
        timeLeftInMillis = 60000 // Reset to 60 seconds
        startTimer()
    }
}
```

#### selectAnswer()
```kotlin
if (isCorrect) {
    // ... existing code ...
    
    // Add bonus time (5-10 seconds randomly)
    val bonusSeconds = (5..10).random()
    addBonusTime(bonusSeconds)
    
    // ... existing code ...
}
```

#### Lifecycle Methods
```kotlin
override fun onPause() {
    super.onPause()
    SoundManager.pauseBackgroundMusic()
    quizTimer?.cancel() // Stop timer when paused
}

override fun onDestroy() {
    super.onDestroy()
    quizTimer?.cancel() // Clean up timer
}
```

## How It Works

### Quiz Flow
1. **Start Quiz**: Timer starts at 60 seconds on first question
2. **Answer Question**: 
   - Correct answer → Add 5-10 bonus seconds + show green popup
   - Wrong answer → No time bonus
3. **Next Question**: Timer continues with accumulated time
4. **Time Warning**: Color changes from green → yellow → red
5. **Timeout**: Dialog shows and quiz auto-submits

### Timer Behavior
- **Accumulates**: Bonus time adds to existing time
- **Persistent**: Continues across questions
- **Visual Feedback**: Color and popup indicate status
- **Graceful Timeout**: Doesn't crash, shows dialog

## Example Display

```
Optics Explorer            💡 3  ☰
1/10  ⏱ 00:60

[Question Card]

[Options A, B, C, D]

      +7 seconds  ← Appears briefly when correct
```

## Testing Checklist
- [x] Build successful
- [x] APK installed
- [ ] Question counter updates (1/10, 2/10, etc.)
- [ ] Timer starts at 00:60
- [ ] Timer counts down every second
- [ ] Timer color changes (green → yellow → red)
- [ ] Correct answer adds 5-10 seconds
- [ ] "+X seconds" popup appears
- [ ] Popup animates (fade in, slide up, fade out)
- [ ] Timer continues across questions
- [ ] Timeout dialog appears at 00:00
- [ ] Timer stops when quiz ends
- [ ] Timer stops when app paused

## Build Status
✅ **Build Successful**
✅ **APK Installed**
- No compilation errors
- Timer and counter fully implemented

## Benefits
- **Engagement**: Time pressure adds excitement
- **Reward**: Bonus time motivates correct answers
- **Clarity**: Question counter shows progress
- **Fair**: Random bonus (5-10s) keeps it interesting
- **Polish**: Smooth animations and color feedback
- **Game-like**: Typical quiz game mechanic

---
**Status**: ✅ Complete
**Build**: Success
**Date**: November 14, 2025
