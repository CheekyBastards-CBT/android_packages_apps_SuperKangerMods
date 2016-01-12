/*
* Copyright (C) 2016 The VRToxin Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.android.vrtoxin;

import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.annotation.NonNull;

import com.android.vrtoxin.R;

import java.util.ArrayList;
import java.util.List;

public class StatusBarFragment extends PreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    public StatusBarFragment(){}

    private static final String BATTERY_FRAG = "battery_style_pref";
    private static final String CARRIER_LABEL_FRAG = "status_bar_carrier_label_settings";
    private static final String CLOCK_FRAG = "status_bar_clock";
    private static final String EXPANDED_HEADER_FRAG = "status_bar_expanded_header";
    private static final String GREETING_FRAG = "status_bar_greeting_settings";
    private static final String NETWORK_ICONS_FRAG = "status_bar_network_status_icons_settings";
    private static final String NETWORK_TRAFFIC_FRAG = "network_traffic_state";
    private static final String NOTIFICATION_ICONS_FRAG = "status_bar_notification_icons_settings";
    private static final String RANDOM_FRAG = "status_bar_random";
    private static final String LOGO_FRAG = "status_bar_vrtoxin_logo";
    private static final String WEATHER_TEMP_FRAG = "status_bar_weather_temperature";

    private Preference mBattery;
    private Preference mCarrier;
    private Preference mClock;
    private Preference mExpandedHeader;
    private Preference mGreeting;
    private Preference mNetworkIcons;
    private Preference mNetworkTraffic;
    private Preference mNotifIcons;
    private Preference mRandom;
    private Preference mLogo;
    private Preference mWeatherTemp;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.status_bar_fragment);

        mBattery = (Preference)findPreference(BATTERY_FRAG);
        mCarrier = (Preference)findPreference(CARRIER_LABEL_FRAG);
        mClock = (Preference)findPreference(CLOCK_FRAG);
        mExpandedHeader = (Preference)findPreference(EXPANDED_HEADER_FRAG);
        mGreeting = (Preference)findPreference(GREETING_FRAG);
        mNetworkIcons = (Preference)findPreference(NETWORK_ICONS_FRAG);
        mNetworkTraffic = (Preference)findPreference(NETWORK_TRAFFIC_FRAG);
        mNotifIcons = (Preference)findPreference(NOTIFICATION_ICONS_FRAG);
        mRandom = (Preference)findPreference(RANDOM_FRAG);
        mLogo = (Preference)findPreference(LOGO_FRAG);
        mWeatherTemp = (Preference)findPreference(WEATHER_TEMP_FRAG);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen prefScreen, @NonNull Preference pref) {
        if (pref == mBattery) {
            ((VRToxinActivity)getActivity()).displaySubFrag(getString(R.string.status_bar_battery_status_settings_title));

            return true;
        }

        if (pref == mCarrier) {
            ((VRToxinActivity)getActivity()).displaySubFrag(getString(R.string.status_bar_carrier_label_settings_title));

            return true;
        }

        if (pref == mClock) {
            ((VRToxinActivity)getActivity()).displaySubFrag(getString(R.string.status_bar_clock_settings_title));

            return true;
        }

        if (pref == mExpandedHeader) {
            ((VRToxinActivity)getActivity()).displaySubFrag(getString(R.string.status_bar_expanded_header_settings_title));

            return true;
        }

        if (pref == mGreeting) {
            ((VRToxinActivity)getActivity()).displaySubFrag(getString(R.string.status_bar_greeting_settings_title));

            return true;
        }

        if (pref == mNetworkIcons) {
            ((VRToxinActivity)getActivity()).displaySubFrag(getString(R.string.status_bar_network_status_icons_settings_title));

            return true;
        }

        if (pref == mNetworkTraffic) {
            ((VRToxinActivity)getActivity()).displaySubFrag(getString(R.string.network_traffic_title));

            return true;
        }

        if (pref == mNotifIcons) {
            ((VRToxinActivity)getActivity()).displaySubFrag(getString(R.string.status_bar_notification_icon_settings_title));

            return true;
        }

        if (pref == mRandom) {
            ((VRToxinActivity)getActivity()).displaySubFrag(getString(R.string.status_bar_random_title));

            return true;
        }

        if (pref == mLogo) {
            ((VRToxinActivity)getActivity()).displaySubFrag(getString(R.string.status_bar_vrtoxin_logo_title));

            return true;
        }

        if (pref == mWeatherTemp) {
            ((VRToxinActivity)getActivity()).displaySubFrag(getString(R.string.status_bar_weather_temperature_title));

            return true;
        }

        return false;
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {

        return false;
    }
}
