package com.example.tvgamelibrary;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.tv.TvContract;
import android.media.tv.TvInputInfo;
import android.media.tv.TvInputManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.List;

public class MainActivity extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /** Called when the user touches the button */
    public void switchToHDMI(View view) {
        Context context = view.getContext();
        TvInputManager tim = (TvInputManager) context.getSystemService(Context.TV_INPUT_SERVICE);
        List<TvInputInfo> a = tim.getTvInputList();
        String msg = "Not info about TV Input";

        if (!a.isEmpty()) {
            for(int i = 0; i<a.size(); i++) {

                TvInputInfo item = a.get(i);
                if (item.loadLabel(context).toString() == "HDMI 1") {

                    Uri inputInfoIdUri =
                            TvContract.buildChannelUriForPassthroughInput(item.getId());

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setData(inputInfoIdUri);
                    context.startActivity(intent);

                    break;
                }

                msg += "<${this.context?.let { item.loadLabel(it).toString() }}> ,";
            }
        }

        Log.d("Hey brou, it works!", msg);
    }
}