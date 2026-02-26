package com.prajwal.myfirstapp;

import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.AlarmManager;

/**
 * Task Manager Activity — Synchronized bidirectional task management.
 * 
 * Tasks are stored locally on the phone AND synced to the PC server.
 * Both devices get notified when tasks are added/completed/deleted.
 * 
 * Protocol (sent via ConnectionManager to PC):
 *   TASK_ADD:title:priority:due_date:due_time
 *   TASK_COMPLETE:id
 *   TASK_DELETE:id
 *   TASK_LIST          — request full list from PC
 *   TASK_SYNC          — request PC to push its list
 * 
 * Protocol (received from PC via ReverseCommandListener):
 *   TASKS_SYNC:{json}  — full task list from PC
 *   TASK_NOTIFY_ADDED:id:title
 *   TASK_NOTIFY_COMPLETED:id:title
 *   TASK_NOTIFY_DELETED:id
 *   TASK_ADDED:id:title — ack when PC confirms add
 */
public class TaskManagerActivity extends AppCompatActivity {

    private static final String TAG = "TaskManager";
    private static final String PREFS_NAME   = "task_manager_prefs";
    private static final String TASKS_KEY    = "tasks_json";
    private static final String CHANNEL_ID   = "task_notifications";
    // Key that stores the ISO timestamp of the most recently synced task
    private static final String LAST_SYNC_TS = "last_sync_ts";
    private static final String EPOCH_TS     = "1970-01-01T00:00:00";

    private ListView taskListView;
    private TaskAdapter taskAdapter;
    private final List<TaskItem> tasks = new ArrayList<>();
    private final Object tasksLock = new Object();

    private EditText etTaskTitle;
    private Spinner spinnerPriority;
    private Button btnDueDate, btnDueTime, btnAddTask, btnSyncTasks;
    private TextView tvTaskCount;

    private String selectedDueDate = null;  // YYYY-MM-DD
    private String selectedDueTime = null;  // HH:MM

    private ConnectionManager connectionManager;
    private String serverIp;

    // Singleton for receiving commands from ReverseCommandListener
    private static TaskManagerActivity instance;

    public static TaskManagerActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_manager);
        instance = this;

        // Get server IP from intent
        serverIp = getIntent().getStringExtra("server_ip");
        if (serverIp == null) {
            serverIp = getSharedPreferences("app_prefs", MODE_PRIVATE)
                    .getString("last_server_ip", null);
        }
        if (serverIp == null) {
            Toast.makeText(this, "No server IP configured", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        connectionManager = new ConnectionManager(serverIp);

        createNotificationChannel();
        initViews();
        loadTasks();
        scheduleAllPendingAlarms();
        refreshList();

        // Initialise the offline outbox so failed sends are queued automatically
        connectionManager.initOutbox(this);

        // Perform a delta handshake: only fetch tasks we are missing
        String lastSyncTs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .getString(LAST_SYNC_TS, EPOCH_TS);
        connectionManager.performHandshake(this, lastSyncTs);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (instance == this) instance = null;
    }

    private void initViews() {
        taskListView = findViewById(R.id.taskListView);
        etTaskTitle = findViewById(R.id.etTaskTitle);
        spinnerPriority = findViewById(R.id.spinnerPriority);
        btnDueDate = findViewById(R.id.btnDueDate);
        btnDueTime = findViewById(R.id.btnDueTime);
        btnAddTask = findViewById(R.id.btnAddTask);
        btnSyncTasks = findViewById(R.id.btnSyncTasks);
        tvTaskCount = findViewById(R.id.tvTaskCount);

        // Priority spinner
        ArrayAdapter<String> priorityAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"normal", "low", "high"});
        spinnerPriority.setAdapter(priorityAdapter);

        // Date picker
        btnDueDate.setOnClickListener(v -> showDatePicker());

        // Time picker
        btnDueTime.setOnClickListener(v -> showTimePicker());

        // Add task
        btnAddTask.setOnClickListener(v -> addTask());

        // Sync
        btnSyncTasks.setOnClickListener(v -> {
            String lastSyncTs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                    .getString(LAST_SYNC_TS, EPOCH_TS);
            connectionManager.performHandshake(this, lastSyncTs);
            Toast.makeText(this, "Syncing tasks...", Toast.LENGTH_SHORT).show();
        });

        // Clear completed via long press on sync button
        btnSyncTasks.setOnLongClickListener(v -> {
            clearCompletedTasks();
            return true;
        });

        taskAdapter = new TaskAdapter(this, tasks);
        taskListView.setAdapter(taskAdapter);
    }

    // ─── DATE / TIME PICKERS ───────────────────────────────────

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, day) -> {
            selectedDueDate = String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, day);
            btnDueDate.setText("📅 " + selectedDueDate);
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showTimePicker() {
        Calendar cal = Calendar.getInstance();
        new TimePickerDialog(this, (view, hour, minute) -> {
            selectedDueTime = String.format(Locale.US, "%02d:%02d", hour, minute);
            btnDueTime.setText("⏰ " + selectedDueTime);
        }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show();
    }

    // ─── TASK OPERATIONS ───────────────────────────────────────

    private void addTask() {
        String title = etTaskTitle.getText().toString().trim();
        if (title.isEmpty()) {
            Toast.makeText(this, "Enter a task title", Toast.LENGTH_SHORT).show();
            return;
        }

        String priority = spinnerPriority.getSelectedItem().toString();

        synchronized (tasksLock) {
            TaskItem task = new TaskItem();
            task.id = System.currentTimeMillis();
            task.title = title;
            task.priority = priority;
            task.dueDate = selectedDueDate;
            task.dueTime = selectedDueTime;
            task.completed = false;
            task.source = "mobile";
            task.createdAt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).format(new Date());
            task.lastModified = task.createdAt;

            tasks.add(task);
            saveTasks();
        }
        refreshList();

        // Send to PC via reliable data channel (queued if offline)
        StringBuilder cmd = new StringBuilder("TASK_ADD:" + title + ":" + priority);
        if (selectedDueDate != null) {
            cmd.append(":").append(selectedDueDate);
            if (selectedDueTime != null) {
                cmd.append(":").append(selectedDueTime);
            }
        }
        connectionManager.sendDataCommand(this, cmd.toString());

        // Schedule local alarm for the new task
        synchronized (tasksLock) {
            scheduleTaskReminder(tasks.get(tasks.size() - 1));
        }

        // Clear inputs
        etTaskTitle.setText("");
        selectedDueDate = null;
        selectedDueTime = null;
        btnDueDate.setText("📅 Date");
        btnDueTime.setText("⏰ Time");

        Toast.makeText(this, "Task added & synced!", Toast.LENGTH_SHORT).show();
    }

    private void completeTask(TaskItem task) {
        synchronized (tasksLock) {
            task.completed = true;
            task.lastModified = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).format(new Date());
            saveTasks();
        }
        cancelTaskReminder(task.id);
        refreshList();

        // Notify PC via reliable data channel
        connectionManager.sendDataCommand(this, "TASK_COMPLETE:" + task.id);
    }

    private void deleteTask(TaskItem task) {
        synchronized (tasksLock) {
            tasks.remove(task);
            saveTasks();
        }
        cancelTaskReminder(task.id);
        refreshList();

        // Notify PC via reliable data channel
        connectionManager.sendDataCommand(this, "TASK_DELETE:" + task.id);
    }

    private void clearCompletedTasks() {
        int removed = 0;
        synchronized (tasksLock) {
            for (int i = tasks.size() - 1; i >= 0; i--) {
                if (tasks.get(i).completed) {
                    tasks.remove(i);
                    removed++;
                }
            }
            saveTasks();
        }
        refreshList();
        Toast.makeText(this, "Cleared " + removed + " completed tasks", Toast.LENGTH_SHORT).show();
    }

    // ─── SYNC FROM PC ──────────────────────────────────────────

    /**
     * Called from ReverseCommandListener when PC sends TASKS_SYNC:{json}
     */
    public void onTasksSyncReceived(String tasksJson) {
        try {
            JSONArray jsonArray = new JSONArray(tasksJson);
            String latestTs = EPOCH_TS;

            synchronized (tasksLock) {
                tasks.clear();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    TaskItem task = new TaskItem();
                    task.id = obj.optLong("id", System.currentTimeMillis());
                    task.title = obj.optString("title", "Untitled");
                    task.completed = obj.optBoolean("completed", false);
                    task.priority = obj.optString("priority", "normal");
                    task.dueDate = obj.optString("due_date", null);
                    task.dueTime = obj.optString("due_time", null);
                    task.source = obj.optString("source", "pc");
                    task.createdAt = obj.optString("created_at", "");
                    task.lastModified = obj.optString("last_modified", task.createdAt);
                    if ("null".equals(task.dueDate)) task.dueDate = null;
                    if ("null".equals(task.dueTime)) task.dueTime = null;
                    tasks.add(task);

                    if (task.lastModified != null && task.lastModified.compareTo(latestTs) > 0) {
                        latestTs = task.lastModified;
                    }
                }

                saveTasks();
            }
            runOnUiThread(this::refreshList);
            runOnUiThread(() -> Toast.makeText(this, "Tasks synced from PC!", Toast.LENGTH_SHORT).show());
            Log.i(TAG, "Synced " + jsonArray.length() + " tasks from PC");

            // Schedule alarms for all synced tasks with due dates
            synchronized (tasksLock) {
                for (TaskItem task : tasks) {
                    scheduleTaskReminder(task);
                }
            }

            updateLastSyncTimestamp(latestTs);

        } catch (JSONException e) {
            Log.e(TAG, "Sync parse error: " + e.getMessage());
        }
    }

    /**
     * Called from ReverseCommandListener when PC sends SYNC_DELTA:{json}
     * 
     * Delta contains only tasks that were modified since our last sync.
     * We merge them with our local data using last-write-wins conflict resolution.
     */
    public void onSyncDeltaReceived(String deltaJson) {
        try {
            JSONObject delta = new JSONObject(deltaJson);
            JSONArray deltaTasks = delta.optJSONArray("tasks");

            if (deltaTasks == null || deltaTasks.length() == 0) {
                Log.i(TAG, "No task changes in delta");
                return;
            }

            String latestTs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                    .getString(LAST_SYNC_TS, EPOCH_TS);

            synchronized (tasksLock) {
                for (int i = 0; i < deltaTasks.length(); i++) {
                    JSONObject obj = deltaTasks.getJSONObject(i);
                    TaskItem serverTask = new TaskItem();
                    serverTask.id = obj.optLong("id", System.currentTimeMillis());
                    serverTask.title = obj.optString("title", "Untitled");
                    serverTask.completed = obj.optBoolean("completed", false);
                    serverTask.priority = obj.optString("priority", "normal");
                    serverTask.dueDate = obj.optString("due_date", null);
                    serverTask.dueTime = obj.optString("due_time", null);
                    serverTask.source = obj.optString("source", "pc");
                    serverTask.createdAt = obj.optString("created_at", "");
                    serverTask.lastModified = obj.optString("last_modified", serverTask.createdAt);
                    if ("null".equals(serverTask.dueDate)) serverTask.dueDate = null;
                    if ("null".equals(serverTask.dueTime)) serverTask.dueTime = null;

                    boolean found = false;
                    for (int j = 0; j < tasks.size(); j++) {
                        TaskItem localTask = tasks.get(j);
                        if (localTask.id == serverTask.id) {
                            found = true;
                            if (serverTask.lastModified.compareTo(localTask.lastModified) > 0) {
                                tasks.set(j, serverTask);
                                Log.i(TAG, "Updated task from server: " + serverTask.title);
                            } else {
                                Log.i(TAG, "Kept local version (newer): " + localTask.title);
                            }
                            break;
                        }
                    }

                    if (!found) {
                        tasks.add(serverTask);
                        Log.i(TAG, "Added new task from server: " + serverTask.title);
                    }

                    if (serverTask.lastModified != null && serverTask.lastModified.compareTo(latestTs) > 0) {
                        latestTs = serverTask.lastModified;
                    }
                }

                saveTasks();
            }

            final int count = deltaTasks.length();
            runOnUiThread(this::refreshList);
            runOnUiThread(() -> Toast.makeText(this, "Synced " + count +
                    " change(s) from PC", Toast.LENGTH_SHORT).show());

            // Schedule alarms for any delta tasks that have due dates
            synchronized (tasksLock) {
                for (TaskItem task : tasks) {
                    if (task.completed) {
                        cancelTaskReminder(task.id);
                    } else {
                        scheduleTaskReminder(task);
                    }
                }
            }

            updateLastSyncTimestamp(latestTs);

        } catch (JSONException e) {
            Log.e(TAG, "Delta parse error: " + e.getMessage());
        }
    }

    /**
     * Called when PC notifies a new task was added from PC side
     */
    public void onTaskNotifyAdded(String taskId, String title) {
        runOnUiThread(() -> {
            showLocalNotification("New Task Added", title + " (from PC)");
            Toast.makeText(this, "📋 New task: " + title, Toast.LENGTH_SHORT).show();
            // Request full sync to get the complete task data
            connectionManager.sendCommand("TASK_SYNC");
        });
    }

    /**
     * Called when PC notifies a task was completed
     */
    public void onTaskNotifyCompleted(String taskId, String title) {
        runOnUiThread(() -> {
            showLocalNotification("Task Completed", "✅ " + title);
            Toast.makeText(this, "✅ Completed: " + title, Toast.LENGTH_SHORT).show();
            connectionManager.sendCommand("TASK_SYNC");
        });
    }

    /**
     * Called when PC notifies a task was deleted
     */
    public void onTaskNotifyDeleted(String taskId) {
        runOnUiThread(() -> {
            showLocalNotification("Task Deleted", "A task was removed from PC");
            connectionManager.sendCommand("TASK_SYNC");
        });
    }

    /**
     * Update the last sync timestamp in SharedPreferences.
     * This is used for delta handshakes to only fetch changes since this time.
     */
    private void updateLastSyncTimestamp(String timestamp) {
        if (timestamp != null && !timestamp.equals(EPOCH_TS)) {
            getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                    .edit()
                    .putString(LAST_SYNC_TS, timestamp)
                    .apply();
            Log.i(TAG, "Updated last sync timestamp: " + timestamp);
        }
    }

    // ─── LOCAL NOTIFICATIONS ───────────────────────────────────

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Task Notifications", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Notifications for task sync");
            NotificationManager nm = getSystemService(NotificationManager.class);
            if (nm != null) nm.createNotificationChannel(channel);
        }
    }

    private void showLocalNotification(String title, String body) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null) {
            nm.notify((int) System.currentTimeMillis(), builder.build());
        }
    }

    // ─── PERSISTENCE ───────────────────────────────────────────

    private void saveTasks() {
        try {
            JSONArray jsonArray = new JSONArray();
            for (TaskItem task : tasks) {
                JSONObject obj = new JSONObject();
                obj.put("id", task.id);
                obj.put("title", task.title);
                obj.put("completed", task.completed);
                obj.put("priority", task.priority);
                obj.put("due_date", task.dueDate);
                obj.put("due_time", task.dueTime);
                obj.put("source", task.source);
                obj.put("created_at", task.createdAt);
                obj.put("last_modified", task.lastModified);
                jsonArray.put(obj);
            }

            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            prefs.edit().putString(TASKS_KEY, jsonArray.toString()).apply();

        } catch (JSONException e) {
            Log.e(TAG, "Save error: " + e.getMessage());
        }
    }

    private void loadTasks() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String json = prefs.getString(TASKS_KEY, "[]");

        try {
            JSONArray jsonArray = new JSONArray(json);
            tasks.clear();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                TaskItem task = new TaskItem();
                task.id = obj.optLong("id", 0);
                task.title = obj.optString("title", "");
                task.completed = obj.optBoolean("completed", false);
                task.priority = obj.optString("priority", "normal");
                task.dueDate = obj.optString("due_date", null);
                task.dueTime = obj.optString("due_time", null);
                task.source = obj.optString("source", "mobile");
                task.createdAt = obj.optString("created_at", "");
                task.lastModified = obj.optString("last_modified", task.createdAt);
                if ("null".equals(task.dueDate)) task.dueDate = null;
                if ("null".equals(task.dueTime)) task.dueTime = null;
                tasks.add(task);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Load error: " + e.getMessage());
        }
    }

    /**
     * Re-schedule alarms for all incomplete tasks with due dates on app open.
     * Ensures alarms are always active even if the app was force-stopped.
     */
    private void scheduleAllPendingAlarms() {
        synchronized (tasksLock) {
            for (TaskItem task : tasks) {
                if (!task.completed) {
                    scheduleTaskReminder(task);
                }
            }
        }
    }

    // ─── REFRESH LIST ──────────────────────────────────────────

    private void refreshList() {
        synchronized (tasksLock) {
            tasks.sort((a, b) -> {
                if (a.completed != b.completed) return a.completed ? 1 : -1;
                int pa = priorityValue(a.priority);
                int pb = priorityValue(b.priority);
                if (pa != pb) return pa - pb;
                return a.createdAt.compareTo(b.createdAt);
            });
        }

        taskAdapter.notifyDataSetChanged();

        long pending = 0;
        synchronized (tasksLock) {
            for (TaskItem t : tasks) if (!t.completed) pending++;
        }
        tvTaskCount.setText(pending + " pending / " + tasks.size() + " total");
    }

    private int priorityValue(String priority) {
        switch (priority) {
            case "high": return 0;
            case "normal": return 1;
            case "low": return 2;
            default: return 1;
        }
    }

    // ─── TASK DATA MODEL ───────────────────────────────────────

    static class TaskItem {
        long id;
        String title;
        boolean completed;
        String priority;    // "low", "normal", "high"
        String dueDate;     // "YYYY-MM-DD" or null
        String dueTime;     // "HH:MM" or null
        String source;      // "pc" or "mobile"
        String createdAt;
        String lastModified; // ISO-8601 timestamp for conflict resolution
    }

    // ─── ALARM SCHEDULING ──────────────────────────────────────

    /**
     * Schedule an AlarmManager alarm for a task if it has a future due_date + due_time.
     */
    private void scheduleTaskReminder(TaskItem task) {
        if (task.dueDate == null || task.dueTime == null || task.completed) return;

        Calendar cal = TaskAlarmHelper.parseDateTime(task.dueDate, task.dueTime);
        if (cal == null) return;

        // Only schedule if the due time is in the future
        if (cal.getTimeInMillis() <= System.currentTimeMillis()) return;

        TaskAlarmHelper.scheduleAlarm(this, task.id, task.title, task.dueDate, task.dueTime, cal);
    }

    /**
     * Cancel the alarm for a task (when completed or deleted).
     */
    private void cancelTaskReminder(long taskId) {
        TaskAlarmHelper.cancelAlarm(this, taskId);
    }

    // ─── LIST ADAPTER ──────────────────────────────────────────

    class TaskAdapter extends ArrayAdapter<TaskItem> {
        public TaskAdapter(Context context, List<TaskItem> tasks) {
            super(context, 0, tasks);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.item_task, parent, false);
            }

            TaskItem task = getItem(position);
            if (task == null) return convertView;

            CheckBox cbDone = convertView.findViewById(R.id.cbTaskDone);
            TextView tvTitle = convertView.findViewById(R.id.tvTaskTitle);
            TextView tvDue = convertView.findViewById(R.id.tvTaskDue);
            TextView tvPriority = convertView.findViewById(R.id.tvTaskPriority);
            TextView tvSource = convertView.findViewById(R.id.tvTaskSource);
            ImageButton btnDelete = convertView.findViewById(R.id.btnDeleteTask);

            // Title
            tvTitle.setText(task.title);
            if (task.completed) {
                tvTitle.setPaintFlags(tvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                tvTitle.setTextColor(Color.GRAY);
            } else {
                tvTitle.setPaintFlags(tvTitle.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                tvTitle.setTextColor(Color.WHITE);
            }

            // Checkbox
            cbDone.setOnCheckedChangeListener(null);
            cbDone.setChecked(task.completed);
            cbDone.setOnCheckedChangeListener((btn, checked) -> {
                if (checked) completeTask(task);
            });

            // Due date/time
            StringBuilder due = new StringBuilder();
            if (task.dueDate != null) due.append("📅 ").append(task.dueDate);
            if (task.dueTime != null) {
                if (due.length() > 0) due.append("  ");
                due.append("⏰ ").append(task.dueTime);
            }
            if (due.length() > 0) {
                tvDue.setText(due.toString());
                tvDue.setVisibility(View.VISIBLE);
            } else {
                tvDue.setVisibility(View.GONE);
            }

            // Priority
            switch (task.priority) {
                case "high":
                    tvPriority.setText("⚡ HIGH");
                    tvPriority.setTextColor(Color.parseColor("#FF1744"));
                    break;
                case "low":
                    tvPriority.setText("↓ Low");
                    tvPriority.setTextColor(Color.GRAY);
                    break;
                default:
                    tvPriority.setText("");
                    break;
            }

            // Source
            tvSource.setText(task.source.equals("mobile") ? "📱" : "💻");

            // Delete
            btnDelete.setOnClickListener(v -> {
                new AlertDialog.Builder(TaskManagerActivity.this)
                        .setTitle("Delete Task")
                        .setMessage("Delete \"" + task.title + "\"?")
                        .setPositiveButton("Delete", (d, w) -> deleteTask(task))
                        .setNegativeButton("Cancel", null)
                        .show();
            });

            return convertView;
        }
    }
}
