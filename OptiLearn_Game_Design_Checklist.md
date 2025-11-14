# 🎮 OptiLearn - Educational Quiz Adventure (Android Studio Game)

## 🧠 GitHub Copilot Prompt

> Build a level-based educational quiz adventure game called
> **OptiLearn** using Android Studio (Java or Kotlin).\
> The app helps students learn optics (mirrors and lenses) by answering
> multiple-choice questions.\
> Players must score at least 80% to unlock the next level. Perfect
> scores earn "OptiHints" that can be used to skip a future question.\
> Players earn badges for each completed level and can view them in a
> Trophy Room.

**Core Features:** - 15 Levels (each with unique questions, badge, and
theme) - Multiple-choice quiz format - 80% score requirement for
progression - Badge and OptiHint reward system - Trophy Room to view
progress - Quest Map for level navigation - 100% Offline (SharedPreferences + Room Database for local storage) - Animations for transitions and rewards

------------------------------------------------------------------------

## 🚧 Milestones & Tasks

### 🏁 Milestone 1: Project Setup

-   [x] Create Android Studio project `OptiLearn`
-   [x] Add Room Database dependency for local data storage
-   [x] Configure MVVM architecture (Model, View, ViewModel)
-   [x] Add basic navigation (Main Menu → Quest Map → Quiz)

### 🌈 Milestone 2: UI Design

-   [x] Design `activity_main_menu.xml` (Play, Trophy Room, Quest Map,
    Settings)
-   [x] Design `activity_quest_map.xml` (level icons with
    locked/unlocked states)
-   [x] Design `activity_quiz.xml` (question text, options buttons,
    progress bar)
-   [x] Design `activity_result.xml` (score, badge, retry/next buttons)
-   [x] Design `activity_trophy_room.xml` (badge collection grid)

### 🧩 Milestone 3: Core Logic

-   [x] Implement quiz logic (randomized questions, answer checking)
-   [x] Add scoring system with 80% pass threshold
-   [x] Unlock next level when passed
-   [x] Award badges and OptiHints
-   [x] Save player progress to Room Database and SharedPreferences

### 🏅 Milestone 4: Rewards & Animation

-   [x] Add confetti/star animation for correct answers
-   [x] Animate badge pop-up upon level completion
-   [x] Show OptiHint count in Trophy Room
-   [x] Add confetti animation and "Congratulations" dialog

### 🧱 Milestone 5: Data Structure

-   [x] Create Room Database with entities:
    -   `UserProgressEntity` (userId, currentLevel, totalScore, optiHints)
    -   `LevelEntity` (levelId, title, isUnlocked, isCompleted, badgeEarned)
    -   `QuestionEntity` (questionId, levelId, questionText, options, correctAnswer)
-   [x] Define model classes: `Question.java`, `Level.java`,
    `Badge.java`, `UserProgress.java`
-   [x] Implement DAO (Data Access Objects) for database operations
-   [x] Create `DatabaseHelper.java` for Room database management
-   [x] Use SharedPreferences for quick settings (sound, music toggles)
-   [x] Pre-populate database with all 15 levels and questions on first launch
-   [x] Update all questions from Questions.md (85 comprehensive questions)
-   [x] Add question images for Levels 3-7, 9-10 (7 images total)

### 🎨 Milestone 6: Enhancements

-   [x] Add sound effects for correct/wrong answers
-   [x] Add background music with toggle in settings
-   [x] Add "Retry Level" and "Next Level" buttons in results screen
-   [ ] Implement data backup/export feature (optional)

------------------------------------------------------------------------

## 📁 Recommended File Structure

    app/
     ├─ java/com/example/optilern/
     │   ├─ model/
     │   │   ├─ Question.java
     │   │   ├─ Level.java
     │   │   ├─ Badge.java
     │   │   └─ UserProgress.java
     │   ├─ database/
     │   │   ├─ AppDatabase.java (Room Database)
     │   │   ├─ dao/
     │   │   │   ├─ UserProgressDao.java
     │   │   │   ├─ LevelDao.java
     │   │   │   └─ QuestionDao.java
     │   │   └─ entity/
     │   │       ├─ UserProgressEntity.java
     │   │       ├─ LevelEntity.java
     │   │       └─ QuestionEntity.java
     │   ├─ view/
     │   │   ├─ SplashActivity.java
     │   │   ├─ MainMenuActivity.java
     │   │   ├─ QuizActivity.java
     │   │   ├─ ResultActivity.java
     │   │   └─ TrophyRoomActivity.java
     │   ├─ viewmodel/
     │   │   └─ GameViewModel.java
     │   └─ utils/
     │       ├─ DatabaseHelper.java
     │       ├─ PrefManager.java (SharedPreferences)
     │       └─ Constants.java
     ├─ res/
     │   ├─ layout/ (XML layouts)
     │   ├─ drawable/ (icons, badges, buttons)
     │   ├─ anim/ (fade_in, slide_up, confetti)
     │   └─ values/ (colors, strings, styles)

------------------------------------------------------------------------

## 🏆 Level Progression

  Level   Name                     Badge                 Icon
  ------- ------------------------ --------------------- ------
  1       Optics Explorer          Finder                🔆
  2       Reflection Rookie        Mirror Novice         🪞
  3       Ray Tracker              Ray Ranger            📏
  4       Mirror Mapper            Mirror Mapper         🪞
  5       Reflection Specialist    Reflection Expert     ✨
  6       Lateral Inverter         Lateral Wizard        🔄
  7       Curved Mirror Champion   Curvature Conqueror   🏹
  8       Image Identifier         Image Inspector       👁️
  9       Plane Mirror Pro         Plane Pro             🪞
  10      Focal Finder             Focal Master          🎯
  11      Real or Virtual?         Vision Virtuoso       🔎
  12      Mirror Match             Mirror Matcher        🪞
  13      Lens Learner             Lens Luminary         🔬
  14      Mirror Life              Reflector Pro         🚗
  15      Final Quest              Optics Legend         🌟

\`\`\`

# ✅ Completion Criteria

-   [ ] All 15 levels functional with randomized questions
-   [ ] Scores and badges saved in Room Database
-   [ ] Quest Map updates based on progress
-   [ ] Polished UI and smooth transitions
-   [ ] App works 100% offline with no internet dependency
