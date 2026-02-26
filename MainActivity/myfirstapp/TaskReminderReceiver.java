package com.prajwal.myfirstapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import androidx.core.app.NotificationCompat;

/**
 * BroadcastReceiver that fires when an AlarmManager alarm triggers
 * for a task that has a due_date and due_time.
 *
 * Shows a high-priority notification and vibrates the device.
 */
public class TaskReminderReceiver extends BroadcastReceiver {

    private static final String TAG = "TaskReminder";
    private static final String CHANNEL_ID = "task_reminders";

    public static final String EXTRA_TASK_ID = "task_id";
    public static final String EXTRA_TASK_TITLE = "task_title";
    public static final String EXTRA_TASK_DUE_TIME = "task_due_time";

    @Override
    public void onReceive(Context context, Intent intent) {
        long taskId = intent.getLongExtra(EXTRA_TASK_ID, -1);
        String title = intent.getStringExtra(EXTRA_TASK_TITLE);
        String dueTime = intent.getStringExtra(EXTRA_TASK_DUE_TIME);

        if (title == null || title.isEmpty()) {
            title = "Task Due";
        }

        Log.i(TAG, "Reminder fired for task: " + title + " (id=" + taskId + ")");

        createNotificationChannel(context);

        String body = "Due now";
        if (dueTime != null && !dueTime.isEmpty()) {
            body = "Due at " + dueTime;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle("🔔 Task Reminder")
                .setContentText(title + " — " + body)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true)
                .setCategory(NotificationCompat.CATEGORY_REMINDER);

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null) {
            // Use task id as notification id so each task gets its own notification
            nm.notify((int) (taskId % Integer.MAX_VALUE), builder.build());
        }

        // Vibrate
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(500);
            }
        }
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Task Reminders",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Reminders for tasks with due dates");
            channel.enableVibration(true);
            NotificationManager nm = context.getSystemService(NotificationManager.class);
            if (nm != null) nm.createNotificationChannel(channel);
        }
    }
}
