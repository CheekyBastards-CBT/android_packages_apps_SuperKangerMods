/* 
 * Copyright (C) 2015 DarkKat
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

package com.android.vrtoxin.lockscreen;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.ListPreference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.vrtoxin.R;

import com.android.vrtoxin.preferences.ColorPickerPreference;

public class LockScreenColorSettings extends PreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    public LockScreenColorSettings(){}

    private static final String PREF_CAT_COLORS =
            "lock_screen_colors_cat_colors";
    private static final String PREF_BUTTONS_BAR_ICON_COLOR_MODE =
            "colors_buttons_bar_icon_color_mode";
    private static final String PREF_BUTTONS_BAR_RIPPLE_COLOR_MODE =
            "colors_buttons_bar_ripple_color_mode";
    private static final String PREF_BUTTONS_DEFAULT_COLORIZE_CUSTOM_ICONS =
            "colors_buttons_default_colorize_custom_icons";
    private static final String PREF_TEXT_COLOR =
            "colors_text_color";
    private static final String PREF_ICON_COLOR =
            "colors_icon_color";
    private static final String LOCKSCREEN_INDICATION_TEXT_COLOR =
            "lockscreen_indication_text_color";
    private static final String LOCKSCREEN_OWNER_INFO_COLOR = 
            "lockscreen_owner_info_color";
    private static final String LOCKSCREEN_ALARM_COLOR = 
            "lockscreen_alarm_color";
    private static final String LOCKSCREEN_CLOCK_COLOR = 
            "lockscreen_clock_color";
    private static final String LOCKSCREEN_CLOCK_DATE_COLOR = 
            "lockscreen_clock_date_color";
    private static final String PREF_BUTTONS_BAR_RIPPLE_COLOR =
            "colors_buttons_bar_ripple_color";

    private static final int DEFAULT_COLOR = 0xffffffff;
    private static final int VRTOXIN_BLUE = 0xff1976D2;

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET = 0;

    private ListPreference mButtonsBarIconColorMode;
    private ListPreference mButtonsBarRippleColorMode;
    private ColorPickerPreference mTextColor;
    private ColorPickerPreference mIconColor;
    private ColorPickerPreference mLockscreenIndicationTextColorPicker;
    private ColorPickerPreference mLockscreenOwnerInfoColorPicker;
    private ColorPickerPreference mLockscreenAlarmColorPicker;
    private ColorPickerPreference mLockscreenClockColorPicker;
    private ColorPickerPreference mLockscreenClockDateColorPicker;
    private ColorPickerPreference mButtonsBarRippleColor;

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

        addPreferencesFromResource(R.xml.lock_screen_color_settings);
        mResolver = getActivity().getContentResolver();

        boolean colorizeButtonsBarRipple = Settings.System.getInt(mResolver,
                Settings.System.LOCK_SCREEN_BUTTONS_BAR_RIPPLE_COLOR_MODE, 2) == 1;

        int intColor;
        String hexColor;
        int intValue;

        PreferenceCategory catColors =
                (PreferenceCategory) findPreference(PREF_CAT_COLORS);

        mButtonsBarIconColorMode =
                (ListPreference) findPreference(PREF_BUTTONS_BAR_ICON_COLOR_MODE);
        intValue = Settings.System.getInt(mResolver,
                Settings.System.LOCK_SCREEN_BUTTONS_BAR_ICON_COLOR_MODE, 0);
        mButtonsBarIconColorMode.setValue(String.valueOf(intValue));
        mButtonsBarIconColorMode.setSummary(mButtonsBarIconColorMode.getEntry());
        mButtonsBarIconColorMode.setOnPreferenceChangeListener(this);

        mButtonsBarRippleColorMode =
                (ListPreference) findPreference(PREF_BUTTONS_BAR_RIPPLE_COLOR_MODE);
        intValue = Settings.System.getInt(mResolver,
                Settings.System.LOCK_SCREEN_BUTTONS_BAR_RIPPLE_COLOR_MODE, 2);
        mButtonsBarRippleColorMode.setValue(String.valueOf(intValue));
        mButtonsBarRippleColorMode.setSummary(mButtonsBarRippleColorMode.getEntry());
        mButtonsBarRippleColorMode.setOnPreferenceChangeListener(this);

        mTextColor =
                (ColorPickerPreference) findPreference(PREF_TEXT_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.LOCK_SCREEN_TEXT_COLOR,
                DEFAULT_COLOR); 
        mTextColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mTextColor.setSummary(hexColor);
        mTextColor.setOnPreferenceChangeListener(this);
        mTextColor.setAlphaSliderEnabled(true);

        mIconColor =
                (ColorPickerPreference) findPreference(PREF_ICON_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.LOCK_SCREEN_ICON_COLOR,
                DEFAULT_COLOR); 
        mIconColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mIconColor.setSummary(hexColor);
        mIconColor.setOnPreferenceChangeListener(this);
        mIconColor.setAlphaSliderEnabled(true);

        mLockscreenIndicationTextColorPicker = (ColorPickerPreference) findPreference(LOCKSCREEN_INDICATION_TEXT_COLOR);
        mLockscreenIndicationTextColorPicker.setOnPreferenceChangeListener(this);
        intColor = Settings.System.getInt(mResolver,
                    Settings.System.LOCKSCREEN_INDICATION_TEXT_COLOR, DEFAULT_COLOR);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mLockscreenIndicationTextColorPicker.setSummary(hexColor);
        mLockscreenIndicationTextColorPicker.setNewPreviewColor(intColor);

        mLockscreenOwnerInfoColorPicker = (ColorPickerPreference) findPreference(LOCKSCREEN_OWNER_INFO_COLOR);
        mLockscreenOwnerInfoColorPicker.setOnPreferenceChangeListener(this);
        intColor = Settings.System.getInt(mResolver,
                    Settings.System.LOCKSCREEN_OWNER_INFO_COLOR, DEFAULT_COLOR);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mLockscreenOwnerInfoColorPicker.setSummary(hexColor);
        mLockscreenOwnerInfoColorPicker.setNewPreviewColor(intColor);

        mLockscreenAlarmColorPicker = (ColorPickerPreference) findPreference(LOCKSCREEN_ALARM_COLOR);
        mLockscreenAlarmColorPicker.setOnPreferenceChangeListener(this);
        intColor = Settings.System.getInt(mResolver,
                    Settings.System.LOCKSCREEN_ALARM_COLOR, DEFAULT_COLOR);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mLockscreenAlarmColorPicker.setSummary(hexColor);
        mLockscreenAlarmColorPicker.setNewPreviewColor(intColor);

        mLockscreenClockColorPicker = (ColorPickerPreference) findPreference(LOCKSCREEN_CLOCK_COLOR);
        mLockscreenClockColorPicker.setOnPreferenceChangeListener(this);
        intColor = Settings.System.getInt(mResolver,
                    Settings.System.LOCKSCREEN_CLOCK_COLOR, DEFAULT_COLOR);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mLockscreenClockColorPicker.setSummary(hexColor);
        mLockscreenClockColorPicker.setNewPreviewColor(intColor);

        mLockscreenClockDateColorPicker = (ColorPickerPreference) findPreference(LOCKSCREEN_CLOCK_DATE_COLOR);
        mLockscreenClockDateColorPicker.setOnPreferenceChangeListener(this);
        intColor = Settings.System.getInt(mResolver,
                    Settings.System.LOCKSCREEN_CLOCK_DATE_COLOR, DEFAULT_COLOR);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mLockscreenClockDateColorPicker.setSummary(hexColor);
        mLockscreenClockDateColorPicker.setNewPreviewColor(intColor);

        if (colorizeButtonsBarRipple) {
            mButtonsBarRippleColor =
                    (ColorPickerPreference) findPreference(PREF_BUTTONS_BAR_RIPPLE_COLOR);
            intColor = Settings.System.getInt(mResolver,
                    Settings.System.LOCK_SCREEN_BUTTONS_BAR_RIPPLE_COLOR, DEFAULT_COLOR); 
            mButtonsBarRippleColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mButtonsBarRippleColor.setSummary(hexColor);
            mButtonsBarRippleColor.setOnPreferenceChangeListener(this);
        } else {
            catColors.removePreference(findPreference("colors_buttons_bar_ripple_color"));
        }

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add(0, MENU_RESET, 0, R.string.reset)
                .setIcon(com.android.internal.R.drawable.ic_settings_backup_restore)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_RESET:
                showDialogInner(DLG_RESET);
                return true;
             default:
                return super.onContextItemSelected(item);
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String hex;
        int intHex;
        boolean value;
        int intValue;
        int index;

        if (preference == mButtonsBarIconColorMode) {
            intValue = Integer.valueOf((String) newValue);
            index = mButtonsBarIconColorMode.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                  Settings.System.LOCK_SCREEN_BUTTONS_BAR_ICON_COLOR_MODE, intValue);
            preference.setSummary(mButtonsBarIconColorMode.getEntries()[index]);
            return true;
        } else if (preference == mButtonsBarRippleColorMode) {
            intValue = Integer.valueOf((String) newValue);
            index = mButtonsBarRippleColorMode.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.LOCK_SCREEN_BUTTONS_BAR_RIPPLE_COLOR_MODE, intValue);
            preference.setSummary(mButtonsBarRippleColorMode.getEntries()[index]);
            refreshSettings();
            return true;
        } else if (preference == mTextColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.LOCK_SCREEN_TEXT_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mIconColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.LOCK_SCREEN_ICON_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mLockscreenIndicationTextColorPicker) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.LOCKSCREEN_INDICATION_TEXT_COLOR, intHex);
            return true;
        } else if (preference == mLockscreenOwnerInfoColorPicker) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.LOCKSCREEN_OWNER_INFO_COLOR, intHex);
            return true;
        } else if (preference == mLockscreenAlarmColorPicker) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.LOCKSCREEN_ALARM_COLOR, intHex);
            return true;
        } else if (preference == mLockscreenClockColorPicker) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.LOCKSCREEN_CLOCK_COLOR, intHex);
            return true;
        } else if (preference == mLockscreenClockDateColorPicker) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.LOCKSCREEN_CLOCK_DATE_COLOR, intHex);
            return true;
        } else if (preference == mButtonsBarRippleColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.LOCK_SCREEN_BUTTONS_BAR_RIPPLE_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        }
        return false;
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

        LockScreenColorSettings getOwner() {
            return (LockScreenColorSettings) getTargetFragment();
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
                                    Settings.System.LOCK_SCREEN_BUTTONS_BAR_ICON_COLOR_MODE, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.LOCK_SCREEN_BUTTONS_BAR_RIPPLE_COLOR_MODE, 2);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.LOCK_SCREEN_TEXT_COLOR,
                                    DEFAULT_COLOR);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.LOCK_SCREEN_ICON_COLOR,
                                    DEFAULT_COLOR);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.LOCKSCREEN_INDICATION_TEXT_COLOR,
                                    DEFAULT_COLOR);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.LOCKSCREEN_OWNER_INFO_COLOR,
                                    DEFAULT_COLOR);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.LOCKSCREEN_ALARM_COLOR,
                                    DEFAULT_COLOR);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.LOCKSCREEN_CLOCK_COLOR,
                                    DEFAULT_COLOR);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.LOCKSCREEN_CLOCK_DATE_COLOR,
                                    DEFAULT_COLOR);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.LOCK_SCREEN_BUTTONS_BAR_RIPPLE_COLOR,
                                    DEFAULT_COLOR);
                            getOwner().refreshSettings();
                        }
                    })
                    .setPositiveButton(R.string.reset_vrtoxin,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.LOCK_SCREEN_BUTTONS_BAR_ICON_COLOR_MODE, 2);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.LOCK_SCREEN_BUTTONS_BAR_RIPPLE_COLOR_MODE, 2);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.LOCK_SCREEN_TEXT_COLOR,
                                    VRTOXIN_BLUE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.LOCK_SCREEN_ICON_COLOR,
                                    VRTOXIN_BLUE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.LOCKSCREEN_INDICATION_TEXT_COLOR,
                                    VRTOXIN_BLUE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.LOCKSCREEN_OWNER_INFO_COLOR,
                                    VRTOXIN_BLUE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.LOCKSCREEN_ALARM_COLOR,
                                    VRTOXIN_BLUE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.LOCKSCREEN_CLOCK_COLOR,
                                    VRTOXIN_BLUE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.LOCKSCREEN_CLOCK_DATE_COLOR,
                                    VRTOXIN_BLUE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.LOCK_SCREEN_BUTTONS_BAR_RIPPLE_COLOR,
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
