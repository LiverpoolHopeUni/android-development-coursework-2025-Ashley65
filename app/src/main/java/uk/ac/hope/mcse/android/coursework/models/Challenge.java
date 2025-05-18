package uk.ac.hope.mcse.android.coursework.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import uk.ac.hope.mcse.android.coursework.utils.Converters;

@Entity(tableName = "challenges")
@TypeConverters(Converters.class)
public class Challenge {
    @PrimaryKey
    @NonNull
    private String id;
    private String name;
    private String description;
    private List<String> habitIds;
    private Date startDate;
    private Date endDate;
    private boolean isCompleted;
    private int requiredDaysToComplete;

    public Challenge(String name, String description, List<String> habitIds, Date startDate, Date endDate, int requiredDaysToComplete) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.description = description;
        this.habitIds = habitIds != null ? habitIds : new ArrayList<>();
        this.startDate = startDate;
        this.endDate = endDate;
        this.isCompleted = false;
        this.requiredDaysToComplete = requiredDaysToComplete;
    }

    // Getters and setters
    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getHabitIds() { return habitIds; }
    public void setHabitIds(List<String> habitIds) { this.habitIds = habitIds; }

    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }

    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }

    public int getRequiredDaysToComplete() { return requiredDaysToComplete; }
    public void setRequiredDaysToComplete(int requiredDaysToComplete) { this.requiredDaysToComplete = requiredDaysToComplete; }
}