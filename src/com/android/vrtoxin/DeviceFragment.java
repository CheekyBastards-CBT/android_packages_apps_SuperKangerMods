/*
 * Copyright (C) 2015 The Pure Nexus Project
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
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.annotation.NonNull;

public class DeviceFragment extends PreferenceFragment {
    public static final String KEY_KERNEL_ACTIVITY_PACKAGE_NAME = "com.grarak.kerneladiutor";
    public static final String KEY_KERNEL_CLASS_NAME = "com.grarak.kerneladiutor.MainActivity";
    public static final String KEY_STWEAKS_ACTIVITY_PACKAGE_NAME = "com.gokhanmoral.stweaks.app";
    public static final String KEY_STWEAKS_CLASS_NAME = "com.gokhanmoral.stweaks.app.MainActivity";

    private static final String RADIO_INFO = "radioinfo";
    private static final String BUILDPROPEDITOR = "buildpropeditor";
    private static final String FISWITCH = "fiswitch";
    private static final String SIZER = "slim_sizer";
    private static final String WAKELOCK = "wakelock_blocker";
    private static final String KERNEL_ADUITOR = "kernel_aduitor";
    private static final String STWEAKS = "stweaks";

    private Preference mBuildProp;
    private Preference mFiSwitch;
    private Preference mSizer;
    private Preference mWakeLock;
    private Preference mKernel;
    private Preference mStweaks;

    public DeviceFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean FiAppInstalled;
        boolean KernelAduitorInstalled;
        boolean STweaksInstalled;

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.device_fragment);

        Preference mRadioInfo = (Preference)findPreference(RADIO_INFO);
        mBuildProp = (Preference)findPreference(BUILDPROPEDITOR);
        mFiSwitch = (Preference)findPreference(FISWITCH);
        mSizer = (Preference)findPreference(SIZER);
        mWakeLock = (Preference)findPreference(WAKELOCK);
        mKernel = (Preference)findPreference(KERNEL_ADUITOR);
        mStweaks = (Preference)findPreference(STWEAKS);

        PreferenceScreen prefScreen = getPreferenceScreen();

        // remove the radioinfo pref if tablet
        if ( (getActivity().getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE ) {
            prefScreen.removePreference(mRadioInfo);
        }

        // check if Project Fi app installed
        try {
            PackageInfo pi = getActivity().getPackageManager().getPackageInfo(VRToxinActivity.PROJFI_PACKAGE_NAME, 0);
            FiAppInstalled = pi.applicationInfo.enabled;
        } catch (PackageManager.NameNotFoundException e) {
            FiAppInstalled = false;
        }

        // check if Kernel Aduitor app installed
        try {
            PackageInfo pi = getActivity().getPackageManager().getPackageInfo(KEY_KERNEL_ACTIVITY_PACKAGE_NAME, 0);
            KernelAduitorInstalled = pi.applicationInfo.enabled;
        } catch (PackageManager.NameNotFoundException e) {
            KernelAduitorInstalled = false;
        }

        // check if STweaks app installed
        try {
            PackageInfo pi = getActivity().getPackageManager().getPackageInfo(KEY_STWEAKS_ACTIVITY_PACKAGE_NAME, 0);
            STweaksInstalled = pi.applicationInfo.enabled;
        } catch (PackageManager.NameNotFoundException e) {
            STweaksInstalled = false;
        }

        if (!FiAppInstalled) {
            prefScreen.removePreference(mFiSwitch);
        }

        if (!KernelAduitorInstalled) {
            prefScreen.removePreference(mKernel);
        }

        if (!STweaksInstalled) {
            prefScreen.removePreference(mStweaks);
        }
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen prefScreen, @NonNull Preference pref) {
        if (pref == mBuildProp) {
            ((VRToxinActivity)getActivity()).displaySubFrag(getString(R.string.buildprop_frag_title));

            return true;
        }

        if (pref == mFiSwitch) {
            ((VRToxinActivity)getActivity()).displaySubFrag(getString(R.string.fiswitch_frag_title));

            return true;
        }

        if (pref == mSizer) {
            ((VRToxinActivity)getActivity()).displaySubFrag(getString(R.string.sizer_frag_title));

            return true;
        }

        if (pref == mWakeLock) {
            ((VRToxinActivity)getActivity()).displaySubFrag(getString(R.string.wakelock_frag_title));

            return true;
        }

        if (pref == mKernel) {
            Intent action = new Intent(Intent.ACTION_MAIN);
            ComponentName cn = new ComponentName(KEY_KERNEL_ACTIVITY_PACKAGE_NAME, KEY_KERNEL_CLASS_NAME);
            action.setComponent(cn);
            startActivity(action);

            return true;
        }

        if (pref == mStweaks) {
            Intent action = new Intent(Intent.ACTION_MAIN);
            ComponentName cn = new ComponentName(KEY_STWEAKS_ACTIVITY_PACKAGE_NAME, KEY_STWEAKS_CLASS_NAME);
            action.setComponent(cn);
            startActivity(action);

            return true;
        }

        return false;
    }
}
