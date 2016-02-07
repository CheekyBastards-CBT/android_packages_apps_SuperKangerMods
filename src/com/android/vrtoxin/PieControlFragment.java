/*
 * Copyright (C) 2014 Slimroms
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
import android.content.ContentResolver;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.preference.SwitchPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import android.support.annotation.NonNull;

import com.android.vrtoxin.R;

public class PieControlFragment extends PreferenceFragment
        implements Preference.OnPreferenceChangeListener {

    //VRToxinActivity
    private static final String PIEBUTTONFRAG = "piebutton";
    private static final String PIESTYLEFRAG = "piestyle";
    private static final String PIETRIGGERFRAG = "pietrigger";

    private Preference mPieButton;
    private Preference mPieStyle;
    private Preference mPieTrigger;

    private static final String PIE_CONTROL = "pie_control";
    private static final String PIE_MENU = "pie_menu";

    private SwitchPreference mPieControl;
    private ListPreference mPieMenuDisplay;

    private ContentResolver mResolver;

    public PieControlFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.pie_control_fragment);

        PreferenceScreen prefSet = getPreferenceScreen();
        mResolver = getActivity().getContentResolver();

        //VRToxinActivity
        mPieButton = (Preference)findPreference(PIEBUTTONFRAG);
        mPieStyle = (Preference)findPreference(PIESTYLEFRAG);
        mPieTrigger = (Preference)findPreference(PIETRIGGERFRAG);

        mPieControl = (SwitchPreference) prefSet.findPreference(PIE_CONTROL);
        mPieControl.setOnPreferenceChangeListener(this);

        mPieMenuDisplay = (ListPreference) prefSet.findPreference(PIE_MENU);
        mPieMenuDisplay.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mPieControl) {
            Settings.System.putInt(mResolver,
                    Settings.System.PIE_CONTROLS, (Boolean) newValue ? 1 : 0);
        } else if (preference == mPieMenuDisplay) {
            Settings.System.putInt(mResolver,
                    Settings.System.PIE_MENU, Integer.parseInt((String) newValue));
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();

        mPieMenuDisplay.setValue(Settings.System.getInt(mResolver,
                Settings.System.PIE_MENU,
                2) + "");
        mPieControl.setChecked(Settings.System.getInt(mResolver,
                Settings.System.PIE_CONTROLS, 0) == 1);
    }

    //VRToxinActivity
    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen prefScreen, @NonNull Preference pref) {
        if (pref == mPieButton) {

            ((VRToxinActivity) getActivity()).displayActionListViewPie();

            return true;
        }

        if (pref == mPieStyle) {
            ((VRToxinActivity)getActivity()).displaySubFrag(getString(R.string.pie_style));

            return true;
        }

        if (pref == mPieTrigger) {
            ((VRToxinActivity)getActivity()).displaySubFrag(getString(R.string.pie_control_trigger_positions));

            return true;
        }

        return false;
    }
}
