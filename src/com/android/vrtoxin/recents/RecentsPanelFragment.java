/*
 * Copyright (C) 2015 The VRToxin Project
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

package com.android.vrtoxin.recents;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

import com.android.vrtoxin.R;
import com.android.vrtoxin.Utils;
import com.android.vrtoxin.VRToxinActivity;

public class RecentsPanelFragment extends PreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    public RecentsPanelFragment(){}

    private static final String PREF_USE_SLIM_RECENTS =
            "use_slim_recents";
    private static final String SLIM_RECENTS_SETTINGS = "slim_recents_settings";
    private static final String SLIM_RECENTS_APP_SIDEBAR = "recent_app_sidebar_content";

    private SwitchPreference mUseSlimRecents;
    private Preference mSlimSettings;
    private Preference mSlimAppBar;

    private ContentResolver mResolver;

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

        addPreferencesFromResource(R.xml.recents_panel_settings);
        mResolver = getActivity().getContentResolver();

        mSlimSettings = (Preference)findPreference(SLIM_RECENTS_SETTINGS);
        mSlimAppBar = (Preference)findPreference(SLIM_RECENTS_APP_SIDEBAR);

        boolean useSlimRecents = Settings.System.getInt(mResolver,
                    Settings.System.USE_SLIM_RECENTS, 0) == 1;
        mUseSlimRecents = (SwitchPreference) findPreference(PREF_USE_SLIM_RECENTS);
        mUseSlimRecents.setChecked(useSlimRecents);
        mUseSlimRecents.setOnPreferenceChangeListener(this);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mUseSlimRecents) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.USE_SLIM_RECENTS,
                    value ? 1 : 0);
            refreshSettings();
            return true;
        }
        return false;
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mSlimSettings) {
            ((VRToxinActivity)getActivity()).displaySubFrag(getString(R.string.slim_recents_settings_title));

            return true;
        }

        if (preference == mSlimAppBar) {

            ((VRToxinActivity) getActivity()).displayActionListViewSlimAppBar();

            return true;
        }

        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
}
