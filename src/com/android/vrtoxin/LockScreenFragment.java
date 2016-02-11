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

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.os.UserHandle;
import android.preference.SwitchPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.support.annotation.NonNull;

import com.android.vrtoxin.OwnerInfoSettings;
import com.android.vrtoxin.R;
import com.android.vrtoxin.Utils;
import com.android.vrtoxin.preferences.SeekBarPreference;

import com.android.internal.widget.LockPatternUtils;

public class LockScreenFragment extends PreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String KEY_LOCK_SCREEN_BUTTONS = "lock_screen_buttons_settings";
    private static final String KEY_LOCK_SCREEN_COLOR = "lock_screen_color_settings";
    private static final String KEY_LOCK_SCREEN_VISUALIZER = "lock_screen_visualizer_settings";
    private static final String KEY_LOCK_SCREEN_WALLPAPER = "lockscreen_wallpaper";
    private static final String KEY_OWNER_INFO_SETTINGS = "owner_info_settings";
    private static final String OWNER_INFO_FONT_SIZE  = "owner_info_font_size";

    private static final int MY_USER_ID = UserHandle.myUserId();
    private LockPatternUtils mLockPatternUtils;
    private Preference mOwnerInfoPref;
    private SeekBarPreference mOwnerSize;

    private boolean mIsPrimary;
    private ContentResolver mResolver;

    private Preference mButtons;
    private Preference mColor;
    private Preference mVisualizer;
    private Preference mWallpaper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        refreshSettings();
    }

    public void refreshSettings() {
        PreferenceScreen prefs = getPreferenceScreen();
        if (prefs != null) {
            prefs.removeAll();
        }

        addPreferencesFromResource(R.xml.lock_screen_fragment);

        mResolver = getActivity().getContentResolver();

        // Add options for device encryption
        mIsPrimary = MY_USER_ID == UserHandle.USER_OWNER;

        mButtons = (Preference)findPreference(KEY_LOCK_SCREEN_BUTTONS);
        mColor = (Preference)findPreference(KEY_LOCK_SCREEN_COLOR);
        mVisualizer = (Preference)findPreference(KEY_LOCK_SCREEN_VISUALIZER);
        mWallpaper = (Preference)findPreference(KEY_LOCK_SCREEN_WALLPAPER);

        mOwnerInfoPref = findPreference(KEY_OWNER_INFO_SETTINGS);
        if (mOwnerInfoPref != null) {
            mOwnerInfoPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    OwnerInfoSettings.show(LockScreenFragment.this);
                    return true;
                }
            });
        }

        mOwnerSize = (SeekBarPreference) findPreference(OWNER_INFO_FONT_SIZE);
        mOwnerSize.setValue(Settings.System.getInt(mResolver,
                Settings.System.OWNER_INFO_FONT_SIZE, 14));
        mOwnerSize.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen prefScreen, @NonNull Preference pref) {
        if (pref == mButtons) {
            ((VRToxinActivity)getActivity()).displaySubFrag(getString(R.string.lock_screen_buttons_frag_title));

            return true;
        }

        if (pref == mColor) {
            ((VRToxinActivity)getActivity()).displaySubFrag(getString(R.string.lock_clock_color_frag_title));

            return true;
        }

        if (pref == mVisualizer) {
            ((VRToxinActivity)getActivity()).displaySubFrag(getString(R.string.lock_screen_visualizer_frag_title));

            return true;
        }

        if (pref == mWallpaper) {
            ((VRToxinActivity)getActivity()).displaySubFrag(getString(R.string.lockscreen_wallpaper_frag_title));

            return true;
        }

        return false;
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mOwnerSize) {
            int width = ((Integer)newValue).intValue();
            Settings.System.putInt(mResolver,
                    Settings.System.OWNER_INFO_FONT_SIZE, width);
            return true;
        }
        return false;
    }
}
