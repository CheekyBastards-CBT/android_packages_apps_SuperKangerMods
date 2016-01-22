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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.Vibrator;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.ListPreference;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.provider.Settings.Global;
import android.provider.Settings.System;
import android.util.Log;

import com.android.vrtoxin.preferences.SystemSettingSwitchPreference;

public class NotificationFragment extends PreferenceFragment implements
         OnPreferenceChangeListener {

    public NotificationFragment(){}

    private static final String TAG = "NotificationFragment";

    private FingerprintManager mFingerprintManager;
    private SystemSettingSwitchPreference mFingerprintVib;

    public static final String KEY_NOTIFICATION_ACTIVITY_PACKAGE_NAME = "com.android.settings";
    public static final String KEY_NOTIF_BATTERY_LIGHT_CLASS_NAME = "com.android.settings.Settings$BatteryLightSettingsActivity";
    public static final String KEY_NOTIF_LIGHT_CLASS_NAME = "com.android.settings.Settings$NotificationLightSettingsActivity";
    public static final String KEY_NOTIF_MANAGER_CLASS_NAME = "com.android.settings.Settings$NotificationManagerSettingsActivity";
    public static final String KEY_NOTIF_HEADS_UP_CLASS_NAME = "com.android.settings.Settings$HeadsUpSettingsActivity";

    private static final int DLG_SAFE_HEADSET_VOLUME = 0;

    private static final String NOTIF_BREATHING = "breathing_notifications";
    private static final String NOTIF_COLOR = "notification_colors";
    private static final String KEY_NOTIFICATION = "notification";
    private static final String NOTIF_BATTERY_LIGHT = "battery_light";
    private static final String NOTIF_LIGHT = "notification_light";
    private static final String NOTIF_MANAGER = "notification_manager";
    private static final String KEY_CAMERA_SOUNDS = "camera_sounds";
    private static final String PROP_CAMERA_SOUND = "persist.sys.camera-sound";
    private static final String NOTIF_HEADS_UP = "heads_up_notif";
    private static final String KEY_SAFE_HEADSET_VOLUME = "safe_headset_volume";
    private static final String PREF_LESS_NOTIFICATION_SOUNDS = "less_notification_sounds";
    private static final String KEY_VOLUME_DIALOG_COLOR = "volume_dialog_frag";

    private static final String KEY_POWER_NOTIFICATIONS = "power_notifications";
    private static final String KEY_POWER_NOTIFICATIONS_VIBRATE = "power_notifications_vibrate";
    private static final String KEY_POWER_NOTIFICATIONS_RINGTONE = "power_notifications_ringtone";

    // Request code for power notification ringtone picker
    private static final int REQUEST_CODE_POWER_NOTIFICATIONS_RINGTONE = 1;

    // Used for power notification uri string if set to silent
    private static final String POWER_NOTIFICATIONS_SILENT_URI = "silent";

    private Preference mBreathing;
    private Preference mColor;
    private Preference mBatteryLight;
    private Preference mNotifLight;
    private Preference mNotifManager;
    private SwitchPreference mCameraSounds;
    private Preference mHeadsUp;
    private SwitchPreference mSafeHeadsetVolume;
    private ListPreference mAnnoyingNotifications;
    private Preference mVolumeDialogColor;
    private SwitchPreference mPowerSounds;
    private SwitchPreference mPowerSoundsVibrate;
    private Preference mPowerSoundsRingtone;

    private ContentResolver mResolver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.notification_fragment);
        final PreferenceScreen prefScreen = getPreferenceScreen();
        mResolver = getActivity().getContentResolver();

        final PreferenceCategory notification = (PreferenceCategory)
                findPreference(KEY_NOTIFICATION);
        initPulse(notification);

        mBreathing = (Preference)findPreference(NOTIF_BREATHING);
        mColor = (Preference)findPreference(NOTIF_COLOR);
        mBatteryLight = (Preference)findPreference(NOTIF_BATTERY_LIGHT);
        mNotifLight = (Preference)findPreference(NOTIF_LIGHT);
        mNotifManager = (Preference)findPreference(NOTIF_MANAGER);
        mHeadsUp = (Preference)findPreference(NOTIF_HEADS_UP);
        mVolumeDialogColor = (Preference)findPreference(KEY_VOLUME_DIALOG_COLOR);

        mCameraSounds = (SwitchPreference) findPreference(KEY_CAMERA_SOUNDS);
        mCameraSounds.setChecked(SystemProperties.getBoolean(PROP_CAMERA_SOUND, true));
        mCameraSounds.setOnPreferenceChangeListener(this);

        mSafeHeadsetVolume = (SwitchPreference) findPreference(KEY_SAFE_HEADSET_VOLUME);
        mSafeHeadsetVolume.setChecked(Settings.System.getInt(mResolver,
                Settings.System.SAFE_HEADSET_VOLUME, 1) != 0);
        mSafeHeadsetVolume.setOnPreferenceChangeListener(this);

        mAnnoyingNotifications = (ListPreference) findPreference(PREF_LESS_NOTIFICATION_SOUNDS);
        int notificationThreshold = Settings.System.getInt(mResolver,
                Settings.System.MUTE_ANNOYING_NOTIFICATIONS_THRESHOLD,
                0);
        mAnnoyingNotifications.setValue(Integer.toString(notificationThreshold));
        mAnnoyingNotifications.setOnPreferenceChangeListener(this);

        // power state change notification sounds
        mPowerSounds = (SwitchPreference) findPreference(KEY_POWER_NOTIFICATIONS);
        mPowerSounds.setChecked(Global.getInt(mResolver,
                Global.POWER_NOTIFICATIONS_ENABLED, 0) != 0);
        mPowerSoundsVibrate = (SwitchPreference) findPreference("power_notifications_vibrate");
        mPowerSoundsVibrate.setChecked(Global.getInt(mResolver,
                Global.POWER_NOTIFICATIONS_VIBRATE, 0) != 0);
        Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator == null || !vibrator.hasVibrator()) {
            prefScreen.removePreference(mPowerSoundsVibrate);
        }

        mPowerSoundsRingtone = findPreference(KEY_POWER_NOTIFICATIONS_RINGTONE);
        String currentPowerRingtonePath =
                Global.getString(mResolver, Global.POWER_NOTIFICATIONS_RINGTONE);

        // set to default notification if we don't yet have one
        if (currentPowerRingtonePath == null) {
                currentPowerRingtonePath = System.DEFAULT_NOTIFICATION_URI.toString();
                Global.putString(mResolver,
                        Global.POWER_NOTIFICATIONS_RINGTONE, currentPowerRingtonePath);
        }
        // is it silent ?
        if (currentPowerRingtonePath.equals(POWER_NOTIFICATIONS_SILENT_URI)) {
            mPowerSoundsRingtone.setSummary(
                    getString(R.string.power_notifications_ringtone_silent));
        } else {
            final Ringtone ringtone =
                    RingtoneManager.getRingtone(getActivity(), Uri.parse(currentPowerRingtonePath));
            if (ringtone != null) {
                mPowerSoundsRingtone.setSummary(ringtone.getTitle(getActivity()));
            }
        }

        mFingerprintManager = (FingerprintManager) getActivity().getSystemService(Context.FINGERPRINT_SERVICE);
        mFingerprintVib = (SystemSettingSwitchPreference) prefScreen.findPreference("fingerprint_success_vib");
        if (!mFingerprintManager.isHardwareDetected()){
            prefScreen.removePreference(mFingerprintVib);
        }
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
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mBreathing) {
            ((VRToxinActivity)getActivity()).displaySubFrag(getString(R.string.breathing_notifications_frag_title));

            return true;
        }

        if (preference == mColor) {
            ((VRToxinActivity)getActivity()).displaySubFrag(getString(R.string.notification_color_frag_title));

            return true;
        }

        if (preference == mBatteryLight) {
            Intent action = new Intent(Intent.ACTION_MAIN);
            ComponentName cn = new ComponentName(KEY_NOTIFICATION_ACTIVITY_PACKAGE_NAME, KEY_NOTIF_BATTERY_LIGHT_CLASS_NAME);
            action.setComponent(cn);
            startActivity(action);

            return true;
        }

        if (preference == mNotifLight) {
            Intent action = new Intent(Intent.ACTION_MAIN);
            ComponentName cn = new ComponentName(KEY_NOTIFICATION_ACTIVITY_PACKAGE_NAME, KEY_NOTIF_LIGHT_CLASS_NAME);
            action.setComponent(cn);
            startActivity(action);

            return true;
        }

        if (preference == mNotifManager) {
            Intent action = new Intent(Intent.ACTION_MAIN);
            ComponentName cn = new ComponentName(KEY_NOTIFICATION_ACTIVITY_PACKAGE_NAME, KEY_NOTIF_MANAGER_CLASS_NAME);
            action.setComponent(cn);
            startActivity(action);

            return true;
        }

        if (preference == mHeadsUp) {
            Intent action = new Intent(Intent.ACTION_MAIN);
            ComponentName cn = new ComponentName(KEY_NOTIFICATION_ACTIVITY_PACKAGE_NAME, KEY_NOTIF_HEADS_UP_CLASS_NAME);
            action.setComponent(cn);
            startActivity(action);

            return true;
        }

        if (preference == mVolumeDialogColor) {
            ((VRToxinActivity)getActivity()).displaySubFrag(getString(R.string.volume_dialog_frag_title));

            return true;
        }

        if (preference == mPowerSounds) {
            Global.putInt(mResolver,
                    Global.POWER_NOTIFICATIONS_ENABLED,
                    mPowerSounds.isChecked() ? 1 : 0);

        } else if (preference == mPowerSoundsVibrate) {
            Global.putInt(mResolver,
                    Global.POWER_NOTIFICATIONS_VIBRATE,
                    mPowerSoundsVibrate.isChecked() ? 1 : 0);

        } else if (preference == mPowerSoundsRingtone) {
            launchNotificationSoundPicker(REQUEST_CODE_POWER_NOTIFICATIONS_RINGTONE,
                    Global.getString(mResolver,
                            Global.POWER_NOTIFICATIONS_RINGTONE));
        } else {
            // If we didn't handle it, let preferences handle it.
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }

        return true;
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
        if (KEY_SAFE_HEADSET_VOLUME.equals(key)) {
            if ((Boolean) objValue) {
                Settings.System.putInt(mResolver,
                        Settings.System.SAFE_HEADSET_VOLUME, 1);
            } else {
                showDialogInner(DLG_SAFE_HEADSET_VOLUME);
            }
        }
        if (PREF_LESS_NOTIFICATION_SOUNDS.equals(key)) {
            final int val = Integer.valueOf((String) objValue);
            Settings.System.putInt(mResolver,
                    Settings.System.MUTE_ANNOYING_NOTIFICATIONS_THRESHOLD, val);
        }
        return true;
    }

    private void showDialogInner(int id) {
        DialogFragment newFragment = MyAlertDialogFragment.newInstance(id);
        newFragment.setTargetFragment(this, 0);
        newFragment.show(getFragmentManager(), "dialog " + id);
    }

    public static class MyAlertDialogFragment extends DialogFragment {

        public static MyAlertDialogFragment newInstance(int id) {
            MyAlertDialogFragment frag = new MyAlertDialogFragment();
            Bundle args = new Bundle();
            args.putInt("id", id);
            frag.setArguments(args);
            return frag;
        }

        NotificationFragment getOwner() {
            return (NotificationFragment) getTargetFragment();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int id = getArguments().getInt("id");
            switch (id) {
                case DLG_SAFE_HEADSET_VOLUME:
                    return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.attention)
                    .setMessage(R.string.safe_headset_volume_warning_dialog_text)
                    .setPositiveButton(R.string.dlg_ok,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.SAFE_HEADSET_VOLUME, 0);

                        }
                    })
                    .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .create();
            }
            throw new IllegalArgumentException("unknown id " + id);
        }

        @Override
        public void onCancel(DialogInterface dialog) {
            int id = getArguments().getInt("id");
            switch (id) {
                case DLG_SAFE_HEADSET_VOLUME:
                    getOwner().mSafeHeadsetVolume.setChecked(true);
                    break;
            }
        }
    }

    private void launchNotificationSoundPicker(int code, String currentPowerRingtonePath) {
        final Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);

        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE,
                getString(R.string.power_notifications_ringtone_title));
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE,
                RingtoneManager.TYPE_NOTIFICATION);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI,
                System.DEFAULT_NOTIFICATION_URI);
        if (currentPowerRingtonePath != null &&
                !currentPowerRingtonePath.equals(POWER_NOTIFICATIONS_SILENT_URI)) {
            Uri uri = Uri.parse(currentPowerRingtonePath);
            if (uri != null) {
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, uri);
            }
        }
        startActivityForResult(intent, code);
    }

    private void setPowerNotificationRingtone(Intent intent) {
        final Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

        final String toneName;
        final String toneUriPath;

        if ( uri != null ) {
            final Ringtone ringtone = RingtoneManager.getRingtone(getActivity(), uri);
            toneName = ringtone.getTitle(getActivity());
            toneUriPath = uri.toString();
        } else {
            // silent
            toneName = getString(R.string.power_notifications_ringtone_silent);
            toneUriPath = POWER_NOTIFICATIONS_SILENT_URI;
        }

        mPowerSoundsRingtone.setSummary(toneName);
        Global.putString(mResolver,
                Global.POWER_NOTIFICATIONS_RINGTONE, toneUriPath);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_POWER_NOTIFICATIONS_RINGTONE:
                if (resultCode == Activity.RESULT_OK) {
                    setPowerNotificationRingtone(data);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }
}
