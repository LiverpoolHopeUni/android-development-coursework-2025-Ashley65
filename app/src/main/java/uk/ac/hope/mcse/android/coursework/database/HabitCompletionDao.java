package uk.ac.hope.mcse.android.coursework.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.Date;
import java.util.List;

import uk.ac.hope.mcse.android.coursework.models.HabitCompletion;

@Dao
public interface HabitCompletionDao {
    @Insert
    void insert(HabitCompletion habitCompletion);

    @Update
    void update(HabitCompletion habitCompletion);

    @Delete
    void delete(HabitCompletion habitCompletion);

    @Query("SELECT * FROM habit_completions WHERE habitId = :habitId ORDER BY completedDate DESC")
    List<HabitCompletion> getCompletionsForHabit(String habitId);

    @Query("SELECT * FROM habit_completions WHERE completedDate BETWEEN :startDate AND :endDate")
    List<HabitCompletion> getCompletionsBetweenDates(Date startDate, Date endDate);

    @Query("SELECT COUNT(*) FROM habit_completions WHERE habitId = :habitId")
    int getCompletionCountForHabit(String habitId);

    @Query("SELECT COUNT(*) FROM habit_completions WHERE habitId = :habitId AND completedDate BETWEEN :startDate AND :endDate")
    int getCompletionCountForHabitInRange(String habitId, Date startDate, Date endDate);
}