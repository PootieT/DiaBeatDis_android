package com.example.peter.diabeatdis_android;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class ManageAccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_account);
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
        Intent intent = new Intent(this, AdminMainActivity.class);
        startActivity(intent);
    }

    /** display the voltage inputted into the tablet with click of button */
    public void deleteAccount(View view) {
        final TextView warningView = findViewById(R.id.textView_manage_account_message);
        EditText editText = findViewById(R.id.editText_manage_account_id);
        final String userID = editText.getText().toString();

        String FILENAME = "user_accounts.txt";
        JSONArray existingUsers = new JSONArray();                        // read in existing JSON file for user database
        try {
            existingUsers = new JSONArray(readFromFile(FILENAME));
        } catch (JSONException e) {
            Log.e("convert",e.getMessage());
        }

        boolean userFound = false;
        for (int i=0;i<existingUsers.length();i++){
            if (existingUsers.optJSONObject(i).optString("UserID").equals(userID)){
                userFound = true;
                break;
            }
        }
        if (userFound){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Reseting the system will remove all of patient and \nuser data, are you sure you want to do this?")
                    .setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            String FILENAME = "user_accounts.txt";
                            JSONArray existingUsers = new JSONArray();                        // read in existing JSON file for user database
                            try {
                                existingUsers = new JSONArray(readFromFile(FILENAME));
                            } catch (JSONException e) {
                                Log.e("convert",e.getMessage());
                            }
                            for (int i=0;i<existingUsers.length();i++){
                                if (existingUsers.optJSONObject(i).optString("UserID").equals(userID)){
//                                    existingUsers.remove(i);
//                                    writeToFile(FILENAME, existingUsers.toString());
                                    warningView.setText("This user account has been deleted!");
                                    break;
                                }
                            }
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                            warningView.setText("");
                        }
                    });
            // Create the AlertDialog object and return it
            builder.create();
            builder.show();
        } else {
            warningView.setText("No user of this ID found!");
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
