package uk.ac.hope.mcse.android.coursework.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;

import uk.ac.hope.mcse.android.coursework.MainActivity;
import uk.ac.hope.mcse.android.coursework.R;
import uk.ac.hope.mcse.android.coursework.models.Habit;

public class ReminderReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "habit_reminder_channel";
    private static final String HABIT_ID_KEY = "habit_id";
    private static final String HABIT_TITLE_KEY = "habit_title";

    @Override
    public void onReceive(Context context, Intent intent) {
        String habitId = intent.getStringExtra(HABIT_ID_KEY);
        String habitTitle = intent.getStringExtra(HABIT_TITLE_KEY);

        createNotificationChannel(context);
        showNotification(context, habitId, habitTitle);
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Habit Reminders";
            String description = "Notifications for habit reminders";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void showNotification(Context context, String habitId, String habitTitle) {
        // Create intent to open app when notification is tapped
        Intent openAppIntent = new Intent(context, MainActivity.class);
        openAppIntent.putExtra(HABIT_ID_KEY, habitId);
        openAppIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, openAppIntent,
                PendingIntent.FLAG_IMMUTABLE);

        // Create complete action intent
        Intent completeIntent = new Intent(context, ReminderActionReceiver.class);
        completeIntent.putExtra(HABIT_ID_KEY, habitId);
        completeIntent.setAction("COMPLETE_HABIT");
        PendingIntent completePendingIntent = PendingIntent.getBroadcast(context,
                habitId.hashCode(), completeIntent, PendingIntent.FLAG_IMMUTABLE);

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Habit Reminder")
                .setContentText("Time to complete your habit: " + habitTitle)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .addAction(android.R.drawable.ic_menu_send, "Complete", completePendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(habitId.hashCode(), builder.build());
    }

    public static void scheduleReminder(Context context, Habit habit, long reminderTimeMillis) {
        Intent intent = new Intent(context, ReminderReceiver.class);
        intent.putExtra(HABIT_ID_KEY, habit.getId());
        intent.putExtra(HABIT_TITLE_KEY, habit.getTitle());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                habit.getId().hashCode(), intent, PendingIntent.FLAG_IMMUTABLE);

        ReminderManager.scheduleAlarm(context, pendingIntent, reminderTimeMillis, habit.isDaily());
    }

    public static void cancelReminder(Context context, String habitId) {
        Intent intent = new Intent(context, ReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                habitId.hashCode(), intent, PendingIntent.FLAG_IMMUTABLE);

        ReminderManager.cancelAlarm(context, pendingIntent);
    }
}