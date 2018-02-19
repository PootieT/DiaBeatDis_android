package com.example.peter.diabeatdis_android;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class PatientRegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_registration);
    }

    /** Called when the user taps the message to doctor button */
    public void messageDoctor(View view) {
        Intent intent = new Intent(this, MessageDoctorActivity.class);
        startActivity(intent);
    }

    /** Called when the user taps the log out button */
    public void logOut(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /** Called when the user taps the cancel button */
    public void backToSelectPatient(View view) {
        Intent intent = new Intent(this, PatientSelectorActivity.class);
        startActivity(intent);
    }

    /** Called when the user taps the enter button */
    public void registerNewPatient(View view) {
        String FILENAME = "patient_registry.txt";
        EditText editText1 = findViewById(R.id.editText_patient_reg_id);     // collect user input id, pass word, and phone number
        String patientID = editText1.getText().toString();
        EditText editText2 = findViewById(R.id.editText_patient_reg_age);
        String age = editText2.getText().toString();
        EditText editText3 = findViewById(R.id.editText_patient_reg_location);
        String location = editText3.getText().toString();
        location = location.replaceAll(" ", "_").replaceAll(",","_").toLowerCase().replaceAll("(_){2}", "_");
        EditText editText4 = findViewById(R.id.editText_patient_reg_mobile);
        String mobile = editText4.getText().toString();
        EditText editText5 = findViewById(R.id.editText_patient_reg_name);
        String name = editText5.getText().toString();
        name = name.replaceAll(" ", "_").replaceAll(",","_").toLowerCase();
        EditText editText6 = findViewById(R.id.editText_patient_reg_sex);
        String sex = editText6.getText().toString();
        CheckBox box1 = findViewById(R.id.checkBox_patient_reg_if_diabetic);
        Boolean ifDiabetic = box1.isChecked();
        CheckBox box2 = findViewById(R.id.checkBox_patient_reg_if_at_risk);
        Boolean ifAtRisk = box2.isChecked();
        CheckBox box3 = findViewById(R.id.checkBox_patient_reg_if_no_risk);
        Boolean ifNoRisk = box3.isChecked();
        CheckBox box4 = findViewById(R.id.checkBox_patient_reg_if_unknown_risk);
        Boolean ifUnknownRisk = box4.isChecked();
        String riskCategory = ifDiabetic? "diabetic":ifAtRisk?"at_risk":ifNoRisk?"no_risk":ifUnknownRisk?"unknown_risk":"unknown_risk";
        TextView textView = findViewById(R.id.textView_patient_reg_error);

        if (patientID != "" || age != ""  || location != ""  || mobile != ""  || name != ""  || sex != "") {
            JSONArray existingPatients = new JSONArray();                      // read in existing JSON file for user database
            try {
                existingPatients = new JSONArray(readFromFile(FILENAME));
            } catch (JSONException e) {
                Log.e("convert", e.getMessage());
            }

            JSONObject patientData = new JSONObject();                           // combine and convert them into JSON data format
            try {
                patientData = new JSONObject("{\"PatientID\":" + patientID +",\"Name\":" + name +",\"Location\":" + location + ",\"Age\":" + age + ",\"Sex\":" + sex +",\"Mobile\":" + mobile + ",\"RiskCategory\":" + riskCategory + "}");
            } catch (JSONException e) {
                patientData = new JSONObject();
            }

            existingPatients.put(patientData);                                      // combine the old user data with new user data
            writeToFile(FILENAME, existingPatients.toString());                  // save all the user data away
            String dataOut = readFromFile(FILENAME);
            Log.d("success: ", dataOut);
            System.out.print(dataOut);

            Intent intent = new Intent(this, DataCollectionActivity.class);
            intent.putExtra("patientID", patientID);
            startActivity(intent);
        } else {
            textView.setText("Please fill out all information.");
        }
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
