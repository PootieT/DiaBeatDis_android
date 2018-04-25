package com.example.peter.diabeatdis_android;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MessageDoctorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_doctor);
        Log.d("Pootie", "the caller id is " + getIntent().getStringExtra("caller"));
    }

    /** upon pressing main menu button, go back to main menu activity */
    public void mainMenu(View view) {
        String caller = getIntent().getStringExtra("caller");
        Class callerClass;
        try {
            callerClass = Class.forName(caller);
            Intent intent = new Intent(this, callerClass);
            intent.putExtra("PatientID", getIntent().getStringExtra("PatientID"))
                    .putExtra("caller", getIntent().getStringExtra("caller"));
            startActivity(intent);
        } catch (Exception e){
            Log.e(e.getMessage(),"cannot get caller id");
        }
    }

    /** return to last activity when pressed back */
    public void goBack(View view) {
        super.onBackPressed();
    }

    /** return to main activity when pressed logout */
    public void logOut(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /** Called when the user taps the any button that have not been implemented yet */
    public void futureWarning(View view) {
        TextView box = findViewById(R.id.textView_message_doc_message);
        box.setText("This feature has not been implemented yet, check it out in our future version!");
    }

    /** return to main activity when sending a message */
    public void sendMessage(View view) {
        TextView message = findViewById(R.id.textView_message_doc_message);
        EditText subjectView = findViewById(R.id.editText_message_doc_subject);
        EditText contentView = findViewById(R.id.editText_message_doc_content);
        String subject = subjectView.getText().toString();
        String content = contentView.getText().toString();

        if (subject.length() == 0 ||content.length() == 0) {
            message.setText("Please include a subject and some message content");
        } else {
            message.setText("Message sent!");
            String caller = getIntent().getStringExtra("caller");
            Class callerClass;
            try {
                callerClass = Class.forName(caller);
                Intent intent = new Intent(this, callerClass);
                intent.putExtra("PatientID", getIntent().getStringExtra("PatientID"))
                        .putExtra("caller", getIntent().getStringExtra("caller"));
                startActivity(intent);
            } catch (Exception e) {
                Log.e(e.getMessage(), "cannot get caller id");
            }
        }
    }
}
