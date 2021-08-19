package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatapp.Adapter.MessageAdapter;
import com.example.chatapp.Model.Chats;
import com.example.chatapp.Model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {
    private CircleImageView mImage;
    private TextView mName;
    private EditText message;
    private ImageButton send;

    private Toolbar mToolbar;

    private FirebaseUser cUser;
    private DatabaseReference reference;

    private MessageAdapter messageAdapter;
    private List<Chats> chat;

    private RecyclerView mChats;


    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        mToolbar = (Toolbar)findViewById(R.id.message_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MessageActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        mImage = (CircleImageView)findViewById(R.id.message_profile_image);
        mName = (TextView)findViewById(R.id.message_username);
        message = (EditText)findViewById(R.id.text_Send);
        send = (ImageButton)findViewById(R.id.btn_sent);

        mChats = (RecyclerView)findViewById(R.id.message_recycle_view);
        mChats.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        mChats.setLayoutManager(linearLayoutManager);

        intent = getIntent();
        String userid = intent.getStringExtra("userid");

        cUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance("https://chatapp-a488b-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users").child(userid);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = message.getText().toString();
                if(!msg.equals("")){
                    sendMessage(cUser.getUid(), userid, msg);
                }
                else
                {
                    Toast.makeText(MessageActivity.this,"You can't send empty message",Toast.LENGTH_LONG).show();
                }
                message.setText("");
            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                mName.setText(user.getName());
                if(user.getImage().equals("default"))
                    mImage.setImageResource(R.drawable.profilepic);
                else
                    Picasso.with(MessageActivity.this).load(user.getImage()).placeholder(R.drawable.profilepic).into(mImage);

                readMessages(cUser.getUid(), userid);
            }


            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
    private void sendMessage(String sender, String receiver, String message){
        DatabaseReference reference = FirebaseDatabase.getInstance("https://chatapp-a488b-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();
        HashMap<String, String> map = new HashMap<>();
        map.put("sender",sender);
        map.put("receiver",receiver);
        map.put("message",message);
        reference.child("Chats").push().setValue(map);
    }

    private void readMessages(String myId, String userId){
        chat = new ArrayList<>();
        reference = FirebaseDatabase.getInstance("https://chatapp-a488b-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                chat.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Chats chats = dataSnapshot.getValue(Chats.class);
                    if((chats.getSender().equals(myId) && chats.getReceiver().equals(userId))
                            ||(chats.getSender().equals(userId) && chats.getReceiver().equals(myId)))
                        chat.add(chats);
                }
                messageAdapter = new MessageAdapter(getApplicationContext(),chat);
                mChats.setAdapter(messageAdapter);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}