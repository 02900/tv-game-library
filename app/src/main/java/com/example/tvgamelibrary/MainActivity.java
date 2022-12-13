package com.example.tvgamelibrary;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.tv.TvContract;
import android.media.tv.TvInputInfo;
import android.media.tv.TvInputManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;

import java.util.List;

public class MainActivity extends Activity
{
    private EditText textField;
    private static String ip = "192.168.1.77";
    private static String broadcast = "192.168.1.255";
    private static String mac = "F8:75:A4:7E:23:2A";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void WakeOnLAN(View view) {
        sendEvent("Wake On LAN");
        new Thread(new WakeOnLANThread(broadcast, mac)).start();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                initGameLibrary(view);
            }
        }, 1000);
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
                    // sendEvent("HDMI 1 found!");

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

        // sendEvent(inputs);
    }

    private void sendEvent(String event) {
        new Thread(new ClientThread(ip, event)).start();
    }
}
