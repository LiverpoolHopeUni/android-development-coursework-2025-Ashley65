package uk.ac.hope.mcse.android.coursework.models;

import java.util.Date;
import java.util.UUID;

public class Habit {
    private String id;
    private String title;
    private String description;
    private int frequency; // times per week/day
    private boolean isDaily; // daily or weekly
    private Date createdDate;
    private Date lastCompletedDate;
    private int completedCount;

    public Habit(String title, String description, int frequency, boolean isDaily) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.description = description;
        this.frequency = frequency;
        this.isDaily = isDaily;
        this.createdDate = new Date();
        this.completedCount = 0;
    }

    // Getters and setters
    public String getId() { return id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getFrequency() { return frequency; }
    public void setFrequency(int frequency) { this.frequency = frequency; }

    public boolean isDaily() { return isDaily; }
    public void setDaily(boolean daily) { isDaily = daily; }

    public Date getCreatedDate() { return createdDate; }

    public Date getLastCompletedDate() { return lastCompletedDate; }
    public void setLastCompletedDate(Date lastCompletedDate) { this.lastCompletedDate = lastCompletedDate; }

    public int getCompletedCount() { return completedCount; }
    public void setCompletedCount(int completedCount) { this.completedCount = completedCount; }

    public void incrementCompletedCount() {
        this.completedCount++;
        this.lastCompletedDate = new Date();
    }
}