package com.example.peter.diabeatdis_android;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class DoctorMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_main);
    }

    /** Called when the user taps the main menu button */
    public void enterPatientData(View view) {
        Intent intent = new Intent(this, PatientSelectorActivity.class);
        intent.putExtra("caller", "com.example.peter.diabeatdis_android.DoctorMainActivity")
              .putExtra("purpose", "recordData");
        startActivity(intent);
    }

    /** Called when the user taps the log out button */
    public void logOut(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /** Called when the user taps the patient lookup button */
    public void lookupPatient(View view) {
        Intent intent = new Intent(this, PatientLookupActivity.class);
        intent.putExtra("caller", "com.example.peter.diabeatdis_android.DoctorMainActivity");
        startActivity(intent);
    }

    /** Called when the user taps the patient lookup button */
    public void patientSummary(View view) {
        Intent intent = new Intent(this, PatientSelectorActivity.class);
        intent.putExtra("caller", "com.example.peter.diabeatdis_android.DoctorMainActivity")
              .putExtra("purpose", "patientSummary");
        startActivity(intent);
    }

    /** Called when the user taps the device setting button */
    public void deviceSetting(View view) {
        Intent intent = new Intent(this, DeviceSettingActivity.class);
        intent.putExtra("caller", "com.example.peter.diabeatdis_android.DoctorMainActivity");
        startActivity(intent);
    }

    /** Called when the user taps the user lookup button */
    public void userLookup(View view) {
        Intent intent = new Intent(this, UserLookupActivity.class);
        intent.putExtra("caller", "com.example.peter.diabeatdis_android.DoctorMainActivity");
        startActivity(intent);
    }

}
