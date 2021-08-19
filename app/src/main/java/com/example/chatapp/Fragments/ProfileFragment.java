package com.example.chatapp.Fragments;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatapp.Model.User;
import com.example.chatapp.R;
import com.example.chatapp.StatusActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {
    CircleImageView mImage;
    TextView mName, mStatus;

    Button changeStatus, changeImage;

    DatabaseReference mDatabase;
    FirebaseUser cUser;

    StorageReference storageReference;

    private ProgressDialog mProgress;
    private static final int GALLERY_PICK = 1;
    private Uri uri;
    private StorageTask uploadTask;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);
        mImage = view.findViewById(R.id.profile_pic);
        mName = view.findViewById(R.id.profile_username);
        mStatus = view.findViewById(R.id.profile_status);
        changeStatus = view.findViewById(R.id.settings_change_status);
        changeImage = view.findViewById(R.id.settings_change_image);
        cUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance("https://chatapp-a488b-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users").child(cUser.getUid());

        storageReference = FirebaseStorage.getInstance("gs://chatapp-a488b.appspot.com/").getReference().child("profile_images");

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                mName.setText(user.getName());
                mStatus.setText(user.getStatus());
                if(!user.getImage().equals("default")){
                    Picasso.with(getContext()).load(user.getImage()).placeholder(R.drawable.profilepic).into(mImage);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        changeStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status_value = mStatus.getText().toString();
                Intent statusIntent = new Intent(getContext(), StatusActivity.class);
                statusIntent.putExtra("status_value", status_value);
                startActivity(statusIntent);
            }
        });

        changeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);
            }
        });
        return view;
    }
    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, GALLERY_PICK);
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    private void uploadImage() {
        mProgress = new ProgressDialog(getContext());
        mProgress.setMessage("Uploading...");
        mProgress.setCanceledOnTouchOutside(false);
        mProgress.show();

        if(uri!=null)
        {
            final StorageReference filepath = storageReference.child(System.currentTimeMillis()
                    +"."+getFileExtension(uri));

            uploadTask = filepath.getFile(uri);
            uploadTask.continueWith(new Continuation<UploadTask.TaskSnapshot, Task<Uri>> (){
                @Override
                public Task<Uri> then(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){

                    }
                    return filepath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();

                        cUser = FirebaseAuth.getInstance().getCurrentUser();
                        mDatabase = FirebaseDatabase.getInstance("https://chatapp-a488b-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users").child(cUser.getUid());
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("image", mUri);
                        mDatabase.updateChildren(hashMap);

                        mProgress.dismiss();
                    } else {
                        Toast.makeText(getContext(), "Failed", Toast.LENGTH_LONG).show();
                        mProgress.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    mProgress.dismiss();
                }
            });
        } else {
            Toast.makeText(getContext(), "No image selected", Toast.LENGTH_LONG).show();
        }

    }
}