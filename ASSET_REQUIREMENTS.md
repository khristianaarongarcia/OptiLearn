# 🎨 OptiLearn - Asset Requirements Document

This document lists all required assets for the OptiLearn educational quiz game redesign. For each asset, placeholders will be used in the app, and this document provides detailed descriptions and search keywords to help you find or create the real assets.

---

## 📂 Directory Structure

```
app/src/main/res/
├── drawable/           # PNG/XML images and icons
├── raw/               # Lottie JSON animations & sound files
└── values/            # Colors, strings, styles
```

---

## 🎭 1. LOTTIE ANIMATIONS (.json files)
*Location: `app/src/main/res/raw/`*

### 1.1 Confetti Celebration ✅ (Already Created)
- **File**: `confetti_animation.json`
- **Status**: ✅ Already exists
- **Description**: Colorful confetti bursting animation
- **Usage**: Correct answers, level completion
- **Search Keywords**: "confetti celebration lottie animation", "party popper animation", "success celebration"

### 1.2 Loading/Thinking Animation
- **File**: `loading_animation.json`
- **Description**: Cute brain or light bulb thinking/processing animation with gears or sparkles
- **Duration**: 2-3 seconds loop
- **Usage**: While loading questions, calculating results
- **Search Keywords**: "brain thinking lottie", "loading brain animation", "lightbulb idea animation", "cute thinking animation"
- **Recommended**: Brain with rotating gears, pulsing light bulb, or sparkle effects

### 1.3 Correct Answer Animation
- **File**: `correct_answer.json`
- **Description**: Green checkmark with expanding circles, sparkles, or thumbs up animation
- **Duration**: 1-2 seconds
- **Usage**: When answer is correct (instant feedback)
- **Search Keywords**: "green checkmark lottie", "success tick animation", "correct answer celebration", "thumbs up animation"
- **Recommended**: Bouncing checkmark with particle effects

### 1.4 Wrong Answer Animation
- **File**: `wrong_answer.json`
- **Description**: Red X or shaking animation with disappointed expression
- **Duration**: 1-2 seconds
- **Usage**: When answer is wrong (instant feedback)
- **Search Keywords**: "wrong answer animation", "red X cross lottie", "shake error animation", "oops animation"
- **Recommended**: Shaking X with red glow or sad face

### 1.5 Level Up Animation
- **File**: `level_up.json`
- **Description**: Explosive star burst with "LEVEL UP" text and upward arrows
- **Duration**: 2-3 seconds
- **Usage**: When unlocking new level
- **Search Keywords**: "level up animation lottie", "game level unlock", "achievement unlock animation", "star burst celebration"
- **Recommended**: Gold/rainbow star explosion with upward motion

### 1.6 Trophy/Badge Unlock Animation
- **File**: `badge_unlock.json`
- **Description**: Trophy or badge spinning and shining with sparkles
- **Duration**: 2-3 seconds
- **Usage**: When earning a new badge
- **Search Keywords**: "trophy unlock animation", "badge reward lottie", "medal achievement", "gold trophy sparkle"
- **Recommended**: 3D rotating trophy/medal with light rays

### 1.7 Coin/XP Collection Animation
- **File**: `coin_collect.json`
- **Description**: Coins or stars flying upward and collecting
- **Duration**: 1-2 seconds
- **Usage**: XP gain, score increase
- **Search Keywords**: "coin collect animation", "star collect lottie", "XP gain animation", "points collection"
- **Recommended**: Gold coins with sparkle trail flying upward

### 1.8 Character Celebration
- **File**: `character_celebrate.json`
- **Description**: Cute mascot/character jumping with joy, confetti around
- **Duration**: 2-3 seconds
- **Usage**: Perfect score, milestone achievements
- **Search Keywords**: "character celebration animation", "mascot happy dance", "cute character jump joy", "student success animation"
- **Recommended**: Simple character design (could be owl, robot, or student avatar)

### 1.9 Character Thinking
- **File**: `character_thinking.json`
- **Description**: Character with hand on chin, question marks appearing
- **Duration**: 2-3 seconds loop
- **Usage**: Quiz screen idle state
- **Search Keywords**: "character thinking animation", "question mark animation", "pondering mascot", "thinking pose lottie"
- **Recommended**: Same character as celebration, different pose

### 1.10 Streak Fire Animation
- **File**: `streak_fire.json`
- **Description**: Fire or lightning streak effect for combo multipliers
- **Duration**: 1-2 seconds loop
- **Usage**: Showing answer streak (3+ correct in a row)
- **Search Keywords**: "fire streak animation", "combo multiplier effect", "lightning bolt lottie", "game streak effect"
- **Recommended**: Orange/red flames or electric bolts

### 1.11 Quest Path Progress
- **File**: `path_progress.json`
- **Description**: Dotted line or footsteps moving forward animation
- **Duration**: 1-2 seconds
- **Usage**: Quest map level connections
- **Search Keywords**: "progress path animation", "footsteps trail lottie", "dotted line movement", "journey path animation"
- **Recommended**: Animated dotted line with sparkles or footprints

### 1.12 Lock/Unlock Animation
- **File**: `lock_unlock.json`
- **Description**: Lock opening with key turning and sparkles
- **Duration**: 1-2 seconds
- **Usage**: Unlocking new levels on quest map
- **Search Keywords**: "lock unlock animation lottie", "key unlock effect", "padlock open animation", "unlock sparkle"
- **Recommended**: Padlock opening with golden glow

---

## 🔊 2. SOUND EFFECTS (.mp3 or .ogg files)
*Location: `app/src/main/res/raw/`*

### 2.1 Correct Answer Sound
- **File**: `sfx_correct.mp3`
- **Description**: Pleasant, satisfying "ding" or chime sound
- **Duration**: 0.5-1 second
- **Volume**: Medium
- **Search Keywords**: "correct answer sound effect", "success ding", "positive chime", "quiz correct sfx"
- **Recommended Sources**: Freesound.org, Zapsplat.com, Mixkit.co

### 2.2 Wrong Answer Sound
- **File**: `sfx_wrong.mp3`
- **Description**: Gentle "buzz" or "boop" (not harsh), slightly sad tone
- **Duration**: 0.5-1 second
- **Volume**: Medium-Low
- **Search Keywords**: "wrong answer sound", "error buzz gentle", "quiz incorrect sound", "negative boop"
- **Recommended Sources**: Freesound.org, Zapsplat.com

### 2.3 Button Click/Tap Sound
- **File**: `sfx_button_click.mp3`
- **Description**: Soft "pop" or "click" sound
- **Duration**: 0.2-0.5 seconds
- **Volume**: Low
- **Search Keywords**: "button click sound", "UI tap sound", "soft pop sfx", "interface click"
- **Recommended Sources**: Freesound.org, UI Sound library

### 2.4 Level Complete Sound
- **File**: `sfx_level_complete.mp3`
- **Description**: Victory fanfare (short and upbeat)
- **Duration**: 2-3 seconds
- **Volume**: Medium-High
- **Search Keywords**: "level complete sound", "victory fanfare short", "stage clear sound", "quiz win sound"
- **Recommended Sources**: Freesound.org, Game audio libraries

### 2.5 Badge Unlock Sound
- **File**: `sfx_badge_unlock.mp3`
- **Description**: Magical "shimmer" or "sparkle" with triumphant tone
- **Duration**: 1-2 seconds
- **Volume**: Medium
- **Search Keywords**: "achievement unlock sound", "badge earned sfx", "magic sparkle sound", "trophy unlock"
- **Recommended Sources**: Freesound.org, Zapsplat.com

### 2.6 Coin/XP Collection Sound
- **File**: `sfx_coin_collect.mp3`
- **Description**: Quick "bling" or "chime" sound
- **Duration**: 0.3-0.5 seconds
- **Volume**: Medium
- **Search Keywords**: "coin collect sound", "XP gain sfx", "point collect", "pickup sound game"
- **Recommended Sources**: Freesound.org, Game asset stores

### 2.7 Streak/Combo Sound
- **File**: `sfx_streak.mp3`
- **Description**: Rising pitch "whoosh" or "power-up" sound
- **Duration**: 0.5-1 second
- **Volume**: Medium
- **Search Keywords**: "combo sound effect", "streak sfx", "multiplier sound", "power up whoosh"
- **Recommended Sources**: Freesound.org, Game SFX libraries

### 2.8 Level Unlock Sound
- **File**: `sfx_level_unlock.mp3`
- **Description**: Lock clicking open with magical chime
- **Duration**: 1-2 seconds
- **Volume**: Medium
- **Search Keywords**: "unlock sound effect", "lock open sfx", "new level unlock", "door unlock game"
- **Recommended Sources**: Freesound.org

### 2.9 OptiHint Use Sound
- **File**: `sfx_hint_use.mp3`
- **Description**: Magical "whoosh" with light bulb "ding"
- **Duration**: 0.5-1 second
- **Volume**: Medium
- **Search Keywords**: "hint sound effect", "help power-up sfx", "light bulb ding", "idea sound"
- **Recommended Sources**: Freesound.org

### 2.10 Timer Tick Sound
- **File**: `sfx_timer_tick.mp3`
- **Description**: Subtle "tick" for last 10 seconds of timer
- **Duration**: 0.1 seconds
- **Volume**: Low-Medium
- **Search Keywords**: "clock tick sound", "timer countdown sfx", "game timer tick", "time running out"
- **Recommended Sources**: Freesound.org

### 2.11 Timer Expire Sound (Not Needed)
- **File**: `sfx_timer_expire.mp3`
- **Description**: Buzzer or alarm sound (not harsh)
- **Duration**: 1 second
- **Volume**: Medium
- **Search Keywords**: "time up sound", "buzzer alarm game", "timer expired sfx", "alarm clock sound"
- **Recommended Sources**: Freesound.org

---

## 🎵 3. BACKGROUND MUSIC (.mp3 files - looping)
*Location: `app/src/main/res/raw/`*

### 3.1 Menu Background Music
- **File**: `music_menu.mp3`
- **Description**: Upbeat, cheerful instrumental music (no lyrics)
- **Duration**: 2-3 minutes (seamless loop)
- **Genre**: Electronic, Chiptune, or Light Pop
- **Mood**: Energetic, Fun, Inviting
- **Search Keywords**: "game menu music loop", "upbeat background music", "educational game soundtrack", "chiptune menu theme"
- **Recommended Sources**: Free Music Archive, Incompetech, Bensound

### 3.2 Quiz Background Music
- **File**: `music_quiz.mp3`
- **Description**: Focused, moderate tempo instrumental (helps concentration)
- **Duration**: 2-3 minutes (seamless loop)
- **Genre**: Ambient Electronic, Lo-fi, or Light Classical
- **Mood**: Focused, Calm yet Engaging
- **Search Keywords**: "thinking music instrumental", "quiz game background", "study music loop", "concentration game music"
- **Recommended Sources**: Free Music Archive, Incompetech, Bensound

### 3.3 Result/Victory Music (Not Needed)
- **File**: `music_victory.mp3`
- **Description**: Triumphant, celebratory instrumental
- **Duration**: 30-60 seconds (doesn't need to loop)
- **Genre**: Orchestral, Electronic, or Upbeat Pop
- **Mood**: Victorious, Proud, Accomplished
- **Search Keywords**: "victory music short", "level complete theme", "achievement music", "success fanfare"
- **Recommended Sources**: Free Music Archive, Incompetech

---

## 🖼️ 4. IMAGE ASSETS (PNG/SVG)
*Location: `app/src/main/res/drawable/`*

### 4.1 Character/Mascot Avatar
- **Files**: `mascot_happy.png`, `mascot_thinking.png`, `mascot_sad.png`, `mascot_celebrate.png`
- **Description**: Friendly mascot character (owl, robot, student, or light bulb character)
- **Size**: 512x512px (transparent background)
- **Style**: Flat design, colorful, friendly
- **Search Keywords**: "educational mascot character", "cute owl mascot", "student avatar character", "friendly robot mascot"
- **Fallback**: Use emoji or text-based avatar

### 4.2 Level Node Icons
- **Files**: `node_locked.png`, `node_unlocked.png`, `node_completed.png`, `node_current.png`
- **Description**: Circular badges/nodes for quest map with different states
- **Size**: 256x256px (transparent background)
- **Style**: 3D-like, glossy, game-style
- **Search Keywords**: "game level node icon", "quest map marker", "level badge icon", "progress node design"
- **Fallback**: Use colored circles with Material Design icons

### 4.3 Background Patterns
- **Files**: `bg_pattern_menu.png`, `bg_pattern_quiz.png`, `bg_gradient.png`
- **Description**: Subtle geometric patterns or gradients for backgrounds
- **Size**: 1920x1080px (tileable if pattern)
- **Style**: Subtle, non-distracting, modern
- **Search Keywords**: "subtle geometric pattern", "education background pattern", "game UI background", "gradient background design"
- **Fallback**: Use solid colors or simple gradients via XML

### 4.4 Button Decorations
- **Files**: `btn_star.png`, `btn_sparkle.png`, `btn_glow.png`
- **Description**: Small decorative elements to enhance buttons
- **Size**: 64x64px (transparent background)
- **Style**: Simple, bright, game-like
- **Search Keywords**: "button decoration star", "UI sparkle element", "game button glow", "small star icon"
- **Fallback**: Use XML shape drawables with gradients

### 4.5 XP/Progress Bar Graphics
- **Files**: `xp_bar_fill.png`, `xp_bar_shine.png`, `xp_star.png`
- **Description**: Fill patterns and decorations for progress bars
- **Size**: Varies (scalable)
- **Style**: Glossy, game-like, animated feel
- **Search Keywords**: "XP bar fill texture", "progress bar game design", "health bar graphics", "level bar UI"
- **Fallback**: Use gradient XML drawables

### 4.6 Badge/Trophy Icons
- **Files**: Individual badge icons (already using emojis, these are optional enhancements)
- **Description**: High-quality trophy/medal designs for each level
- **Size**: 256x256px (transparent background)
- **Style**: 3D-like, shiny, varied designs
- **Search Keywords**: "trophy icon design", "badge medal graphic", "achievement icon set", "game reward icon"
- **Fallback**: Continue using emoji icons (🔆🪞📏 etc.)

### 4.7 Particle Effects
- **Files**: `particle_star.png`, `particle_sparkle.png`, `particle_circle.png`
- **Description**: Small particle images for custom animations
- **Size**: 32x32px (transparent background)
- **Style**: Bright, glowing, simple shapes
- **Search Keywords**: "star particle PNG", "sparkle effect graphic", "game particle sprite", "glow particle"
- **Fallback**: Use simple colored circles/stars via XML

### 4.8 UI Icons
- **Files**: `icon_timer.png`, `icon_hint.png`, `icon_streak.png`, `icon_coin.png`
- **Description**: Small icons for UI elements
- **Size**: 64x64px (transparent background)
- **Style**: Flat, colorful, consistent with Material Design
- **Search Keywords**: "timer icon flat", "hint bulb icon", "streak fire icon", "coin icon game"
- **Fallback**: Use Material Design Icons library

---

## 🎨 5. COLOR PALETTE (For Reference)

### Primary Colors
```xml
<!-- Vibrant Game Colors -->
<color name="primary_purple">#7C4DFF</color>
<color name="primary_blue">#2196F3</color>
<color name="primary_green">#4CAF50</color>
<color name="primary_orange">#FF9800</color>
<color name="primary_pink">#E91E63</color>

<!-- Success/Error Colors -->
<color name="success_bright">#00E676</color>
<color name="error_bright">#FF5252</color>
<color name="warning_bright">#FFD740</color>

<!-- Background Gradients -->
<color name="gradient_start">#667EEA</color>
<color name="gradient_end">#764BA2</color>
```

---

## 📚 6. RECOMMENDED ASSET SOURCES

### Free Lottie Animations
- **LottieFiles**: https://lottiefiles.com/ (largest library, free & premium)
- **IconScout**: https://iconscout.com/lottie-animations (quality animations)
- **Lottielab**: https://lottielab.com/ (create custom animations)

### Free Sound Effects
- **Freesound**: https://freesound.org/ (community, CC licensed)
- **Zapsplat**: https://www.zapsplat.com/ (free with attribution)
- **Mixkit**: https://mixkit.co/free-sound-effects/ (royalty-free)
- **Pixabay**: https://pixabay.com/sound-effects/ (free for commercial use)

### Free Music
- **Free Music Archive**: https://freemusicarchive.org/
- **Incompetech**: https://incompetech.com/music/ (CC licensed)
- **Bensound**: https://www.bensound.com/ (free with attribution)
- **Purple Planet**: https://www.purple-planet.com/ (royalty-free)

### Free Images & Icons
- **Flaticon**: https://www.flaticon.com/ (millions of icons)
- **Freepik**: https://www.freepik.com/ (graphics, vectors)
- **Material Design Icons**: https://fonts.google.com/icons
- **Noun Project**: https://thenounproject.com/ (simple icons)

---

## 🎯 7. IMPLEMENTATION PRIORITY

### Phase 1 - Essential (Implement First)
1. ✅ Confetti animation (already done)
2. Correct/Wrong answer animations
3. Button click sound
4. Correct/Wrong answer sounds
5. Basic background music

### Phase 2 - Enhanced Experience
6. Loading animation
7. Level up animation
8. Badge unlock animation
9. Mascot character images
10. Level complete sound

### Phase 3 - Polish & Delight
11. Character celebration/thinking animations
12. Streak animations
13. XP collection animations
14. Quest path progress animation
15. All remaining sounds

---

## 📝 8. LICENSING NOTES

- **Free Resources**: Always check license requirements (attribution, commercial use)
- **Creative Commons**: Most require attribution in app settings/about section
- **Royalty-Free**: Can be used without recurring fees, but read terms
- **Custom Creation**: Consider hiring freelancers on Fiverr/Upwork for unique assets
- **AI Generation**: Can use AI tools like Midjourney, DALL-E for custom graphics

---

## 🔧 9. ASSET SPECIFICATIONS

### Lottie JSON Files
- Format: JSON (Lottie format)
- Max file size: 500KB per file
- Frame rate: 30-60 FPS
- Compatibility: Lottie-Android 6.2.0+

### Sound Effects
- Format: MP3 or OGG (OGG preferred for Android)
- Bitrate: 128-192 kbps
- Sample rate: 44.1 kHz
- Max duration: 3 seconds (except music)

### Background Music
- Format: MP3 or OGG
- Bitrate: 128-192 kbps
- Sample rate: 44.1 kHz
- Must be seamless loop (fade in/out or perfect loop points)

### Images
- Format: PNG (with transparency) or WebP
- Color space: sRGB
- Compression: Optimized for mobile
- Provide @1x, @2x, @3x versions if possible

---

## 📞 10. SUPPORT & RESOURCES

If you need help finding or creating assets:
1. Check the recommended sources above
2. Search with provided keywords
3. Consider free alternatives (Material Icons, emoji)
4. Use placeholder implementations (already in code)
5. Hire freelancers for custom work

**Note**: All placeholders in the app use emoji, text, and XML drawables as fallbacks, so the app is fully functional without external assets!
