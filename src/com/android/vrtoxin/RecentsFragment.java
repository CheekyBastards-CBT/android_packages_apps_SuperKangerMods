/*
* Copyright (C) 2015 The VRToxin Project
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

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.UserHandle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.vrtoxin.R;
import com.android.vrtoxin.Utils;

import com.android.vrtoxin.utils.Helpers;

import com.android.vrtoxin.preferences.ColorPickerPreference;

public class RecentsFragment extends PreferenceFragment implements
			Preference.OnPreferenceChangeListener {

    public RecentsFragment(){}

    private static final String SYSTEMUI_RECENTS_MEM_DISPLAY = "systemui_recents_mem_display";
    private static final String MEMORY_BAR_CAT_COLORS = "memory_bar";
    private static final String PREF_CAT_CLEAR_ALL = "recents_panel";
    private static final String SHOW_CLEAR_ALL_RECENTS = "show_clear_all_recents";
    private static final String RECENTS_CLEAR_ALL_LOCATION = "recents_clear_all_location";
    private static final String PREF_CLEAR_ALL_USE_ICON_COLOR = "recents_clear_all_use_icon_color";
    private static final String PREF_CLEAR_ALL_BG_COLOR = "recent_apps_clear_all_bg_color";
    private static final String PREF_CLEAR_ALL_ICON_COLOR = "recent_apps_clear_all_icon_color";
    private static final String MEM_TEXT_COLOR = "mem_text_color";
    private static final String MEMORY_BAR_COLOR = "memory_bar_color";
    private static final String MEMORY_BAR_USED_COLOR = "memory_bar_used_color";
    private static final String RECENTS_USE_OMNISWITCH = "recents_use_omniswitch";
    private static final String OMNISWITCH_START_SETTINGS = "omniswitch_start_settings";
    public static final String OMNISWITCH_PACKAGE_NAME = "org.omnirom.omniswitch";
    public static Intent INTENT_OMNISWITCH_SETTINGS = new Intent(Intent.ACTION_MAIN)
            .setClassName(OMNISWITCH_PACKAGE_NAME, OMNISWITCH_PACKAGE_NAME + ".SettingsActivity");
    private static final String CATEGORY_OMNI_RECENTS = "omni_recents";
    private static final String RECENTS_EMPTY_VRTOXIN_LOGO = "recents_empty_vrtoxin_logo";
    private static final String KEY_SCREEN_PINNING = "screen_pinning_settings";
    private static final String RECENTS_FULL_SCREEN_CAT_OPTIONS = "recents_full_screen_cat_options";
    private static final String RECENTS_FULL_SCREEN = "recents_full_screen";
    private static final String RECENTS_FULL_SCREEN_CLOCK_COLOR = "recents_full_screen_clock_color";
    private static final String RECENTS_FULL_SCREEN_DATE_COLOR = "recents_full_screen_date_color";
    private static final String SLIM_RECENTS = "slim_recents_settings";
    
    private static final int DEFAULT_COLOR = 0xff009688;
    private static final int WHITE = 0xffffffff;
    private static final int VRTOXIN_BLUE = 0xff33b5e5;

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET = 0;
    
    private SwitchPreference mMemoryBar;
    private SwitchPreference mRecentsClearAll;
    private ListPreference mRecentsClearAllLocation;
    private SwitchPreference mClearAllUseIconColor;
    private ColorPickerPreference mClearAllIconColor;
    private ColorPickerPreference mClearAllBgColor;
    private ColorPickerPreference mMemTextColor;
    private ColorPickerPreference mMemBarColor;
    private ColorPickerPreference mMemBarUsedColor;
    private PreferenceScreen mScreenPinning;
    private SwitchPreference mRecentsUseOmniSwitch;
    private Preference mOmniSwitchSettings;
    private boolean mOmniSwitchInitCalled;
    private PreferenceCategory mOmniSwitch;
    private SwitchPreference mRecentsStyle;
    private SwitchPreference mRecentsFullScreen;
    private ColorPickerPreference mClockColor;
    private ColorPickerPreference mDateColor;
    private PreferenceCategory mMemColors;
    private PreferenceCategory mRecentsOptions;
    private Preference mSlimRecents;

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
        addPreferencesFromResource(R.xml.recents_fragment);

        PreferenceScreen prefSet = getPreferenceScreen();
        mResolver = getActivity().getContentResolver();
        int intColor;
        String hexColor;
        PackageManager pm = getActivity().getPackageManager();

        PreferenceCategory catClearAll =
                (PreferenceCategory) findPreference(PREF_CAT_CLEAR_ALL);

        mSlimRecents = (Preference)findPreference(SLIM_RECENTS);

        mRecentsStyle = (SwitchPreference) prefSet.findPreference(RECENTS_EMPTY_VRTOXIN_LOGO);
        mRecentsStyle.setChecked(Settings.System.getInt(mResolver,
            Settings.System.RECENTS_EMPTY_VRTOXIN_LOGO, 0) == 1);
        mRecentsStyle.setOnPreferenceChangeListener(this);

        boolean enableMemoryBar = Settings.System.getInt(mResolver,
                Settings.System.SYSTEMUI_RECENTS_MEM_DISPLAY, 0) == 1;
        mMemoryBar = (SwitchPreference) findPreference(SYSTEMUI_RECENTS_MEM_DISPLAY);
        mMemoryBar.setChecked(enableMemoryBar);
        mMemoryBar.setOnPreferenceChangeListener(this);

        mRecentsClearAll = (SwitchPreference) prefSet.findPreference(SHOW_CLEAR_ALL_RECENTS);
        mRecentsClearAll.setChecked(Settings.System.getIntForUser(mResolver,
            Settings.System.SHOW_CLEAR_ALL_RECENTS, 1, UserHandle.USER_CURRENT) == 1);
        mRecentsClearAll.setOnPreferenceChangeListener(this);

        mRecentsClearAllLocation = (ListPreference) prefSet.findPreference(RECENTS_CLEAR_ALL_LOCATION);
        int location = Settings.System.getIntForUser(mResolver,
                Settings.System.RECENTS_CLEAR_ALL_LOCATION, 3, UserHandle.USER_CURRENT);
        mRecentsClearAllLocation.setValue(String.valueOf(location));
        mRecentsClearAllLocation.setOnPreferenceChangeListener(this);
        updateRecentsLocation(location);
        
        mClearAllUseIconColor = (SwitchPreference) findPreference(PREF_CLEAR_ALL_USE_ICON_COLOR);
        boolean clearAllUseIconColor = Settings.System.getInt(mResolver,
               Settings.System.RECENTS_CLEAR_ALL_USE_ICON_COLOR, 0) == 1;
        mClearAllUseIconColor.setChecked(clearAllUseIconColor);
        mClearAllUseIconColor.setOnPreferenceChangeListener(this);

        mClearAllBgColor = (ColorPickerPreference) findPreference(PREF_CLEAR_ALL_BG_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.RECENT_APPS_CLEAR_ALL_BG_COLOR, DEFAULT_COLOR); 
        mClearAllBgColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mClearAllBgColor.setSummary(hexColor);
        mClearAllBgColor.setOnPreferenceChangeListener(this);

        if (clearAllUseIconColor) {
            mClearAllIconColor = (ColorPickerPreference) findPreference(PREF_CLEAR_ALL_ICON_COLOR);
            intColor = Settings.System.getInt(mResolver,
                    Settings.System.RECENT_APPS_CLEAR_ALL_ICON_COLOR, WHITE); 
            mClearAllIconColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mClearAllIconColor.setSummary(hexColor);
            mClearAllIconColor.setOnPreferenceChangeListener(this);
        } else {
            catClearAll.removePreference(findPreference(PREF_CLEAR_ALL_ICON_COLOR));
        }

        mMemColors = (PreferenceCategory) findPreference(MEMORY_BAR_CAT_COLORS);
        if (enableMemoryBar) {
            mMemTextColor =
                    (ColorPickerPreference) findPreference(MEM_TEXT_COLOR);
            intColor = Settings.System.getInt(mResolver,
                    Settings.System.MEM_TEXT_COLOR, WHITE); 
            mMemTextColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mMemTextColor.setSummary(hexColor);
            mMemTextColor.setOnPreferenceChangeListener(this);

            mMemBarColor =
                    (ColorPickerPreference) findPreference(MEMORY_BAR_COLOR);
            intColor = Settings.System.getInt(mResolver,
                    Settings.System.MEMORY_BAR_COLOR, WHITE); 
            mMemBarColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mMemBarColor.setSummary(hexColor);
            mMemBarColor.setOnPreferenceChangeListener(this);

            mMemBarUsedColor =
                    (ColorPickerPreference) findPreference(MEMORY_BAR_USED_COLOR);
            intColor = Settings.System.getInt(mResolver,
                    Settings.System.MEMORY_BAR_USED_COLOR, WHITE); 
            mMemBarUsedColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mMemBarUsedColor.setSummary(hexColor);
            mMemBarUsedColor.setOnPreferenceChangeListener(this);
        } else {
            prefSet.removePreference(mMemColors);
        }

        final boolean screenPinning = Settings.System.getInt(mResolver,
                Settings.System.LOCK_TO_APP_ENABLED, 0) == 1;
        if (mScreenPinning != null) {
            mScreenPinning.setSummary(screenPinning ?
                getResources().getString(R.string.switch_on_text) :
                getResources().getString(R.string.switch_off_text));
        }

        mRecentsUseOmniSwitch = (SwitchPreference)
                findPreference(RECENTS_USE_OMNISWITCH);

        try {
            mRecentsUseOmniSwitch.setChecked(Settings.System.getInt(mResolver,
                    Settings.System.RECENTS_USE_OMNISWITCH) == 1);
            mOmniSwitchInitCalled = true;
        } catch(SettingNotFoundException e){
            // if the settings value is unset
        }
        mRecentsUseOmniSwitch.setOnPreferenceChangeListener(this);

        mOmniSwitchSettings = (Preference)
                findPreference(OMNISWITCH_START_SETTINGS);
        mOmniSwitchSettings.setEnabled(mRecentsUseOmniSwitch.isChecked());

        mOmniSwitch = (PreferenceCategory) findPreference(CATEGORY_OMNI_RECENTS);
        if (!Utils.isPackageInstalled(getActivity(), OMNISWITCH_PACKAGE_NAME)) {
            prefs.removePreference(mOmniSwitch);
        }

        boolean enableRecentsFullScreen = Settings.System.getInt(mResolver,
                Settings.System.RECENTS_FULL_SCREEN, 0) == 1;
        mRecentsFullScreen = (SwitchPreference) findPreference(RECENTS_FULL_SCREEN);
        mRecentsFullScreen.setChecked(enableRecentsFullScreen);
        mRecentsFullScreen.setOnPreferenceChangeListener(this);

        mRecentsOptions = (PreferenceCategory) findPreference(RECENTS_FULL_SCREEN_CAT_OPTIONS);
        if (enableRecentsFullScreen) {
            mClockColor =
                    (ColorPickerPreference) findPreference(RECENTS_FULL_SCREEN_CLOCK_COLOR);
            intColor = Settings.System.getInt(mResolver,
                    Settings.System.RECENTS_FULL_SCREEN_CLOCK_COLOR, WHITE);
            mClockColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mClockColor.setSummary(hexColor);
            mClockColor.setOnPreferenceChangeListener(this);

            mDateColor =
                    (ColorPickerPreference) findPreference(RECENTS_FULL_SCREEN_DATE_COLOR);
            intColor = Settings.System.getInt(mResolver,
                    Settings.System.RECENTS_FULL_SCREEN_DATE_COLOR, WHITE);
            mDateColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mDateColor.setSummary(hexColor);
            mDateColor.setOnPreferenceChangeListener(this);
        } else {
            prefSet.removePreference(mRecentsOptions);
        }
        
        setHasOptionsMenu(true);
    }
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add(0, MENU_RESET, 0, R.string.reset)
                .setIcon(com.android.internal.R.drawable.ic_settings_backup_restore)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_RESET:
                showDialogInner(DLG_RESET);
                return true;
             default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mOmniSwitchSettings){
            startActivity(INTENT_OMNISWITCH_SETTINGS);
            return true;
        }

        if (preference == mSlimRecents) {
            ((VRToxinActivity)getActivity()).displaySubFrag(getString(R.string.slim_recent_category));

            return true;
        }
         return super.onPreferenceTreeClick(preferenceScreen, preference);
     }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        if (preference == mMemoryBar) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(mResolver,
                    Settings.System.SYSTEMUI_RECENTS_MEM_DISPLAY,
                    value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mRecentsClearAll) {
            boolean show = (Boolean) objValue;
            Settings.System.putIntForUser(getActivity().getContentResolver(),
                    Settings.System.SHOW_CLEAR_ALL_RECENTS, show ? 1 : 0, UserHandle.USER_CURRENT);
            return true;
        } else if (preference == mRecentsClearAllLocation) {
            int location = Integer.valueOf((String) objValue);
            Settings.System.putIntForUser(getActivity().getContentResolver(),
                    Settings.System.RECENTS_CLEAR_ALL_LOCATION, location, UserHandle.USER_CURRENT);
            updateRecentsLocation(location);
            refreshSettings();
            return true;
        } else if (preference == mClearAllUseIconColor) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(mResolver,
                    Settings.System.RECENTS_CLEAR_ALL_USE_ICON_COLOR,
                    value ? 1 : 0);
            refreshSettings();
        } else if (preference == mClearAllBgColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(objValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.RECENT_APPS_CLEAR_ALL_BG_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mClearAllIconColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(objValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.RECENT_APPS_CLEAR_ALL_ICON_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mMemTextColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(objValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.MEM_TEXT_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mMemBarColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(objValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.MEMORY_BAR_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mMemBarUsedColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(objValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.MEMORY_BAR_USED_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mScreenPinning) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(mResolver,
                    Settings.System.LOCK_TO_APP_ENABLED,
                    value ? 1 : 0);
            return true;
        } else if (preference == mRecentsUseOmniSwitch) {
            boolean value = (Boolean) objValue;
            if (value && !mOmniSwitchInitCalled){
                openOmniSwitchFirstTimeWarning();
                mOmniSwitchInitCalled = true;
            }
            Settings.System.putInt(mResolver,
                    Settings.System.RECENTS_USE_OMNISWITCH,
                    value ? 1 : 0);
            mOmniSwitchSettings.setEnabled(value);
            return true;
        } else if (preference == mRecentsStyle) {
            boolean show = (Boolean) objValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.RECENTS_EMPTY_VRTOXIN_LOGO, show ? 1 : 0);
            Helpers.restartSystemUI();
            return true;
        } else if (preference == mRecentsFullScreen) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(mResolver,
                    Settings.System.RECENTS_FULL_SCREEN,
                    value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mClockColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(objValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.RECENTS_FULL_SCREEN_CLOCK_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mDateColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(objValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.RECENTS_FULL_SCREEN_DATE_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        }
        return false;
    }

    private void updateRecentsLocation(int value) {
        Resources res = getResources();
        int summary = -1;

        Settings.System.putInt(mResolver, Settings.System.RECENTS_CLEAR_ALL_LOCATION, value);

        if (value == 0) {
            Settings.System.putInt(mResolver, Settings.System.RECENTS_CLEAR_ALL_LOCATION, 0);
            summary = R.string.recents_clear_all_location_top_right;
        } else if (value == 1) {
            Settings.System.putInt(mResolver, Settings.System.RECENTS_CLEAR_ALL_LOCATION, 1);
            summary = R.string.recents_clear_all_location_top_left;
        } else if (value == 2) {
            Settings.System.putInt(mResolver, Settings.System.RECENTS_CLEAR_ALL_LOCATION, 2);
            summary = R.string.recents_clear_all_location_top_center;
        } else if (value == 3) {
            Settings.System.putInt(mResolver, Settings.System.RECENTS_CLEAR_ALL_LOCATION, 3);
            summary = R.string.recents_clear_all_location_bottom_right;
        } else if (value == 4) {
            Settings.System.putInt(mResolver, Settings.System.RECENTS_CLEAR_ALL_LOCATION, 4);
            summary = R.string.recents_clear_all_location_bottom_left;
        } else if (value == 5) {
            Settings.System.putInt(mResolver, Settings.System.RECENTS_CLEAR_ALL_LOCATION, 5);
            summary = R.string.recents_clear_all_location_bottom_center;
        }
        if (mRecentsClearAllLocation != null && summary != -1) {
            mRecentsClearAllLocation.setSummary(res.getString(summary));
        }
    }

    private void openOmniSwitchFirstTimeWarning() {
        new AlertDialog.Builder(getActivity())
                .setTitle(getResources().getString(R.string.omniswitch_first_time_title))
                .setMessage(getResources().getString(R.string.omniswitch_first_time_message))
                .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                }).show();
    }
    
    private void showDialogInner(int id) {
        DialogFragment newFragment = MyAlertDialogFragment.newInstance(id);
        newFragment.setTargetFragment(this, 0);
        newFragment.show(getFragmentManager(), "dialog " + id);
    }

    public static class MyAlertDialogFragment extends DialogFragment {

        public static MyAlertDialogFragment newInstance(int id) {
            MyAlertDialogFragment frag = new MyAlertDialogFragment();
            Bundle args = new Bundle();
            args.putInt("id", id);
            frag.setArguments(args);
            return frag;
        }

        RecentsFragment getOwner() {
            return (RecentsFragment) getTargetFragment();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int id = getArguments().getInt("id");
            switch (id) {
                case DLG_RESET:
                    return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.reset)
                    .setMessage(R.string.reset_message)
                    .setNegativeButton(R.string.cancel, null)
                    .setNeutralButton(R.string.reset_android,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.SHOW_CLEAR_ALL_RECENTS, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENTS_CLEAR_ALL_LOCATION, 3);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENTS_CLEAR_ALL_USE_ICON_COLOR, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENT_APPS_CLEAR_ALL_BG_COLOR,
                                    DEFAULT_COLOR);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENT_APPS_CLEAR_ALL_ICON_COLOR,
                                    WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.SYSTEMUI_RECENTS_MEM_DISPLAY, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.MEM_TEXT_COLOR,
                                    WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.MEMORY_BAR_COLOR,
                                    WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.MEMORY_BAR_USED_COLOR,
                                    WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENTS_EMPTY_VRTOXIN_LOGO, 0);
                                    Helpers.restartSystemUI();
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENTS_FULL_SCREEN, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENTS_FULL_SCREEN_CLOCK_COLOR,
                                    WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENTS_FULL_SCREEN_DATE_COLOR,
                                    WHITE);
                            getOwner().refreshSettings();
                        }
                    })
                    .setPositiveButton(R.string.reset_vrtoxin,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.SHOW_CLEAR_ALL_RECENTS, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENTS_CLEAR_ALL_LOCATION, 5);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENTS_CLEAR_ALL_USE_ICON_COLOR, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENT_APPS_CLEAR_ALL_BG_COLOR,
                                    VRTOXIN_BLUE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENT_APPS_CLEAR_ALL_ICON_COLOR,
                                    0xff00ff00);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.SYSTEMUI_RECENTS_MEM_DISPLAY, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.MEM_TEXT_COLOR,
                                    VRTOXIN_BLUE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.MEMORY_BAR_COLOR,
                                    VRTOXIN_BLUE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.MEMORY_BAR_USED_COLOR,
                                    0xff00ff00);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENTS_EMPTY_VRTOXIN_LOGO, 1);
                                    Helpers.restartSystemUI();
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENTS_FULL_SCREEN, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENTS_FULL_SCREEN_CLOCK_COLOR,
                                    VRTOXIN_BLUE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENTS_FULL_SCREEN_DATE_COLOR,
                                    VRTOXIN_BLUE);
                            getOwner().refreshSettings();
                        }
                    })
                    .create();
            }
            throw new IllegalArgumentException("unknown id " + id);
        }

        @Override
        public void onCancel(DialogInterface dialog) {

        }
    }

}
