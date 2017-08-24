package com.android.provision;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Created by pansen on 12/30/16.
 */

public class ActivateService extends Service {

    private static final String TAG = "ansen";
    private static final String SIM = "android.intent.action.SIM_STATE_CHANGED";
    private SIMReceiver mSIMReceiver;
    private TelephonyManager mTelephonyManager;
    private AlarmManager mAlarmManager;
    private boolean isRegist;
    private boolean isAlarm;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        handleAlarmTask();

    }

    private void handleAlarmTask() {
        if (mTelephonyManager.getSimState() == TelephonyManager.SIM_STATE_ABSENT) {
            if (!isRegist) {
                registSIMReceiver();
                isRegist = true;
            }
        } else {
            if (!isAlarm) {
                alarm();
                isAlarm = true;
            }
        }
    }

    private void alarm() {
        Log.d(TAG, "alarm");
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, 0);
        if (mAlarmManager == null) {
            mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        }
        Long a = SystemClock.elapsedRealtime() + 1000 * 60*60*8;
        mAlarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, a, sender);
    }

    private void registSIMReceiver() {
        Log.d(TAG, "registSIMReceiver");
        if (mSIMReceiver == null) {
            mSIMReceiver = new SIMReceiver();
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SIM);
        registerReceiver(mSIMReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mSIMReceiver!=null){
            unregisterReceiver(mSIMReceiver);
        }
    }

    private class SIMReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (SIM.equals(action)) {
                Log.d(TAG, "SIMReceiver: " + action);
                handleAlarmTask();
            }

        }
    }


}
