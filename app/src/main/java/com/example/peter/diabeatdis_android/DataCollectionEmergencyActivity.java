package com.example.peter.diabeatdis_android;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class DataCollectionEmergencyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_collection_emergency);
        Log.d("Pootie", "the caller id is " + getIntent().getStringExtra("caller"));
    }

    /** Called when the user taps the log out button */
    public void logOut(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /** return to last activity when pressed back */
    public void goBack(View view) {
        super.onBackPressed();
    }

    /** Called when the user taps the message to doctor button */
    public void messageDoctor(View view) {
        Intent intent = new Intent(this, MessageDoctorActivity.class);
        intent.putExtra("PatientID", getIntent().getStringExtra("PatientID"))
              .putExtra("caller", getIntent().getStringExtra("caller"));
        startActivity(intent);
    }

    /** Called when the user taps the test again button */
    public void testAgain(View view) {
        Intent intent = new Intent(this, MainDataCollectionSimple.class);
        intent.putExtra("PatientID", getIntent().getStringExtra("PatientID"))
                .putExtra("caller", getIntent().getStringExtra("caller"));
        startActivity(intent);
    }

    /** Called when the user taps the change patient button */
    public void changePatient(View view) {
        Intent intent = new Intent(this, PatientSelectorActivity.class);
        intent.putExtra("caller", getIntent().getStringExtra("caller"));
        startActivity(intent);
    }

    /** Called when the user taps the enter patient data button */
    public void backToMainMenu(View view) {
        String caller = getIntent().getStringExtra("caller");
        Class callerClass;
        try {
            callerClass = Class.forName(caller);
            Intent intent = new Intent(this, callerClass);
            startActivity(intent);
        } catch (Exception e){
            Log.e(e.getMessage(),"cannot get caller id");
        }
    }

}
