/*
 * Copyright (C) 2014 Slimroms
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

package com.android.vrtoxin.pie;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.preference.PreferenceFragment;
import android.preference.Preference.OnPreferenceChangeListener;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.vrtoxin.R;
import android.support.annotation.NonNull;

import com.android.vrtoxin.VRToxinActivity;

public class PieButtonFragment extends PreferenceFragment {

    public PieButtonFragment(){}

    private static final String PIESTYLEFRAG = "pie_button_style";
    private static final String PIEBUTTONFRAG = "pie_button_second_layer";

    public static final String KEY_ACTION_LISTVIEW_PACKAGE_NAME = "com.android.settings";
    public static final String KEY_ACTION_LISTVIEW_CLASS_NAME = "com.android.settings.Settings$ActionListViewSettingsActivity";

    private Preference mPieButton;
    private Preference mPieStyle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pie_button_fragment);

        //VRToxinActivity
        mPieStyle = (Preference)findPreference(PIESTYLEFRAG);
        mPieButton = (Preference)findPreference(PIEBUTTONFRAG);
    }
/*
    @Override
    public View onCreateView(LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        final View view = super.onCreateView(inflater, container, savedInstanceState);
        final ListView list = (ListView) view.findViewById(android.R.id.list);
        // our container already takes care of the padding
        if (list != null) {
            int paddingTop = list.getPaddingTop();
            int paddingBottom = list.getPaddingBottom();
            list.setPadding(0, paddingTop, 0, paddingBottom);
        }
        return view;
    }
*/
    //VRToxinActivity
    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen prefScreen, @NonNull Preference pref) {
        if (pref == mPieStyle) {
            ((VRToxinActivity)getActivity()).displaySubFrag(getString(R.string.pie_button_style));

            return true;
        }

        if (pref == mPieButton) {
            Intent action = new Intent(Intent.ACTION_MAIN);
            ComponentName cn = new ComponentName(KEY_ACTION_LISTVIEW_PACKAGE_NAME, KEY_ACTION_LISTVIEW_CLASS_NAME);
            action.putExtra("actionMode", 2);
            action.putExtra("maxAllowedActions", 7);
            action.setComponent(cn);
            startActivity(action);

            return true;
        }

        return false;
    }
}
