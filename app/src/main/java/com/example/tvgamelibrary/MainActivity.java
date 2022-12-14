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
    // private EditText textField;
    private static String ip = "192.168.1.77";
    private static String broadcast = "192.168.1.255";
    private static String mac = "F8:75:A4:7E:23:2A";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void play(View view) {
        sendEvent("WAKE_ON_LAN");
        new Thread(new WakeOnLANThread(broadcast, mac)).start();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                sendEvent("PLAY");
            }
        }, 1000);
    }

    public void sendResetDS4Event(View view) {
        sendEvent("RESET_DS4");
        switchToHDMI();
    }

    public void sendSleepEvent (View view) {
        sendEvent("SLEEP");
    }

    public void sendRestartEvent (View view) {
        sendEvent("RESTART");
        switchToHDMI();
    }

    /* public void sendCustomEvent(View view) {
        if (textField == null)
            textField = (EditText) findViewById(R.id.editText1);

        String event = textField.getText().toString();
        if (!event.isEmpty()) {
            sendEvent(event);
            switchToHDMI();
        }
    }*/

    private void sendEvent(String event) {
        new Thread(new ClientThread(ip, event)).start();
    }

    private void switchToHDMI() {
        Context context = this.getApplicationContext();
        TvInputManager tvInputManager = (TvInputManager) context.getSystemService(Context.TV_INPUT_SERVICE);
        List<TvInputInfo> tvInputList = tvInputManager.getTvInputList();

        if (!tvInputList.isEmpty()) {
            for(int i = 0; i<tvInputList.size(); i++) {
                TvInputInfo inputInfo = tvInputList.get(i);
                String inputLabel = inputInfo.loadLabel(context).toString();
                if (inputLabel.equals("HDMI 1")) {
                    Uri inputInfoIdUri =
                            TvContract.buildChannelUriForPassthroughInput(inputInfo.getId());

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setData(inputInfoIdUri);
                    context.startActivity(intent);
                    break;
                }
            }
        }
    }
}
