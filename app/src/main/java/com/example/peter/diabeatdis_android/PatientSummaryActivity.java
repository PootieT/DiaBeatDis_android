package com.example.peter.diabeatdis_android;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PatientSummaryActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_summary);
        final String patientID = getIntent().getStringExtra("PatientID");

        createGraph(patientID);     // creating a line graph with the data
        createProfile(patientID);   // show patient information through textview
        Log.d("Pootie", "the caller id is " + getIntent().getStringExtra("caller"));
    }

    /** this function is called when back to patient lookup button is clicked*/
    public void backToPatientLookup(View view) {
        Intent intent = new Intent(this, PatientSelectorActivity.class);
        intent.putExtra("purpose","patientSummary")
              .putExtra("caller","com.example.peter.diabeatdis_android.DoctorMainActivity");
        startActivity(intent);
    }

    /** Called when the user taps the log out button */
    public void logOut(View view) {
        Intent intent = new Intent(this, MainActivity.class);
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

    /** Called when the user taps the enter patient data button */
    public void createProfile(String patientID) {
        String FILENAME = "patient_registry.txt";

        JSONArray patientRecord = new JSONArray();                      // read in patient_data file
        try {
            patientRecord = new JSONArray(readFromFile(FILENAME));
        } catch (JSONException e) {
            Log.e("convert", e.getMessage());
            patientRecord = new JSONArray();
        }
        for (int i = 0; i<patientRecord.length(); i++) {
            if (patientRecord.optJSONObject(i).optString("PatientID").equals(patientID)) {
                Log.d("pootie","the data is: " + patientRecord.optJSONObject(i).toString());
                String name = patientRecord.optJSONObject(i).optString("Name");
                String age = patientRecord.optJSONObject(i).optString("Age");
                String sex = patientRecord.optJSONObject(i).optString("Sex");
                String location = patientRecord.optJSONObject(i).optString("Location");
                String mobile = patientRecord.optJSONObject(i).optString("Mobile");
                String riskCategory = patientRecord.optJSONObject(i).optString("RiskCategory");
                TextView textView = findViewById(R.id.textView_patient_summary_patient_info);
                textView.setText("Name: "+name+"  Age: "+age+"  Sex: "+sex+"  Location: "+location+
                                 "  Mobile Number: "+mobile+"  Risk Category: " + riskCategory);
            }
        }
    }

    /** to create the line graph of patient's information overtime */
    private void createGraph(String patientID) {

        String FILENAME = "patient_data.txt";

        JSONArray patientRecord = new JSONArray();                      // read in patient_data file
        try {
            patientRecord = new JSONArray(readFromFile(FILENAME));
        } catch (JSONException e) {
            Log.e("convert", e.getMessage());
            patientRecord = new JSONArray();
        }

        GraphView graph = (GraphView) findViewById(R.id.graph_patient_summary);
        graph.removeAllSeries();
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
        for (int i = 0; i<patientRecord.length(); i++) {
            if (patientRecord.optJSONObject(i).optString("PatientID").equals(patientID)) {
                JSONArray thisPatient = patientRecord.optJSONObject(i).optJSONArray("Data");
                Date minDate = new Date(thisPatient.optJSONObject(0).optString("Date"));
                Date maxDate = new Date(thisPatient.optJSONObject(thisPatient.length()-1).optString("Date"));
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Log.d("pootie","the max and min dates are " + formatter.format(maxDate) + " and " + formatter.format(minDate));
                Log.d("pootie", "the data for this patient is " + thisPatient.toString() + "number of data point is " + thisPatient.length());
                for (int j = 0; j<thisPatient.length(); j ++) {
                    series.appendData(new DataPoint(new Date(thisPatient.optJSONObject(j).optString("Date")),
                            thisPatient.optJSONObject(j).optDouble("BloodGlucose")),
                            true, 10, true);
                    Log.d("pootie","this data point is " +  thisPatient.optJSONObject(j).optDouble("BloodGlucose"));
                }
                graph.addSeries(series);
                // set date label formatter
                graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(PatientSummaryActivity.this));
                graph.getGridLabelRenderer().setNumHorizontalLabels(5); // only 4 because of the space

                // set manual x bounds to have nice steps
                graph.getViewport().setMinX(minDate.getTime());
                graph.getViewport().setMaxX(maxDate.getTime());
                graph.getViewport().setXAxisBoundsManual(true);
            }

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
