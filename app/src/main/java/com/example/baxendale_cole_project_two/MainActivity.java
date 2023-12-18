package com.example.baxendale_cole_project_two;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DataBaseHelper dbHelper = new DataBaseHelper(MainActivity.this);

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);

        // Find the "Sign Up Now" TextView
        TextView signUpText = findViewById(R.id.signupText);

        // Set an OnClickListener for the "Sign Up Now" TextView
        signUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                if (!username.isEmpty() && !password.isEmpty()) {

                    if (dbHelper.doesUserExist(username)) {
                        // User already exists, show a message
                        Toast.makeText(getApplicationContext(), "You already have an account. Please sign in.", Toast.LENGTH_SHORT).show();
                    } else {
                        // User doesn't exist, sign them up
                        dbHelper.registerUser(username, password);
                        Toast.makeText(getApplicationContext(), "Account created successfully!", Toast.LENGTH_SHORT).show();
                    }
                    dbHelper.close();

                } else {
                    Toast.makeText(getApplicationContext(), "Username and password must be completed to sign up!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if (!username.isEmpty() && !password.isEmpty()) {
                    if(dbHelper.loginUser(username, password)){
                        Intent intent = new Intent(MainActivity.this, MainScreen.class);
                        intent.putExtra("USERNAME_KEY", username);
                        startActivity(intent);
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Incorrect login!", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Username and password must be completed!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}