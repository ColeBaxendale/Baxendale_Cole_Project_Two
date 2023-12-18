package com.example.baxendale_cole_project_two;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PhoneScreen extends AppCompatActivity {

    String username = null;
    private DataBaseHelper dataBaseHelper;
    private EditText editTextPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String username = getIntent().getStringExtra("USERNAME_KEY");
        if (username == null) {
            Log.d("PhoneScreen", "Username is null in onCreate");
        } else {
            Log.d("PhoneScreen", "Username: " + username);
            this.username = username;

        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sms_messages);

        dataBaseHelper = new DataBaseHelper(this);
        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);

        Button buttonYes = findViewById(R.id.buttonYes);
        Button buttonNo = findViewById(R.id.buttonNo);

        buttonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserInfo(); // User consented to SMS
            }
        });

        buttonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataBaseHelper.updateUserDetailsFalse(username, false);
                Toast.makeText(PhoneScreen.this, "User: " + username + " declined SMS updates", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(PhoneScreen.this, MainScreen.class);
                intent.putExtra("USERNAME_KEY", username);
                startActivity(intent);
            }
        });
    }

    private void updateUserInfo() {
        String phoneNumber = editTextPhoneNumber.getText().toString();

        if (!phoneNumber.isEmpty()) {
            dataBaseHelper.updateUserDetails(username, phoneNumber, true);
            Toast.makeText(PhoneScreen.this, "User: " + username + " accepted SMS updates to " + phoneNumber, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(PhoneScreen.this, MainScreen.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Please enter a phone number", Toast.LENGTH_SHORT).show();
        }
    }
}