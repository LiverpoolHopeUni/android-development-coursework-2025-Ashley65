package uk.ac.hope.mcse.android.coursework.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import uk.ac.hope.mcse.android.coursework.models.Habit;
import uk.ac.hope.mcse.android.coursework.repositories.HabitRepository;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            // Reschedule all reminders after device reboot
            HabitRepository repository = new HabitRepository(
                    (android.app.Application) context.getApplicationContext());

            repository.getAllHabits(habits -> {
                for (Habit habit : habits) {
                    if (habit.isReminderEnabled() && habit.getReminderTime() > 0) {
                        // Re-schedule the reminder
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