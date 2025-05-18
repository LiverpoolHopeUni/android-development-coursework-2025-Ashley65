package uk.ac.hope.mcse.android.coursework.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import uk.ac.hope.mcse.android.coursework.repositories.HabitRepository;

public class ReminderActionReceiver extends BroadcastReceiver {
    private static final String HABIT_ID_KEY = "habit_id";

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("COMPLETE_HABIT".equals(intent.getAction())) {
            String habitId = intent.getStringExtra(HABIT_ID_KEY);
            if (habitId != null) {
                // Complete the habit
                HabitRepository repository = new HabitRepository(
                        (android.app.Application) context.getApplicationContext());
                repository.completeHabit(habitId);

                // Cancel this notification
                int notificationId = habitId.hashCode();
                android.app.NotificationManager notificationManager =
                        (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(notificationId);

                // Show toast confirmation
                Toast.makeText(context, "Habit marked as completed", Toast.LENGTH_SHORT).show();
            }
        }
    }
}