package com.example.peter.diabeatdis_android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
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
import java.text.SimpleDateFormat;
import java.util.Date;

public class DownloadDataActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_data);
    }

    /** upon pressing main menu button, go back to main menu activity */
    public void mainMenu(View view) {
        String caller = getIntent().getStringExtra("caller");
        Class callerClass;
        try {
            callerClass = Class.forName(caller);
            Intent intent = new Intent(this, callerClass);
            intent.putExtra("PatientID", getIntent().getStringExtra("PatientID"))
                    .putExtra("caller", getIntent().getStringExtra("caller"));
            startActivity(intent);
        } catch (Exception e){
            Log.e(e.getMessage(),"cannot get caller id");
        }
    }

    /** return to last activity when pressed back */
    public void goBack(View view) {
        super.onBackPressed();
    }

    /** return to main activity when pressed logout */
    public void logOut(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /** called when user taps download data button */
    public void downloadData(View view) {
        // check if password entered is correct
        EditText passwordView = findViewById(R.id.editText_download_data_password);
        String password = passwordView.getText().toString();
        String userID = getIntent().getStringExtra("userID");
        JSONArray existingUsers = new JSONArray();                      // read in existing JSON file for user database
        try {
            existingUsers = new JSONArray(readFromFile("user_accounts.txt"));
        } catch (JSONException e) {
            Log.e("convert",e.getMessage());
        }
        boolean successFlag = false;
        TextView message = findViewById(R.id.textView_download_data_warning);
        RadioButton usbButton = findViewById(R.id.radioButton_dowload_data_usb);
        boolean ifUSB = usbButton.isChecked();
        if (ifUSB) {
            for (int i = 0; i < existingUsers.length(); i++) {
                if (existingUsers.optJSONObject(i).optString("UserID").equals(userID) &&
                        existingUsers.optJSONObject(i).optString("Password").equals(password)) {
                    // increment sharedPreference user lookup clicks
                    Log.d("Pootie", "updating device statistics");
                    String MY_PREFS_NAME = "deviceStatistics";
                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putInt("downloadDataClicks", getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getInt("downloadDataClicks", 0) + 1);
                    Log.d("Pootie", "download data clicks:" + getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getInt("downloadDataClicks", 0));
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

                    message.setText("Patient Data has been copied to publicly available data folder, please access it through USB cable.");

                    successFlag = true;
                }
            }
            if (!successFlag) {
                message.setText("The password entered is not correct, please try again with the correct password!");
            }
        } else {
            message.setText("Please select download data through USB option.");
        }
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
