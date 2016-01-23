/*
 * Copyright (C) 2015 The VRToxin Project
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

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.ListPreference;
import android.preference.PreferenceScreen;
import android.preference.PreferenceFragment;
import android.preference.SlimSeekBarPreference;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import android.support.annotation.NonNull;

import com.android.vrtoxin.R;
import com.android.vrtoxin.preferences.ColorPickerPreference;
import com.android.vrtoxin.preferences.SeekBarPreference;

public class PowerMenuFragment extends PreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    public PowerMenuFragment(){}

    //VRToxinActivity
    private static final String POWERACTIONSFRAG = "power_menu_actions";

    public static final String KEY_ACTION_LISTVIEW_PACKAGE_NAME = "com.android.settings";
    public static final String KEY_ACTION_LISTVIEW_CLASS_NAME = "com.android.settings.Settings$ActionListViewSettingsActivity";

    private Preference mPowerActions;

    private static final String ADVANCED_REBOOT_KEY = "advanced_reboot";
    private static final String PREF_ON_THE_GO_ALPHA = "on_the_go_alpha";
    private static final String PREF_BG_COLOR = "power_menu_bg_color";
    private static final String PREF_ICON_NORMAL_COLOR = "power_menu_icon_normal_color";
    private static final String PREF_ICON_ENABLED_SELECTED_COLOR = "power_menu_icon_enabled_selected_color";
    private static final String PREF_RIPPLE_COLOR = "power_menu_ripple_color";
    private static final String PREF_TEXT_COLOR = "power_menu_text_color";
    private static final String POWER_MENU_ANIMATIONS = "power_menu_animations";
    private static final String PREF_TRANSPARENT_POWER_DIALOG_DIM = "transparent_power_dialog_dim";

    private static final int WHITE = 0xffffffff;
    private static final int VRTOXIN_BLUE = 0xff33b5e5;
    private static final int MATERIAL_TEAL_500 = 0xff009688;

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET = 0;

    private SwitchPreference mAdvancedReboot;
    private SlimSeekBarPreference mOnTheGoAlphaPref;

    private ColorPickerPreference mBgColor;
    private ColorPickerPreference mIconNormalColor;
    private ColorPickerPreference mIconEnabledSelectedColor;
    private ColorPickerPreference mRippleColor;
    private ColorPickerPreference mTextColor;
    ListPreference mPowerMenuAnimations;
    private SeekBarPreference mPowerDialogDim;
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

        addPreferencesFromResource(R.xml.powermenu_fragment);
        mResolver = getActivity().getContentResolver();

        //VRToxinActivity
        mPowerActions = (Preference)findPreference(POWERACTIONSFRAG);

        int intColor;
        String hexColor;

        mAdvancedReboot = (SwitchPreference) findPreference(ADVANCED_REBOOT_KEY);
        mAdvancedReboot.setChecked(Settings.System.getInt(mResolver,
            Settings.System.ADVANCED_REBOOT, 1) == 1);
        mAdvancedReboot.setOnPreferenceChangeListener(this);

        mOnTheGoAlphaPref = (SlimSeekBarPreference) findPreference(PREF_ON_THE_GO_ALPHA);
        mOnTheGoAlphaPref.setDefault(50);
        mOnTheGoAlphaPref.setInterval(1);
        mOnTheGoAlphaPref.setOnPreferenceChangeListener(this);

        mBgColor =
                (ColorPickerPreference) findPreference(PREF_BG_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.POWER_MENU_BG_COLOR,
                WHITE); 
        mBgColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mBgColor.setSummary(hexColor);
        mBgColor.setOnPreferenceChangeListener(this);

        mIconNormalColor =
                (ColorPickerPreference) findPreference(PREF_ICON_NORMAL_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.POWER_MENU_ICON_NORMAL_COLOR,
                WHITE); 
        mIconNormalColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (MATERIAL_TEAL_500 & intColor));
        mIconNormalColor.setSummary(hexColor);
        mIconNormalColor.setOnPreferenceChangeListener(this);

        mIconEnabledSelectedColor =
                (ColorPickerPreference) findPreference(PREF_ICON_ENABLED_SELECTED_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.POWER_MENU_ICON_ENABLED_SELECTED_COLOR,
                MATERIAL_TEAL_500); 
        mIconEnabledSelectedColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mIconEnabledSelectedColor.setSummary(hexColor);
        mIconEnabledSelectedColor.setOnPreferenceChangeListener(this);

        mRippleColor =
                (ColorPickerPreference) findPreference(PREF_RIPPLE_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.POWER_MENU_RIPPLE_COLOR, WHITE); 
        mRippleColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mRippleColor.setSummary(hexColor);
        mRippleColor.setOnPreferenceChangeListener(this);

        mTextColor =
                (ColorPickerPreference) findPreference(PREF_TEXT_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.POWER_MENU_TEXT_COLOR,
                WHITE); 
        mTextColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xff000000 & intColor));
        mTextColor.setSummary(hexColor);
        mTextColor.setOnPreferenceChangeListener(this);

        mPowerMenuAnimations = (ListPreference) findPreference(POWER_MENU_ANIMATIONS);
        mPowerMenuAnimations.setValue(String.valueOf(Settings.System.getInt(
                mResolver, Settings.System.POWER_MENU_ANIMATIONS, 0)));
        mPowerMenuAnimations.setSummary(mPowerMenuAnimations.getEntry());
        mPowerMenuAnimations.setOnPreferenceChangeListener(this);

        // Power/reboot dialog dim
        mPowerDialogDim =
                (SeekBarPreference) findPreference(PREF_TRANSPARENT_POWER_DIALOG_DIM);
        int powerDialogDim = Settings.System.getInt(mResolver,
                Settings.System.TRANSPARENT_POWER_DIALOG_DIM, 50);
        mPowerDialogDim.setValue(powerDialogDim / 1);
        mPowerDialogDim.setOnPreferenceChangeListener(this);

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

    //VRToxinActivity
    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen prefScreen, @NonNull Preference pref) {
        if (pref == mPowerActions) {
            Intent action = new Intent(Intent.ACTION_MAIN);
            ComponentName cn = new ComponentName(KEY_ACTION_LISTVIEW_PACKAGE_NAME, KEY_ACTION_LISTVIEW_CLASS_NAME);
            action.putExtra("actionMode", 4);
            action.putExtra("maxAllowedActions", 12);
            action.putExtra("disableLongpress", true);
            action.putExtra("disableIconPicker", true);
            action.putExtra("disableDeleteLastEntry", true);
            action.putExtra("actionValues", "power_menu_action_values");
            action.putExtra("actionEntries", "power_menu_action_entries");
            action.setComponent(cn);
            startActivity(action);

            return true;
        }

        return false;
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        int alpha = (Integer) newValue;
        String hex;
        int intHex;

        if (preference == mAdvancedReboot) {
            Settings.System.putInt(mResolver,
                    Settings.System.ADVANCED_REBOOT,
            (Boolean) newValue ? 1 : 0);
            return true;
        } else if (preference == mOnTheGoAlphaPref) {
            float val = Float.parseFloat((String) newValue);
            Settings.System.putFloat(mResolver, Settings.System.ON_THE_GO_ALPHA,
                    val / 100);
            return true;
        } else if (preference == mBgColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.POWER_MENU_BG_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mIconNormalColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.POWER_MENU_ICON_NORMAL_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mIconEnabledSelectedColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.POWER_MENU_ICON_ENABLED_SELECTED_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mRippleColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.POWER_MENU_RIPPLE_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mTextColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.POWER_MENU_TEXT_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mPowerMenuAnimations) {
            Settings.System.putInt(mResolver, Settings.System.POWER_MENU_ANIMATIONS,
                    Integer.valueOf((String) newValue));
            mPowerMenuAnimations.setValue(String.valueOf(newValue));
            mPowerMenuAnimations.setSummary(mPowerMenuAnimations.getEntry());
            return true;
        } else if (preference == mPowerDialogDim) {
            Settings.System.putInt(mResolver,
                    Settings.System.TRANSPARENT_POWER_DIALOG_DIM, alpha * 1);
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

        PowerMenuFragment getOwner() {
            return (PowerMenuFragment) getTargetFragment();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int id = getArguments().getInt("id");
            switch (id) {
                case DLG_RESET:
                    return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.reset)
                    .setMessage(R.string.dlg_reset_colors_message)
                    .setNegativeButton(R.string.cancel, null)
                    .setNeutralButton(R.string.reset_android,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.ADVANCED_REBOOT, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.POWER_MENU_BG_COLOR,
                                    WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.POWER_MENU_ICON_NORMAL_COLOR,
                                    VRTOXIN_BLUE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.POWER_MENU_ICON_ENABLED_SELECTED_COLOR,
                                    MATERIAL_TEAL_500);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.POWER_MENU_RIPPLE_COLOR,
                                    WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.POWER_MENU_TEXT_COLOR,
                                    0xff000000);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.POWER_MENU_ANIMATIONS, 0);
                            getOwner().refreshSettings();
                        }
                    })
                    .setPositiveButton(R.string.reset_vrtoxin,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.ADVANCED_REBOOT, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.POWER_MENU_BG_COLOR,
                                    0x00000000);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.POWER_MENU_ICON_NORMAL_COLOR,
                                    MATERIAL_TEAL_500);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.POWER_MENU_ICON_ENABLED_SELECTED_COLOR,
                                    VRTOXIN_BLUE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.POWER_MENU_RIPPLE_COLOR,
                                    VRTOXIN_BLUE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.POWER_MENU_TEXT_COLOR,
                                    VRTOXIN_BLUE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.POWER_MENU_ANIMATIONS, 1);
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
