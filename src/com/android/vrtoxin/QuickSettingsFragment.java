/*
 * Copyright (C) 2015 VRToxin Project
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

import android.content.ContentResolver;
import android.content.Context;
import android.content.ComponentName;
import android.content.res.Resources;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import android.support.annotation.NonNull;

import com.android.vrtoxin.R;
import com.android.vrtoxin.qs.DraggableGridView;
import com.android.vrtoxin.qs.QSTiles;

import com.android.internal.widget.LockPatternUtils;

import java.util.ArrayList;
import java.util.List;

public class QuickSettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

    private static final String PREF_QS_TYPE = "qs_type";
    private static final String QUICK_PULLDOWN = "quick_pulldown";
    private static final String PREF_SMART_PULLDOWN = "smart_pulldown";
    private static final String PREF_NUM_OF_COLUMNS = "sysui_qs_num_columns";
    private static final String PREF_MAIN_TILES = "sysui_qs_main_tiles";
    private static final String PREF_LOCATION_ADVANCED = "qs_location_advanced";
    private static final String PREF_COLLAPSE_PANEL = "quick_settings_collapse_panel";
    private static final String PREF_WIFI_DETAIL = "qs_wifi_detail";
    private static final String PREF_QS_VIBRATE = "quick_settings_vibrate";
    private static final String PREF_BLOCK_ON_SECURE_KEYGUARD = "block_on_secure_keyguard";

    private static final int QS_TYPE_PANEL  = 0;
    private static final int QS_TYPE_BAR    = 1;
    private static final int QS_TYPE_HIDDEN = 2;

    //VRToxinActivity
    public static final String KEY_ACTION_LISTVIEW_PACKAGE_NAME =
            "com.android.settings";
    public static final String KEY_ACTION_LISTVIEW_CLASS_NAME =
            "com.android.settings.Settings$ActionListViewSettingsActivity";
    private static final String QS_ORDER =
            "qs_panel_tiles";
    private static final String QS_BAR =
            "qs_bar_buttons";
    private static final String QS_COLOR =
            "qs_colors";

    private Preference mQsTiles;
    private Preference mQsBar;
    private Preference mQsColor;

    private ListPreference mQSType;
    private ListPreference mQuickPulldown;
    private ListPreference mSmartPulldown;
    private ListPreference mNumColumns;
    private SwitchPreference mMainTiles;
    private SwitchPreference mLocationAdvanced;
    private SwitchPreference mCollapsePanel;
    private SwitchPreference mWifiDetail;
    private SwitchPreference mVibrate;
    private SwitchPreference mBlockOnSecureKeyguard;

    private ContentResolver mResolver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        refreshSettings();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        refreshSettings();
    }

    public void refreshSettings() {
        PreferenceScreen prefs = getPreferenceScreen();
        if (prefs != null) {
            prefs.removeAll();
        }

        addPreferencesFromResource(R.xml.quick_settings);
        mResolver = getActivity().getContentResolver();
        PreferenceScreen prefSet = getPreferenceScreen();

        mQsTiles = prefSet.findPreference(QS_ORDER);
        mQsBar = prefSet.findPreference(QS_BAR);
        mQsColor = prefSet.findPreference(QS_COLOR);
        mMainTiles = (SwitchPreference) findPreference(PREF_MAIN_TILES);
        mLocationAdvanced = (SwitchPreference) findPreference(PREF_LOCATION_ADVANCED);
        mCollapsePanel = (SwitchPreference) findPreference(PREF_COLLAPSE_PANEL);
        mWifiDetail = (SwitchPreference) findPreference(PREF_WIFI_DETAIL);
        mVibrate = (SwitchPreference) findPreference(PREF_QS_VIBRATE);

        final int qsType = Settings.System.getInt(mResolver,
               Settings.System.QS_TYPE, QS_TYPE_PANEL);

        mQSType = (ListPreference) findPreference(PREF_QS_TYPE);
        mQSType.setValue(String.valueOf(qsType));
        mQSType.setSummary(mQSType.getEntry());
        mQSType.setOnPreferenceChangeListener(this);

        if (qsType == QS_TYPE_PANEL) {
            mNumColumns = (ListPreference) findPreference(PREF_NUM_OF_COLUMNS);
            int numColumns = Settings.System.getIntForUser(mResolver,
                    Settings.System.QS_NUM_TILE_COLUMNS, getDefaultNumColums(),
                    UserHandle.USER_CURRENT);
            mNumColumns.setValue(String.valueOf(numColumns));
            updateNumColumnsSummary(numColumns);
            mNumColumns.setOnPreferenceChangeListener(this);
            DraggableGridView.setColumnCount(numColumns);

        } else {
            prefSet.removePreference(mNumColumns);
        }

        if (qsType == QS_TYPE_BAR || qsType == QS_TYPE_HIDDEN) {
            prefSet.removePreference(mQsTiles);
            prefSet.removePreference(mNumColumns);
            prefSet.removePreference(mMainTiles);
            prefSet.removePreference(mLocationAdvanced);
            prefSet.removePreference(mCollapsePanel);
            prefSet.removePreference(mWifiDetail);
            prefSet.removePreference(mVibrate);
        }

        if (qsType == QS_TYPE_PANEL || qsType == QS_TYPE_HIDDEN) {
            prefSet.removePreference(mQsBar);
        }

        mQuickPulldown = (ListPreference) findPreference(QUICK_PULLDOWN);
        mQuickPulldown.setOnPreferenceChangeListener(this);
        int quickPulldownValue = Settings.System.getIntForUser(mResolver,
                Settings.System.QS_QUICK_PULLDOWN, 1, UserHandle.USER_CURRENT);
        mQuickPulldown.setValue(String.valueOf(quickPulldownValue));
        updatePulldownSummary(quickPulldownValue);

        // Smart Pulldown
        mSmartPulldown = (ListPreference) findPreference(PREF_SMART_PULLDOWN);
        mSmartPulldown.setOnPreferenceChangeListener(this);
        int smartPulldown = Settings.System.getInt(mResolver,
                Settings.System.QS_SMART_PULLDOWN, 0);
        mSmartPulldown.setValue(String.valueOf(smartPulldown));
        updateSmartPulldownSummary(smartPulldown);

        final LockPatternUtils lockPatternUtils = new LockPatternUtils(getActivity());
        mBlockOnSecureKeyguard = (SwitchPreference) findPreference(PREF_BLOCK_ON_SECURE_KEYGUARD);
        if (lockPatternUtils.isSecure(UserHandle.myUserId())) {
            mBlockOnSecureKeyguard.setChecked(Settings.Secure.getInt(mResolver,
                    Settings.Secure.STATUS_BAR_LOCKED_ON_SECURE_KEYGUARD, 1) == 1);
            mBlockOnSecureKeyguard.setOnPreferenceChangeListener(this);
        } else {
            prefSet.removePreference(mBlockOnSecureKeyguard);
        }
    }

    /*@Override
    public void onResume() {
        super.onResume();

        int qsTileCount = QSTiles.determineTileCount(getActivity());
        mQSTiles.setSummary(getResources().getQuantityString(R.plurals.qs_tiles_summary,
                    qsTileCount, qsTileCount));
    }*/

    //VRToxinActivity
    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen prefScreen, @NonNull Preference pref) {
        if (pref == mQsTiles) {
            ((VRToxinActivity)getActivity()).displaySubFrag(getString(R.string.qs_order_title));

            return true;
        }

        if (pref == mQsBar) {
            Intent action = new Intent(Intent.ACTION_MAIN);
            ComponentName cn = new ComponentName(KEY_ACTION_LISTVIEW_PACKAGE_NAME, KEY_ACTION_LISTVIEW_CLASS_NAME);
            action.putExtra("actionMode", 6);
            action.putExtra("maxAllowedActions", 30);
            action.putExtra("defaultNumberOfActions", 6);
            action.putExtra("disableLongpress", true);
            action.putExtra("disableIconPicker", true);
            action.putExtra("disableDeleteLastEntry", true);
            action.putExtra("actionValues", "qab_button_values");
            action.putExtra("actionEntries", "qab_button_entries");
            action.setComponent(cn);
            startActivity(action);

            return true;
        }

        if (pref == mQsColor) {
            ((VRToxinActivity)getActivity()).displaySubFrag(getString(R.string.qs_colors_title));

            return true;
        }

        return false;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mQSType) {
            int intValue = Integer.valueOf((String) newValue);
            int index = mQSType.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                Settings.System.QS_TYPE, intValue);
            preference.setSummary(mQSType.getEntries()[index]);
            refreshSettings();
            return true;
        } else if (preference == mNumColumns) {
            int numColumns = Integer.valueOf((String) newValue);
            Settings.System.putIntForUser(mResolver, Settings.System.QS_NUM_TILE_COLUMNS,
                    numColumns, UserHandle.USER_CURRENT);
            updateNumColumnsSummary(numColumns);
            DraggableGridView.setColumnCount(numColumns);
            return true;
		} else if (preference == mQuickPulldown) {
            int quickPulldownValue = Integer.valueOf((String) newValue);
            Settings.System.putIntForUser(mResolver, Settings.System.QS_QUICK_PULLDOWN,
                    quickPulldownValue, UserHandle.USER_CURRENT);
            updatePulldownSummary(quickPulldownValue);
            return true;
        } else if (preference == mSmartPulldown) {
            int smartPulldown = Integer.valueOf((String) newValue);
            Settings.System.putIntForUser(mResolver, Settings.System.QS_SMART_PULLDOWN,
                    smartPulldown, UserHandle.USER_CURRENT);
            updateSmartPulldownSummary(smartPulldown);
            return true;
        } else if (preference == mBlockOnSecureKeyguard) {
            Settings.Secure.putInt(mResolver,
                    Settings.Secure.STATUS_BAR_LOCKED_ON_SECURE_KEYGUARD,
                    (Boolean) newValue ? 1 : 0);
            return true;
        }
        return false;
    }

    private void updatePulldownSummary(int value) {
        Resources res = getResources();

        if (value == 0) {
            // quick pulldown deactivated
            mQuickPulldown.setSummary(res.getString(R.string.quick_pulldown_off));
        } else {
            String direction = res.getString(value == 2
                    ? R.string.quick_pulldown_summary_left
                    : R.string.quick_pulldown_summary_right);
            mQuickPulldown.setSummary(res.getString(R.string.quick_pulldown_summary, direction));
        }
    }

    private void updateSmartPulldownSummary(int value) {
        Resources res = getResources();

        if (value == 0) {
            // Smart pulldown deactivated
            mSmartPulldown.setSummary(res.getString(R.string.smart_pulldown_off));
        } else {
            String type = null;
            switch (value) {
                case 1:
                    type = res.getString(R.string.smart_pulldown_dismissable);
                    break;
                case 2:
                    type = res.getString(R.string.smart_pulldown_persistent);
                    break;
                default:
                    type = res.getString(R.string.smart_pulldown_all);
                    break;
            }
            // Remove title capitalized formatting
            type = type.toLowerCase();
            mSmartPulldown.setSummary(res.getString(R.string.smart_pulldown_summary, type));
        }
    }

    private void updateNumColumnsSummary(int numColumns) {
        String prefix = (String) mNumColumns.getEntries()[mNumColumns.findIndexOfValue(String
                .valueOf(numColumns))];
        mNumColumns.setSummary(getResources().getString(R.string.qs_num_columns_showing, prefix));
    }

    private int getDefaultNumColums() {
        try {
            Resources res = getActivity().getPackageManager()
                    .getResourcesForApplication("com.android.systemui");
            int val = res.getInteger(res.getIdentifier("quick_settings_num_columns", "integer",
                    "com.android.systemui")); // better not be larger than 5, that's as high as the
                                              // list goes atm
            return Math.max(1, val);
        } catch (Exception e) {
            return 3;
        }
    }
}
