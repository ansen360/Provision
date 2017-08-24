package com.android.provision;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by pansen on 12/31/16.
 */

public class AlarmReceiver extends BroadcastReceiver {

    private static final String SEND = "com.homecare.warranty";

    @Override
    public void onReceive(Context context, Intent intent) {

        context.stopService(new Intent(context, ActivateService.class));
        Intent send = new Intent(SEND);
        context.sendBroadcast(send);
        Log.d("ansen", "AlarmReceiver");


    }
}