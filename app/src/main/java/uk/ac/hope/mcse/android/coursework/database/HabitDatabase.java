package uk.ac.hope.mcse.android.coursework.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import uk.ac.hope.mcse.android.coursework.models.Habit;

@Database(entities = {Habit.class}, version = 2, exportSchema = false)
public abstract class HabitDatabase extends RoomDatabase {
    public abstract HabitDao habitDao();

    private static volatile HabitDatabase INSTANCE;

    public static HabitDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (HabitDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    HabitDatabase.class, "habit_database")
                            .addMigrations(MIGRATION_1_2) // Add the migration here
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Add the new columns to the habits table
            database.execSQL("ALTER TABLE habits ADD COLUMN reminderEnabled INTEGER NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE habits ADD COLUMN reminderTime INTEGER NOT NULL DEFAULT 0");
        }
    };

}
