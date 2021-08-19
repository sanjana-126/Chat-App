package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    TextInputLayout mName,mEmail,mPassword;

    Toolbar mToolbar;

    Button mCreate;

    ProgressDialog mProgress;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        mToolbar = (Toolbar)findViewById(R.id.reg_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Register User");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mName = (TextInputLayout)findViewById(R.id.reg_name);
        mEmail = (TextInputLayout)findViewById(R.id.reg_email);
        mPassword = (TextInputLayout)findViewById(R.id.reg_password);

        mCreate = (Button)findViewById(R.id.reg_create_btn);

        mProgress = new ProgressDialog(this);
        mCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mName.getEditText().getText().toString();
                String email = mEmail.getEditText().getText().toString();
                String password = mPassword.getEditText().getText().toString();
                if(TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
                    Toast.makeText(RegisterActivity.this,"All Fields are required",Toast.LENGTH_LONG).show();
                }
                else if(password.length() < 6){
                    Toast.makeText(RegisterActivity.this,"Password should be minimum of 6 characters",Toast.LENGTH_LONG).show();
                }
                else {
                    mProgress.setTitle("Registering User");
                    mProgress.setMessage("Please wait, while we create your account");
                    mProgress.setCanceledOnTouchOutside(false);
                    mProgress.show();
                    register_user(name, email, password);
                }
            }
        });
    }
    private void register_user(String name, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    FirebaseUser mCurrentUser = mAuth.getCurrentUser();
                    String user_id = mCurrentUser.getUid();
                    mDatabase = FirebaseDatabase.getInstance("https://chatapp-a488b-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference().child("Users").child(user_id);
                    HashMap <String,String> userMap = new HashMap<>();
                    userMap.put("id",user_id);
                    userMap.put("name",name);
                    userMap.put("status","Hey there, I am using LetsChat");
                    userMap.put("image","default");
                    userMap.put("currentStatus","offline");
                    userMap.put("search",name.toLowerCase());

                    mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                mProgress.dismiss();
                                Intent mainIntent = new Intent(RegisterActivity.this,MainActivity.class);
                                mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(mainIntent);
                                finish();
                            }
                        }
                    });
                }
                else
                {
                    mProgress.hide();
                    Toast.makeText(RegisterActivity.this,"You can't register with this email or password",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}