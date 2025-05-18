package uk.ac.hope.mcse.android.coursework.models;

import android.icu.util.Calendar;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Date;
import java.util.UUID;

@Entity(tableName = "habits")
@TypeConverters({DateConverter.class})
public class Habit {
    @PrimaryKey
    @NonNull
    private String id;

    private String title;
    private String description;
    private int frequency; // times per week/day
    private boolean isDaily; // daily or weekly
    private Date createdDate;
    private Date lastCompletedDate;
    private int completedCount;
    private int currentStreak;

    // Add these fields to the Habit class
    private boolean reminderEnabled;
    private long reminderTime; // Store time in milliseconds

    // Add getters and setters
    public boolean isReminderEnabled() {
        return reminderEnabled;
    }

    public void setReminderEnabled(boolean reminderEnabled) {
        this.reminderEnabled = reminderEnabled;
    }

    public long getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(long reminderTime) {
        this.reminderTime = reminderTime;
    }

    // Constructor for creating a new habit
    public Habit(String title, String description, int frequency, boolean isDaily) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.description = description;
        this.frequency = frequency;
        this.isDaily = isDaily;
        this.createdDate = new Date();
        this.completedCount = 0;
        this.currentStreak = 0;
    }

    // Getters and Setters
    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public boolean isDaily() {
        return isDaily;
    }

    public void setDaily(boolean daily) {
        isDaily = daily;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastCompletedDate() {
        return lastCompletedDate;
    }

    public void setLastCompletedDate(Date lastCompletedDate) {
        this.lastCompletedDate = lastCompletedDate;
    }

    public int getCompletedCount() {
        return completedCount;
    }

    public void setCompletedCount(int completedCount) {
        this.completedCount = completedCount;
    }

    public int getCurrentStreak() {
        return currentStreak;
    }

    public void setCurrentStreak(int currentStreak) {
        this.currentStreak = currentStreak;
    }

    // Business logic methods
    public void incrementCompletedCount() {
        this.completedCount++;
        this.lastCompletedDate = new Date();

        // Update streak logic here - simplified version
        this.currentStreak++;
    }

    private void updateStreak() {
        if (isCompletedConsistently()) {
            currentStreak++;
        } else {
            currentStreak = 1; // Reset to 1 as we just completed it
        }
    }


    private boolean isCompletedConsistently() {
        if (lastCompletedDate == null || currentStreak == 0) {
            return true; // First completion or no previous streak
        }

        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(new Date());
        cal2.setTime(lastCompletedDate);

        if (isDaily) {
            // For daily habits, check if last completion was yesterday
            cal2.add(Calendar.DAY_OF_YEAR, 1);
            return isSameDay(cal1, cal2);
        } else {
            // For weekly habits, check if last completion was last week
            cal2.add(Calendar.WEEK_OF_YEAR, 1);
            return isSameWeek(cal1, cal2);
        }
    }

    private boolean isSameDay(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    private boolean isSameWeek(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR);
    }


    public double getCompletionPercentage() {
        // Calculate days or weeks since creation
        Calendar calCreated = Calendar.getInstance();
        Calendar calNow = Calendar.getInstance();
        calCreated.setTime(createdDate);
        calNow.setTime(new Date());

        int timePeriods = 0;
        if (isDaily) {
            // Calculate days between created date and now
            long diffMillis = calNow.getTimeInMillis() - calCreated.getTimeInMillis();
            timePeriods = (int) (diffMillis / (24 * 60 * 60 * 1000)) + 1; // Add 1 to include today
        } else {
            // Calculate weeks between created date and now
            int createdWeek = calCreated.get(Calendar.WEEK_OF_YEAR);
            int currentWeek = calNow.get(Calendar.WEEK_OF_YEAR);
            int createdYear = calCreated.get(Calendar.YEAR);
            int currentYear = calNow.get(Calendar.YEAR);

            timePeriods = (currentYear - createdYear) * 52 + (currentWeek - createdWeek) + 1; // Add 1 to include current week
        }

        int expectedCompletions = timePeriods * frequency;

        // Avoid division by zero
        if (expectedCompletions == 0) return 0;

        return (completedCount * 100.0) / expectedCompletions;
    }


    public boolean isOnTrack() {
        // Simple check: completion percentage >= 80%
        return getCompletionPercentage() >= 80.0;
    }


    public boolean isCompletedForToday() {
        if (lastCompletedDate == null) return false;

        Calendar calToday = Calendar.getInstance();
        Calendar calLastCompleted = Calendar.getInstance();
        calLastCompleted.setTime(lastCompletedDate);

        return isSameDay(calToday, calLastCompleted);
    }


}