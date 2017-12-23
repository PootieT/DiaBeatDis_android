package com.example.peter.diabeatdis_android;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
        JSONObject userData = new JSONObject();                           // combine and convert them into JSON data format
        try {
            userData = new JSONObject("{\"UserID\":"+userID+",\"Password\":"+password+",\"MobilePhone\":" + phone + "}");
        } catch (JSONException e) {
            userData = new JSONObject();
        }
        JSONArray existingUsers = new JSONArray();                        // read in existing JSON file for user database
        try {
            existingUsers = new JSONArray(readFromFile(FILENAME));
        } catch (JSONException e) {
            Log.e("convert",e.getMessage());
        }
        existingUsers.put(userData);                                      // combine the old user data with new user data
        writeToFile(FILENAME, existingUsers.toString());                  // save all the user data away
        String dataOut = readFromFile(FILENAME);
        Log.d("success: ", dataOut);
        System.out.print(dataOut);
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
