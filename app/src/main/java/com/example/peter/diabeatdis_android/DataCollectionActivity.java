package com.example.peter.diabeatdis_android;

import android.content.DialogInterface;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.logging.Logger;

public class DataCollectionActivity extends AppCompatActivity {

    private Audio audio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_collection);
        audio = new Audio();
    }

    /** display the voltage inputted into the tablet with click of button */
    public void showVoltage(View view) {
        // do something to read the voltage
        Log.d("voltage","button clicked");
        audio.run();
    }

    private static final int counts[] = {
        256, 512, 1024, 2048,
        4096, 8192, 16384, 32768,
        65536, 131072, 262144, 524288
        };

    // Show alert
    void showAlert(int appName, int errorBuffer) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this); // Create an alert dialog builder
        builder.setTitle(appName);                    // Set the title, message and button
        builder.setMessage(errorBuffer);
        builder.setNeutralButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int which) {
                        dialog.dismiss(); // Dismiss dialog
                    }
                });
        AlertDialog dialog = builder.create();        // Create the dialog
        dialog.show();                                // Show it
    }

    protected class Audio implements Runnable
    {
        // Preferences
        protected boolean bright;
        protected boolean single;
        protected boolean trigger;

        protected int input;
        protected int sample;

        // Data
        protected Thread thread;
        protected short data[];
        protected long length;

        // Private data
        private static final int SAMPLES = 524288;
        private static final int FRAMES = 4096;

        private static final int INIT  = 0;
        private static final int FIRST = 1;
        private static final int NEXT  = 2;
        private static final int LAST  = 3;

        private AudioRecord audioRecord;
        private short buffer[];

        // Constructor
        protected Audio()
        {
            buffer = new short[FRAMES];
            data = new short[SAMPLES];
        }

        // Start audio
        protected void start()
        {
            // Start the thread
            thread = new Thread(this, "Audio");
            thread.start();
        }

        // Run
        @Override
        public void run()
        {
            processAudio();
        }

        // Stop
        protected void stop() {
            // Stop and release the audio recorder
            cleanUpAudioRecord();

            Thread t = thread;
            thread = null;

            // Wait for the thread to exit
            while (t != null && t.isAlive())
                Thread.yield();
        }

        // Stop and release the audio recorder
        private void cleanUpAudioRecord() {
            if (audioRecord != null && audioRecord.getState() == AudioRecord.STATE_INITIALIZED){
                try {
                    if (audioRecord.getRecordingState() ==
                            AudioRecord.RECORDSTATE_RECORDING)
                        audioRecord.stop();

                    audioRecord.release();
                } catch (Exception e) {}
            }
        }

        // Process Audio
        protected void processAudio()
        {
            // Assume the output sample rate will work on the input as
            // there isn't an AudioRecord.getNativeInputSampleRate()
            sample = AudioTrack.getNativeOutputSampleRate(AudioManager.STREAM_MUSIC);

            // Get buffer size
            int size = AudioRecord.getMinBufferSize(sample,
                            AudioFormat.CHANNEL_IN_MONO,
                            AudioFormat.ENCODING_PCM_16BIT);
            Log.d("voltage","got buffer size" + size);
            // Give up if it doesn't work
            if (size == AudioRecord.ERROR_BAD_VALUE || size == AudioRecord.ERROR || size <= 0){
                runOnUiThread(new Runnable(){
                    @Override
                    public void run(){
                        showAlert(R.string.app_name, 1);
                    }
                });
                thread = null;
                return;
            }

            // Create the AudioRecord object
            try {audioRecord = new AudioRecord(input, sample,
                                AudioFormat.CHANNEL_IN_MONO,
                                AudioFormat.ENCODING_PCM_16BIT,
                                size);
            } catch (Exception e) {                            // Exception
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showAlert(R.string.app_name, 2);
                    }
                });
                thread = null;
                return;
            }

            // Check audiorecord
            if (audioRecord == null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showAlert(R.string.app_name,2);
                    }
                });

                thread = null;
                return;
            }

            // Check state
            int state = audioRecord.getState();

            if (state != AudioRecord.STATE_INITIALIZED) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showAlert(R.string.app_name,2);
                    }
                });

                audioRecord.release();
                thread = null;
                return;
            }

            // Start recording
            audioRecord.startRecording();
            Log.d("voltage","start audio recording and thread is "+thread);
            int index = 0;
            int count = 0;

            state = INIT;
            short last = 0;

            // Continue until he thread is stopped
            while (thread != null) {
                System.out.print("in main loop");
                size = audioRecord.read(buffer, 0, FRAMES);   // Read a buffer of data

                // Stop the thread if no data or error state
                if (size <= 0) {
                    Log.d("voltage","there is no data or error state");
                    thread = null;
                    break;
                }

                TextView textView = findViewById(R.id.textView_data_collection_show_voltage);
                textView.setText(size);
//                // State machine for sync and copying data to display buffer
//                switch (state) {
//                    // INIT: waiting for sync
//                    case INIT:
//                        index = 0;
//                        if (bright) state++;
//                        else {
//                            if (single && !trigger) break;
//                            float level = -yscale.index * scope.yscale; // Calculate sync level
//                            int dx = 0;                                 // Initialise sync
//                            if (level < 0) {                            // Sync polarity
//                                for (int i = 0; i < size; i++) {
//                                    dx = buffer[i] - last;
//                                    if (dx < 0 && last > level && buffer[i] < level) {
//                                        index = i;
//                                        state++;
//                                        break;
//                                    }
//                                    last = buffer[i];
//                                }
//                            } else {
//                                for (int i = 0; i < size; i++) {
//                                    dx = buffer[i] - last;
//                                    if (dx > 0 && last < level && buffer[i] > level) {
//                                        index = i;
//                                        state++;
//                                        break;
//                                    }
//                                    last = buffer[i];
//                                }
//                            }
//                        }
//                        if (state == INIT) break;                       // No sync, try next time
//                        if (single && trigger) trigger = false;         // Reset trigger
//                    case FIRST:                                       // FIRST: First chunk of data
//                        count = counts[timebase];                     // Update count
//                        length = count;
//                        System.arraycopy(buffer, index, data, 0, size - index); // Copy data
//                        index = size - index;
//                        if (index >= count) state = INIT;     // If done, wait for sync again
//                        else                                  // Else get some more data next time
//                            state++;
//                        break;
//                    case NEXT:                                // NEXT: Subsequent chunks of data
//                        System.arraycopy(buffer, 0, data, index, size); // Copy data
//                        index += size;
//                        if (index >= count) state = INIT;     // Done, wait for sync again
//                        else if (index + size >= count) // Else if last but one chunk, get last chunk next time
//                            state++;
//                        break;
//                    case LAST:                                // LAST: Last chunk of data
//                        System.arraycopy(buffer, 0, data, index, count - index); // Copy data
//                        state = INIT;                         // Wait for sync next time
//                        break;
//                }

//                // Update display
//                scope.postInvalidate();
            }

            // Stop and release the audio recorder
            cleanUpAudioRecord();
        }
    }
}
