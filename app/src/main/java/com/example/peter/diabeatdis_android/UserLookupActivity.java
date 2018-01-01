package com.example.peter.diabeatdis_android;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
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

public class UserLookupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_lookup);
    }

    /** this function takes in JSON search result and create the TableRow objects within the
     * TableLayout in the scrollable search result interface*/
    private void createTable(JSONArray searchResult) {
        //create table
        TableLayout stk = (TableLayout) findViewById(R.id.TableLayout_user_lookup_table);
        TableRow row0 = new TableRow(this);
        TextView tv0 = new TextView(this);
        tv0.setText(" User ID ");
        tv0.setTextColor(Color.WHITE);
        row0.addView(tv0);
        TextView tv1 = new TextView(this);
        tv1.setText(" User Type ");
        tv1.setTextColor(Color.WHITE);
        row0.addView(tv1);
        TextView tv2 = new TextView(this);
        tv2.setText(" Training Level ");
        tv2.setTextColor(Color.WHITE);
        row0.addView(tv2);
        TextView tv3 = new TextView(this);
        tv3.setText(" Location ");
        tv3.setTextColor(Color.WHITE);
        row0.addView(tv3);
        stk.addView(row0);
        for (int i = 0; i < searchResult.length(); i++) {
            TableRow tbrow = new TableRow(this);
            TextView t1v = new TextView(this);
            t1v.setText(searchResult.optJSONObject(i).optString("UserID"));
            t1v.setTextColor(Color.WHITE);
            t1v.setGravity(Gravity.CENTER);
            tbrow.addView(t1v);
            TextView t2v = new TextView(this);
            t2v.setText(searchResult.optJSONObject(i).optString("UserType"));
            t2v.setTextColor(Color.WHITE);
            t2v.setGravity(Gravity.CENTER);
            tbrow.addView(t2v);
            TextView t3v = new TextView(this);
            t3v.setText(searchResult.optJSONObject(i).optString("Training"));
            t3v.setTextColor(Color.WHITE);
            t3v.setGravity(Gravity.CENTER);
            tbrow.addView(t3v);
            TextView t4v = new TextView(this);
            t4v.setText(searchResult.optJSONObject(i).optString("Location"));
            t4v.setTextColor(Color.WHITE);
            t4v.setGravity(Gravity.CENTER);
            tbrow.addView(t4v);
            stk.addView(tbrow);
        }
    }

    /** this function queries the result using the search provided by the user, it then calls
     * create table to visualize it*/
    public void searchForUser() {
        // collect interface data
        EditText editText1 = findViewById(R.id.editText_user_lookup_id);     // collect user search id
        String userID = editText1.getText().toString();
        CheckBox Box1 = findViewById(R.id.checkBox_user_lookup_if_health_worker);     // collect user type search criteria
        Boolean ifHealthWorker = Box1.isChecked();
        CheckBox Box2 = findViewById(R.id.checkBox_user_lookup_if_doctor);
        Boolean ifDoctor = Box2.isChecked();
        CheckBox Box3 = findViewById(R.id.checkBox_user_lookup_if_admin);
        Boolean ifAdmin = Box3.isChecked();
        List<String> userTypeList = new ArrayList<String>();
        if (ifHealthWorker){userTypeList.add("health_worker");}
        if (ifDoctor){userTypeList.add("doctor");}
        if (ifAdmin){userTypeList.add("admin");}

        CheckBox Box4 = findViewById(R.id.checkBox_user_lookup_if_proficient);     // collect user training level search criteria
        Boolean ifProficient = Box4.isChecked();
        CheckBox Box5 = findViewById(R.id.checkBox_user_lookup_if_limited);
        Boolean ifLimited = Box5.isChecked();
        CheckBox Box6 = findViewById(R.id.checkBox_user_lookup_if_no_training);
        Boolean ifNoTraining = Box6.isChecked();
        String training = ifProficient?"proficient":ifLimited?"limited":ifNoTraining?"no_training":"";

        AutoCompleteTextView ACText = findViewById(R.id.ACTextView_user_lookup_location);     // collect user search location
        String location = ACText.getText().toString();
        Spinner spinner1 = findViewById(R.id.spinner_user_lookup_user_type);                  // collect conjunction statement
        String withUserType = spinner1.getSelectedItem().toString();
        Spinner spinner2 = findViewById(R.id.spinner_user_lookup_training);
        String withTraining = spinner2.getSelectedItem().toString();
        Spinner spinner3 = findViewById(R.id.spinner_user_lookup_location);
        String withLocation = spinner3.getSelectedItem().toString();

        // read in JSON file
        String FILENAME = "user_accounts.txt";
        JSONArray existingUsers = new JSONArray();                      // read in existing JSON file for user database
        try {
            existingUsers = new JSONArray(readFromFile(FILENAME));
        } catch (JSONException e) {
            Log.e("convert",e.getMessage());
        }


        // create JSON search result
        JSONArray searchResult = new JSONArray();
        if (userID != "") {                                                  // if user entered a search id
            for (int i = 0; i < existingUsers.length(); i++){
                if (existingUsers.optJSONObject(i).optString("UserID").equals(userID)){
                    searchResult.put(existingUsers.optJSONObject(i));
                }
            }
        }
        if (ifHealthWorker||ifDoctor||ifAdmin){                              // if user search about user type
            if (withUserType == "And"){
                JSONArray temp = new JSONArray();
                if (searchResult.length()!=0){
                    for (int i = 0; i < searchResult.length(); i++) {
                        if (userTypeList.contains(searchResult.optJSONObject(i).optString("UserType"))) {
                            temp.put(searchResult.optJSONObject(i));
                        }
                    }
                }
                searchResult = temp;
            } else {
                for (int i = 0; i < existingUsers.length(); i++){
                    if (userTypeList.contains(existingUsers.optJSONObject(i).optString("UserType"))){
                        existingUsers.put(searchResult.optJSONObject(i));
                    }
                }
            }
        }
        if (ifProficient||ifLimited||ifNoTraining){                          // if user search about user training
            if (withUserType == "And"){
                JSONArray temp = new JSONArray();
                if (searchResult.length()!=0) {
                    for (int i = 0; i < searchResult.length(); i++) {
                        if (searchResult.optJSONObject(i).optString("Training").equals(training)) {
                            temp.put(searchResult.optJSONObject(i));
                        }
                    }
                }
                searchResult = temp;
            } else {
                for (int i = 0; i < existingUsers.length(); i++){
                    if (searchResult.optJSONObject(i).optString("Training").equals(training)){
                        existingUsers.put(searchResult.optJSONObject(i));
                    }
                }
            }
        }
        if (location != ""){                                                 // if user search with location
            if (withUserType == "And"){
                JSONArray temp = new JSONArray();
                if (searchResult.length()!=0) {
                    for (int i = 0; i < searchResult.length(); i++) {
                        if (searchResult.optJSONObject(i).optString("Location").equals(location)) {
                            temp.put(searchResult.optJSONObject(i));
                        }
                    }
                }
                searchResult = temp;
            } else {
                for (int i = 0; i < existingUsers.length(); i++){
                    if (searchResult.optJSONObject(i).optString("Location").equals(location)){
                        existingUsers.put(searchResult.optJSONObject(i));
                    }
                }
            }
        }


        // in the end remove duplicate results
        if (searchResult.length()!=0){
            JSONArray temp = new JSONArray();
            for (int i = 0; i < searchResult.length(); i++) {
                for (int j = i+1; j < searchResult.length(); j++) {
                    if (!searchResult.optJSONObject(i).optString("UserID").equals(searchResult.optJSONObject(j).optString("UserID"))){
                        temp.put(searchResult.optJSONObject(i));
                    }
                }
            }
            searchResult = temp;
        }
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
