package com.example.peter.diabeatdis_android;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class DeviceStatisticsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_statistics);

        TextView record_data_view = findViewById(R.id.textView_dev_stat_record_data); // find textViews
        TextView download_data_view = findViewById(R.id.textView_dev_stat_download_data);
        TextView message_sent_view = findViewById(R.id.textView_dev_stat_message_sent);
        TextView patient_lookup_view = findViewById(R.id.textView_dev_stat_patient_lookup);
        TextView patient_summary_view = findViewById(R.id.textView_dev_stat_patient_summary);
        TextView user_lookup_view = findViewById(R.id.textView_dev_stat_user_lookup);
        TextView patient_number_view = findViewById(R.id.textView_dev_stat_patient_number);
        TextView user_number_view = findViewById(R.id.textView_dev_stat_user_number);

        Log.d("Pootie","Retrieving statistics from sharedPreferences...");
        record_data_view.setText(Integer.toString(getSharedPreferences("deviceStatistics",MODE_PRIVATE).getInt("recordDataClicks", 0)));
        download_data_view.setText(Integer.toString(getSharedPreferences("deviceStatistics",MODE_PRIVATE).getInt("downloadDataClicks", 0)));
//        message_sent_view.setText(getSharedPreferences("deviceStatistics",MODE_PRIVATE).getInt("messageSentClicks", 0));
        message_sent_view.setText("0");
        patient_lookup_view.setText(Integer.toString(getSharedPreferences("deviceStatistics",MODE_PRIVATE).getInt("patientLookupClicks", 0)));
        patient_summary_view.setText(Integer.toString(getSharedPreferences("deviceStatistics",MODE_PRIVATE).getInt("patientSummaryClicks", 0)));
        user_lookup_view.setText(Integer.toString(getSharedPreferences("deviceStatistics",MODE_PRIVATE).getInt("userLookupClicks", 0)));


        Log.d("Pootie","Calculating patient information");
        JSONArray existingPatients = new JSONArray();               // calculate patient information
        try {
            existingPatients = new JSONArray(readFromFile("patient_registry.txt"));
        } catch (JSONException e) {
            Log.e("convert", e.getMessage());
        }
//        //removing duplicate results, there shouldn't be once system is perfect
//        JSONArray temp = new JSONArray();
//        for (int i = 0; i < existingPatients.length(); i++) {
//            boolean seen = false;
//            for (int j = i+1; j < existingPatients.length(); j++) {
//                if (existingPatients.optJSONObject(i).optInt("PatientID") == (existingPatients.optJSONObject(j).optInt("PatientID"))){
//                    seen = true;
//                }
//            }
//            if (!seen && existingPatients.optJSONObject(i).length()==6) {temp.put(existingPatients.optJSONObject(i));}
//        }
//        existingPatients = temp;
//        writeToFile("patient_registry.txt", existingPatients.toString());

        int numDiabetic = 0;
        int numAtRisk = 0;
        int numNotAtRisk = 0;
        int numUnknown = 0;
        Log.d("Pootie","current patient database:"+existingPatients);
        for (int i = 0; i < existingPatients.length(); i++) {
            String riskCategory = existingPatients.optJSONObject(i).optString("RiskCategory");
            if (riskCategory.equals("diabetic")) {
                numDiabetic += 1;
            } else if (riskCategory.equals("at_risk")) {
                numAtRisk += 1;
            } else if (riskCategory.equals("no_risk")) {
                numNotAtRisk += 1;
            } else if (riskCategory.equals("unknown_risk")) {
                numUnknown += 1;
            }
        }
        String patientSummary = "Diabetic patients:"+numDiabetic+"\nPatients at risk:"+numAtRisk+
                "\nHealthy patients:"+numNotAtRisk+"\nPatients with unknown risk:"+numUnknown+
                "\nTotal patients:"+(numAtRisk+numDiabetic+numNotAtRisk+numUnknown);
        patient_number_view.setText(patientSummary);


        Log.d("Pootie","Calculating user information");
        JSONArray existingUsers = new JSONArray();               // calculate user information
        try {
            existingUsers = new JSONArray(readFromFile("user_accounts.txt"));
        } catch (JSONException e) {
            Log.e("convert", e.getMessage());
        }

        int numHealthWorker = 0;
        int numDoctor = 0;
        int numAdmin = 0;
        Log.d("Pootie","current user database:"+existingUsers);
        for (int i = 0; i < existingUsers.length(); i++){
            String userType = existingUsers.optJSONObject(i).optString("UserType");
            if (userType.equals("health_worker")) {
                numHealthWorker += 1;
            } else if (userType.equals("doctor")) {
                numDoctor += 1;
            } else if (userType.equals("admin")) {
                numAdmin += 1;
            }
        }
        String userSummary = "Health workers:"+numHealthWorker+"\nDoctors:"+numDoctor+
                "\nAdmins:"+numAdmin+"\nTotal users:"+(numAdmin+numDoctor+numHealthWorker);
        user_number_view.setText(userSummary);
    }

    /** Called when the user taps the log out button */
    public void logOut(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
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
