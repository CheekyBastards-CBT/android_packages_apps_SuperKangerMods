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

package com.android.vrtoxin.statusbar;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

import com.android.vrtoxin.R;
import com.android.vrtoxin.VRToxinActivity;
import com.android.vrtoxin.Utils;

public class BatteryStyleFragment extends PreferenceFragment {

    public BatteryStyleFragment(){}

    private static final String STATUS_BAR_BATTERY = "status_bar_battery_status_settings";
    private static final String STATUS_BAR_BATTERY_BAR = "battery_bar";

    private Preference mBattery;
    private Preference mBatteryBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.battery_style);

        mBattery = (Preference)findPreference(STATUS_BAR_BATTERY);
        mBatteryBar = (Preference)findPreference(STATUS_BAR_BATTERY_BAR);
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {

        return false;
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mBattery) {
            ((VRToxinActivity)getActivity()).displaySubFrag(getString(R.string.status_bar_battery_style));

            return true;
        }

        if (preference == mBatteryBar) {
            ((VRToxinActivity)getActivity()).displaySubFrag(getString(R.string.battery_bar));

            return true;
        }

        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
}
