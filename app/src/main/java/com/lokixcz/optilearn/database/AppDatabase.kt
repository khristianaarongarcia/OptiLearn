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
    version = 4,
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
                QuestionEntity(levelId = 3, questionText = "Reflection that forms clear images occurs on ______ surfaces.",
                    optionA = "Rough", optionB = "Smooth", 
                    optionC = "Curved", optionD = "Transparent",
                    correctAnswer = "B", explanation = "Smooth surfaces produce clear reflected images.",
                    imageResource = "level3_q1"),
                
                QuestionEntity(levelId = 3, questionText = "The line drawn perpendicular to the reflecting surface at the point of incidence is called the ______.",
                    optionA = "Reflected ray", optionB = "Normal line", 
                    optionC = "Incident ray", optionD = "Ray of sight",
                    correctAnswer = "B", explanation = "The normal line is perpendicular to the reflecting surface.",
                    imageResource = "level3_q1"),
                
                QuestionEntity(levelId = 3, questionText = "The ray of light that comes from the light source and strikes the surface is known as the ______.",
                    optionA = "Reflected ray", optionB = "Refracted ray", 
                    optionC = "Incident ray", optionD = "Normal",
                    correctAnswer = "C", explanation = "The incident ray is the light ray coming from the source.",
                    imageResource = "level3_q1"),
                
                QuestionEntity(levelId = 3, questionText = "The ray of light that bounces off from the mirror surface is called the ______.",
                    optionA = "Incident ray", optionB = "Reflected ray", 
                    optionC = "Normal line", optionD = "Transmitted ray",
                    correctAnswer = "B", explanation = "The reflected ray is the light bouncing off the surface.",
                    imageResource = "level3_q1"),
                
                QuestionEntity(levelId = 3, questionText = "The incident ray, reflected ray, and normal line all lie ______.",
                    optionA = "At right angles", optionB = "In different planes", 
                    optionC = "Randomly", optionD = "In the same plane",
                    correctAnswer = "D", explanation = "All three rays lie in the same plane according to the laws of reflection.",
                    imageResource = "level3_q1"),
                
                // Level 4: Mirror Mapper (5 questions with image reference)
                QuestionEntity(levelId = 4, questionText = "What type of image is formed by a plane mirror?",
                    optionA = "Real", optionB = "Inverted", 
                    optionC = "Enlarged", optionD = "Virtual",
                    correctAnswer = "D", explanation = "A plane mirror forms a virtual image.",
                    imageResource = "level4_q1"),
                
                QuestionEntity(levelId = 4, questionText = "The image formed by a plane mirror is ______ as the object.",
                    optionA = "Smaller than", optionB = "The same size", 
                    optionC = "Larger than", optionD = "Upside down",
                    correctAnswer = "B", explanation = "Plane mirrors form images of the same size as the object.",
                    imageResource = "level4_q1"),
                
                QuestionEntity(levelId = 4, questionText = "How is the orientation of the image compared to the object in a plane mirror?",
                    optionA = "Same orientation", optionB = "Upside down", 
                    optionC = "Tilted", optionD = "Opposite direction vertically",
                    correctAnswer = "A", explanation = "Plane mirror images have the same orientation (upright).",
                    imageResource = "level4_q1"),
                
                QuestionEntity(levelId = 4, questionText = "The image appears to be ______ the mirror as the object is in front of it.",
                    optionA = "Farther behind", optionB = "Closer behind", 
                    optionC = "At the same distance behind", optionD = "On the surface of",
                    correctAnswer = "C", explanation = "Image distance equals object distance in plane mirrors.",
                    imageResource = "level4_q1"),
                
                QuestionEntity(levelId = 4, questionText = "When light hits an object and reflects in all directions, we are able to ______.",
                    optionA = "Produce sound", optionB = "Absorb heat", 
                    optionC = "See the object", optionD = "Block the light",
                    correctAnswer = "C", explanation = "We see objects when light reflects off them in all directions.",
                    imageResource = "level4_q1"),
                
                // Level 5: Reflection Specialist (5 questions with image reference)
                QuestionEntity(levelId = 5, questionText = "What happens in diffused reflection?",
                    optionA = "Light reflects in one direction", optionB = "Light scatters in many directions", 
                    optionC = "Light is absorbed", optionD = "Light passes through",
                    correctAnswer = "B", explanation = "Diffuse reflection scatters light in many directions.",
                    imageResource = "level5_q1"),
                
                QuestionEntity(levelId = 5, questionText = "Reflection from a smooth surface is called _____.",
                    optionA = "Irregular reflection", optionB = "Specular reflection", 
                    optionC = "Scattered reflection", optionD = "Diffused reflection",
                    correctAnswer = "B", explanation = "Specular reflection occurs on smooth surfaces and produces clear images.",
                    imageResource = "level5_q1"),
                
                QuestionEntity(levelId = 5, questionText = "Rough surfaces cause light rays to _____.",
                    optionA = "Stay parallel", optionB = "Not reflect", 
                    optionC = "Scatter", optionD = "Focus",
                    correctAnswer = "C", explanation = "Rough surfaces scatter light rays in many directions.",
                    imageResource = "level5_q1"),
                
                QuestionEntity(levelId = 5, questionText = "Which situation best shows specular reflection?",
                    optionA = "Light bouncing off calm water.", optionB = "Light reflecting on wrinkled paper.", 
                    optionC = "Light reflecting on a polished metal or mirror.", optionD = "Light hitting a rocky mountain.",
                    correctAnswer = "C", explanation = "Polished metal and mirrors have smooth surfaces that produce specular reflection.",
                    imageResource = "level5_q1"),
                
                QuestionEntity(levelId = 5, questionText = "A clear image is formed only in which type of reflection?",
                    optionA = "Specular reflection", optionB = "Diffused reflection", 
                    optionC = "Irregular reflection", optionD = "Scattered reflection",
                    correctAnswer = "A", explanation = "Specular reflection from smooth surfaces produces clear images.",
                    imageResource = "level5_q1"),
                
                // Level 6: Lateral Inverter (5 questions with image reference)
                QuestionEntity(levelId = 6, questionText = "Why is the word \"AMBULANCE\" written in reverse on the front of ambulances?",
                    optionA = "For decoration", optionB = "To look unique", 
                    optionC = "So it appears correctly in a rear-view mirror", optionD = "To confuse drivers",
                    correctAnswer = "C", explanation = "The reverse writing appears correct when viewed in a mirror.",
                    imageResource = "level6_q1"),
                
                QuestionEntity(levelId = 6, questionText = "The phenomenon where left appears right and right appears left in a mirror is called _____.",
                    optionA = "Reflection of light", optionB = "Lateral inversion", 
                    optionC = "Diffused reflection", optionD = "Mirror distortion",
                    correctAnswer = "B", explanation = "Lateral inversion is the sideways reversal of images in mirrors.",
                    imageResource = "level6_q1"),
                
                QuestionEntity(levelId = 6, questionText = "What type of mirror causes lateral inversion?",
                    optionA = "Concave mirror", optionB = "Convex mirror", 
                    optionC = "Plane mirror", optionD = "Two-way mirror",
                    correctAnswer = "C", explanation = "Plane mirrors produce laterally inverted images.",
                    imageResource = "level6_q1"),
                
                QuestionEntity(levelId = 6, questionText = "In a plane mirror, the image formed is _____.",
                    optionA = "Real and inverted", optionB = "Virtual and laterally inverted", 
                    optionC = "Magnified and real", optionD = "Diminished and upright",
                    correctAnswer = "B", explanation = "Plane mirrors form virtual, laterally inverted images.",
                    imageResource = "level6_q1"),
                
                QuestionEntity(levelId = 6, questionText = "What happens when you raise your right hand in front of a mirror?",
                    optionA = "The image raises its right hand", optionB = "The image raises its left hand", 
                    optionC = "The image raises both hands", optionD = "The image doesn't move",
                    correctAnswer = "B", explanation = "Due to lateral inversion, when you raise your right hand, the mirror image raises its left hand.",
                    imageResource = "level6_q1"),
                
                // Level 7: Curved Mirror Champion (5 questions with image reference)
                QuestionEntity(levelId = 7, questionText = "What are mirrors with a curved surface called?",
                    optionA = "Plane mirrors", optionB = "Flat mirrors", 
                    optionC = "Spherical mirrors", optionD = "Parabolic mirrors",
                    correctAnswer = "C", explanation = "Mirrors with curved surfaces are called spherical mirrors.",
                    imageResource = "level7_q1"),
                
                QuestionEntity(levelId = 7, questionText = "A concave mirror is also known as a ____ mirror.",
                    optionA = "Diverging", optionB = "Converging", 
                    optionC = "Plane", optionD = "Reflective",
                    correctAnswer = "B", explanation = "Concave mirrors converge light rays to a focal point.",
                    imageResource = "level7_q1"),
                
                QuestionEntity(levelId = 7, questionText = "What does a concave mirror do to light rays?",
                    optionA = "Spreads them apart", optionB = "Reflects them inward to a focal point", 
                    optionC = "Makes them parallel", optionD = "Absorbs them",
                    correctAnswer = "B", explanation = "Concave mirrors reflect light rays inward toward a focal point.",
                    imageResource = "level7_q1"),
                
                QuestionEntity(levelId = 7, questionText = "Which type of mirror is used to see a wider view, such as in vehicles?",
                    optionA = "Concave mirror", optionB = "Convex mirror", 
                    optionC = "Plane mirror", optionD = "Parabolic mirror",
                    correctAnswer = "B", explanation = "Convex mirrors provide a wider field of view, making them ideal for vehicle side mirrors.",
                    imageResource = "level7_q1"),
                
                QuestionEntity(levelId = 7, questionText = "What happens to light rays when they hit a convex mirror?",
                    optionA = "They spread out or diverge", optionB = "They focus at a point", 
                    optionC = "They get absorbed", optionD = "They reflect inward",
                    correctAnswer = "A", explanation = "Convex mirrors cause light rays to diverge or spread out.",
                    imageResource = "level7_q1"),
                
                // Level 8: Image Identifier (5 questions)
                QuestionEntity(levelId = 8, questionText = "When light rays after reflection, what type of image is formed?",
                    optionA = "Real image", optionB = "Virtual image", 
                    optionC = "Upright image", optionD = "Enlarged image",
                    correctAnswer = "A", explanation = "When light rays actually meet after reflection, a real image is formed."),
                
                QuestionEntity(levelId = 8, questionText = "A real image is always ____ with respect to the object.",
                    optionA = "Erect", optionB = "Inverted", 
                    optionC = "Magnified", optionD = "Smaller",
                    correctAnswer = "B", explanation = "Real images are always inverted (upside down) with respect to the object."),
                
                QuestionEntity(levelId = 8, questionText = "A virtual image is formed when light rays ____ after reflection.",
                    optionA = "Actually meet", optionB = "Pass through the mirror", 
                    optionC = "Do not actually intersect", optionD = "Absorb into the surface",
                    correctAnswer = "C", explanation = "Virtual images form when light rays do not actually intersect but only appear to."),
                
                QuestionEntity(levelId = 8, questionText = "What type of mirror forms a virtual and erect image?",
                    optionA = "Convex mirror", optionB = "Concave mirror", 
                    optionC = "Plane mirror", optionD = "Spherical mirror",
                    correctAnswer = "C", explanation = "Plane mirrors always form virtual and erect images."),
                
                QuestionEntity(levelId = 8, questionText = "An image that can be formed on a screen?",
                    optionA = "Virtual image", optionB = "Real image", 
                    optionC = "Enlarged image", optionD = "Magnified image",
                    correctAnswer = "B", explanation = "Real images can be projected onto screens."),
                
                // Level 9: Plane Mirror Pro (5 questions with image reference)
                QuestionEntity(levelId = 9, questionText = "Where does the image formed by a plane mirror appear?",
                    optionA = "In front of the mirror", optionB = "On the surface of the mirror", 
                    optionC = "Behind the mirror", optionD = "Inside the mirror",
                    correctAnswer = "C", explanation = "Images in plane mirrors appear to be behind the mirror.",
                    imageResource = "level9_q1"),
                
                QuestionEntity(levelId = 9, questionText = "What is the orientation of the image formed by a plane mirror?",
                    optionA = "Inverted", optionB = "Upright", 
                    optionC = "Sideways", optionD = "Reversed vertically",
                    correctAnswer = "B", explanation = "Plane mirrors form upright (erect) images.",
                    imageResource = "level9_q1"),
                
                QuestionEntity(levelId = 9, questionText = "The image formed by a plane mirror is ____ in size compared to the object.",
                    optionA = "Smaller", optionB = "Larger", 
                    optionC = "The same", optionD = "Variable",
                    correctAnswer = "C", explanation = "Plane mirrors form images that are the same size as the object.",
                    imageResource = "level9_q1"),
                
                QuestionEntity(levelId = 9, questionText = "The type of image produced by a plane mirror is _____.",
                    optionA = "Real", optionB = "Virtual", 
                    optionC = "Enlarged", optionD = "Inverted",
                    correctAnswer = "B", explanation = "Plane mirrors always produce virtual images.",
                    imageResource = "level9_q1"),
                
                QuestionEntity(levelId = 9, questionText = "How is the image in a plane mirror formed?",
                    optionA = "By absorption of light", optionB = "By reflection of light rays that appear to meet behind the mirror", 
                    optionC = "By refraction of light through glass", optionD = "By scattering of light in all directions",
                    correctAnswer = "B", explanation = "Plane mirror images form when light rays reflect and appear to meet behind the mirror.",
                    imageResource = "level9_q1"),
                
                // Level 10: Focal Finder (5 questions with image reference)
                QuestionEntity(levelId = 10, questionText = "What is the orientation of an image formed by a plane mirror?",
                    optionA = "Inverted", optionB = "Laterally inverted", 
                    optionC = "Upright", optionD = "Real",
                    correctAnswer = "B", explanation = "Plane mirrors produce laterally inverted images.",
                    imageResource = "level10_q1"),
                
                QuestionEntity(levelId = 10, questionText = "Which term describes the point where the principal axis meets the curved mirror?",
                    optionA = "Center of Curvature (C)", optionB = "Focal Point (F)", 
                    optionC = "Vertex (V)", optionD = "Radius of Curvature (R)",
                    correctAnswer = "C", explanation = "The vertex (V) is the point where the principal axis meets the curved mirror.",
                    imageResource = "level10_q1"),
                
                QuestionEntity(levelId = 10, questionText = "If the Radius of Curvature (R) of a curved mirror is 20 cm, what is its focal length (f)?",
                    optionA = "5 cm", optionB = "10 cm", 
                    optionC = "20 cm", optionD = "40 cm",
                    correctAnswer = "B", explanation = "Focal length is half the radius of curvature: f = R/2 = 20/2 = 10 cm.",
                    imageResource = "level10_q1"),
                
                QuestionEntity(levelId = 10, questionText = "The image formed by a plane mirror is always classified as Virtual because:",
                    optionA = "It is inverted.", optionB = "It is smaller than the object.", 
                    optionC = "It is formed by the actual intersection of light rays.", optionD = "It is formed by the apparent intersection of the extended reflected rays.",
                    correctAnswer = "D", explanation = "Virtual images are formed by the apparent intersection of extended rays, not actual intersection.",
                    imageResource = "level10_q1"),
                
                QuestionEntity(levelId = 10, questionText = "The distance between the Focal Point (F) and the Vertex (V) on a curved mirror is known as the:",
                    optionA = "Center of Curvature (C)", optionB = "Radius of Curvature (R)", 
                    optionC = "Focal Length (f)", optionD = "Principal Axis",
                    correctAnswer = "C", explanation = "Focal length (f) is the distance between the focal point and the vertex.",
                    imageResource = "level10_q1"),
                
                // Level 11: Real or Virtual? (5 questions - 2 options)
                QuestionEntity(levelId = 11, questionText = "Which type of image can always be projected onto a screen?",
                    optionA = "Real Image", optionB = "Virtual Image", 
                    optionC = "", optionD = "",
                    correctAnswer = "A", explanation = "Real images can be projected onto screens."),
                
                QuestionEntity(levelId = 11, questionText = "An image described as upright (same orientation as the object) is usually a:",
                    optionA = "Real Image", optionB = "Virtual Image", 
                    optionC = "", optionD = "",
                    correctAnswer = "B", explanation = "Upright images are usually virtual images."),
                
                QuestionEntity(levelId = 11, questionText = "Which image type is formed when reflected light rays do not actually converge, but only appear to come from a point behind the mirror?",
                    optionA = "Real Image", optionB = "Virtual Image", 
                    optionC = "", optionD = "",
                    correctAnswer = "B", explanation = "Virtual images form when rays appear to come from a point but don't actually converge."),
                
                QuestionEntity(levelId = 11, questionText = "If an image is inverted (upside down) with respect to the object, it must be a:",
                    optionA = "Real Image", optionB = "Virtual Image", 
                    optionC = "", optionD = "",
                    correctAnswer = "A", explanation = "Inverted images are real images."),
                
                QuestionEntity(levelId = 11, questionText = "An image that is formed on the same side of a mirror as the object (in front) is considered a:",
                    optionA = "Real Image", optionB = "Virtual Image", 
                    optionC = "", optionD = "",
                    correctAnswer = "A", explanation = "Images formed on the same side as the object (in front of mirror) are real images."),
                
                // Level 12: Mirror Match (5 questions - 3 options)
                QuestionEntity(levelId = 12, questionText = "Which type of mirror always forms reduced images?",
                    optionA = "Plane Mirror", optionB = "Concave Mirror", 
                    optionC = "Convex Mirror", optionD = "",
                    correctAnswer = "C", explanation = "Convex mirrors always form reduced (diminished) images."),
                
                QuestionEntity(levelId = 12, questionText = "Which type of mirror can form either real or virtual images?",
                    optionA = "Plane Mirror", optionB = "Concave Mirror", 
                    optionC = "Convex Mirror", optionD = "",
                    correctAnswer = "B", explanation = "Concave mirrors can form both real and virtual images depending on object position."),
                
                QuestionEntity(levelId = 12, questionText = "Which type of mirror always forms virtual images?",
                    optionA = "Plane Mirror", optionB = "Concave Mirror", 
                    optionC = "Convex Mirror", optionD = "",
                    correctAnswer = "A", explanation = "Plane mirrors always form virtual images."),
                
                QuestionEntity(levelId = 12, questionText = "Which type of mirror is flat and smooth?",
                    optionA = "Plane Mirror", optionB = "Concave Mirror", 
                    optionC = "Convex Mirror", optionD = "",
                    correctAnswer = "A", explanation = "Plane mirrors have flat and smooth surfaces."),
                
                QuestionEntity(levelId = 12, questionText = "Which type of mirror can form images that are upright or inverted?",
                    optionA = "Plane Mirror", optionB = "Concave Mirror", 
                    optionC = "Convex Mirror", optionD = "",
                    correctAnswer = "B", explanation = "Concave mirrors can form both upright and inverted images depending on object position."),
                
                // Level 13: Lens Learner (5 questions - 2 options)
                QuestionEntity(levelId = 13, questionText = "In which lens does the curve face inward?",
                    optionA = "Concave Lens", optionB = "Convex Lens", 
                    optionC = "", optionD = "",
                    correctAnswer = "A", explanation = "Concave lenses have surfaces that curve inward."),
                
                QuestionEntity(levelId = 13, questionText = "In which lens does the curve face outward?",
                    optionA = "Concave Lens", optionB = "Convex Lens", 
                    optionC = "", optionD = "",
                    correctAnswer = "B", explanation = "Convex lenses have surfaces that curve outward."),
                
                QuestionEntity(levelId = 13, questionText = "Which lens is used for the correction of nearsightedness or myopia?",
                    optionA = "Concave Lens", optionB = "Convex Lens", 
                    optionC = "", optionD = "",
                    correctAnswer = "A", explanation = "Concave lenses are used to correct nearsightedness (myopia)."),
                
                QuestionEntity(levelId = 13, questionText = "Which lens is used for the correction of farsightedness or hyperopia?",
                    optionA = "Concave Lens", optionB = "Convex Lens", 
                    optionC = "", optionD = "",
                    correctAnswer = "B", explanation = "Convex lenses are used to correct farsightedness (hyperopia)."),
                
                QuestionEntity(levelId = 13, questionText = "Which lens is a converging lens that brings refracted rays together?",
                    optionA = "Concave Lens", optionB = "Convex Lens", 
                    optionC = "", optionD = "",
                    correctAnswer = "B", explanation = "Convex lenses are converging lenses that bring light rays together."),
                
                // Level 14: Mirror Life (5 questions - 2 options)
                QuestionEntity(levelId = 14, questionText = "Vehicle side view mirror",
                    optionA = "Concave Mirror", optionB = "Convex Mirror", 
                    optionC = "", optionD = "",
                    correctAnswer = "B", explanation = "Vehicle side view mirrors use convex mirrors for wider field of view."),
                
                QuestionEntity(levelId = 14, questionText = "Headlight of motorcycle",
                    optionA = "Concave Mirror", optionB = "Convex Mirror", 
                    optionC = "", optionD = "",
                    correctAnswer = "A", explanation = "Motorcycle headlights use concave mirrors to produce focused beams."),
                
                QuestionEntity(levelId = 14, questionText = "Inner surface of glasses",
                    optionA = "Concave Mirror", optionB = "Convex Mirror", 
                    optionC = "", optionD = "",
                    correctAnswer = "A", explanation = "Inner surface of glasses typically uses concave mirrors."),
                
                QuestionEntity(levelId = 14, questionText = "Lunch plates",
                    optionA = "Concave Mirror", optionB = "Convex Mirror", 
                    optionC = "", optionD = "",
                    correctAnswer = "A", explanation = "Lunch plates have concave curved surfaces."),
                
                QuestionEntity(levelId = 14, questionText = "Globe",
                    optionA = "Concave Mirror", optionB = "Convex Mirror", 
                    optionC = "", optionD = "",
                    correctAnswer = "B", explanation = "A globe has a convex curved surface."),
                
                // Level 15: Final Quest (15 comprehensive questions)
                QuestionEntity(levelId = 15, questionText = "Light is a form of energy that can travel even through a vacuum because it is ______.",
                    optionA = "Chemical energy", optionB = "Thermal energy", 
                    optionC = "Electromagnetic radiation", optionD = "Sound energy",
                    correctAnswer = "C", explanation = "Light is electromagnetic radiation that can travel through vacuum."),
                
                QuestionEntity(levelId = 15, questionText = "Light travels in straight lines called ______.",
                    optionA = "Beams", optionB = "Rays", 
                    optionC = "Streams", optionD = "Waves",
                    correctAnswer = "B", explanation = "Light travels in straight lines called rays."),
                
                QuestionEntity(levelId = 15, questionText = "When light strikes a smooth surface and bounces back, the process is called ______.",
                    optionA = "Refraction", optionB = "Reflection", 
                    optionC = "Absorption", optionD = "Transmission",
                    correctAnswer = "B", explanation = "Reflection is when light bounces back from a surface."),
                
                QuestionEntity(levelId = 15, questionText = "When light hits a rough surface and scatters in many directions, it produces a ______ reflection.",
                    optionA = "Specular", optionB = "Regular", 
                    optionC = "Diffused", optionD = "Irregular",
                    correctAnswer = "C", explanation = "Diffused reflection occurs when light scatters in many directions from rough surfaces."),
                
                QuestionEntity(levelId = 15, questionText = "According to the Law of Reflection, the angle of incidence is ______ the angle of reflection.",
                    optionA = "Greater than", optionB = "Less than", 
                    optionC = "Equal to", optionD = "Not related to",
                    correctAnswer = "C", explanation = "The Law of Reflection states that angle of incidence equals angle of reflection."),
                
                QuestionEntity(levelId = 15, questionText = "Which of the following surfaces would form a clear reflected image?",
                    optionA = "Rough wood", optionB = "Wrinkled paper", 
                    optionC = "Polished metal", optionD = "Cloth",
                    correctAnswer = "C", explanation = "Polished metal has a smooth surface that forms clear reflected images."),
                
                QuestionEntity(levelId = 15, questionText = "What type of image is produced by a plane mirror?",
                    optionA = "Real and inverted", optionB = "Virtual and upright", 
                    optionC = "Magnified and real", optionD = "Reduced and inverted",
                    correctAnswer = "B", explanation = "Plane mirrors produce virtual and upright images."),
                
                QuestionEntity(levelId = 15, questionText = "When you raise your right hand in front of a mirror, the image raises its ______ hand.",
                    optionA = "Right", optionB = "Left", 
                    optionC = "Both", optionD = "Neither",
                    correctAnswer = "B", explanation = "Due to lateral inversion, your right hand appears as the left hand in the mirror."),
                
                QuestionEntity(levelId = 15, questionText = "A concave mirror is also known as a ______ mirror.",
                    optionA = "Diverging", optionB = "Converging", 
                    optionC = "Plane", optionD = "Flat",
                    correctAnswer = "B", explanation = "Concave mirrors are converging mirrors that focus light rays."),
                
                QuestionEntity(levelId = 15, questionText = "A convex mirror is useful as a vehicle side mirror because it ______.",
                    optionA = "Produces real images", optionB = "Provides a wider field of view", 
                    optionC = "Magnifies the object greatly", optionD = "Focuses light rays to a point",
                    correctAnswer = "B", explanation = "Convex mirrors provide a wider field of view, making them ideal for vehicle side mirrors."),
                
                QuestionEntity(levelId = 15, questionText = "Which type of image can be projected onto a screen?",
                    optionA = "Real image", optionB = "Virtual image", 
                    optionC = "Upright image", optionD = "Enlarged image",
                    correctAnswer = "A", explanation = "Real images can be projected onto screens."),
                
                QuestionEntity(levelId = 15, questionText = "The distance between the focal point and the vertex of a curved mirror is called the ______.",
                    optionA = "Radius of curvature", optionB = "Center of curvature", 
                    optionC = "Focal length", optionD = "Principal axis",
                    correctAnswer = "C", explanation = "Focal length is the distance between the focal point and the vertex."),
                
                QuestionEntity(levelId = 15, questionText = "Which lens is used to correct nearsightedness (myopia)?",
                    optionA = "Convex lens", optionB = "Concave lens", 
                    optionC = "Cylindrical lens", optionD = "Plane lens",
                    correctAnswer = "B", explanation = "Concave lenses diverge light to correct nearsightedness."),
                
                QuestionEntity(levelId = 15, questionText = "Which lens brings parallel light rays together to a single point?",
                    optionA = "Concave lens", optionB = "Convex lens", 
                    optionC = "Diverging lens", optionD = "Flat lens",
                    correctAnswer = "B", explanation = "Convex lenses converge parallel light rays to a focal point."),
                
                QuestionEntity(levelId = 15, questionText = "The mirror that always forms virtual, erect, and smaller images is the ______ mirror.",
                    optionA = "Plane", optionB = "Concave", 
                    optionC = "Convex", optionD = "Parabolic",
                    correctAnswer = "C", explanation = "Convex mirrors always form virtual, erect, and smaller images.")
            )
        }
    }
}
