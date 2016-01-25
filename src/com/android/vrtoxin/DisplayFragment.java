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

import com.android.internal.view.RotationPolicy;

import static android.provider.Settings.Secure.DOZE_ENABLED;
import static android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE;
import static android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
import static android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL;
import static android.provider.Settings.System.SCREEN_OFF_TIMEOUT;

import android.app.Activity;
import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.app.ProgressDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Display;
import android.view.IWindowManager;
import android.view.WindowManager;
import android.view.WindowManagerGlobal;
import android.view.WindowManagerImpl;
import android.widget.Toast;

import java.util.ArrayList;
import android.util.DisplayMetrics;
import android.util.Log;

import com.android.vrtoxin.display.DisplayRotationFragment;

public class DisplayFragment extends PreferenceFragment implements
        Preference.OnPreferenceChangeListener {
    private static final String TAG = "DisplayFragment";

    public DisplayFragment(){}

    /** If there is no setting in the provider, use this. */
    private static final int FALLBACK_SCREEN_TIMEOUT_VALUE = 30000;

    private static final String KEY_SCREEN_TIMEOUT =
            "screen_timeout";
    private static final String KEY_DOZE =
            "doze";
    private static final String KEY_DOZE_CAT =
            "doze_notification_cat";
    private static final String KEY_AUTO_BRIGHTNESS =
            "auto_brightness";
    private static final String KEY_DISPLAY_ROTATION =
            "display_rotation";
    private static final String KEY_LCD_DENSITY =
            "lcd_density";
    private static final String KEY_WAKE_WHEN_PLUGGED_OR_UNPLUGGED =
            "wake_when_plugged_or_unplugged";

    private static final int MIN_VIB_VALUE = 1;
    private static final int MAX_VIB_VALUE = 750;

    private ListPreference mScreenTimeoutPreference;
    private SwitchPreference mAutoBrightnessPreference;
    private SwitchPreference mDozePreference;
    private Preference mDisplayRotationPreference;
    private ListPreference mLcdDensityPreference;
    private SwitchPreference mWakeWhenPluggedOrUnplugged;

    private ContentResolver mResolver;
    private PreferenceScreen mPrefSet;

    private static final String ROTATION_ANGLE_0 = "0";
    private static final String ROTATION_ANGLE_90 = "90";
    private static final String ROTATION_ANGLE_180 = "180";
    private static final String ROTATION_ANGLE_270 = "270";

    private ContentObserver mAccelerometerRotationObserver =
            new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            updateDisplayRotationPreferenceDescription();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Activity activity = getActivity();

        addPreferencesFromResource(R.xml.display_fragment);

        mPrefSet = getPreferenceScreen();
        mResolver = getActivity().getContentResolver();

        mScreenTimeoutPreference = (ListPreference) findPreference(KEY_SCREEN_TIMEOUT);
        final long currentTimeout = Settings.System.getLong(mResolver, SCREEN_OFF_TIMEOUT,
                FALLBACK_SCREEN_TIMEOUT_VALUE);
        mScreenTimeoutPreference.setValue(String.valueOf(currentTimeout));
        mScreenTimeoutPreference.setOnPreferenceChangeListener(this);
        disableUnusableTimeouts(mScreenTimeoutPreference);
        updateTimeoutPreferenceDescription(currentTimeout);

        mWakeWhenPluggedOrUnplugged =
                (SwitchPreference) findPreference(KEY_WAKE_WHEN_PLUGGED_OR_UNPLUGGED);

        mLcdDensityPreference = (ListPreference) findPreference(KEY_LCD_DENSITY);
        if (mLcdDensityPreference != null) {
            int defaultDensity = getDefaultDensity();
            int currentDensity = getCurrentDensity();
            if (currentDensity < 10 || currentDensity >= 1000) {
                // Unsupported value, force default
                currentDensity = defaultDensity;
            }

            int factor = defaultDensity >= 480 ? 40 : 20;
            int minimumDensity = defaultDensity - 4 * factor;
            int currentIndex = -1;
            String[] densityEntries = new String[7];
            String[] densityValues = new String[7];
            for (int idx = 0; idx < 7; ++idx) {
                int val = minimumDensity + factor * idx;
                int valueFormatResId = val == defaultDensity
                        ? R.string.lcd_density_default_value_format
                        : R.string.lcd_density_value_format;

                densityEntries[idx] = getString(valueFormatResId, val);
                densityValues[idx] = Integer.toString(val);
                if (currentDensity == val) {
                    currentIndex = idx;
                }
            }
            mLcdDensityPreference.setEntries(densityEntries);
            mLcdDensityPreference.setEntryValues(densityValues);
            if (currentIndex != -1) {
                mLcdDensityPreference.setValueIndex(currentIndex);
            }
            mLcdDensityPreference.setOnPreferenceChangeListener(this);
            updateLcdDensityPreferenceDescription(currentDensity);

            if (isAutomaticBrightnessAvailable(getResources())) {
                mAutoBrightnessPreference = (SwitchPreference) findPreference(KEY_AUTO_BRIGHTNESS);
                mAutoBrightnessPreference.setOnPreferenceChangeListener(this);
            } else {
                mPrefSet.removePreference(findPreference("auto_brightness"));
            }

            if (isDozeAvailable(activity)) {
                mDozePreference = (SwitchPreference) findPreference(KEY_DOZE);
                mDozePreference.setOnPreferenceChangeListener(this);
            } else {
                mPrefSet.removePreference(findPreference("doze_notification_cat"));
            }

            if (RotationPolicy.isRotationLockToggleVisible(activity)) {
                mDisplayRotationPreference = (Preference) findPreference(KEY_DISPLAY_ROTATION);
            } else {
                mPrefSet.removePreference(findPreference("display_rotation"));
            }
        }
    }

    private int getDefaultDensity() {
        IWindowManager wm = IWindowManager.Stub.asInterface(ServiceManager.checkService(
                Context.WINDOW_SERVICE));
        try {
            return wm.getInitialDisplayDensity(Display.DEFAULT_DISPLAY);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return DisplayMetrics.DENSITY_DEVICE;
    }

    private int getCurrentDensity() {
        IWindowManager wm = IWindowManager.Stub.asInterface(ServiceManager.checkService(
                Context.WINDOW_SERVICE));
        try {
            return wm.getBaseDisplayDensity(Display.DEFAULT_DISPLAY);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return DisplayMetrics.DENSITY_DEVICE;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Default value for wake-on-plug behavior from config.xml
        boolean wakeUpWhenPluggedOrUnpluggedConfig = getResources().getBoolean(
                com.android.internal.R.bool.config_unplugTurnsOnScreen);

        mResolver.registerContentObserver(
                Settings.System.getUriFor(Settings.System.ACCELEROMETER_ROTATION), true,
                mAccelerometerRotationObserver);
        updateDisplayRotationPreferenceDescription();

        mWakeWhenPluggedOrUnplugged.setChecked(Settings.Global.getInt(mResolver,
                Settings.Global.WAKE_WHEN_PLUGGED_OR_UNPLUGGED,
                (wakeUpWhenPluggedOrUnpluggedConfig ? 1 : 0)) == 1);

        updateState();
    }

    @Override
    public void onPause() {
        super.onPause();
        mResolver.unregisterContentObserver(mAccelerometerRotationObserver);
    }

    private static boolean isDozeAvailable(Context context) {
        String name = Build.IS_DEBUGGABLE ? SystemProperties.get("debug.doze.component") : null;
        if (TextUtils.isEmpty(name)) {
            name = context.getResources().getString(
                    com.android.internal.R.string.config_dozeComponent);
        }
        return !TextUtils.isEmpty(name);
    }

    private static boolean isAutomaticBrightnessAvailable(Resources res) {
        return res.getBoolean(com.android.internal.R.bool.config_automatic_brightness_available);
    }

    private void updateState() {
        // Update doze if it is available.
        if (mDozePreference != null) {
            int value = Settings.Secure.getInt(mResolver, DOZE_ENABLED, 0);
            mDozePreference.setChecked(value != 0);
        }

        // Update auto brightness if it is available.
        if (mAutoBrightnessPreference != null) {
            int brightnessMode = Settings.System.getInt(mResolver,
                    SCREEN_BRIGHTNESS_MODE, SCREEN_BRIGHTNESS_MODE_MANUAL);
            mAutoBrightnessPreference.setChecked(brightnessMode != SCREEN_BRIGHTNESS_MODE_MANUAL);
        }
    }

    private void updateTimeoutPreferenceDescription(long currentTimeout) {
        ListPreference preference = mScreenTimeoutPreference;
        String summary;
        if (currentTimeout < 0) {
            // Unsupported value
            summary = "";
        } else {
            final CharSequence[] entries = preference.getEntries();
            final CharSequence[] values = preference.getEntryValues();
            if (entries == null || entries.length == 0) {
                summary = "";
            } else {
                int best = 0;
                for (int i = 0; i < values.length; i++) {
                    long timeout = Long.parseLong(values[i].toString());
                    if (currentTimeout >= timeout) {
                        best = i;
                    }
                }
                summary = preference.getContext().getString(R.string.screen_timeout_summary,
                        entries[best]);
            }
        }
        preference.setSummary(summary);
    }

    private void updateDisplayRotationPreferenceDescription() {
        Preference preference = mDisplayRotationPreference;
        if (preference == null) {
            return;
        }
        preference.setEnabled(RotationPolicy.isRotationLockToggleVisible(getActivity()));
        StringBuilder summary = new StringBuilder();
        Boolean rotationEnabled = Settings.System.getInt(mResolver,
                Settings.System.ACCELEROMETER_ROTATION, 0) != 0;
        int mode = Settings.System.getInt(mResolver,
                Settings.System.ACCELEROMETER_ROTATION_ANGLES,
                DisplayRotationFragment.ROTATION_0_MODE|DisplayRotationFragment.ROTATION_90_MODE
                |DisplayRotationFragment.ROTATION_270_MODE);

        if (!rotationEnabled) {
            summary.append(getString(R.string.display_rotation_disabled));
        } else {
            ArrayList<String> rotationList = new ArrayList<String>();
            String delim = "";
            if ((mode & DisplayRotationFragment.ROTATION_0_MODE) != 0) {
                rotationList.add(ROTATION_ANGLE_0);
            }
            if ((mode & DisplayRotationFragment.ROTATION_90_MODE) != 0) {
                rotationList.add(ROTATION_ANGLE_90);
            }
            if ((mode & DisplayRotationFragment.ROTATION_180_MODE) != 0) {
                rotationList.add(ROTATION_ANGLE_180);
            }
            if ((mode & DisplayRotationFragment.ROTATION_270_MODE) != 0) {
                rotationList.add(ROTATION_ANGLE_270);
            }
            for (int i = 0; i < rotationList.size(); i++) {
                summary.append(delim).append(rotationList.get(i));
                if ((rotationList.size() - i) > 2) {
                    delim = ", ";
                } else {
                    delim = " & ";
                }
            }
            summary.append(" " + getString(R.string.display_rotation_unit));
        }
        preference.setSummary(summary);
    }

    private void disableUnusableTimeouts(ListPreference screenTimeoutPreference) {
        final DevicePolicyManager dpm =
                (DevicePolicyManager) getActivity().getSystemService(
                Context.DEVICE_POLICY_SERVICE);
        final long maxTimeout = dpm != null ? dpm.getMaximumTimeToLock(null) : 0;
        if (maxTimeout == 0) {
            return; // policy not enforced
        }
        final CharSequence[] entries = screenTimeoutPreference.getEntries();
        final CharSequence[] values = screenTimeoutPreference.getEntryValues();
        ArrayList<CharSequence> revisedEntries = new ArrayList<CharSequence>();
        ArrayList<CharSequence> revisedValues = new ArrayList<CharSequence>();
        for (int i = 0; i < values.length; i++) {
            long timeout = Long.parseLong(values[i].toString());
            if (timeout <= maxTimeout) {
                revisedEntries.add(entries[i]);
                revisedValues.add(values[i]);
            }
        }
        if (revisedEntries.size() != entries.length || revisedValues.size() != values.length) {
            final int userPreference = Integer.parseInt(screenTimeoutPreference.getValue());
            screenTimeoutPreference.setEntries(
                    revisedEntries.toArray(new CharSequence[revisedEntries.size()]));
            screenTimeoutPreference.setEntryValues(
                    revisedValues.toArray(new CharSequence[revisedValues.size()]));
            if (userPreference <= maxTimeout) {
                screenTimeoutPreference.setValue(String.valueOf(userPreference));
            } else if (revisedValues.size() > 0
                    && Long.parseLong(revisedValues.get(revisedValues.size() - 1).toString())
                    == maxTimeout) {
                // If the last one happens to be the same as the max timeout, select that
                screenTimeoutPreference.setValue(String.valueOf(maxTimeout));
            } else {
                // There will be no highlighted selection since nothing in the list matches
                // maxTimeout. The user can still select anything less than maxTimeout.
                // TODO: maybe append maxTimeout to the list and mark selected.
            }
        }
        screenTimeoutPreference.setEnabled(revisedEntries.size() > 0);
    }

    private void updateLcdDensityPreferenceDescription(int currentDensity) {
        final int summaryResId = currentDensity == getDefaultDensity()
                ? R.string.lcd_density_default_value_format : R.string.lcd_density_value_format;
        mLcdDensityPreference.setSummary(getString(summaryResId, currentDensity));
    }

    private void writeLcdDensityPreference(final Context context, final int density) {
        final IActivityManager am = ActivityManagerNative.asInterface(
                ServiceManager.checkService("activity"));
        final IWindowManager wm = IWindowManager.Stub.asInterface(ServiceManager.checkService(
                Context.WINDOW_SERVICE));
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                ProgressDialog dialog = new ProgressDialog(context);
                dialog.setMessage(getResources().getString(R.string.restarting_ui));
                dialog.setCancelable(false);
                dialog.setIndeterminate(true);
                dialog.show();
            }
            @Override
            protected Void doInBackground(Void... params) {
                // Give the user a second to see the dialog
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // Ignore
                }

                try {
                    wm.setForcedDisplayDensity(Display.DEFAULT_DISPLAY, density);
                } catch (RemoteException e) {
                    Log.e(TAG, "Failed to set density to " + density, e);
                }

                // Restart the UI
                try {
                    am.restart();
                } catch (RemoteException e) {
                    Log.e(TAG, "Failed to restart");
                }
                return null;
            }
        };
        task.execute();
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mWakeWhenPluggedOrUnplugged) {
            Settings.Global.putInt(mResolver,
                    Settings.Global.WAKE_WHEN_PLUGGED_OR_UNPLUGGED,
                    mWakeWhenPluggedOrUnplugged.isChecked() ? 1 : 0);
            return true;
        }

        if (preference == mDisplayRotationPreference) {
            ((VRToxinActivity)getActivity()).displaySubFrag(getString(R.string.display_rotation_frag_title));

            return true;
        }

        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        final String key = preference.getKey();

        if (preference == mAutoBrightnessPreference) {
            boolean auto = (Boolean) objValue;
            Settings.System.putInt(mResolver, SCREEN_BRIGHTNESS_MODE,
                    auto ? SCREEN_BRIGHTNESS_MODE_AUTOMATIC : SCREEN_BRIGHTNESS_MODE_MANUAL);
        }

        if (preference == mDozePreference) {
            boolean value = (Boolean) objValue;
            Settings.Secure.putInt(mResolver, DOZE_ENABLED, value ? 1 : 0);
        }

        if (KEY_LCD_DENSITY.equals(key)) {
            try {
                int value = Integer.parseInt((String) objValue);
                writeLcdDensityPreference(preference.getContext(), value);
                updateLcdDensityPreferenceDescription(value);
            } catch (NumberFormatException e) {
                Log.e(TAG, "could not persist display density setting", e);
            }
        }

        return true;
    }
}
