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
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.annotation.NonNull;

import com.android.vrtoxin.R;
import com.android.vrtoxin.Utils;

public class GesturesFragment extends PreferenceFragment {

    private static final String KEY_APP_CIRCLE_BAR_FRAG = "app_circle_bar";
    private static final String KEY_APP_SIDE_BAR_FRAG = "app_sidebar";
    private static final String KEY_GESTURE_ANYWHERE_FRAG = "gesture_anywhere";

    private Preference mAppCircleBar;
    private Preference mAppSideBar;
    private Preference mGestureAnywhere;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.gestures_fragment);

        mAppCircleBar = (Preference)findPreference(KEY_APP_CIRCLE_BAR_FRAG);
        mAppSideBar = (Preference)findPreference(KEY_APP_SIDE_BAR_FRAG);
        mGestureAnywhere = (Preference)findPreference(KEY_GESTURE_ANYWHERE_FRAG);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen prefScreen, @NonNull Preference pref) {
        if (pref == mAppCircleBar) {
            ((VRToxinActivity)getActivity()).displaySubFrag(getString(R.string.app_circle_bar_frag_title));

            return true;
        }

        if (pref == mAppSideBar) {
            ((VRToxinActivity)getActivity()).displaySubFrag(getString(R.string.app_sidebar_frag_title));

            return true;
        }

        if (pref == mGestureAnywhere) {
            ((VRToxinActivity)getActivity()).displaySubFrag(getString(R.string.gesture_anywhere_frag_title));

            return true;
        }

        return false;
    }
}
