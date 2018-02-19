package com.example.peter.diabeatdis_android;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


import static java.lang.Math.abs;

public class DataCollectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_collection);
    }

    /** Called when the user taps the log out button */
    public void logOut(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /** Called when the user taps the message to doctor button */
    public void messageDoctor(View view) {
        Intent intent = new Intent(this, MessageDoctorActivity.class);
        startActivity(intent);
    }

    /** Called when the user taps the change patient button */
    public void changePatient(View view) {
        Intent intent = new Intent(this, PatientSelectorActivity.class);
        startActivity(intent);
    }

    /** display the voltage inputted into the tablet with click of button */
    public void showVoltage(View view) {
        // do something to read the voltage
        Log.d("voltage","button clicked");
        recordAudio();
    }

    final int SAMPLE_RATE = 8000;// there are some results! i don't know what
//    final int SAMPLE_RATE = 11025; // works! respond to my voice
//    final int SAMPLE_RATE = 22050; // nope doesnt work
//    final int SAMPLE_RATE = 44100; // this actually doesnt work
    boolean mShouldContinue; // Indicates if recording / playback should stop

    void recordAudio() {
        //check for permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M  &&     checkSelfPermission(Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},1);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);

                // buffer size in bytes
                int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE,
                        AudioFormat.CHANNEL_IN_MONO,         // in mono guarenteed to work on device
                        AudioFormat.ENCODING_PCM_16BIT);

                if (bufferSize == AudioRecord.ERROR || bufferSize == AudioRecord.ERROR_BAD_VALUE) {
                    bufferSize = SAMPLE_RATE * 2;
                }

                short[] audioBuffer = new short[bufferSize / 2];
                List<Double> overallBuffer = new ArrayList<>();

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
                TextView textView = findViewById(R.id.textView_data_collection_show_voltage);
                mShouldContinue = true;

                while (mShouldContinue) {
                    int numberOfShort = record.read(audioBuffer, 0, audioBuffer.length);
                    shortsRead += numberOfShort;
//                    for (int i = 0;i<audioBuffer.length; i++) {
//                        overallBuffer = overallBuffer.add(audioBuffer[i]);
//                    }
                    // Do something with the audioBuffer
                    Log.d("Pootie","numberofshort is  "+numberOfShort);
                    for (int i = 0;i<10; i++){
                        Log.d("Pootie","audioBuffer is "+audioBuffer[i]);
                    }
                    double sum = 0.0;
                    double max = 0.0;
                    double min = 0.0;
                    for (int i = 0;i<audioBuffer.length; i++){
                        sum += audioBuffer[i];
                        max = Math.max(max, audioBuffer[i]);
                        min = Math.min(min, audioBuffer[i]);
                    }
                    Log.d("Pootie", "average voltage is" + sum/audioBuffer.length);
                    Log.d("Pootie", "maximum voltage is" + max);
                    Log.d("Pootie", "minimum voltage is" + min);
//                    textView.setText((char)audioBuffer[0]);        // somehow this breaks the loop
                    if (abs(shortsRead) > 10000){
                        mShouldContinue = false;
                    }
                }


                record.stop();
                record.release();

                Log.v("Pootie", String.format("Recording stopped. Samples read: %d", shortsRead));
            }
        }).start();
    }
}
