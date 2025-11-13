package com.lokixcz.optilearn.utils

object Constants {
    // Database
    const val DATABASE_NAME = "optilearn_database"
    const val DATABASE_VERSION = 1
    
    // SharedPreferences
    const val PREFS_NAME = "OptiLearnPrefs"
    const val KEY_FIRST_LAUNCH = "first_launch"
    const val KEY_SOUND_ENABLED = "sound_enabled"
    const val KEY_MUSIC_ENABLED = "music_enabled"
    const val KEY_USER_ID = "user_id"
    
    // Game Settings
    const val TOTAL_LEVELS = 15
    const val QUESTIONS_PER_LEVEL = 5
    const val PASS_THRESHOLD = 80 // 80% required to pass
    const val PERFECT_SCORE = 100
    const val OPTI_HINT_REWARD = 1 // OptiHints earned for perfect score
    
    // Question Time (optional)
    const val QUESTION_TIME_SECONDS = 30
    
    // Intent Extras
    const val EXTRA_LEVEL_ID = "level_id"
    const val EXTRA_SCORE = "score"
    const val EXTRA_TOTAL_QUESTIONS = "total_questions"
    const val EXTRA_CORRECT_ANSWERS = "correct_answers"
    const val EXTRA_IS_PERFECT = "is_perfect"
    const val EXTRA_LEVEL_TITLE = "level_title"
    const val EXTRA_BADGE_NAME = "badge_name"
    const val EXTRA_BADGE_ICON = "badge_icon"
    
    // Animation Durations
    const val ANIMATION_DURATION_SHORT = 300L
    const val ANIMATION_DURATION_MEDIUM = 500L
    const val ANIMATION_DURATION_LONG = 1000L
    
    // Level Data
    val LEVEL_TITLES = mapOf(
        1 to "Optics Explorer",
        2 to "Reflection Rookie",
        3 to "Ray Tracker",
        4 to "Mirror Mapper",
        5 to "Reflection Specialist",
        6 to "Lateral Inverter",
        7 to "Curved Mirror Champion",
        8 to "Image Identifier",
        9 to "Plane Mirror Pro",
        10 to "Focal Finder",
        11 to "Real or Virtual?",
        12 to "Mirror Match",
        13 to "Lens Learner",
        14 to "Mirror Life",
        15 to "Final Quest"
    )
    
    val BADGE_NAMES = mapOf(
        1 to "Finder",
        2 to "Mirror Novice",
        3 to "Ray Ranger",
        4 to "Mirror Mapper",
        5 to "Reflection Expert",
        6 to "Lateral Wizard",
        7 to "Curvature Conqueror",
        8 to "Image Inspector",
        9 to "Plane Pro",
        10 to "Focal Master",
        11 to "Vision Virtuoso",
        12 to "Mirror Matcher",
        13 to "Lens Luminary",
        14 to "Reflector Pro",
        15 to "Optics Legend"
    )
    
    val BADGE_ICONS = mapOf(
        1 to "🔆",
        2 to "🪞",
        3 to "📏",
        4 to "🪞",
        5 to "✨",
        6 to "🔄",
        7 to "🏹",
        8 to "👁️",
        9 to "🪞",
        10 to "🎯",
        11 to "🔎",
        12 to "🪞",
        13 to "🔬",
        14 to "🚗",
        15 to "🌟"
    )
}
