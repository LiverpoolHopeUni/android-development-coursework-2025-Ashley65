package uk.ac.hope.mcse.android.coursework;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import uk.ac.hope.mcse.android.coursework.models.Habit;
import uk.ac.hope.mcse.android.coursework.repositories.HabitRepository;

public class HabitRepositoryUnitTest {

    private HabitRepository repository;

    @Before
    public void setup() {
        repository = new HabitRepository();
    }

    @Test
    public void addAndGetHabit_isCorrect() {
        Habit habit = new Habit("Read", "Read for 30 minutes", 1, true);
        repository.addHabit(habit);

        Habit retrievedHabit = repository.getHabit(habit.getId());

        assertNotNull(retrievedHabit);
        assertEquals(habit.getTitle(), retrievedHabit.getTitle());
    }

    @Test
    public void getAllHabits_isCorrect() {
        Habit habit1 = new Habit("Read", "Read for 30 minutes", 1, true);
        Habit habit2 = new Habit("Meditate", "Meditate for 10 minutes", 1, true);

        repository.addHabit(habit1);
        repository.addHabit(habit2);

        assertEquals(2, repository.getAllHabits().size());
    }

    @Test
    public void completeHabit_isCorrect() {
        Habit habit = new Habit("Exercise", "30 minutes workout", 5, true);
        repository.addHabit(habit);

        repository.completeHabit(habit.getId());

        Habit updatedHabit = repository.getHabit(habit.getId());
        assertEquals(1, updatedHabit.getCompletedCount());
    }
}