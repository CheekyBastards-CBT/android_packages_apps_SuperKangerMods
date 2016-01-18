/*
 * Copyright (C) 2016 The VRToxin Project
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

package com.android.vrtoxin;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.support.annotation.NonNull;

public class NotificationFragment extends PreferenceFragment implements
         OnPreferenceChangeListener {

    public NotificationFragment(){}

    public static final String KEY_NOTIFICATION_ACTIVITY_PACKAGE_NAME = "com.android.settings";
    public static final String KEY_NOTIF_BATTERY_LIGHT_CLASS_NAME = "com.android.settings.Settings$BatteryLightSettingsActivity";
    public static final String KEY_NOTIF_LIGHT_CLASS_NAME = "com.android.settings.Settings$NotificationLightSettingsActivity";
    public static final String KEY_NOTIF_MANAGER_CLASS_NAME = "com.android.settings.Settings$NotificationManagerSettingsActivity";
    public static final String KEY_NOTIF_HEADS_UP_CLASS_NAME = "com.android.settings.Settings$HeadsUpSettingsActivity";

    private static final String NOTIF_BREATHING = "breathing_notifications";
    private static final String NOTIF_COLOR = "notification_colors";
    private static final String KEY_NOTIFICATION = "notification";
    private static final String NOTIF_BATTERY_LIGHT = "battery_light";
    private static final String NOTIF_LIGHT = "notification_light";
    private static final String NOTIF_MANAGER = "notification_manager";
    private static final String KEY_CAMERA_SOUNDS = "camera_sounds";
    private static final String PROP_CAMERA_SOUND = "persist.sys.camera-sound";
    private static final String NOTIF_HEADS_UP = "heads_up_notif";

    private Preference mBreathing;
    private Preference mColor;
    private Preference mBatteryLight;
    private Preference mNotifLight;
    private Preference mNotifManager;
    private SwitchPreference mCameraSounds;
    private Preference mHeadsUp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.notification_fragment);

        final PreferenceCategory notification = (PreferenceCategory)
                findPreference(KEY_NOTIFICATION);
        initPulse(notification);

        mBreathing = (Preference)findPreference(NOTIF_BREATHING);
        mColor = (Preference)findPreference(NOTIF_COLOR);
        mBatteryLight = (Preference)findPreference(NOTIF_BATTERY_LIGHT);
        mNotifLight = (Preference)findPreference(NOTIF_LIGHT);
        mNotifManager = (Preference)findPreference(NOTIF_MANAGER);
        mHeadsUp = (Preference)findPreference(NOTIF_HEADS_UP);

        mCameraSounds = (SwitchPreference) findPreference(KEY_CAMERA_SOUNDS);
        mCameraSounds.setChecked(SystemProperties.getBoolean(PROP_CAMERA_SOUND, true));
        mCameraSounds.setOnPreferenceChangeListener(this);
    }

    private void initPulse(PreferenceCategory parent) {
        if (!getResources().getBoolean(
                com.android.internal.R.bool.config_intrusiveNotificationLed)) {
            parent.removePreference(parent.findPreference("notification_light"));
        }
        if (!getResources().getBoolean(
                com.android.internal.R.bool.config_intrusiveBatteryLed)
                || UserHandle.myUserId() != UserHandle.USER_OWNER) {
            parent.removePreference(parent.findPreference("battery_light"));
        }
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen prefScreen, @NonNull Preference pref) {
        if (pref == mBreathing) {
            ((VRToxinActivity)getActivity()).displaySubFrag(getString(R.string.breathing_notifications_frag_title));

            return true;
        }

        if (pref == mColor) {
            ((VRToxinActivity)getActivity()).displaySubFrag(getString(R.string.notification_color_frag_title));

            return true;
        }

        if (pref == mBatteryLight) {
            Intent action = new Intent(Intent.ACTION_MAIN);
            ComponentName cn = new ComponentName(KEY_NOTIFICATION_ACTIVITY_PACKAGE_NAME, KEY_NOTIF_BATTERY_LIGHT_CLASS_NAME);
            action.setComponent(cn);
            startActivity(action);

            return true;
        }

        if (pref == mNotifLight) {
            Intent action = new Intent(Intent.ACTION_MAIN);
            ComponentName cn = new ComponentName(KEY_NOTIFICATION_ACTIVITY_PACKAGE_NAME, KEY_NOTIF_LIGHT_CLASS_NAME);
            action.setComponent(cn);
            startActivity(action);

            return true;
        }

        if (pref == mNotifManager) {
            Intent action = new Intent(Intent.ACTION_MAIN);
            ComponentName cn = new ComponentName(KEY_NOTIFICATION_ACTIVITY_PACKAGE_NAME, KEY_NOTIF_MANAGER_CLASS_NAME);
            action.setComponent(cn);
            startActivity(action);

            return true;
        }

        if (pref == mHeadsUp) {
            Intent action = new Intent(Intent.ACTION_MAIN);
            ComponentName cn = new ComponentName(KEY_NOTIFICATION_ACTIVITY_PACKAGE_NAME, KEY_NOTIF_HEADS_UP_CLASS_NAME);
            action.setComponent(cn);
            startActivity(action);

            return true;
        }

        return false;
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        final String key = preference.getKey();
        if (KEY_CAMERA_SOUNDS.equals(key)) {
           if ((Boolean) objValue) {
               SystemProperties.set(PROP_CAMERA_SOUND, "1");
           } else {
               SystemProperties.set(PROP_CAMERA_SOUND, "0");
           }
        }
        return true;
    }
}
