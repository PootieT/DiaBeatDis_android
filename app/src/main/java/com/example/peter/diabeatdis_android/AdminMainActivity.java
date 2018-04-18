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
import java.text.SimpleDateFormat;
import java.util.Date;

public class AdminMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);
    }

    /** Called when the user taps the log out button */
    public void logOut(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /** Called when the user taps the manage account button */
    public void manageAccount(View view) {
        Intent intent = new Intent(this, ManageAccountActivity.class);
        startActivity(intent);
    }

    /** Called when the user taps the enter patient data button */
    public void backToMainMenu(View view) {
        String caller = getIntent().getStringExtra("caller");
        Log.d("pootie", "caller is " + caller);
        Class callerClass;
        try {
            callerClass = Class.forName(caller);
            Intent intent = new Intent(this, callerClass);
            startActivity(intent);
        } catch (Exception e){
            Log.e(e.getMessage(),"cannot get caller id");
        }
    }

    /** Called when the user taps the any button that have not been implemented yet */
    public void futureWarning(View view) {
        TextView box = findViewById(R.id.textView_admin_main_message);
        box.setText("This feature has not been implemented yet, check it out in our future version!");
    }

    /** Called when the user taps the device setting button */
    public void deviceSetting(View view) {
        Intent intent = new Intent(this, DeviceSettingActivity.class);
        intent.putExtra("caller", "com.example.peter.diabeatdis_android.AdminMainActivity");
        startActivity(intent);
    }

    /** Called when the user taps the device setting button */
    public void calibrateDevice(View view) {
        Intent intent = new Intent(this, DataCollectionActivity.class);
        intent.putExtra("caller", "com.example.peter.diabeatdis_android.AdminMainActivity");
        startActivity(intent);
    }

    /** Called when the user taps the device statistics button */
    public void deviceStatistics(View view) {
        Intent intent = new Intent(this, DeviceStatisticsActivity.class);
        intent.putExtra("caller", "com.example.peter.diabeatdis_android.AdminMainActivity");
        startActivity(intent);
    }

    /** Called when the user taps the user lookup button */
    public void userLookup(View view) {
        Intent intent = new Intent(this, UserLookupActivity.class);
        intent.putExtra("caller", "com.example.peter.diabeatdis_android.AdminMainActivity");
        startActivity(intent);
    }

    /** Called when the user taps the RESET SYSTEM button */
    public void resetSystem(View view) {
        Log.d("Pootie","wiping patient registry and user account information...");
        writeToFile("patient_registry.txt","");
        writeToFile("user_accounts.txt","");
        Log.d("Pootie","updating device statistics");            // reset sharedPreference
        String MY_PREFS_NAME = "deviceStatistics";
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putInt("recordDataClicks",0);
        editor.putInt("patientLookupClicks",0);
        editor.putInt("userLookupClicks",0);
        editor.putInt("patientSummaryClicks",0);
        editor.putInt("downloadDataClicks",0);
//        editor.putInt("messagesSent",0);
        editor.apply();

        TextView outPut = findViewById(R.id.textView_admin_main_message);
        outPut.setText("Device has been reset. Patient and user information and device statistics has been cleaned.");
    }

    /** called when user taps download data button */
    public void downloadData(View view) {
        // increment sharedPreference user lookup clicks
        Log.d("Pootie","updating device statistics");
        String MY_PREFS_NAME = "deviceStatistics";
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putInt("downloadDataClicks",getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getInt("downloadDataClicks", 0)+1);
        Log.d("Pootie","download data clicks:"+getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getInt("downloadDataClicks", 0));
        editor.apply();

        String FILENAME = "patient_data.txt";
        JSONArray patientData = new JSONArray();                      // read in patient_data file
        try {
            patientData = new JSONArray(readFromFile(FILENAME));
        } catch (JSONException e) {
            Log.e("convert", e.getMessage());
            patientData = new JSONArray();
        }
        writeToFile(patientData.toString());

        TextView outPut = findViewById(R.id.textView_admin_main_message);
        outPut.setText("Patient Data has been copied to a USB accessible location.");
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
