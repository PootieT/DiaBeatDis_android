package com.example.peter.diabeatdis_android;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class PatientLookupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_lookup);
    }

    /** Called when the user taps the log out button */
    public void logOut(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
