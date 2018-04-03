package com.example.peter.diabeatdis_android;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class PatientSelectorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_selector);
        Log.d("Pootie", "the caller id is " + getIntent().getStringExtra("caller"));
    }

    /** Called when the user taps the message to doctor button */
    public void messageDoctor(View view) {
        Intent intent = new Intent(this, MessageDoctorActivity.class);
        intent.putExtra("PatientID", getIntent().getStringExtra("PatientID"))
                .putExtra("caller", getIntent().getStringExtra("caller"));
        startActivity(intent);
    }

    /** Called when the user taps the log out button */
    public void logOut(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /** Called when the user taps the new patient button */
    public void newPatientRegistration(View view) {
        Intent intent = new Intent(this, PatientRegistrationActivity.class);
        intent.putExtra("caller", getIntent().getStringExtra("caller"));
        startActivity(intent);
    }

    /** Called when the user taps the message to doctor button, looks for specific patient then
     * goes to the data collection activity with the corresponding patient */
    public void searchPatient(View view) {
        // look for patient in existing database, load patient data
        String FILENAME = "patient_registry.txt";
        EditText editText1 = findViewById(R.id.editText_patient_select_id);     // collect user input id, pass word, and phone number
        String patientID = editText1.getText().toString();
        TextView textView = findViewById(R.id.textView_patient_select_error);
        String purpose = getIntent().getStringExtra("purpose");

        JSONArray existingUsers = new JSONArray();                      // read in existing JSON file for user database
        try {
            existingUsers = new JSONArray(readFromFile(FILENAME));
        } catch (JSONException e) {
            Log.e("convert",e.getMessage());
        }
        boolean patientFound = false;
        for (int i = 0; i < existingUsers.length(); i++) {              // loop through all user to see if there is exisiting user ID
            if (existingUsers.optJSONObject(i).optString("PatientID").equals(patientID)){
                if (purpose.equals("recordData")) {
                    Intent intent = new Intent(this, DataCollectionActivity.class);
                    intent.putExtra("PatientID", patientID)
                          .putExtra("caller", getIntent().getStringExtra("caller"));
                    startActivity(intent);
                } else if (purpose.equals("patientSummary")) {
                    Intent intent = new Intent(this, PatientSummaryActivity.class);
                    intent.putExtra("PatientID", patientID)
                          .putExtra("caller", getIntent().getStringExtra("caller"));
                    startActivity(intent);
                }
                patientFound = true;
            }
        }
        if (!patientFound){
            textView.setText("Sorry, there is no patient with this registered ID");
        } else {
            textView.setText("Patient record found.");
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
}
