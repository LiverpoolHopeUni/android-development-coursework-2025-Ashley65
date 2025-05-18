package uk.ac.hope.mcse.android.coursework.utils;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import uk.ac.hope.mcse.android.coursework.models.Habit;
import uk.ac.hope.mcse.android.coursework.repositories.HabitRepository;

public class AlarmPermissionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED.equals(intent.getAction())) {
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

                if (alarmManager != null && alarmManager.canScheduleExactAlarms()) {
                    // Permission granted, reschedule all pending reminders
                    HabitRepository repository = new HabitRepository(
                            (android.app.Application) context.getApplicationContext());

                    repository.getAllHabits(habits -> {
                        for (Habit habit : habits) {
                            if (habit.isReminderEnabled() && habit.getReminderTime() > 0) {
                                // Re-schedule the reminder using exact alarms
                                ReminderReceiver.scheduleReminder(
                                        context,
                                        habit,
                                        habit.getReminderTime()
                                );
                            }
                        }
                    });
                }
            }
        }
    }
}