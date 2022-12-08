package com.example.tvgamelibrary;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.tv.TvContract;
import android.media.tv.TvInputInfo;
import android.media.tv.TvInputManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import java.util.List;

import android.widget.EditText;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class MainActivity extends Activity
{
    private Socket client;
    private PrintWriter printwriter;
    private EditText textField;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void initGameLibrary(View view) {
        sendEvent("init game library");
        switchToHDMI(view);
    }

    public void resetDS4(View view) {
        sendEvent("reset DS4");
    }

    public void sendCustomEvent(View view) {
        if (textField == null)
            textField = (EditText) findViewById(R.id.editText1);

        String event = textField.getText().toString();
        if (!event.isEmpty())
            sendEvent(event);
    }

    private void switchToHDMI(View view) {
        Context context = this.getApplicationContext();
        TvInputManager tim = (TvInputManager) context.getSystemService(Context.TV_INPUT_SERVICE);
        List<TvInputInfo> a = tim.getTvInputList();
        String inputs = "No information was found about the inputs";

        if (!a.isEmpty()) {
            inputs = "";

            for(int i = 0; i<a.size(); i++) {

                TvInputInfo inputInfo = a.get(i);
                String inputLabel = inputInfo.loadLabel(context).toString();
                if (inputLabel.equals("HDMI 1")) {
                    sendEvent("HDMI 1 found!");

                    Uri inputInfoIdUri =
                            TvContract.buildChannelUriForPassthroughInput(inputInfo.getId());

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setData(inputInfoIdUri);
                    context.startActivity(intent);
                    break;
                }

                inputs += inputLabel + ",  ";
            }
        }

        sendEvent(inputs);
    }

    private void sendEvent(String event) {
        new Thread(new ClientThread(event)).start();
    }

    // source: https://www.geeksforgeeks.org/how-to-communicate-with-pc-using-android/
    // the ClientThread class performs
    // the networking operations
    class ClientThread implements Runnable {
        private final String message;

        ClientThread(String message) {
            this.message = message;
        }

        @Override
        public void run() {
            try {
                // the IP and port should be correct to have a connection established
                // Creates a stream socket and connects it to the specified port number on the named host.
                client = new Socket("192.168.1.77", 4444); // connect to server
                printwriter = new PrintWriter(client.getOutputStream(),true);
                printwriter.write(message); // write the message to output stream

                printwriter.flush();
                printwriter.close();

                // closing the connection
                client.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}