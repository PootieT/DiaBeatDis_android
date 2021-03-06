package com.example.peter.diabeatdis_android;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class HealthWorkerMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_worker_main);
    }

    /** Called when the user taps the message to doctor button */
    public void messageDoctor(View view) {
        Intent intent = new Intent(this, MessageDoctorActivity.class);
        intent.putExtra("caller", "com.example.peter.diabeatdis_android.HealthWorkerMainActivity");
        startActivity(intent);
    }

    /** Called when the user taps the log out button */
    public void logOut(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /** Called when the user taps the device setting button */
    public void calibrateDevice(View view) {
        Intent intent = new Intent(this, DataCollectionActivity.class);
        intent.putExtra("caller", "com.example.peter.diabeatdis_android.HealthWorkerMainActivity");
        startActivity(intent);
    }

    /** Called when the user taps the enter patient data button */
    public void enterPatientData(View view) {
        Intent intent = new Intent(this, PatientSelectorActivity.class);
        intent.putExtra("caller", "com.example.peter.diabeatdis_android.HealthWorkerMainActivity")
              .putExtra("purpose", "recordData");
        startActivity(intent);
    }
}
