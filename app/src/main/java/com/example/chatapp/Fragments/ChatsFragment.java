package com.example.chatapp.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chatapp.Adapter.UserAdapter;
import com.example.chatapp.Model.Chats;
import com.example.chatapp.Model.User;
import com.example.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class ChatsFragment extends Fragment {
    private RecyclerView recyclerview;

    DatabaseReference reference;
    FirebaseUser cUser;

    private UserAdapter userAdapter;
    private List<User> mUsers;

    private List<String> usersList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

         View view = inflater.inflate(R.layout.fragment_chats, container, false);
         recyclerview = (RecyclerView)view.findViewById(R.id.chats_recyclerview);
         recyclerview.setHasFixedSize(true);
         recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));

         cUser = FirebaseAuth.getInstance().getCurrentUser();

         usersList = new ArrayList<>();

         reference = FirebaseDatabase.getInstance("https://chatapp-a488b-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Chats");

         reference.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                 usersList.clear();
                 for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                     Chats chat = dataSnapshot.getValue(Chats.class);
                     if(chat.getSender().equals(cUser.getUid())){
                         usersList.add(chat.getReceiver());
                     }
                     if(chat.getReceiver().equals(cUser.getUid())){
                         usersList.add(chat.getSender());
                     }
                 }
                 readChats();
             }

             @Override
             public void onCancelled(@NonNull @NotNull DatabaseError error) {

             }
         });
         
         return view;
    }

    private void readChats() {
        mUsers = new ArrayList<>();
        reference = FirebaseDatabase.getInstance("https://chatapp-a488b-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                mUsers.clear();


                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);

                    for(String id : usersList){
                        if(user.getId().equals(id)){
                           if(mUsers.size() != 0){
                               int flag=0;
                               for(int i=0;i<mUsers.size();i++)
                               {
                                   User user1 = mUsers.get(i);
                                   if(user.getId().equals(user1.getId()))
                                       flag=1;
                               }
                               if(flag == 0)
                                   mUsers.add(user);
                           } else {
                               mUsers.add(user);
                           }
                        }
                    }
                }
                userAdapter = new UserAdapter(getContext(), mUsers, true);
                recyclerview.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }


}