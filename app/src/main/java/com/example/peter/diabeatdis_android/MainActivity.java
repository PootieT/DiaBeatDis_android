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

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /** Called when the user taps the new user button, view is the button object */
    public void registerNewUser(View view) {
        Intent intent = new Intent(this, UserRegistrationActivity.class);
        startActivity(intent);
    }

    /** Called when the user taps the enter button, it reads in the user_account.tet JSON file,
     * if finds the user with correct password, it starts the respective activity */
    public void userSignIn(View view) {
        String FILENAME = "user_accounts.txt";
        EditText editText1 = findViewById(R.id.editText_log_in_ID);     // collect user input id, pass word, and phone number
        String userID = editText1.getText().toString();
        EditText editText2 = findViewById(R.id.editText_log_in_password);
        String password = editText2.getText().toString();
        TextView textView = findViewById(R.id.textView_log_in_error);

        JSONArray existingUsers = new JSONArray();                      // read in existing JSON file for user database
        try {
            existingUsers = new JSONArray(readFromFile(FILENAME));
        } catch (JSONException e) {
            Log.e("convert",e.getMessage());
        }

        String userType = "";
        Boolean passwordCorrect = Boolean.TRUE;
        for (int i = 0; i < existingUsers.length(); i++) {              // loop through all user to see if there is exisiting user ID, if yes, check if passowrd is correct
            Log.d("output", existingUsers.optJSONObject(i).optString("UserID").equals(userID)? "True":"False");
            if (existingUsers.optJSONObject(i).optString("UserID").equals(userID)){
                if(existingUsers.optJSONObject(i).optString("Password").equals(password)){
                    userType = existingUsers.optJSONObject(i).optString("UserType");
                } else {
                    passwordCorrect = Boolean.FALSE;                    // if wrong right ID wrong password, break loop and set bool value to false
                    break;
                }
            }
        }
        if (passwordCorrect){
            if (userType.equals("")){
                textView.setText("No Existing user with the ID entered, please register as a new user");   //print no existing user
            } else if (userType.equals("health_worker")){
                System.out.println("going to health worker activity");       //take to healthworker site
            } else if (userType.equals("doctor")){
                System.out.println("going to doctor activity");              //take to doctor site
            } else if (userType.equals("admin")){
                System.out.println("going to admin activity");               //take to admin site
            }
        } else {
            textView.setText("Please try again with the correct password!");                               // ehhhh, wrong password
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
