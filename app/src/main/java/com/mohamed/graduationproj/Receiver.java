package com.mohamed.graduationproj;

import android.content.Context;
import android.content.Intent;

import com.parse.ParsePushBroadcastReceiver;

/**
 * Created by KiMoo on 26/06/2016.
 */
public class Receiver extends ParsePushBroadcastReceiver {
    @Override
    public void onPushOpen(Context context, Intent intent) {
        try {
            Intent i = new Intent(context, MainActivity.class);
            i.putExtras(intent.getExtras());
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
