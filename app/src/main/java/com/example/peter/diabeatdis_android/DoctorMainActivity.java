package com.example.peter.diabeatdis_android;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

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

    /** called when user taps download data button */
    public void downloadData(View view) {
        String FILENAME = "patient_data.txt";
        JSONArray patientData = new JSONArray();                      // read in patient_data file
        try {
            patientData = new JSONArray(readFromFile(FILENAME));
        } catch (JSONException e) {
            Log.e("convert", e.getMessage());
            patientData = new JSONArray();
        }
        writeToFile(patientData.toString());

    }


    public File getPublicAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), albumName);
        if (!file.mkdirs()) {
            Log.e("", "Directory not created");
        }
        return file;
    }

    /** helper function to read string data into a txt file*/
    private String readFromFile(String file) {

        String ret = "";

        try {
            InputStream inputStream = openFileInput(file);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

    /** helper function to write string data into a txt file*/
    private void writeToFile(String data) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd_MM_yyyy");
        Date date = new Date();
        String formattedDate = formatter.format(date);
        File patternDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath().toString()+"/" +formattedDate +"_data");
        patternDirectory.mkdirs();

        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(new File(patternDirectory.getAbsolutePath().toString()+"/" +formattedDate +"_data"), true); // true will be same as Context.MODE_APPEND
            outputStream.write(data.getBytes());
            outputStream.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }


}
