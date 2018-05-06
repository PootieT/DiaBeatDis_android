package com.example.peter.diabeatdis_android;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DataCollectionAfterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Double DIABETIC_FASTING_THRESHOLD = 130.0;
        Double DIABETIC_NON_FASTING_THRESHOLD = 180.0;
        Double NORMAL_FASTING_THRESHOLD = 110.0;
        Double NORMAL_NON_FASTING_THRESHOLD = 140.0;
        String FILENAME = "patient_data.txt";

        TextView message = findViewById(R.id.textView_data_collection_successful_message);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_collection_after);
        Log.d("Pootie", "the caller id is " + getIntent().getStringExtra("caller"));

        Double reading = getIntent().getDoubleExtra("reading", 0);
        boolean ifFasting = getIntent().getBooleanExtra("ifFasting", true);
        String patientID = getIntent().getStringExtra("PatientID");

        JSONArray patientRecord = new JSONArray();                      // read in patient_data file
        try {
            patientRecord = new JSONArray(readFromFile(FILENAME));
        } catch (JSONException e) {
            Log.e("convert", e.getMessage());
            patientRecord = new JSONArray();
        }

        String riskCategory = "no_risk";
        for (int i = 0; i<patientRecord.length(); i++){
            if (patientRecord.optJSONObject(i).optString("PatientID").equals(patientID)){
                riskCategory = patientRecord.optJSONObject(i).optString("RiskCategory");
            }
        }

        if (riskCategory.equals("diabetic") || riskCategory.equals("at_risk")){
            if (ifFasting) {
                if (reading < DIABETIC_FASTING_THRESHOLD) {
                    message.setText("Glucose collection successful! Patient blood glucose is in normal range");
                } else {
                    message.setText("Glucose collection successful! Patient blood glucose is high, consider taking blood glucose treatment.");
                }
            } else {
                if (reading < DIABETIC_NON_FASTING_THRESHOLD) {
                    message.setText("Glucose collection successful! Patient blood glucose is in normal range");
                } else {
                    message.setText("Glucose collection successful! Patient blood glucose is high, consider taking blood glucose treatment.");
                }
            }
        } else if (riskCategory.equals("no_risk") || riskCategory.equals("unknown_risk")) {
            if (ifFasting) {
                if (reading < NORMAL_FASTING_THRESHOLD) {
                    message.setText("Glucose collection successful! Patient blood glucose is in normal range");
                } else {
                    message.setText("Glucose collection successful! Patient blood glucose is high, consider following up to see if he/she is diabetic or at risk.");
                }
            } else {
                if (reading < NORMAL_NON_FASTING_THRESHOLD) {
                    message.setText("Glucose collection successful! Patient blood glucose is in normal range");
                } else {
                    message.setText("Glucose collection successful! Patient blood glucose is high, consider following up to see if he/she is diabetic or at risk.");
                }
            }
        }
    }

    /** Called when the user taps the log out button */
    public void logOut(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /** Called when the user taps the message to doctor button */
    public void messageDoctor(View view) {
        Intent intent = new Intent(this, MessageDoctorActivity.class);
        intent.putExtra("PatientID", getIntent().getStringExtra("PatientID"))
                .putExtra("caller", getIntent().getStringExtra("caller"));
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

    /** return to last activity when pressed back */
    public void goBack(View view) {
        super.onBackPressed();
    }

    /** Called when the user taps the change patient button */
    public void changePatient(View view) {
        Intent intent = new Intent(this, PatientSelectorActivity.class);
        intent.putExtra("caller", getIntent().getStringExtra("caller"));
        startActivity(intent);
    }

    /** Called when the user taps the change patient button */
    public void testAgain(View view) {
        Intent intent = new Intent(this, MainDataCollectionSimple.class);
        intent.putExtra("PatientID", getIntent().getStringExtra("PatientID"))
                .putExtra("caller", getIntent().getStringExtra("caller"));
        startActivity(intent);
    }

    /** Called when the user taps the any button that have not been implemented yet */
    public void futureWarning(View view) {
        TextView box = findViewById(R.id.textView_admin_main_message);
        box.setText("This feature has not been implemented yet, check it out in our future version!");
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
