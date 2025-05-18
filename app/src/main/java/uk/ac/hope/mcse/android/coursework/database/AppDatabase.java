package uk.ac.hope.mcse.android.coursework.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import uk.ac.hope.mcse.android.coursework.database.ChallengeDao;
import uk.ac.hope.mcse.android.coursework.database.HabitCompletionDao;
import uk.ac.hope.mcse.android.coursework.database.HabitDao;
import uk.ac.hope.mcse.android.coursework.models.Challenge;
import uk.ac.hope.mcse.android.coursework.models.Habit;
import uk.ac.hope.mcse.android.coursework.models.HabitCompletion;
import uk.ac.hope.mcse.android.coursework.utils.Converters;

@Database(entities = {Habit.class, HabitCompletion.class, Challenge.class}, version = 2)
@TypeConverters(Converters.class)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;
    public abstract HabitDao habitDao();
    public abstract HabitCompletionDao habitCompletionDao();
    public abstract ChallengeDao challengeDao();

    // Add migration strategy
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Create the new challenges table
            database.execSQL(
                    "CREATE TABLE IF NOT EXISTS challenges (" +
                            "id TEXT PRIMARY KEY NOT NULL, " +
                            "name TEXT NOT NULL, " +
                            "description TEXT, " +
                            "habitIds TEXT NOT NULL, " + // Stored as JSON
                            "startDate INTEGER NOT NULL, " +
                            "endDate INTEGER NOT NULL, " +
                            "isCompleted INTEGER NOT NULL DEFAULT 0, " +
                            "requiredDaysToComplete INTEGER NOT NULL DEFAULT 1)"
            );
        }
    };

    // Update getInstance to include migrations
    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "habits_database")
                    .addMigrations(MIGRATION_1_2)
                    .build();
        }
        return instance;
    }
}