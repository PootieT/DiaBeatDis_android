package com.example.peter.diabeatdis_android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class DeviceSettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_setting);
    }

    /** Called when the user taps the log out button */
    public void logOut(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        checkPreference();
    }

    /** return to last activity when pressed back */
    public void goBack(View view) {
        super.onBackPressed();
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
        checkPreference();
    }

    /** Called when the user taps the clear button */
    public void clearMemory(View view) {
        CheckBox box1 = findViewById(R.id.checkBox_device_setting_patient_data);
        Boolean ifPatientData = box1.isChecked();
        CheckBox box2 = findViewById(R.id.checkBox_device_setting_user_data);
        Boolean ifUserData = box2.isChecked();

        if (ifPatientData) {
            writeToFile("patient_registry.txt", "");
            writeToFile("patient_data.txt", "");
        }
        if (ifUserData) {
            writeToFile("user_accounts.txt", "");
        }
        checkPreference();
    }

    private void checkPreference() {
        CheckBox box1 = findViewById(R.id.checkBox_device_setting_can_health_worker_register);
        Boolean canRegister = box1.isChecked();
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        if (canRegister) {
            editor.putBoolean(getString(R.string.pref_can_health_worker_register), true);
        } else {
            editor.putBoolean(getString(R.string.pref_can_health_worker_register), false);
        }
        editor.commit();
        Log.d("pootie","preference is set to "+getString(R.string.pref_can_health_worker_register));
    }

    /** helper function to write string data into a txt file*/
    private void writeToFile(String file, String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput(file, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

}
