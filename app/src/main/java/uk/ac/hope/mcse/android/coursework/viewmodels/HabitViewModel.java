package uk.ac.hope.mcse.android.coursework.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import uk.ac.hope.mcse.android.coursework.database.AppDatabase;
import uk.ac.hope.mcse.android.coursework.models.HabitCompletion;

public class HabitViewModel extends ViewModel {
    private final AppDatabase database;
    private final Executor executor;

    public HabitViewModel(AppDatabase database) {
        this.database = database;
        this.executor = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<HabitCompletion>> getHabitCompletions(String habitId) {
        MutableLiveData<List<HabitCompletion>> completionsLiveData = new MutableLiveData<>();

        executor.execute(() -> {
            List<HabitCompletion> completions = database.habitCompletionDao().getCompletionsForHabit(habitId);
            completionsLiveData.postValue(completions);
        });

        return completionsLiveData;
    }

    public void markHabitCompleted(String habitId, String notes) {
        executor.execute(() -> {
            HabitCompletion completion = new HabitCompletion(habitId, new Date(), notes);
            database.habitCompletionDao().insert(completion);
        });
    }
}