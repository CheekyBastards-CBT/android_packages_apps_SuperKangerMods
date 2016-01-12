/*
 * Copyright (C) Copyright (C) 2015 DarkKat
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

package com.android.vrtoxin.recents;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;

import com.android.vrtoxin.R;

import com.android.vrtoxin.preferences.ColorPickerPreference;

public class SlimRecentsColorSettings extends PreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    public SlimRecentsColorSettings(){}

    private static final String PREF_CAT_COLORS =
            "slim_recents_cat_colors";
    private static final String PREF_USE_TEXT_COLOR =
            "slim_recents_use_text_color";
    private static final String PREF_USE_EXPAND_ICON_COLOR =
            "slim_recents_use_expand_icon_color";
    private static final String PREF_USE_EMPTY_ICON_COLOR =
            "slim_recents_use_empty_icon_color";
    private static final String PREF_BG_COLOR =
            "slim_recents_bg_color";
    private static final String PREF_CARD_BG_COLOR =
            "slim_recents_card_bg_color";
    private static final String PREF_TEXT_COLOR =
            "slim_recents_text_color";
    private static final String PREF_EXPAND_ICON_COLOR =
            "slim_recents_expand_icon_color";
    private static final String PREF_EMPTY_ICON_COLOR =
            "slim_recents_empty_icon_color";

    private SwitchPreference mUseTextColor;
    private SwitchPreference mUseExpandIconColor;
    private SwitchPreference mUseEmptyIconColor;
    private ColorPickerPreference mBgColor;
    private ColorPickerPreference mCardBgColor;
    private ColorPickerPreference mTextColor;
    private ColorPickerPreference mExpandIcontColor;
    private ColorPickerPreference mEmptyIcontColor;

    private static final int TRANSLUCENT_BLACK = 0x80000000;
    private static final int VRTOXIN_BLUE = 0xff1b1f23;
    private static final int HOLO_BLUE_LIGHT = 0xff33b5e5;
    private static final int WHITE = 0xffffffff;
    private static final int BLACK = 0xff000000;

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET = 0;

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

        addPreferencesFromResource(R.xml.slim_recents_color_settings);
        mResolver = getActivity().getContentResolver();

        int intColor = 0xffffffff;
        String hexColor = String.format("#%08x", (0xffffffff & 0xffffffff));

        mUseTextColor =
                (SwitchPreference) findPreference(PREF_USE_TEXT_COLOR);
        boolean useTextColor = Settings.System.getInt(mResolver,
                Settings.System.SLIM_RECENTS_USE_TEXT_COLOR, 0) == 1;
        mUseTextColor.setChecked(useTextColor);
        mUseTextColor.setOnPreferenceChangeListener(this);

        mUseExpandIconColor =
                (SwitchPreference) findPreference(PREF_USE_EXPAND_ICON_COLOR);
        boolean useExpandIconColor = Settings.System.getInt(mResolver,
                Settings.System.SLIM_RECENTS_USE_EXPAND_ICON_COLOR, 0) == 1;
        mUseExpandIconColor.setChecked(useExpandIconColor);
        mUseExpandIconColor.setOnPreferenceChangeListener(this);

        mUseEmptyIconColor =
                (SwitchPreference) findPreference(PREF_USE_EMPTY_ICON_COLOR);
        boolean useEmptyIconColor = Settings.System.getInt(mResolver,
                Settings.System.SLIM_RECENTS_USE_EMPTY_ICON_COLOR, 0) == 1;
        mUseEmptyIconColor.setChecked(useEmptyIconColor);
        mUseEmptyIconColor.setOnPreferenceChangeListener(this);

        mBgColor =
                (ColorPickerPreference) findPreference(PREF_BG_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.SLIM_RECENTS_BG_COLOR, TRANSLUCENT_BLACK);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mBgColor.setNewPreviewColor(intColor);
        mBgColor.setSummary(hexColor);
        mBgColor.setAlphaSliderEnabled(true);
        mBgColor.setOnPreferenceChangeListener(this);

        mCardBgColor =
                (ColorPickerPreference) findPreference(PREF_CARD_BG_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.SLIM_RECENTS_CARD_BG_COLOR, VRTOXIN_BLUE);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mCardBgColor.setNewPreviewColor(intColor);
        mCardBgColor.setSummary(hexColor);
        mCardBgColor.setAlphaSliderEnabled(true);
        mCardBgColor.setOnPreferenceChangeListener(this);

        PreferenceCategory catColors =
                (PreferenceCategory) findPreference(PREF_CAT_COLORS);
        if (useTextColor) {
            mTextColor =
                    (ColorPickerPreference) findPreference(PREF_TEXT_COLOR);
            intColor = Settings.System.getInt(mResolver,
                    Settings.System.SLIM_RECENTS_TEXT_COLOR, WHITE);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mTextColor.setNewPreviewColor(intColor);
            mTextColor.setSummary(hexColor);
            mTextColor.setAlphaSliderEnabled(true);
            mTextColor.setOnPreferenceChangeListener(this);
        } else {
            catColors.removePreference(findPreference(PREF_TEXT_COLOR));
        }

        if (useExpandIconColor) {
            mExpandIcontColor =
                    (ColorPickerPreference) findPreference(PREF_EXPAND_ICON_COLOR);
            intColor = Settings.System.getInt(mResolver,
                    Settings.System.SLIM_RECENTS_EXPAND_ICON_COLOR, WHITE);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mExpandIcontColor.setNewPreviewColor(intColor);
            mExpandIcontColor.setSummary(hexColor);
            mExpandIcontColor.setAlphaSliderEnabled(true);
            mExpandIcontColor.setOnPreferenceChangeListener(this);
        } else {
            catColors.removePreference(findPreference(PREF_EXPAND_ICON_COLOR));
        }

        if (useEmptyIconColor) {
            mEmptyIcontColor =
                    (ColorPickerPreference) findPreference(PREF_EMPTY_ICON_COLOR);
            intColor = Settings.System.getInt(mResolver,
                    Settings.System.SLIM_RECENTS_EMPTY_ICON_COLOR, WHITE);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mEmptyIcontColor.setNewPreviewColor(intColor);
            mEmptyIcontColor.setSummary(hexColor);
            mEmptyIcontColor.setAlphaSliderEnabled(true);
            mEmptyIcontColor.setOnPreferenceChangeListener(this);
        } else {
            catColors.removePreference(findPreference(PREF_EMPTY_ICON_COLOR));
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
        boolean value;
        int intHex;
        String hex;

        if (preference == mUseTextColor) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.SLIM_RECENTS_USE_TEXT_COLOR,
                    value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mUseExpandIconColor) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.SLIM_RECENTS_USE_EXPAND_ICON_COLOR,
                    value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mUseEmptyIconColor) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.SLIM_RECENTS_USE_EMPTY_ICON_COLOR,
                    value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mBgColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.SLIM_RECENTS_BG_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mCardBgColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.SLIM_RECENTS_CARD_BG_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mTextColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.SLIM_RECENTS_TEXT_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mExpandIcontColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.SLIM_RECENTS_EXPAND_ICON_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mEmptyIcontColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.SLIM_RECENTS_EMPTY_ICON_COLOR, intHex);
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

        SlimRecentsColorSettings getOwner() {
            return (SlimRecentsColorSettings) getTargetFragment();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int id = getArguments().getInt("id");
            switch (id) {
                case DLG_RESET:
                    return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.reset)
                    .setMessage(R.string.dlg_reset_values_message)
                    .setNegativeButton(R.string.cancel, null)
                    .setNeutralButton(R.string.reset_android,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.SLIM_RECENTS_USE_TEXT_COLOR, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.SLIM_RECENTS_USE_EXPAND_ICON_COLOR, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.SLIM_RECENTS_USE_EMPTY_ICON_COLOR, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.SLIM_RECENTS_BG_COLOR,
                                    TRANSLUCENT_BLACK);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.SLIM_RECENTS_CARD_BG_COLOR,
                                    VRTOXIN_BLUE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.SLIM_RECENTS_TEXT_COLOR,
                                    WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.SLIM_RECENTS_EXPAND_ICON_COLOR,
                                    WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.SLIM_RECENTS_EMPTY_ICON_COLOR,
                                    WHITE);
                            getOwner().refreshSettings();
                        }
                    })
                    .setPositiveButton(R.string.reset_vrtoxin,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.SLIM_RECENTS_USE_TEXT_COLOR, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.SLIM_RECENTS_USE_EXPAND_ICON_COLOR, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.SLIM_RECENTS_USE_EMPTY_ICON_COLOR, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.SLIM_RECENTS_BG_COLOR,
                                    TRANSLUCENT_BLACK);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.SLIM_RECENTS_CARD_BG_COLOR,
                                    VRTOXIN_BLUE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.SLIM_RECENTS_TEXT_COLOR,
                                    HOLO_BLUE_LIGHT);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.SLIM_RECENTS_EXPAND_ICON_COLOR,
                                    HOLO_BLUE_LIGHT);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.SLIM_RECENTS_EMPTY_ICON_COLOR,
                                    HOLO_BLUE_LIGHT);
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
