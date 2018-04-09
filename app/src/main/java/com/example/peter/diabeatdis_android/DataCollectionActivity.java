package com.example.peter.diabeatdis_android;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Process;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.math.MathContext;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static java.lang.Math.abs;
import static java.lang.Math.max;

public class DataCollectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_collection);
        Log.d("Pootie", "the caller id is " + getIntent().getStringExtra("caller"));
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

    /** display the voltage inputted into the tablet with click of button */
    public void showVoltage(View view) {
        // do something to read the voltage
        readVoltage();
    }

    /** this function converts one voltage reading into blood glucose level. This is a dummy
     * function because we technically need multiple wavelength */
    private double convertOneVoltage(double minVoltage, double maxVoltage) {
        return 0.5047 + 0.0574*maxVoltage + 0.0260*minVoltage;
    }

    /** this function reads in the voltage signal from the audio jack and display the data as a
     * line graph over time, it also displays the final maxmimum voltage value on the tablet that
     * will be recorded when recordVoltage method is hit*/
    final int SAMPLE_RATE = 8000;// there are some results! i don't know what
//    final int SAMPLE_RATE = 11025; // works! respond to my voice
//    final int SAMPLE_RATE = 22050; // nope doesnt work
//    final int SAMPLE_RATE = 44100; // this actually doesnt work
    boolean mShouldContinue; // Indicates if recording / playback should stop
    private void readVoltage() {

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
                final TextView textView = findViewById(R.id.textView_data_collection_show_voltage);
                final TextView textView2 = findViewById(R.id.textView_data_collection_show_voltage2);
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
                    max = Math.max(max, audioBuffer[i]);
                    min = Math.min(min, audioBuffer[i]);
                }

                final double finalMax = max;
                final double finalMin = min;
                Log.d("Pootie", "For overall, average voltage is" + sum/audioBuffer.length);
                Log.d("Pootie", "For overall, maximum voltage is" + max);
                Log.d("Pootie", "For overall, minimum voltage is" + min);

                // creating a line graph with the data
                GraphView graph = (GraphView) findViewById(R.id.graph_data_collection);
                graph.removeAllSeries();
                LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
                for (int i = offset; i<overallBuffer.length; i++) {
                    series.appendData(new DataPoint(i, overallBuffer[i]),true,numberSamples,true);
                }
                graph.addSeries(series);

                record.stop();
                record.release();

                Log.v("Pootie", String.format("Recording stopped. Samples read: %d", shortsRead));

                // display the max of the data
                textView.post(new Runnable() {
                                  public void run() {
                        textView.setText("The voltage reading max/min is: " + Double.toString(finalMax)+" and "+Double.toString(finalMin));
                                  }
                              });
                BigDecimal bd = new BigDecimal(convertOneVoltage(finalMin,finalMax));
                bd = bd.round(new MathContext(4));
                final double rounded = bd.doubleValue();
                textView2.post(new Runnable() {
                    public void run() {
                        textView2.setText("The input voltage calculated: "+rounded);
                    }
                });
            }
        }).start();
    }
}
