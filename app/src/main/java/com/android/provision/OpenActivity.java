package com.android.provision;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;

public class OpenActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().hide();
        setContentView(R.layout.activity_open);
        Button user = (Button) findViewById(R.id.btn_user);
        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNotify();
                init();
                finish();
                overridePendingTransition(R.anim.in_from_left, R.anim.out_from_right);
            }
        });
        hanldeActivate();

    }

    private void showNotify() {
        Notification.Builder mBuilder = new Notification.Builder(this);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mBuilder.setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getResources().getString(R.string.warranty_titile))
                .setContentText(getResources().getString(R.string.warranty_content))
                .setTicker(getResources().getString(R.string.warranty_titile));
        try {
            Intent resultIntent = new Intent();
            resultIntent.setComponent(new ComponentName("com.qucii.usercenter", "com.qucii.usercenter.WarrantyActivity"));
            resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(pendingIntent);
            mNotificationManager.notify(100, mBuilder.getNotification());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void hanldeActivate() {
        Intent intent = new Intent(this, ActivateService.class);
        startService(intent);
    }

    private void init() {
        // Add a persistent setting to allow other apps to know the device has been provisioned.
        Settings.Global.putInt(getContentResolver(), Settings.Global.DEVICE_PROVISIONED, 1);
//        Settings.Secure.putInt(getContentResolver(), Settings.Secure.USER_SETUP_COMPLETE, 1);

        // remove this activity from the package manager.
        PackageManager pm = getPackageManager();
        ComponentName name = new ComponentName(this, WelcomeActivity.class);
        pm.setComponentEnabledSetting(name, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);

    }
}
