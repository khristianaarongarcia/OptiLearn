package com.lokixcz.optilearn.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.lokixcz.optilearn.database.dao.LevelDao
import com.lokixcz.optilearn.database.dao.QuestionDao
import com.lokixcz.optilearn.database.dao.UserProgressDao
import com.lokixcz.optilearn.database.entity.LevelEntity
import com.lokixcz.optilearn.database.entity.QuestionEntity
import com.lokixcz.optilearn.database.entity.UserProgressEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserProgressEntity::class,
        LevelEntity::class,
        QuestionEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun userProgressDao(): UserProgressDao
    abstract fun levelDao(): LevelDao
    abstract fun questionDao(): QuestionDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "optilearn_database"
                )
                    .addCallback(DatabaseCallback())
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
    
    private class DatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateDatabase(database)
                }
            }
        }
        
        override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
            super.onDestructiveMigration(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateDatabase(database)
                }
            }
        }
        
        suspend fun populateDatabase(database: AppDatabase) {
            val userProgressDao = database.userProgressDao()
            val levelDao = database.levelDao()
            val questionDao = database.questionDao()
            
            // Initialize user progress
            userProgressDao.insertUserProgress(
                UserProgressEntity(
                    id = 1,
                    currentLevel = 1,
                    totalScore = 0,
                    optiHints = 0,
                    completedLevels = 0,
                    perfectScores = 0
                )
            )
            
            // Initialize all 15 levels
            val levels = listOf(
                LevelEntity(1, "Optics Explorer", "Finder", "optics_explorer", isUnlocked = true),
                LevelEntity(2, "Reflection Rookie", "Mirror Novice", "reflection_rookie"),
                LevelEntity(3, "Ray Tracker", "Ray Ranger", "ray_tracker"),
                LevelEntity(4, "Mirror Mapper", "Mirror Mapper", "mirror_mapper"),
                LevelEntity(5, "Reflection Specialist", "Reflection Expert", "✨"),
                LevelEntity(6, "Lateral Inverter", "Lateral Wizard", "lateral_inverter"),
                LevelEntity(7, "Curved Mirror Champion", "Curvature Conqueror", "curved_mirror_champion"),
                LevelEntity(8, "Image Identifier", "Image Inspector", "image_identifier"),
                LevelEntity(9, "Plane Mirror Pro", "Plane Pro", "plane_mirror"),
                LevelEntity(10, "Focal Finder", "Focal Master", "🎯"),
                LevelEntity(11, "Real or Virtual?", "Vision Virtuoso", "real_or_virtual"),
                LevelEntity(12, "Mirror Match", "Mirror Matcher", "mirror_match"),
                LevelEntity(13, "Lens Learner", "Lens Luminary", "lens_learner"),
                LevelEntity(14, "Mirror Life", "Reflector Pro", "🚗"),
                LevelEntity(15, "Final Quest", "Optics Legend", "final_quest")
            )
            levelDao.insertAllLevels(levels)
            
            // Pre-populate questions for all levels
            val questions = generateAllQuestions()
            questionDao.insertAllQuestions(questions)
        }
        
        private fun generateAllQuestions(): List<QuestionEntity> {
            return listOf(
                // Level 1: Optics Explorer (10 questions)
                QuestionEntity(levelId = 1, questionText = "Light is a natural agent that stimulates ______ and makes things possible.",
                    optionA = "Hearing", optionB = "Sight", 
                    optionC = "Movement", optionD = "Touch",
                    correctAnswer = "B", explanation = "Light stimulates sight and makes vision possible."),
                
                QuestionEntity(levelId = 1, questionText = "Light is a type of energy known as ______.",
                    optionA = "Chemical energy", optionB = "Thermal energy", 
                    optionC = "Electromagnetic radiation", optionD = "Kinetic energy",
                    correctAnswer = "C", explanation = "Light is electromagnetic radiation that travels in waves."),
                
                QuestionEntity(levelId = 1, questionText = "Light is made up of tiny packets of energy called ______.",
                    optionA = "Protons", optionB = "Photons", 
                    optionC = "Electrons", optionD = "Neutrons",
                    correctAnswer = "B", explanation = "Photons are the fundamental particles of light."),
                
                QuestionEntity(levelId = 1, questionText = "Unlike sound waves, light does not need any material to travel because it can move through a ______.",
                    optionA = "Solid", optionB = "Gas", 
                    optionC = "Vacuum", optionD = "Liquid",
                    correctAnswer = "C", explanation = "Light can travel through a vacuum, unlike sound which needs a medium."),
                
                QuestionEntity(levelId = 1, questionText = "Light waves travel outward from their source in straight lines called ______.",
                    optionA = "Beams", optionB = "Waves", 
                    optionC = "Streams", optionD = "Rays",
                    correctAnswer = "D", explanation = "Light travels in straight lines called rays."),
                
                QuestionEntity(levelId = 1, questionText = "When light strikes matter and part of it is converted into heat, the process is called ______.",
                    optionA = "Reflection", optionB = "Absorption", 
                    optionC = "Transmission", optionD = "Refraction",
                    correctAnswer = "B", explanation = "Absorption occurs when light energy is converted to heat by matter."),
                
                QuestionEntity(levelId = 1, questionText = "When light passes through a transparent material and continues to the other side, it is ______.",
                    optionA = "Absorbed", optionB = "Reflected", 
                    optionC = "Scattered", optionD = "Transmitted",
                    correctAnswer = "D", explanation = "Transmission is when light passes through a material."),
                
                QuestionEntity(levelId = 1, questionText = "When light hits a smooth surface such as a mirror, it bounces back in one direction. This is called ______.",
                    optionA = "Reflection", optionB = "Refraction", 
                    optionC = "Absorption", optionD = "Transmission",
                    correctAnswer = "A", explanation = "Reflection is when light bounces back from a surface."),
                
                QuestionEntity(levelId = 1, questionText = "When light hits a rough surface and bounces in many directions, it is called ______.",
                    optionA = "Absorption", optionB = "Scattering", 
                    optionC = "Reflection", optionD = "Transmission",
                    correctAnswer = "B", explanation = "Scattering occurs when light bounces in multiple directions from a rough surface."),
                
                QuestionEntity(levelId = 1, questionText = "Light allows us to see objects because it ______.",
                    optionA = "Travels slowly through the air", optionB = "Is absorbed by the eyes directly", 
                    optionC = "Reflects off objects into our eyes", optionD = "Passes through solid materials",
                    correctAnswer = "C", explanation = "We see objects when light reflects off them and enters our eyes."),
                
                // Level 2: Reflection Rookie (5 questions)
                QuestionEntity(levelId = 2, questionText = "The Law of Reflection states that the angle of incidence (i) is _____.",
                    optionA = "Always greater than the angle of reflection", optionB = "Always less than the angle of reflection", 
                    optionC = "Equal to the angle of reflection", optionD = "Not related to the angle of reflection",
                    correctAnswer = "C", explanation = "The Law of Reflection states that angle of incidence equals angle of reflection."),
                
                QuestionEntity(levelId = 2, questionText = "Light is a form of energy that travels through a vacuum at an approximate speed of:",
                    optionA = "3 x 10⁶ m/s", optionB = "8 x 10⁸ m/s", 
                    optionC = "3 x 10⁸ m/s", optionD = "8 x 10⁶ m/s",
                    correctAnswer = "C", explanation = "Light travels at approximately 3 × 10⁸ m/s (300,000 km/s) in vacuum."),
                
                QuestionEntity(levelId = 2, questionText = "When light hits a smooth object and ______ off, the phenomenon is called reflection.",
                    optionA = "is transmitted", optionB = "is absorbed", 
                    optionC = "refracts", optionD = "bounces",
                    correctAnswer = "D", explanation = "Reflection occurs when light bounces off a surface."),
                
                QuestionEntity(levelId = 2, questionText = "A mirror that has a flat reflective surface and always produces a virtual image is specifically called a _____.",
                    optionA = "plane mirror", optionB = "concave mirror", 
                    optionC = "convex mirror", optionD = "parabolic mirror",
                    correctAnswer = "A", explanation = "A plane mirror is flat and always produces virtual images."),
                
                QuestionEntity(levelId = 2, questionText = "An image that can be projected onto a screen and is formed by the actual intersection of reflected light rays is known as a ______ image.",
                    optionA = "virtual", optionB = "real", 
                    optionC = "magnified", optionD = "specular",
                    correctAnswer = "B", explanation = "Real images are formed by actual intersection of light rays and can be projected."),
                
                // Level 3: Ray Tracker (5 questions with image reference)
                QuestionEntity(levelId = 3, questionText = "Refer to the diagram.\nReflection that forms clear images occurs on ______ surfaces.",
                    optionA = "Rough", optionB = "Smooth", 
                    optionC = "Curved", optionD = "Transparent",
                    correctAnswer = "B", explanation = "Smooth surfaces produce clear reflected images."),
                
                QuestionEntity(levelId = 3, questionText = "Refer to the diagram.\nThe line drawn perpendicular to the reflecting surface at the point of incidence is called the ______.",
                    optionA = "Reflected ray", optionB = "Normal line", 
                    optionC = "Incident ray", optionD = "Ray of sight",
                    correctAnswer = "B", explanation = "The normal line is perpendicular to the reflecting surface."),
                
                QuestionEntity(levelId = 3, questionText = "Refer to the diagram.\nThe ray of light that comes from the light source and strikes the surface is known as the ______.",
                    optionA = "Reflected ray", optionB = "Refracted ray", 
                    optionC = "Incident ray", optionD = "Normal",
                    correctAnswer = "C", explanation = "The incident ray is the light ray coming from the source."),
                
                QuestionEntity(levelId = 3, questionText = "Refer to the diagram.\nThe ray of light that bounces off the reflecting surface after striking it is called the ______.",
                    optionA = "Incident ray", optionB = "Refracted ray", 
                    optionC = "Reflected ray", optionD = "Normal",
                    correctAnswer = "C", explanation = "The reflected ray is the light bouncing off the surface."),
                
                QuestionEntity(levelId = 3, questionText = "Refer to the diagram.\nThe angle between the normal line and the reflected ray is known as the ______.",
                    optionA = "Angle of incidence", optionB = "Angle of reflection", 
                    optionC = "Angle of refraction", optionD = "Critical angle",
                    correctAnswer = "B", explanation = "The angle between normal and reflected ray is the angle of reflection."),
                
                // Level 4: Mirror Mapper (5 questions with image reference)
                QuestionEntity(levelId = 4, questionText = "Refer to the diagram.\nA _____ mirror is a curved mirror where the reflecting surface curves inward (recessed).",
                    optionA = "Plane", optionB = "Convex", 
                    optionC = "Concave", optionD = "Parabolic",
                    correctAnswer = "C", explanation = "Concave mirrors curve inward like a cave."),
                
                QuestionEntity(levelId = 4, questionText = "Refer to the diagram.\nA _____ mirror is a curved mirror where the reflecting surface curves outward (bulging).",
                    optionA = "Plane", optionB = "Convex", 
                    optionC = "Concave", optionD = "Cylindrical",
                    correctAnswer = "B", explanation = "Convex mirrors curve outward or bulge out."),
                
                QuestionEntity(levelId = 4, questionText = "Refer to the diagram.\nA concave mirror is also commonly referred to as a _____ mirror.",
                    optionA = "Diverging", optionB = "Converging", 
                    optionC = "Dispersing", optionD = "Deflecting",
                    correctAnswer = "B", explanation = "Concave mirrors converge (bring together) light rays."),
                
                QuestionEntity(levelId = 4, questionText = "Refer to the diagram.\nA convex mirror is also commonly referred to as a _____ mirror.",
                    optionA = "Diverging", optionB = "Converging", 
                    optionC = "Focusing", optionD = "Concentrating",
                    correctAnswer = "A", explanation = "Convex mirrors diverge (spread out) light rays."),
                
                QuestionEntity(levelId = 4, questionText = "Refer to the diagram.\nWhich type of curved mirror can form both real and virtual images depending on object position?",
                    optionA = "Plane mirror", optionB = "Convex mirror", 
                    optionC = "Concave mirror", optionD = "Neither",
                    correctAnswer = "C", explanation = "Concave mirrors can form both real and virtual images."),
                
                // Level 5: Reflection Specialist (5 questions with image reference)
                QuestionEntity(levelId = 5, questionText = "Refer to the diagram.\n_____ reflection occurs when light is reflected from a smooth surface at a definite angle.",
                    optionA = "Diffuse", optionB = "Irregular", 
                    optionC = "Regular", optionD = "Scattered",
                    correctAnswer = "C", explanation = "Regular reflection occurs on smooth surfaces with definite angles."),
                
                QuestionEntity(levelId = 5, questionText = "Refer to the diagram.\n_____ reflection occurs when light is reflected from a rough surface in different directions.",
                    optionA = "Specular", optionB = "Diffuse", 
                    optionC = "Regular", optionD = "Direct",
                    correctAnswer = "B", explanation = "Diffuse reflection occurs when light scatters from rough surfaces."),
                
                QuestionEntity(levelId = 5, questionText = "Refer to the diagram.\nRegular reflection is also called _____.",
                    optionA = "Diffuse reflection", optionB = "Scattering", 
                    optionC = "Specular reflection", optionD = "Irregular reflection",
                    correctAnswer = "C", explanation = "Regular reflection is also known as specular reflection."),
                
                QuestionEntity(levelId = 5, questionText = "Refer to the diagram.\nIn which type of reflection can you see a clear image of yourself?",
                    optionA = "Diffuse reflection", optionB = "Irregular reflection", 
                    optionC = "Regular reflection", optionD = "Scattered reflection",
                    correctAnswer = "C", explanation = "Regular reflection from smooth surfaces produces clear images."),
                
                QuestionEntity(levelId = 5, questionText = "Refer to the diagram.\nWhen light reflects off a rough wall, which type of reflection occurs?",
                    optionA = "Regular reflection", optionB = "Specular reflection", 
                    optionC = "Diffuse reflection", optionD = "Perfect reflection",
                    correctAnswer = "C", explanation = "Rough surfaces like walls produce diffuse reflection."),
                
                // Level 6: Lateral Inverter (5 questions with image reference)
                QuestionEntity(levelId = 6, questionText = "Refer to the diagram.\n_____ is the reversal of left and right that occurs when looking at an image in a plane mirror.",
                    optionA = "Vertical inversion", optionB = "Lateral inversion", 
                    optionC = "Horizontal reflection", optionD = "Perpendicular reflection",
                    correctAnswer = "B", explanation = "Lateral inversion is the left-right reversal in mirrors."),
                
                QuestionEntity(levelId = 6, questionText = "Refer to the diagram.\nIn a mirror, your right hand appears as the _____ hand.",
                    optionA = "Right", optionB = "Left", 
                    optionC = "Upper", optionD = "Lower",
                    correctAnswer = "B", explanation = "Due to lateral inversion, right appears as left in mirrors."),
                
                QuestionEntity(levelId = 6, questionText = "Refer to the diagram.\nThe word 'AMBULANCE' is written in reverse on ambulances so it can be read correctly in _____.",
                    optionA = "Direct view", optionB = "Side mirrors", 
                    optionC = "Rear-view mirrors", optionD = "Windows",
                    correctAnswer = "C", explanation = "Reversed text on ambulances reads correctly in rear-view mirrors."),
                
                QuestionEntity(levelId = 6, questionText = "Refer to the diagram.\nDoes lateral inversion flip the image upside down?",
                    optionA = "Yes, always", optionB = "No, only left and right are swapped", 
                    optionC = "Yes, but only sometimes", optionD = "No, it magnifies the image",
                    correctAnswer = "B", explanation = "Lateral inversion only swaps left and right, not up and down."),
                
                QuestionEntity(levelId = 6, questionText = "Refer to the diagram.\nWhich letters look the same even after lateral inversion?",
                    optionA = "F, G, R", optionB = "A, H, I, M", 
                    optionC = "B, C, D", optionD = "E, L, Z",
                    correctAnswer = "B", explanation = "Symmetrical letters like A, H, I, M look the same after lateral inversion."),
                
                // Level 7: Curved Mirror Champion (5 questions with image reference)
                QuestionEntity(levelId = 7, questionText = "Refer to the diagram.\nThe center point of the reflecting surface of a spherical mirror is called the _____.",
                    optionA = "Focus", optionB = "Pole", 
                    optionC = "Center of curvature", optionD = "Vertex",
                    correctAnswer = "B", explanation = "The pole is the center point of the mirror's reflecting surface."),
                
                QuestionEntity(levelId = 7, questionText = "Refer to the diagram.\nThe center of the sphere from which a spherical mirror is a part is called the _____.",
                    optionA = "Pole", optionB = "Focus", 
                    optionC = "Center of curvature", optionD = "Principal axis",
                    correctAnswer = "C", explanation = "The center of curvature is the center of the imaginary sphere."),
                
                QuestionEntity(levelId = 7, questionText = "Refer to the diagram.\nThe distance from the pole to the center of curvature is called the _____.",
                    optionA = "Focal length", optionB = "Radius of curvature", 
                    optionC = "Aperture", optionD = "Principal axis",
                    correctAnswer = "B", explanation = "Radius of curvature is the distance from pole to center of curvature."),
                
                QuestionEntity(levelId = 7, questionText = "Refer to the diagram.\nThe point where parallel light rays converge (or appear to diverge from) after reflection is called the _____.",
                    optionA = "Pole", optionB = "Center of curvature", 
                    optionC = "Focus (Focal point)", optionD = "Aperture",
                    correctAnswer = "C", explanation = "The focus is where parallel rays meet or appear to come from."),
                
                QuestionEntity(levelId = 7, questionText = "Refer to the diagram.\nThe distance from the pole to the focal point is called the _____.",
                    optionA = "Radius of curvature", optionB = "Focal length", 
                    optionC = "Aperture", optionD = "Principal axis",
                    correctAnswer = "B", explanation = "Focal length is the distance between pole and focus."),
                
                // Level 8: Image Identifier (5 questions)
                QuestionEntity(levelId = 8, questionText = "An image formed by the actual convergence of light rays that can be projected onto a screen is called a _____ image.",
                    optionA = "Virtual", optionB = "Real", 
                    optionC = "Erect", optionD = "Magnified",
                    correctAnswer = "B", explanation = "Real images are formed by actual convergence of light rays."),
                
                QuestionEntity(levelId = 8, questionText = "An image that cannot be projected onto a screen because the light rays do not actually meet is called a _____ image.",
                    optionA = "Real", optionB = "Inverted", 
                    optionC = "Virtual", optionD = "Diminished",
                    correctAnswer = "C", explanation = "Virtual images cannot be projected as rays don't actually converge."),
                
                QuestionEntity(levelId = 8, questionText = "A real image is always _____ (opposite in orientation to the object).",
                    optionA = "Erect", optionB = "Virtual", 
                    optionC = "Inverted", optionD = "Magnified",
                    correctAnswer = "C", explanation = "Real images are always inverted (upside down)."),
                
                QuestionEntity(levelId = 8, questionText = "A virtual image is always _____ (same orientation as the object).",
                    optionA = "Inverted", optionB = "Erect", 
                    optionC = "Real", optionD = "Diminished",
                    correctAnswer = "B", explanation = "Virtual images are always erect (upright)."),
                
                QuestionEntity(levelId = 8, questionText = "Which type of mirror can produce both real and virtual images?",
                    optionA = "Plane mirror", optionB = "Convex mirror", 
                    optionC = "Concave mirror", optionD = "None of these",
                    correctAnswer = "C", explanation = "Concave mirrors can form both real and virtual images."),
                
                // Level 9: Plane Mirror Pro (5 questions with image reference)
                QuestionEntity(levelId = 9, questionText = "Refer to the diagram.\nIn a plane mirror, the image distance is _____ the object distance.",
                    optionA = "Greater than", optionB = "Less than", 
                    optionC = "Equal to", optionD = "Twice",
                    correctAnswer = "C", explanation = "Image distance equals object distance in plane mirrors."),
                
                QuestionEntity(levelId = 9, questionText = "Refer to the diagram.\nThe size of the image formed by a plane mirror is _____ the size of the object.",
                    optionA = "Larger than", optionB = "Smaller than", 
                    optionC = "Equal to", optionD = "Twice",
                    correctAnswer = "C", explanation = "Plane mirrors form images of the same size as the object."),
                
                QuestionEntity(levelId = 9, questionText = "Refer to the diagram.\nThe image formed by a plane mirror is always _____.",
                    optionA = "Real and inverted", optionB = "Virtual and erect", 
                    optionC = "Real and erect", optionD = "Virtual and inverted",
                    correctAnswer = "B", explanation = "Plane mirrors always form virtual, erect images."),
                
                QuestionEntity(levelId = 9, questionText = "Refer to the diagram.\nThe magnification produced by a plane mirror is _____.",
                    optionA = "Less than 1", optionB = "Greater than 1", 
                    optionC = "Equal to 1", optionD = "Zero",
                    correctAnswer = "C", explanation = "Magnification of 1 means image size equals object size."),
                
                QuestionEntity(levelId = 9, questionText = "Refer to the diagram.\nIf an object is placed 5 meters in front of a plane mirror, the image will appear _____ meters behind the mirror.",
                    optionA = "2.5", optionB = "5", 
                    optionC = "10", optionD = "15",
                    correctAnswer = "B", explanation = "Image distance equals object distance in plane mirrors."),
                
                // Level 10: Focal Finder (5 questions with image reference)
                QuestionEntity(levelId = 10, questionText = "Refer to the diagram.\nThe relationship between focal length (f) and radius of curvature (R) is _____.",
                    optionA = "f = R", optionB = "f = R/2", 
                    optionC = "f = 2R", optionD = "f = R²",
                    correctAnswer = "B", explanation = "Focal length is half the radius of curvature: f = R/2."),
                
                QuestionEntity(levelId = 10, questionText = "Refer to the diagram.\nFor a concave mirror, the focus is located _____.",
                    optionA = "Behind the mirror", optionB = "At the pole", 
                    optionC = "In front of the mirror", optionD = "At infinity",
                    correctAnswer = "C", explanation = "Concave mirrors have their focus in front of the reflecting surface."),
                
                QuestionEntity(levelId = 10, questionText = "Refer to the diagram.\nFor a convex mirror, the focus is located _____.",
                    optionA = "In front of the mirror", optionB = "Behind the mirror", 
                    optionC = "At the pole", optionD = "At the center of curvature",
                    correctAnswer = "B", explanation = "Convex mirrors have their focus behind the reflecting surface (virtual)."),
                
                QuestionEntity(levelId = 10, questionText = "Refer to the diagram.\nThe imaginary line passing through the pole and center of curvature of a spherical mirror is called the _____.",
                    optionA = "Normal", optionB = "Principal axis", 
                    optionC = "Focal line", optionD = "Ray path",
                    correctAnswer = "B", explanation = "The principal axis passes through pole and center of curvature."),
                
                QuestionEntity(levelId = 10, questionText = "Refer to the diagram.\nThe diameter of the reflecting surface of a spherical mirror is called the _____.",
                    optionA = "Radius", optionB = "Focal length", 
                    optionC = "Aperture", optionD = "Pole",
                    correctAnswer = "C", explanation = "Aperture is the diameter of the reflecting surface."),
                
                // Level 11: Real or Virtual? (5 questions - 2 options)
                QuestionEntity(levelId = 11, questionText = "An image that can be projected onto a screen is a _____.",
                    optionA = "Real Image", optionB = "Virtual Image", 
                    optionC = "", optionD = "",
                    correctAnswer = "A", explanation = "Real images can be projected onto screens."),
                
                QuestionEntity(levelId = 11, questionText = "An image formed behind a plane mirror is a _____.",
                    optionA = "Real Image", optionB = "Virtual Image", 
                    optionC = "", optionD = "",
                    correctAnswer = "B", explanation = "Plane mirrors always form virtual images behind them."),
                
                QuestionEntity(levelId = 11, questionText = "When a concave mirror forms an inverted image of an object placed beyond its center of curvature, it is a _____.",
                    optionA = "Real Image", optionB = "Virtual Image", 
                    optionC = "", optionD = "",
                    correctAnswer = "A", explanation = "Inverted images from concave mirrors are real images."),
                
                QuestionEntity(levelId = 11, questionText = "The enlarged upright image you see when holding a concave mirror very close to your face is a _____.",
                    optionA = "Real Image", optionB = "Virtual Image", 
                    optionC = "", optionD = "",
                    correctAnswer = "B", explanation = "When object is between pole and focus, concave mirrors form virtual images."),
                
                QuestionEntity(levelId = 11, questionText = "The image formed by a convex mirror is always a _____.",
                    optionA = "Real Image", optionB = "Virtual Image", 
                    optionC = "", optionD = "",
                    correctAnswer = "B", explanation = "Convex mirrors always form virtual images."),
                
                // Level 12: Mirror Match (5 questions - 3 options)
                QuestionEntity(levelId = 12, questionText = "Which type of mirror is used in car headlights to produce a powerful beam of light?",
                    optionA = "Plane Mirror", optionB = "Concave Mirror", 
                    optionC = "Convex Mirror", optionD = "",
                    correctAnswer = "B", explanation = "Concave mirrors in headlights produce parallel beams of light."),
                
                QuestionEntity(levelId = 12, questionText = "Which type of mirror is used as a rear-view mirror in vehicles?",
                    optionA = "Plane Mirror", optionB = "Concave Mirror", 
                    optionC = "Convex Mirror", optionD = "",
                    correctAnswer = "C", explanation = "Convex mirrors provide a wider field of view for rear-view mirrors."),
                
                QuestionEntity(levelId = 12, questionText = "Which type of mirror is used by dentists to see an enlarged view of teeth?",
                    optionA = "Plane Mirror", optionB = "Concave Mirror", 
                    optionC = "Convex Mirror", optionD = "",
                    correctAnswer = "B", explanation = "Dentists use concave mirrors to see enlarged images."),
                
                QuestionEntity(levelId = 12, questionText = "Which type of mirror is used in solar cookers to concentrate sunlight?",
                    optionA = "Plane Mirror", optionB = "Concave Mirror", 
                    optionC = "Convex Mirror", optionD = "",
                    correctAnswer = "B", explanation = "Concave mirrors concentrate sunlight at the focus to generate heat."),
                
                QuestionEntity(levelId = 12, questionText = "Which type of mirror always forms a virtual, upright, and diminished image?",
                    optionA = "Plane Mirror", optionB = "Concave Mirror", 
                    optionC = "Convex Mirror", optionD = "",
                    correctAnswer = "C", explanation = "Convex mirrors always produce virtual, upright, and smaller images."),
                
                // Level 13: Lens Learner (5 questions - 2 options)
                QuestionEntity(levelId = 13, questionText = "A lens that is thicker at the center than at the edges is a _____.",
                    optionA = "Concave Lens", optionB = "Convex Lens", 
                    optionC = "", optionD = "",
                    correctAnswer = "B", explanation = "Convex lenses are thicker at the center."),
                
                QuestionEntity(levelId = 13, questionText = "A lens that is thinner at the center than at the edges is a _____.",
                    optionA = "Concave Lens", optionB = "Convex Lens", 
                    optionC = "", optionD = "",
                    correctAnswer = "A", explanation = "Concave lenses are thinner at the center."),
                
                QuestionEntity(levelId = 13, questionText = "A lens that causes parallel light rays to converge (meet) at a focal point is a _____.",
                    optionA = "Concave Lens", optionB = "Convex Lens", 
                    optionC = "", optionD = "",
                    correctAnswer = "B", explanation = "Convex lenses converge light rays."),
                
                QuestionEntity(levelId = 13, questionText = "A lens that causes parallel light rays to diverge (spread apart) is a _____.",
                    optionA = "Concave Lens", optionB = "Convex Lens", 
                    optionC = "", optionD = "",
                    correctAnswer = "A", explanation = "Concave lenses diverge light rays."),
                
                QuestionEntity(levelId = 13, questionText = "The type of lens used in magnifying glasses is a _____.",
                    optionA = "Concave Lens", optionB = "Convex Lens", 
                    optionC = "", optionD = "",
                    correctAnswer = "B", explanation = "Magnifying glasses use convex lenses to enlarge images."),
                
                // Level 14: Mirror Life (5 questions - 2 options)
                QuestionEntity(levelId = 14, questionText = "Which type of mirror is commonly used in makeup or shaving mirrors to see an enlarged view?",
                    optionA = "Concave Mirror", optionB = "Convex Mirror", 
                    optionC = "", optionD = "",
                    correctAnswer = "A", explanation = "Concave mirrors produce enlarged images when held close."),
                
                QuestionEntity(levelId = 14, questionText = "Which type of mirror is used at blind corners and in shop security to provide a wide field of view?",
                    optionA = "Concave Mirror", optionB = "Convex Mirror", 
                    optionC = "", optionD = "",
                    correctAnswer = "B", explanation = "Convex mirrors provide a wide field of view for security."),
                
                QuestionEntity(levelId = 14, questionText = "Which type of mirror is used in searchlights and flashlights to produce a focused beam?",
                    optionA = "Concave Mirror", optionB = "Convex Mirror", 
                    optionC = "", optionD = "",
                    correctAnswer = "A", explanation = "Concave mirrors focus light into parallel beams."),
                
                QuestionEntity(levelId = 14, questionText = "Which type of mirror is used in telescopes to collect and focus light from distant objects?",
                    optionA = "Concave Mirror", optionB = "Convex Mirror", 
                    optionC = "", optionD = "",
                    correctAnswer = "A", explanation = "Telescopes use concave mirrors to collect and focus light."),
                
                QuestionEntity(levelId = 14, questionText = "Which type of mirror would you use if you wanted to see a larger area behind you while driving?",
                    optionA = "Concave Mirror", optionB = "Convex Mirror", 
                    optionC = "", optionD = "",
                    correctAnswer = "B", explanation = "Convex mirrors show a wider area but with smaller images."),
                
                // Level 15: Final Quest (15 comprehensive questions)
                QuestionEntity(levelId = 15, questionText = "What is the speed of light in vacuum?",
                    optionA = "3 × 10⁶ m/s", optionB = "3 × 10⁸ m/s", 
                    optionC = "3 × 10¹⁰ m/s", optionD = "3 × 10¹² m/s",
                    correctAnswer = "B", explanation = "Light travels at 3 × 10⁸ m/s (300,000 km/s) in vacuum."),
                
                QuestionEntity(levelId = 15, questionText = "The angle of incidence is always _____ the angle of reflection.",
                    optionA = "Greater than", optionB = "Less than", 
                    optionC = "Equal to", optionD = "Half of",
                    correctAnswer = "C", explanation = "This is the fundamental law of reflection."),
                
                QuestionEntity(levelId = 15, questionText = "An image that can be projected onto a screen is called a _____ image.",
                    optionA = "Virtual", optionB = "Real", 
                    optionC = "Erect", optionD = "Lateral",
                    correctAnswer = "B", explanation = "Real images can be projected, virtual images cannot."),
                
                QuestionEntity(levelId = 15, questionText = "The mirror formula is _____.",
                    optionA = "1/f = 1/u + 1/v", optionB = "f = u + v", 
                    optionC = "1/f = u + v", optionD = "f = 1/u + 1/v",
                    correctAnswer = "A", explanation = "The mirror formula relates focal length, object distance, and image distance."),
                
                QuestionEntity(levelId = 15, questionText = "The magnification formula for mirrors is _____.",
                    optionA = "m = u/v", optionB = "m = v/u", 
                    optionC = "m = -v/u", optionD = "m = -u/v",
                    correctAnswer = "C", explanation = "Magnification equals negative of image distance over object distance."),
                
                QuestionEntity(levelId = 15, questionText = "For a concave mirror, the focal length is _____.",
                    optionA = "Positive", optionB = "Negative", 
                    optionC = "Zero", optionD = "Infinite",
                    correctAnswer = "B", explanation = "By sign convention, focal length of concave mirror is negative."),
                
                QuestionEntity(levelId = 15, questionText = "For a convex mirror, the focal length is _____.",
                    optionA = "Positive", optionB = "Negative", 
                    optionC = "Zero", optionD = "Infinite",
                    correctAnswer = "A", explanation = "By sign convention, focal length of convex mirror is positive."),
                
                QuestionEntity(levelId = 15, questionText = "The relationship between focal length (f) and radius of curvature (R) is _____.",
                    optionA = "f = R", optionB = "f = R/2", 
                    optionC = "f = 2R", optionD = "f = R/4",
                    correctAnswer = "B", explanation = "Focal length is half the radius of curvature."),
                
                QuestionEntity(levelId = 15, questionText = "When an object is placed at the center of curvature of a concave mirror, the image is _____.",
                    optionA = "At focus", optionB = "At center of curvature", 
                    optionC = "At infinity", optionD = "Behind the mirror",
                    correctAnswer = "B", explanation = "Object at C produces image at C, same size, real, and inverted."),
                
                QuestionEntity(levelId = 15, questionText = "A convex mirror always produces an image that is _____.",
                    optionA = "Real and inverted", optionB = "Virtual and erect", 
                    optionC = "Real and erect", optionD = "Virtual and inverted",
                    correctAnswer = "B", explanation = "Convex mirrors always form virtual, erect, and diminished images."),
                
                QuestionEntity(levelId = 15, questionText = "The power of a lens is measured in _____.",
                    optionA = "Meters", optionB = "Watts", 
                    optionC = "Diopters", optionD = "Hertz",
                    correctAnswer = "C", explanation = "Lens power is measured in diopters (D)."),
                
                QuestionEntity(levelId = 15, questionText = "A converging lens is also known as a _____ lens.",
                    optionA = "Concave", optionB = "Convex", 
                    optionC = "Plane", optionD = "Cylindrical",
                    correctAnswer = "B", explanation = "Convex lenses are converging lenses."),
                
                QuestionEntity(levelId = 15, questionText = "A diverging lens is also known as a _____ lens.",
                    optionA = "Concave", optionB = "Convex", 
                    optionC = "Plane", optionD = "Spherical",
                    correctAnswer = "A", explanation = "Concave lenses are diverging lenses."),
                
                QuestionEntity(levelId = 15, questionText = "To correct myopia (nearsightedness), which type of lens is used?",
                    optionA = "Convex lens", optionB = "Concave lens", 
                    optionC = "Plane mirror", optionD = "Prism",
                    correctAnswer = "B", explanation = "Concave lenses diverge light to correct nearsightedness."),
                
                QuestionEntity(levelId = 15, questionText = "To correct hyperopia (farsightedness), which type of lens is used?",
                    optionA = "Convex lens", optionB = "Concave lens", 
                    optionC = "Plane mirror", optionD = "Cylindrical lens",
                    correctAnswer = "A", explanation = "Convex lenses converge light to correct farsightedness.")
            )
        }
    }
}
