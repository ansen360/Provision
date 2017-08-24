package com.android.provision;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;

public class BaseActivity extends Activity {

    public static final String TAG = "OpenActivity";
    protected static final String ACTION_NAVIGATION_BAR_VISIBLE = "action_navigation_bar_visible";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        StatusBarManager statusBarManager = (StatusBarManager) getSystemService(Context.STATUS_BAR_SERVICE);
//        statusBarManager.disable(statusBarManager.DISABLE2_QUICK_SETTINGS);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        return true;
    }
}
