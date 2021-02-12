package com.example.socialapp.messaging;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialapp.R;
import com.example.socialapp.models.User;
import com.example.socialapp.readapters.ContactsAdapter;
import com.example.socialapp.data.AppData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ContactsActivity extends AppCompatActivity implements ContactsAdapter.OnChatClickListener {

    private static final String TAG = "ContactsActivity";
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadData();
        setContentView(R.layout.activity_contacts);
        setup();
    }

    private void loadData() {
        DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference("users");
        usersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                AppData.userList.clear();
                ArrayList<User> users = new ArrayList<>();
                snapshot.getChildren().forEach(child -> {
                    users.add(child.getValue(User.class));
                });
                AppData.userList.addAll(users);
                updateRecyclerView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void setup() {
        recyclerView = findViewById(R.id.contactRecyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        ContactsAdapter mAdapter = new ContactsAdapter(AppData.userList, this, this);
        recyclerView.setAdapter(mAdapter);
        if (!AppData.userList.isEmpty()) {
            findViewById(R.id.noContactsTextView).setVisibility(View.INVISIBLE);
        }

        Toolbar toolbar = findViewById(R.id.toolbar_contacts);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    @Override
    public void onChatClick(int position) {
        User otherUser = AppData.userList.get(position);

        Log.d(TAG, otherUser.userId);
        String chatKey = null;
        for (String key : AppData.chatKeyList) {
            if (key.contains(otherUser.userId) && key.contains(FirebaseAuth.getInstance().getUid())) {
                chatKey = key;
            }
        }

        if (chatKey == null) {
            chatKey = FirebaseAuth.getInstance().getUid() + "-" + otherUser.userId;
        }

        startActivity(new Intent(this, ChatActivity.class)
                .putExtra("chatKey", chatKey)
                .putExtra("otherUserNick", otherUser.getNick())
        );
    }

    public void updateRecyclerView() {
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.invalidate();
        if (!AppData.userList.isEmpty()) {
            findViewById(R.id.noContactsTextView).setVisibility(View.INVISIBLE);
        } else {
            findViewById(R.id.noContactsTextView).setVisibility(View.VISIBLE);
        }
    }
}