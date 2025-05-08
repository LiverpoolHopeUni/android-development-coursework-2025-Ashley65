package uk.ac.hope.mcse.android.coursework.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import uk.ac.hope.mcse.android.coursework.models.Habit;

@Dao
public interface HabitDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Habit habit);

    @Update
    void update(Habit habit);

    @Delete
    void delete(Habit habit);

    @Query("DELETE FROM habits WHERE id = :id")
    void deleteById(String id);

    @Query("SELECT * FROM habits WHERE id = :id")
    Habit getHabitById(String id);

    @Query("SELECT * FROM habits ORDER BY title ASC")
    LiveData<List<Habit>> getAllHabits();

    @Query("SELECT * FROM habits WHERE isDaily = 1 ORDER BY title ASC")
    LiveData<List<Habit>> getDailyHabits();

    @Query("SELECT * FROM habits WHERE isDaily = 0 ORDER BY title ASC")
    LiveData<List<Habit>> getWeeklyHabits();

    // In HabitDao.java
    @Query("SELECT * FROM habits ORDER BY title ASC")
    List<Habit> getAllHabitsAsList();

}
