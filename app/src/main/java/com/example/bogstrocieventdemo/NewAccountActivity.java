package com.example.bogstrocieventdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class NewAccountActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Button createAccButton;
    private EditText editTextEmail;
    private EditText editTextPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_account);
        createAccButton= findViewById(R.id.createAccButton);
        editTextEmail = findViewById(R.id.emailEditText);
        editTextPass = findViewById(R.id.passEditText);
        mAuth = FirebaseAuth.getInstance();

        createAccButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                   signUpClicked(createAccButton);
            }
        });

    }


    public void signUpClicked(View view){
        mAuth.createUserWithEmailAndPassword(editTextEmail.getText().toString(), editTextPass.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    private final String TAG = null;

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent = new Intent(NewAccountActivity.this, EventActivity.class);
                            startActivity(intent);
                            finish();
                        } else{
                            // If sign in fails, display a message to the user.
                            Toast.makeText(NewAccountActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();

                        }

                    }
                });
    }

}
