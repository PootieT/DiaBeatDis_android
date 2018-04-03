package com.example.peter.diabeatdis_android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.Console;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.logging.ConsoleHandler;

public class UserRegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);
    }

    /** Called when the user taps the back button */
    public void backToMainActivity(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /** Called when the user taps the enter button, it then reads in user_account.txt internal
     * storage file, adds the user input information and stores away all the user_information in
     * JSONarray format*/
    public void registerNewUser(View view) {
        String FILENAME = "user_accounts.txt";
        EditText editText1 = findViewById(R.id.editText_user_reg_ID);     // collect user input id, pass word, and phone number
        String userID = editText1.getText().toString();
        EditText editText2 = findViewById(R.id.editText_user_reg_password);
        String password = editText2.getText().toString();
        EditText editText3 = findViewById(R.id.editText_user_reg_phone);
        String phone = editText3.getText().toString();
        RadioButton box1 = findViewById(R.id.checkBox_user_reg_health_worker); // see what checkbox did user check off
        Boolean ifHealthWorker = box1.isChecked();
        RadioButton box2 = findViewById(R.id.checkBox_user_reg_doctor);
        Boolean ifDoctor = box2.isChecked();
        RadioButton box3 = findViewById(R.id.checkBox_user_reg_admin);
        Boolean ifAdmin = box3.isChecked();
        String userType = ifHealthWorker? "health_worker": ifDoctor? "doctor": ifAdmin? "admin": "health_worker"; // if none selected, default to health worker
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        Boolean canHealthWorkerRegister = sharedPref.getBoolean(getString(R.string.pref_can_health_worker_register), true);
        Log.d("pootie", "health worker can register?" + canHealthWorkerRegister);
        TextView textView = findViewById(R.id.textView_user_reg_warning);


        JSONObject userData = new JSONObject();                           // combine and convert them into JSON data format
        try {
            userData = new JSONObject("{\"UserID\":"+userID+",\"Password\":"+password+",\"MobilePhone\":" + phone + ",\"UserType\":"+userType+ "}");
        } catch (JSONException e) {
            userData = new JSONObject();
        }
        JSONArray existingUsers = new JSONArray();                        // read in existing JSON file for user database
        try {
            existingUsers = new JSONArray(readFromFile(FILENAME));
        } catch (JSONException e) {
            Log.e("convert",e.getMessage());
        }

        // checking if there is existing user ID
        boolean existID = false;
        for (int i = 0; i<existingUsers.length(); i++){
            if (existingUsers.optJSONObject(i).optString("UserID").equals(userID)){
                existID = true;
                textView.setText("There is an existing account with the same user ID!");
            }
        }
        if (!existID) {
            textView.setText("Checking registration permission and adding to database...");
        }

        if (!(ifHealthWorker && !canHealthWorkerRegister) && !existID) {              // as long as person is not register for health worker and register setting allow health worker
            existingUsers.put(userData);                                      // combine the old user data with new user data
            writeToFile(FILENAME, existingUsers.toString());                  // save all the user data away
            String dataOut = readFromFile(FILENAME);
            Log.d("success: ", dataOut);
            System.out.print(dataOut);

            if (userType == "health_worker") {
                Intent intent = new Intent(this, HealthWorkerMainActivity.class);
                startActivity(intent);
            } else if (userType == "doctor") {
                Intent intent = new Intent(this, DoctorMainActivity.class);
                startActivity(intent);
            } else if (userType == "admin") {
                Intent intent = new Intent(this, AdminMainActivity.class);
                startActivity(intent);
            }
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
