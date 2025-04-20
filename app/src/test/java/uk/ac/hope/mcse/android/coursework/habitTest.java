package uk.ac.hope.mcse.android.coursework;

import org.junit.Test;
import static org.junit.Assert.*;
import uk.ac.hope.mcse.android.coursework.models.Habit;

public class habitTest {
    @Test
    public void createHabit_isCorrect() {
        Habit habit = new Habit("Drink Water", "Drink 8 glasses of water", 8, true);

        assertEquals("Drink Water", habit.getTitle());
        assertEquals("Drink 8 glasses of water", habit.getDescription());
        assertEquals(8, habit.getFrequency());
        assertTrue(habit.isDaily());
        assertNotNull(habit.getId());
        assertNotNull(habit.getCreatedDate());
        assertEquals(0, habit.getCompletedCount());
        assertNull(habit.getLastCompletedDate());
    }

    @Test
    public void incrementCompletedCount_isCorrect() {
        Habit habit = new Habit("Exercise", "30 minutes workout", 5, true);

        assertEquals(0, habit.getCompletedCount());
        assertNull(habit.getLastCompletedDate());

        habit.incrementCompletedCount();

        assertEquals(1, habit.getCompletedCount());
        assertNotNull(habit.getLastCompletedDate());
    }
}