/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.provision;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.provider.Settings;

import java.util.List;

/**
 * Application that sets the provisioned bit, like SetupWizard does.
 */
public class DefaultActivity extends Activity {
    private static final String ORIGINAL_LAUNCHER_PACKAGENAME = "com.android.launcher3";
    private static final String ORIGINAL_LAUNCHER_CLASSNAME = "com.android.launcher3.Launcher";
    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Add a persistent setting to allow other apps to know the device has been provisioned.
        Settings.Global.putInt(getContentResolver(), Settings.Global.DEVICE_PROVISIONED, 1);
//        Settings.Secure.putInt(getContentResolver(), Settings.Secure.USER_SETUP_COMPLETE, 1);

        // remove this activity from the package manager.
        PackageManager pm = getPackageManager();
        ComponentName name = new ComponentName(this, DefaultActivity.class);

        pm.setComponentEnabledSetting(name, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
        //set Launcher3 as the preferred home activity
        setupDefaultLauncher(pm);
        // terminate the activity.
        finish();
    }

    private void setupDefaultLauncher(PackageManager pm){
        Intent queryIntent = new Intent();
        queryIntent.addCategory(Intent.CATEGORY_HOME);
        queryIntent.setAction(Intent.ACTION_MAIN);

        List<ResolveInfo> homeActivities = pm.queryIntentActivities(queryIntent, 0);
        if(homeActivities == null) {
            return;
        }

        ComponentName defaultLauncher = new ComponentName(ORIGINAL_LAUNCHER_PACKAGENAME,
                ORIGINAL_LAUNCHER_CLASSNAME);
        int activityNum = homeActivities.size();
        ComponentName[] set = new ComponentName[activityNum];
        int defaultMatch = -1;
        for(int i = 0; i < activityNum; i++){
            ResolveInfo info = homeActivities.get(i);
            set[i] = new ComponentName(info.activityInfo.packageName, info.activityInfo.name);
            if(ORIGINAL_LAUNCHER_CLASSNAME.equals(info.activityInfo.name)
                    && ORIGINAL_LAUNCHER_PACKAGENAME.equals(info.activityInfo.packageName)){
                defaultMatch = info.match;
            }
        }

        //if Launcher3 is not found, do not set anything
        if(defaultMatch == -1){
            return;
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_MAIN);
        filter.addCategory(Intent.CATEGORY_HOME);
        filter.addCategory(Intent.CATEGORY_DEFAULT);

        pm.addPreferredActivity(filter, defaultMatch, set, defaultLauncher);
    }
}

