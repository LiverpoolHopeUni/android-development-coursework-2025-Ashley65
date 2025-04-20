package uk.ac.hope.mcse.android.coursework.repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import uk.ac.hope.mcse.android.coursework.models.Habit;


public class HabitRepository {
    private Map<String, Habit> habits = new HashMap<>();

    public void addHabit(Habit habit) {
        habits.put(habit.getId(), habit);
    }

    public Habit getHabit(String id) {
        return habits.get(id);
    }
    public List<Habit> getAllHabits() {
        return new ArrayList<>(habits.values());
    }
    public void updateHabit(Habit habit){
        if (habits.containsKey(habit.getId())) {
            habits.put(habit.getId(), habit);
        }
    }
    public void deleteHabit(String id) {
        habits.remove(id);
    }
    public void completeHabit(String id){
        Habit habit = habits.get(id);
        if (habit != null) {
            habit.incrementCompletedCount();
            habits.put(id, habit);
        }
    }
}
