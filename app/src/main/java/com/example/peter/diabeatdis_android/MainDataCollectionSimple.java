package com.example.peter.diabeatdis_android;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Process;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import pl.droidsonroids.gif.GifImageView;
import pl.droidsonroids.gif.GifTextView;

import static java.lang.Math.abs;
import static java.lang.Math.max;

public class MainDataCollectionSimple extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_data_collection_simple);

        // hide second and third check box, dim second and third gif
        GifImageView gif2 = findViewById(R.id.gifTextView_data_collection_step2);
        GifImageView gif3 = findViewById(R.id.gifTextView_data_collection_step3);
        gif2.setAlpha((float)0.05);
        gif3.setAlpha((float)0.05);

        Button step2 = findViewById(R.id.button_data_collection_simple_step2);
        Button step3 = findViewById(R.id.button_data_collection_simple_step3);
        step2.setClickable(false);
        step3.setClickable(false);
    }

    /** return to last activity when pressed back */
    public void goBack(View view) {
        super.onBackPressed();
    }

    /** Called when the user taps the log out button */
    public void logOut(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
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

    /** Called when the user taps the message to doctor button */
    public void messageDoctor(View view) {
        Intent intent = new Intent(this, MessageDoctorActivity.class);
        intent.putExtra("caller", getIntent().getStringExtra("caller"));
        startActivity(intent);
    }

    /** Called when the user taps the change patient button */
    public void changePatient(View view) {
        Intent intent = new Intent(this, PatientSelectorActivity.class);
        intent.putExtra("caller", getIntent().getStringExtra("caller"));
        intent.putExtra("purpose", "recordData");
        startActivity(intent);
    }

    /** Called when the user check the first step gif */
    public void firstStepChecked(View view) {
        Button step1 = findViewById(R.id.button_data_collection_simple_step1);
        Button step2 = findViewById(R.id.button_data_collection_simple_step2);
        GifImageView gif1 = findViewById(R.id.gifTextView_data_collection_step1);
        GifImageView gif2 = findViewById(R.id.gifTextView_data_collection_step2);

        gif2.setAlpha((float)1.0);
        step2.setClickable(true);
        step1.setClickable(false);
        step1.setBackgroundColor(Color.rgb(10,255,10));
        step1.setText("Done!");
    }

    /** Called when the user check the second step gif */
    public void secondStepChecked(View view) {
        Button step3 = findViewById(R.id.button_data_collection_simple_step3);
        Button step2 = findViewById(R.id.button_data_collection_simple_step2);
        GifImageView gif2 = findViewById(R.id.gifTextView_data_collection_step2);
        GifImageView gif3 = findViewById(R.id.gifTextView_data_collection_step3);

        gif3.setAlpha((float)1.0);
        step3.setClickable(true);
        step2.setClickable(false);
        step2.setBackgroundColor(Color.rgb(10,255,10));
        step2.setText("Done!");
    }

    /** Called when the user check the second step gif */
    public void thirdStepChecked(View view) {
        Button step3 = findViewById(R.id.button_data_collection_simple_step3);
        GifImageView gif3 = findViewById(R.id.gifTextView_data_collection_step3);

        step3.setClickable(false);
        step3.setBackgroundColor(Color.rgb(10,255,10));
        step3.setText("Done!");
    }

    /** display the voltage inputted into the tablet with click of button */
    public void showVoltage(View view) {
        // do something to read the voltage
        Button step3 = findViewById(R.id.button_data_collection_simple_step3);
        Log.d("Pootie","step3 get text = "+step3.getText());
        if (step3.getText().equals("Done!")){
            readVoltage(view);
        }
    }

    /** This function records the voltage value shown in the textview into the database */
    public void recordVoltage(Double reading) {
        // increment sharedPreference user lookup clicks
        Log.d("Pootie","updating device statistics");
        String MY_PREFS_NAME = "deviceStatistics";
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putInt("recordDataClicks",getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getInt("recordDataClicks", 0)+1);
        Log.d("Pootie","record data clicks:"+getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getInt("recordDataClicks", 0));
        editor.apply();

        String FILENAME = "patient_data.txt";                                 // establish constants
        String patientID = getIntent().getStringExtra("PatientID");
        int EMERGENCY_BLOOD_GLUCOSE = 300;
        Log.d("Pootie", "recording data for patient " + patientID);

//        double reading = recordVoltageFromScreen();                                     // read in the voltage
        Log.d("Pootie", "the read voltage is " + reading);

        JSONArray patientRecord = new JSONArray();                      // read in patient_data file
        try {
            patientRecord = new JSONArray(readFromFile(FILENAME));
        } catch (JSONException e) {
            Log.e("convert", e.getMessage());
            patientRecord = new JSONArray();
        }

        // create new JSONobject based on data collected today
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("America/Houston"));
        Date date = new Date();
        String formattedDate = formatter.format(date);
        Log.d("Pootie", "the date varible is " + formattedDate);

        JSONObject newData = new JSONObject();
        try {
            newData.put("Date", formattedDate).put("BloodGlucose", reading);
        } catch (JSONException e) {
            newData = new JSONObject();
        }

        // find the patient record, if found append to rest, if not, start a new JSON object
        Log.d("Pootie", "Looping through patient record to find the same patient...");
        boolean found = false;
        for (int i = 0; i < patientRecord.length(); i++) {
            if (patientRecord.optJSONObject(i).optString("PatientID").equals(patientID)) {
                patientRecord.optJSONObject(i).optJSONArray("Data").put(newData);
                Log.d("pootie","new patient record: " + patientRecord.toString());
                found = true;
            }
        }
        if (!found) {
            Log.d("Pootie","Patient not found, creating new patient record...");
            JSONObject newPatient = new JSONObject();
            try {
                newPatient.put("PatientID", patientID).put("Data", new JSONArray().put(newData));
                patientRecord.put(newPatient);
                Log.d("pootie","new patient record: " + patientRecord.toString());
            } catch (JSONException e) {
                newPatient = new JSONObject();
            }
        }
        writeToFile(FILENAME, patientRecord.toString());

        RadioButton less = findViewById(R.id.radioButton_data_collection_simple_less_hour);
        RadioButton more = findViewById(R.id.radioButton_data_collection_simple_more_hour);
        TextView message = findViewById(R.id.textView_data_collection_simple_warning2);

        if ((!less.isChecked() && !more.isChecked()) || (less.isChecked() && more.isChecked())) {
            message.setText("Please select (only) one choice\n above about patient's last food intake!");
        } else {
            if (reading < EMERGENCY_BLOOD_GLUCOSE) {
                Intent intent = new Intent(this, DataCollectionAfterActivity.class);
                intent.putExtra("PatientID", patientID)
                      .putExtra("caller", getIntent().getStringExtra("caller"))
                      .putExtra("reading", reading)
                      .putExtra("ifFasting", more.isChecked());
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, DataCollectionEmergencyActivity.class);
                intent.putExtra("PatientID", patientID)
                      .putExtra("caller", getIntent().getStringExtra("caller"))
                      .putExtra("reading", reading)
                      .putExtra("ifFasting", more.isChecked());
                startActivity(intent);
            }
        }
    }

    /** this function converts one voltage reading into blood glucose level. This is a dummy
     * function because we technically need multiple wavelength */
    private double convertOneVoltage(double minVoltage, double maxVoltage) {
        // convert audio input unit into voltage (peak to peak)
        double actualVoltage = 0.5047 + 0.0574*maxVoltage + 0.0260*minVoltage;
        // convert voltage (peak to peak) to glucose level under hexokinase
        double glucoseLevel = max(actualVoltage*-0.4528 + 421.22 , 0.0);
        return glucoseLevel;
    }

    /** this function reads in the voltage signal from the audio jack and display the data as a
     * line graph over time, it also displays the final maxmimum voltage value on the tablet that
     * will be recorded when recordVoltage method is hit*/
    final int SAMPLE_RATE = 8000;// there are some results! i don't know what
    //    final int SAMPLE_RATE = 11025; // works! respond to my voice
//    final int SAMPLE_RATE = 22050; // nope doesnt work
//    final int SAMPLE_RATE = 44100; // this actually doesnt work
    boolean mShouldContinue; // Indicates if recording / playback should stop
    private void readVoltage(View view) {

        final int offset = 6000;
        final int numberSamples = 1000;

        //check for permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M  &&     checkSelfPermission(Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},1);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO);

                // buffer size in bytes
                int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE,
                        AudioFormat.CHANNEL_IN_MONO,         // in mono guarenteed to work on device
                        AudioFormat.ENCODING_PCM_16BIT);

                if (bufferSize == AudioRecord.ERROR || bufferSize == AudioRecord.ERROR_BAD_VALUE) {
                    bufferSize = SAMPLE_RATE * 2;
                }

                short[] audioBuffer = new short[bufferSize / 2];
                short[] overallBuffer = new short[offset + numberSamples];

                AudioRecord record = new AudioRecord(
//                        MediaRecorder.AudioSource.DEFAULT,
                        MediaRecorder.AudioSource.MIC,
                        SAMPLE_RATE,
                        AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT,
                        bufferSize);

                if (record.getState() != AudioRecord.STATE_INITIALIZED) {
                    Log.e("Pootie", "Audio Record can't initialize!");
                    return;
                }
                record.startRecording();

                Log.v("Pootie", "Start recording");
                long shortsRead = 0;
                final TextView textView = findViewById(R.id.textView_data_collection_simple_show_voltage);
                final TextView warningMessage = findViewById(R.id.textView_data_collection_simple_warning);
                mShouldContinue = true;
                int count = 0;
                while (mShouldContinue) {
                    int numberOfShort = record.read(audioBuffer, 0, audioBuffer.length);
                    shortsRead += numberOfShort;

                    for (int i = 0;i<audioBuffer.length && count < offset+numberSamples; i++){
                        overallBuffer[count] = audioBuffer[i];
                        count += 1;
                    }

                    // Do something with the audioBuffer
                    if (abs(shortsRead) > numberSamples + offset){
                        mShouldContinue = false;
                    }
                }

                // overall statistics of the wave
                double sum = 0.0;
                double max = 0.0;
                double min = 0.0;
                for (int i = 0;i<audioBuffer.length; i++){
                    sum += audioBuffer[i];
                    max = max(max, audioBuffer[i]);
                    min = Math.min(min, audioBuffer[i]);
                }

                final double finalMax = max;
                final double finalMin = min;
                Log.d("Pootie", "For overall, average voltage is" + sum/audioBuffer.length);
                Log.d("Pootie", "For overall, maximum voltage is" + max);
                Log.d("Pootie", "For overall, minimum voltage is" + min);

                record.stop();
                record.release();

                Log.v("Pootie", String.format("Recording stopped. Samples read: %d", shortsRead));

                // display the max of the data
                if (convertOneVoltage(finalMin,finalMax) > 700.0) {
                    textView.post(new Runnable() {
                        public void run() {
                            warningMessage.setText("the voltage reading is abnormally high,\nplease make sure there are samples in\nthe capillary tube inside the IR chamber");
                        }
                    });
                } else {
                    textView.post(new Runnable() {
                        public void run() {
                            warningMessage.setText("");
                        }
                    });
                }
                textView.post(new Runnable() {
                    public void run() {
                        textView.setText("The input voltage calculated: "+convertOneVoltage(finalMin,finalMax));
                    }
                });
                recordVoltage(convertOneVoltage(finalMin,finalMax));
            }
        }).start();
    }

    private double recordVoltageFromScreen() {
        TextView textView = findViewById(R.id.textView_data_collection_simple_show_voltage);
        String voltageString =  textView.getText().toString();
        double reading = Double.parseDouble(voltageString.substring(voltageString.indexOf(":")+1,voltageString.length())); // convert to double
        return reading;
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
