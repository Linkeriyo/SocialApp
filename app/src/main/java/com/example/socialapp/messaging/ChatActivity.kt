package com.example.socialapp.messaging;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialapp.R;
import com.example.socialapp.models.Message;
import com.example.socialapp.readapters.MessageAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private final static String TAG = "ChatActivity";
    String chatKey, otherUserNick;
    RecyclerView recyclerView;
    List<Message> messageList = new ArrayList<>();
    DatabaseReference chatReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chatKey = getIntent().getStringExtra("chatKey");
        otherUserNick = getIntent().getStringExtra("otherUserNick");
        loadMessages();
        setContentView(R.layout.activity_chat);
        setup();
    }

    public void setup() {
        recyclerView = findViewById(R.id.chatRecyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        MessageAdapter mAdapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(mAdapter);

        Button sendButton = findViewById(R.id.sendButton);
        sendButton.setOnClickListener(v -> {
            TextView messageTextView = findViewById(R.id.messageTextView);
            if (!messageTextView.getText().toString().isEmpty()) {
                chatReference.child(System.currentTimeMillis() + "-" + FirebaseAuth.getInstance().getUid())
                        .setValue(messageTextView.getText().toString());
                messageTextView.setText("");
            }
        });

        Toolbar toolbar = findViewById(R.id.chat_toolbar);
        toolbar.setTitle(otherUserNick);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    public void loadMessages() {
        chatReference = FirebaseDatabase.getInstance().getReference("chats/" + chatKey);
        chatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageList.clear();
                ArrayList<Message> messages = new ArrayList<>();
                snapshot.getChildren().forEach(child -> {
                    messages.add(new Message(child.getKey(), child.getValue(String.class)));
                });
                messageList.addAll(messages);
                recyclerView.getAdapter().notifyDataSetChanged();
                recyclerView.invalidate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }
}