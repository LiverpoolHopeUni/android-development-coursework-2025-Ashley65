package uk.ac.hope.mcse.android.coursework.repositories;

import android.app.Application;
import androidx.lifecycle.LiveData;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import uk.ac.hope.mcse.android.coursework.database.HabitDao;
import uk.ac.hope.mcse.android.coursework.database.HabitDatabase;
import uk.ac.hope.mcse.android.coursework.models.Habit;

public class HabitRepository {
    private final HabitDao habitDao;
    private final LiveData<List<Habit>> allHabits;
    private final ExecutorService executorService;

    public HabitRepository(Application application) {
        HabitDatabase database = HabitDatabase.getDatabase(application);
        habitDao = database.habitDao();
        allHabits = habitDao.getAllHabits();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<Habit>> getAllHabits() {
        return allHabits;
    }

    public LiveData<List<Habit>> getDailyHabits() {
        return habitDao.getDailyHabits();
    }

    public LiveData<List<Habit>> getWeeklyHabits() {
        return habitDao.getWeeklyHabits();
    }

    public void getHabit(String id, final HabitCallback callback) {
        executorService.execute(() -> {
            Habit habit = habitDao.getHabitById(id);
            callback.onHabitLoaded(habit);
        });
    }

    public void addHabit(Habit habit) {
        executorService.execute(() -> habitDao.insert(habit));
    }

    public void updateHabit(Habit habit) {
        executorService.execute(() -> habitDao.update(habit));
    }

    public void deleteHabit(String id) {
        executorService.execute(() -> habitDao.deleteById(id));
    }

    public void completeHabit(String id) {
        executorService.execute(() -> {
            Habit habit = habitDao.getHabitById(id);
            if (habit != null) {
                habit.incrementCompletedCount();
                habitDao.update(habit);
            }
        });
    }

    public interface HabitCallback {
        void onHabitLoaded(Habit habit);
    }
}
