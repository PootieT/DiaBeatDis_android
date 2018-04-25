package com.example.peter.diabeatdis_android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;

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
    public void calibrateDevice(View view) {
        Intent intent = new Intent(this, DataCollectionActivity.class);
        intent.putExtra("caller", "com.example.peter.diabeatdis_android.DoctorMainActivity");
        startActivity(intent);
    }

    /** Called when the user taps the any button that have not been implemented yet */
    public void futureWarning(View view) {
        TextView box = findViewById(R.id.textView_doc_main_message);
        box.setText("This feature has not been implemented yet, check it out in our future version!");
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

    /** new dowload method, go to download data interface when download button is pressed */
    public void downloadData(View view) {
        Intent intent = new Intent(this, DownloadDataActivity.class);
        intent.putExtra("caller", "com.example.peter.diabeatdis_android.DoctorMainActivity");
        intent.putExtra("userID", getIntent().getStringExtra("userID"));
        startActivity(intent);
    }

}
