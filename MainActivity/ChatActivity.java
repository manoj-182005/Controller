package com.prajwal.myfirstapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {
    private ChatRepository repository;
    private ArrayList<ChatMessage> messages;
    private ChatAdapter adapter;
    private ConnectionManager connectionManager; // You might need to pass IP via Intent

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_chat);

        String ip = getIntent().getStringExtra("server_ip");
        connectionManager = new ConnectionManager(ip != null ? ip : "192.168.1.1"); // Fallback

        // Setup Repository & Load History
        repository = new ChatRepository(this);
        messages = repository.loadMessages();

        // Setup UI
        RecyclerView rv = findViewById(R.id.chat_messages_list);
        adapter = new ChatAdapter(this, messages);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
        rv.scrollToPosition(messages.size() - 1);

        EditText input = findViewById(R.id.chat_input);
        
        // Send Button Logic
        findViewById(R.id.send_button).setOnClickListener(v -> {
            String text = input.getText().toString().trim();
            if (!text.isEmpty()) {
                // 1. Send to PC
                connectionManager.sendCommand("CHAT_MSG:" + text);
                
                // 2. Add to Local UI
                addMessage(new ChatMessage(text, "text", true));
                input.setText("");
            }
        });

        // Register Receiver for Incoming Messages from MainActivity
        LocalBroadcastManager.getInstance(this).registerReceiver(chatReceiver, 
            new IntentFilter("com.prajwal.myfirstapp.CHAT_EVENT"));
    }

    // Handle messages coming from PC
    private final BroadcastReceiver chatReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String content = intent.getStringExtra("content");
            String type = intent.getStringExtra("type"); // text or file
            
            addMessage(new ChatMessage(content, type, false));
        }
    };

    private void addMessage(ChatMessage msg) {
        messages.add(msg);
        adapter.notifyItemInserted(messages.size() - 1);
        findViewById(R.id.chat_messages_list).scrollToPosition(messages.size() - 1);
        repository.saveMessages(messages); // Auto-save
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(chatReceiver);
    }
}