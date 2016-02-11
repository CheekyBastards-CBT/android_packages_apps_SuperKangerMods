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
import android.os.Bundle;
import android.os.UserHandle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import com.android.internal.widget.LockPatternUtils;

import java.util.ArrayList;
import java.util.List;

public class SecuritySettings extends PreferenceFragment {

    private static final int MY_USER_ID = UserHandle.myUserId();

    private LockPatternUtils mLockPatternUtils;

    private static final String KEY_OWNER_INFO_SETTINGS = "owner_info_settings";

    private Preference mOwnerInfoPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.security_settings);

        mLockPatternUtils = new LockPatternUtils(getActivity());


        mOwnerInfoPref = findPreference(KEY_OWNER_INFO_SETTINGS);
        if (mOwnerInfoPref != null) {
            mOwnerInfoPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    OwnerInfoSettings.show(SecuritySettings.this);
                    return true;
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        updateOwnerInfo();
    }

    public void updateOwnerInfo() {
        if (mOwnerInfoPref != null) {
            mOwnerInfoPref.setSummary(mLockPatternUtils.isOwnerInfoEnabled(MY_USER_ID)
                    ? mLockPatternUtils.getOwnerInfo(MY_USER_ID)
                    : getString(R.string.owner_info_settings_summary));
        }
    }
}
