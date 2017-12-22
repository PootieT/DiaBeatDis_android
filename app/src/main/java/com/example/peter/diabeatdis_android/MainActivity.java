package com.example.peter.diabeatdis_android;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /** Called when the user taps the new user button, view is the button object */
    public void registerNewUser(View view) {
        Intent intent = new Intent(this, UserRegistrationActivity.class);
        startActivity(intent);
    }

}
