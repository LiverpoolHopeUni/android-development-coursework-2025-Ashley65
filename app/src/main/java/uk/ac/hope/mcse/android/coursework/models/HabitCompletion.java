package uk.ac.hope.mcse.android.coursework.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Date;
import java.util.UUID;

import uk.ac.hope.mcse.android.coursework.utils.Converters;

@Entity(tableName = "habit_completions",
        foreignKeys = @ForeignKey(entity = Habit.class,
                parentColumns = "id",
                childColumns = "habitId",
                onDelete = ForeignKey.CASCADE))
@TypeConverters(Converters.class)
public class HabitCompletion {
    @PrimaryKey
    @NonNull
    private String id;

    @NonNull
    private String habitId;

    private Date completedDate;
    private String notes;

    public HabitCompletion(@NonNull String habitId, Date completedDate, String notes) {
        this.id = UUID.randomUUID().toString();
        this.habitId = habitId;
        this.completedDate = completedDate;
        this.notes = notes;
    }

    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }

    @NonNull
    public String getHabitId() { return habitId; }
    public void setHabitId(@NonNull String habitId) { this.habitId = habitId; }

    public Date getCompletedDate() { return completedDate; }
    public void setCompletedDate(Date completedDate) { this.completedDate = completedDate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}