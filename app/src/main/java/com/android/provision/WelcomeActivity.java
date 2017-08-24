package com.android.provision;

import android.app.AlarmManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Locale;

public class WelcomeActivity extends BaseActivity implements View.OnClickListener {

    private static final String[] PLANETS = new String[]{"English", "简体中文", "繁体中文"};
    private static final String[] TIME_ZONE = {"America/Santiago", "Asia/Shanghai", "Asia/Taipei"};
    private String mCurrentStatus = PLANETS[1];
    private int mSelectedIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActionBar().hide();
        setContentView(R.layout.activity_main);
        Button next = (Button) findViewById(R.id.next);
        next.setOnClickListener(this);
        WheelView mWheelView = (WheelView) findViewById(R.id.wheelview);
        mWheelView.setOffset(1);
        mWheelView.setSeletion(1);
        mWheelView.setItems(Arrays.asList(PLANETS));
        mWheelView.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                mCurrentStatus = item;
                mSelectedIndex = selectedIndex - 1;
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (mCurrentStatus) {
            case "English":
                updateLanguage(Locale.US);
                break;
            case "简体中文":
                updateLanguage(Locale.SIMPLIFIED_CHINESE);
                break;
            case "繁体中文":
                updateLanguage(Locale.TRADITIONAL_CHINESE);
                break;
        }

//        startWifiWizard();
        startActivity(new Intent(this, SimActivity.class));
        finish();
        overridePendingTransition(R.anim.in_from_right, R.anim.out_from_left);

    }

    private void startWifiWizard() {
        // Wifi Wizard 1
//        Intent startActivity = new Intent();
//        startActivity.setComponent(new ComponentName("com.android.settings", "com.android.settings.wifi.WifiSetupActivity"));
//        startActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(startActivity);

        // Wifi Wizard 2
        Intent intent = new Intent("android.net.wifi.PICK_WIFI_NETWORK");
        intent.putExtra("extra_prefs_show_button_bar", true);
        intent.putExtra("extra_prefs_set_next_text", "下一步");
        intent.putExtra("extra_prefs_set_back_text", "返回");
        intent.putExtra("wifi_enable_next_on_connect", false);   // 是否打开网络连接检测功能
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public static void updateLanguage(Locale locale) {
//        com.android.internal.app.LocalePicker.updateLocale(locale);

        try {
            Class classActivityManagerNative = Class.forName("android.app.ActivityManagerNative");
            Method getDefault = classActivityManagerNative.getDeclaredMethod("getDefault");
            // IActivityManager iActMag = ActivityManagerNative.getDefault();
            Object objIActivityManager = getDefault.invoke(classActivityManagerNative);

            Class classIActivityManager = Class.forName("android.app.IActivityManager");
            // Configuration config = iActMag.getConfiguration();
            Method getConfiguration = classIActivityManager.getDeclaredMethod("getConfiguration");
            Configuration config = (Configuration) getConfiguration.invoke(objIActivityManager);
            config.locale = locale;
            // 此处需要声明权限:android.permission.CHANGE_CONFIGURATION
            // 会重新调用 onCreate();
            Class[] clzParams = {Configuration.class};
            Method updateConfiguration = classIActivityManager.getDeclaredMethod("updateConfiguration", clzParams);
            // iActMag.updateConfiguration(config);
            updateConfiguration.invoke(objIActivityManager, config);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateTimeZone(String time_zone) {
        AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarm.setTimeZone(time_zone);
    }
}
