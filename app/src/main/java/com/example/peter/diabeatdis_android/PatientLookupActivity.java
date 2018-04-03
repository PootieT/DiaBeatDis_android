package com.example.peter.diabeatdis_android;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class PatientLookupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_lookup);
        TableLayout stk = findViewById(R.id.TableLayout_patient_lookup_table);

        //create table
        TableRow row0 = new TableRow(this);
        TextView tv0 = new TextView(this);
        tv0.setText(" Patient ID ");
        tv0.setTextColor(Color.BLACK);
        row0.addView(tv0);
        TextView tv1 = new TextView(this);
        tv1.setText(" Patient Name ");
        tv1.setTextColor(Color.BLACK);
        row0.addView(tv1);
        TextView tv2 = new TextView(this);
        tv2.setText(" Age ");
        tv2.setTextColor(Color.BLACK);
        row0.addView(tv2);
        TextView tv3 = new TextView(this);
        tv3.setText(" Sex ");
        tv3.setTextColor(Color.BLACK);
        row0.addView(tv3);
        TextView tv4 = new TextView(this);
        tv4.setText(" Location ");
        tv4.setTextColor(Color.BLACK);
        row0.addView(tv4);
        TextView tv5 = new TextView(this);
        tv5.setText(" Risk Category ");
        tv5.setTextColor(Color.BLACK);
        row0.addView(tv5);
        TextView tv6 = new TextView(this);
        tv6.setText(" Last Tested ");
        tv6.setTextColor(Color.BLACK);
        row0.addView(tv6);
        TextView tv7 = new TextView(this);
        tv7.setText(" Contact Info ");
        tv7.setTextColor(Color.BLACK);
        row0.addView(tv7);
        stk.addView(row0);
    }

    /** Called when the user taps the log out button */
    public void logOut(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /** upon pressing back button, go back to previous activity */
    public void mainMenu(View view) {
        String caller = getIntent().getStringExtra("caller");
        Log.d("Pootie", "the caller class is " + caller);
        Class callerClass;
        try {
            callerClass = Class.forName(caller);
            Intent intent = new Intent(this, callerClass);
            startActivity(intent);
        } catch (Exception e){
            Log.e(e.getMessage(),"cannot get caller id");
        }
    }

    /** helper function to erase search result at beginning of each search */
    private void cleanTable(TableLayout table) {
        try {
            int childCount = table.getChildCount();
            Log.d("Pootie","childcount before clean table: " + childCount);
            // Remove all rows except the first one
            if (childCount > 1) {
                table.removeViews(5, childCount - 5);
            }
            Log.d("Pootie","childcount after clean table: " + table.getChildCount());
        } catch (Exception e) {
            Log.e("cleaning up table",e.getMessage());
        }
    }

    /** this function takes in JSON search result and create the TableRow objects within the
     * TableLayout in the scrollable search result interface*/
    private void createTable(JSONArray searchResult) {
        TableLayout stk = findViewById(R.id.TableLayout_patient_lookup_table);
        cleanTable(stk);

        //create table
        Log.d("Pootie", "creating individual rows of table");
        for (int i = 0; i < searchResult.length(); i++) {
            Log.d("Pootie", "creating row number "+ i);
            TableRow tbrow = new TableRow(this);
            TextView t1v = new TextView(this);
            t1v.setText(searchResult.optJSONObject(i).optString("PatientID"));
            t1v.setTextColor(Color.BLACK);
            t1v.setGravity(Gravity.CENTER);
            tbrow.addView(t1v);
            TextView t2v = new TextView(this);
            t2v.setText(searchResult.optJSONObject(i).optString("Name"));
            t2v.setTextColor(Color.BLACK);
            t2v.setGravity(Gravity.CENTER);
            tbrow.addView(t2v);
            TextView t3v = new TextView(this);
            t3v.setText(searchResult.optJSONObject(i).optString("Age"));
            t3v.setTextColor(Color.BLACK);
            t3v.setGravity(Gravity.CENTER);
            tbrow.addView(t3v);
            TextView t4v = new TextView(this);
            t4v.setText(searchResult.optJSONObject(i).optString("Sex"));
            t4v.setTextColor(Color.BLACK);
            t4v.setGravity(Gravity.CENTER);
            tbrow.addView(t4v);
            TextView t5v = new TextView(this);
            t5v.setText(searchResult.optJSONObject(i).optString("Location"));
            t5v.setTextColor(Color.BLACK);
            t5v.setGravity(Gravity.CENTER);
            tbrow.addView(t5v);
            TextView t6v = new TextView(this);
            t6v.setText(searchResult.optJSONObject(i).optString("RiskCategory"));
            t6v.setTextColor(Color.BLACK);
            t6v.setGravity(Gravity.CENTER);
            tbrow.addView(t6v);
            TextView t7v = new TextView(this);
            t7v.setText(searchResult.optJSONObject(i).optString("LastTested"));
            t7v.setTextColor(Color.BLACK);
            t7v.setGravity(Gravity.CENTER);
            tbrow.addView(t7v);
            TextView t8v = new TextView(this);
            t8v.setText(searchResult.optJSONObject(i).optString("Mobile"));
            t8v.setTextColor(Color.BLACK);
            t8v.setGravity(Gravity.CENTER);
            tbrow.addView(t8v);
            stk.addView(tbrow);
        }
    }

    /** this function queries the result using the search provided by the user, it then calls
     * create table to visualize it*/
    public void searchForPatients(View view) {

        // read in JSON file
        String FILENAME = "patient_registry.txt";
        JSONArray existingUsers = new JSONArray();                      // read in existing JSON file for user database
        try {
            existingUsers = new JSONArray(readFromFile(FILENAME));
        } catch (JSONException e) {
            Log.e("convert",e.getMessage());
        }

        // find max age value
        Integer maxAgeLimit = 0;
        for (int i = 0; i < existingUsers.length(); i++){
            maxAgeLimit = Math.max(existingUsers.optJSONObject(i).optInt("Age"), maxAgeLimit);
        }

        // collect interface data
        EditText editText1 = findViewById(R.id.editText_patient_lookup_id);     // collect user search id
        String patientID = editText1.getText().toString();
        CheckBox Box1 = findViewById(R.id.checkBox_patient_lookup_if_diabetic);     // collect patient risk category
        Boolean ifDiabetic = Box1.isChecked();
        CheckBox Box2 = findViewById(R.id.checkBox_patient_lookup_if_at_risk);
        Boolean ifAtRisk = Box2.isChecked();
        CheckBox Box3 = findViewById(R.id.checkBox_patient_lookup_if_not_at_risk);
        Boolean ifNotAtRisk = Box3.isChecked();
        CheckBox Box4 = findViewById(R.id.checkBox_patient_lookup_if_unknown);
        Boolean ifUnknown = Box4.isChecked();
        List<String> patientRiskList = new ArrayList<String>();
        if (ifDiabetic){patientRiskList.add("diabetic");}
        if (ifAtRisk){patientRiskList.add("at_risk");}
        if (ifNotAtRisk){patientRiskList.add("not_at_risk");}
        if (ifUnknown){patientRiskList.add("unknown");}

        CheckBox Box5 = findViewById(R.id.checkBox_patient_lookup_if_male);    // collect patient sex
        Boolean ifMale = Box5.isChecked();
        CheckBox Box6 = findViewById(R.id.checkBox_patient_lookup_if_female);
        Boolean ifFemale = Box6.isChecked();
        String sex = ifMale?"M":ifFemale?"F":"";

        AutoCompleteTextView ACText = findViewById(R.id.ACTextView_patient_lookup_location);     // collect user search location
        String location = ACText.getText().toString();

        TextView text1 = findViewById(R.id.editText_patient_lookup_max_age);                  // collect age range search information
        String max = text1.getText().toString();
        TextView text2 = findViewById(R.id.editText_patient_lookup_min_age);                  // collect age range search information
        String min = text2.getText().toString();
        Integer maxAge = max.equals("")?0:Integer.parseInt(max);
        Integer minAge = min.equals("")?0:Integer.parseInt(min);

        // create JSON search result
        JSONArray searchResult = new JSONArray();
        Boolean initialSearch = true;
        if (!patientID.equals("")) {                                                  // if user entered a search id
            for (int i = 0; i < existingUsers.length(); i++){
                if (existingUsers.optJSONObject(i).optString("PatientID").equals(patientID)){
                    searchResult.put(existingUsers.optJSONObject(i));
                }
            }
            initialSearch = false;
        }
        if (ifDiabetic||ifAtRisk||ifNotAtRisk||ifUnknown){                // if user search about patient risk category
            Log.d("Pootie", "checking patient risk category");
            JSONArray temp = new JSONArray();
            if (searchResult.length()!=0 && !initialSearch){              // if this is not the first search category used
                for (int i = 0; i < searchResult.length(); i++) {
                    if (patientRiskList.contains(searchResult.optJSONObject(i).optString("RiskCategory"))) {
                        temp.put(searchResult.optJSONObject(i));
                    }
                }
            } else {
                for (int i = 0; i < existingUsers.length(); i++) {        //if this is the first search category used 11
                    if (patientRiskList.contains(existingUsers.optJSONObject(i).optString("RiskCategory"))) {
                        temp.put(existingUsers.optJSONObject(i));
                    }
                }
            }
            searchResult = temp;
            initialSearch = false;
        }
        if (ifMale||ifFemale){                                            // if user search about patient sex
            Log.d("Pootie", "checking sex");
            JSONArray temp = new JSONArray();
            if (!initialSearch) {
                for (int i = 0; i < searchResult.length(); i++) {
                    if (searchResult.optJSONObject(i).optString("Sex").equals(sex)) {
                        temp.put(searchResult.optJSONObject(i));
                    }
                }
            } else {
                for (int i = 0; i < existingUsers.length(); i++) {
                    if (existingUsers.optJSONObject(i).optString("Sex").equals(sex)) {
                        temp.put(existingUsers.optJSONObject(i));
                    }
                }
            }
            searchResult = temp;
            initialSearch = false;
        }
        if (!location.equals("")){                                                 // if user search with location
            Log.d("Pootie", "checking user location");
            JSONArray temp = new JSONArray();
            if (!initialSearch) {
                for (int i = 0; i < searchResult.length(); i++) {
                    if (searchResult.optJSONObject(i).optString("Location").contains(location)) {
                        temp.put(searchResult.optJSONObject(i));
                    }
                }
            } else {
                for (int i = 0; i < existingUsers.length(); i++) {
                    if (existingUsers.optJSONObject(i).optString("Location").contains(location)) {
                        temp.put(existingUsers.optJSONObject(i));
                    }
                }
            }
            searchResult = temp;
            initialSearch = false;
        }
        if (maxAge!=0 ||minAge != 0){           // if user search with progress
            Log.d("Pootie", "checking user age range");
            Log.d("Pootie", "maxAge:"+maxAge+"minAge"+minAge);
            Log.d("Pootie", "initialSearch:"+initialSearch+"search result:"+searchResult.length());
            JSONArray temp = new JSONArray();
            if (!initialSearch) {
                for (int i = 0; i < searchResult.length(); i++) {
                    if (searchResult.optJSONObject(i).optInt("Age") < maxAge &&
                        searchResult.optJSONObject(i).optInt("Age") > minAge) {
                        temp.put(searchResult.optJSONObject(i));
                    }
                }
            } else {
                for (int i = 0; i < existingUsers.length(); i++) {
                    Log.d("Pootie","record age"+existingUsers.optJSONObject(i).optInt("Age"));
                    if (existingUsers.optJSONObject(i).optInt("Age") < maxAge &&
                            existingUsers.optJSONObject(i).optInt("Age") > minAge) {
                        temp.put(existingUsers.optJSONObject(i));
                    }
                }
            }
            searchResult = temp;
        }


        // in the end remove duplicate results
        if (searchResult.length()!=0){
            Log.d("Pootie", "removing duplicate results:"+searchResult);
            JSONArray temp = new JSONArray();
            for (int i = 0; i < searchResult.length(); i++) {
                boolean seen = false;
                for (int j = i+1; j < searchResult.length(); j++) {
                    if (searchResult.optJSONObject(i).optInt("PatientID") == (searchResult.optJSONObject(j).optInt("PatientID"))){
                        seen = true;
                    }
                }
                if (!seen) {temp.put(searchResult.optJSONObject(i));}
            }
            searchResult = temp;
        }
        Log.d("Pootie","current search result" + searchResult);
        createTable(searchResult);
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
