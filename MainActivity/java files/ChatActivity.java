package com.prajwal.myfirstapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";
    private static final int PICK_FILE_REQUEST = 101;

    private RecyclerView chatList;
    private EditText chatInput;
    private ChatAdapter adapter;
    private ArrayList<ChatMessage> messages;
    private ChatRepository repository;
    private ConnectionManager connectionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_chat);

        String ip = getIntent().getStringExtra("server_ip");
        if (ip == null) {
            ip = getSharedPreferences("app_prefs", MODE_PRIVATE)
                    .getString("last_server_ip", null);
        }
        if (ip == null) {
            Toast.makeText(this, "No server IP configured", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        connectionManager = new ConnectionManager(ip);
        connectionManager.initOutbox(this);

        repository = new ChatRepository(this);
        messages = repository.loadMessages();

        chatList = findViewById(R.id.chat_messages_list);
        chatInput = findViewById(R.id.chat_input);

        adapter = new ChatAdapter(this, messages);
        chatList.setLayoutManager(new LinearLayoutManager(this));
        chatList.setAdapter(adapter);
        if (messages.size() > 0) chatList.scrollToPosition(messages.size() - 1);

        // Send Text — uses reliable delivery (queues if offline)
        findViewById(R.id.send_button).setOnClickListener(v -> {
            String text = chatInput.getText().toString().trim();
            if (!text.isEmpty()) {
                ChatMessage msg = new ChatMessage(text, "text", true);
                connectionManager.sendDataCommand(this, "CHAT_MSG:" + msg.id + ":" + text);
                addMessage(msg);
                chatInput.setText("");
            }
        });

        // Attach File
        findViewById(R.id.attach_button).setOnClickListener(v -> openFilePicker());

        // Request sync of missed messages from PC
        requestChatSync();
    }

    /** Ask the PC server for any messages we missed since our last sync. */
    private void requestChatSync() {
        long lastSync = repository.getLastSyncTimestamp();
        connectionManager.sendCommand("CHAT_SYNC:" + lastSync);
        Log.i(TAG, "Requested chat sync since: " + lastSync);
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_FILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri fileUri = data.getData();
            if (fileUri != null) {
                uploadFile(fileUri);
            }
        }
    }

    private void uploadFile(Uri uri) {
        Toast.makeText(this, "Sending file...", Toast.LENGTH_SHORT).show();

        connectionManager.sendFileToLaptop(this, uri,
                () -> {},
                () -> {
                    runOnUiThread(() -> {
                        addMessage(new ChatMessage(uri.toString(), "file", true));
                        Toast.makeText(this, "File Sent!", Toast.LENGTH_SHORT).show();
                        connectionManager.sendCommand("CHAT_FILE:" + getFileName(uri));
                    });
                },
                () -> {
                    runOnUiThread(() -> Toast.makeText(this, "Failed to send file", Toast.LENGTH_SHORT).show());
                }
        );
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (android.database.Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME);
                    if (index >= 0) result = cursor.getString(index);
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) result = result.substring(cut + 1);
        }
        return result;
    }

    private void addMessage(ChatMessage msg) {
        messages.add(msg);
        adapter.notifyItemInserted(messages.size() - 1);
        chatList.scrollToPosition(messages.size() - 1);
        repository.saveMessages(messages);
        repository.setLastSyncTimestamp(msg.timestamp);
    }

    /** Handle incoming sync: merge server messages with local, deduplicate. */
    private void handleChatSync(String jsonStr) {
        try {
            JSONArray array = new JSONArray(jsonStr);
            ArrayList<ChatMessage> incoming = new ArrayList<>();

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                ChatMessage msg = new ChatMessage(
                        obj.optString("content", ""),
                        obj.optString("type", "text"),
                        obj.optString("sender", "pc").equals("phone")
                );
                msg.id = obj.optString("id", msg.id);
                msg.timestamp = obj.optLong("timestamp", System.currentTimeMillis());
                msg.metadata = obj.optString("metadata", "");
                incoming.add(msg);
            }

            int before = messages.size();
            messages = repository.mergeMessages(messages, incoming);
            repository.saveMessages(messages);

            if (messages.size() > before) {
                adapter = new ChatAdapter(this, messages);
                chatList.setAdapter(adapter);
                chatList.scrollToPosition(messages.size() - 1);
                Toast.makeText(this, "Synced " + (messages.size() - before) + " message(s)", Toast.LENGTH_SHORT).show();
            }

            // Update sync timestamp to the newest message
            if (!messages.isEmpty()) {
                repository.setLastSyncTimestamp(messages.get(messages.size() - 1).timestamp);
            }

            Log.i(TAG, "Chat sync: " + incoming.size() + " from server, " + (messages.size() - before) + " new");
        } catch (Exception e) {
            Log.e(TAG, "Chat sync parse error: " + e.getMessage());
        }
    }

    private final BroadcastReceiver chatReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String content = intent.getStringExtra("content");
            String type = intent.getStringExtra("type");

            if ("sync".equals(type)) {
                // Bulk sync from PC
                handleChatSync(content);
            } else {
                // Single incoming message
                addMessage(new ChatMessage(content, type, false));
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(chatReceiver, new IntentFilter("com.prajwal.myfirstapp.CHAT_EVENT"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(chatReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (connectionManager != null) connectionManager.stopConnectionMonitor();
    }
}