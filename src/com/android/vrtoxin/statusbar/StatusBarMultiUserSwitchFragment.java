/*
 * Copyright (C) 2015 DarkKat
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

package com.android.vrtoxin.statusbar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.vrtoxin.R;

import com.android.vrtoxin.preferences.ColorPickerPreference;

public class StatusBarMultiUserSwitchFragment extends PreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String PREF_CAT_COLORS =
            "multi_user_switch_cat_colors";
    private static final String PREF_ICON_COLOR =
            "multi_user_switch_icon_color";
    private static final String PREF_ACTIVE_TEXT_FRAME_COLOR =
            "multi_user_switch_active_text_frame_color";
    private static final String PREF_INACTIVE_TEXT_COLOR =
            "multi_user_switch_inactive_text_color";

    private static final int WHITE             = 0xffffffff;
    private static final int VRTOXIN_BLUE      = 0xff1976D2;

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET  = 0;

    private ColorPickerPreference mIconColor;
    private ColorPickerPreference mActiveTextFrameColor;
    private ColorPickerPreference mInactiveTextColor;

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

        addPreferencesFromResource(R.xml.status_bar_multi_user_switch_fragment);
        mResolver = getActivity().getContentResolver();

        int intColor;
        String hexColor;

        mIconColor =
                (ColorPickerPreference) findPreference(PREF_ICON_COLOR);
        mIconColor.setTitle(getResources().getString(
                R.string.multi_user_switch_guest_icon_color_title));
        intColor = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_MULTI_USER_SWITCH_ICON_COLOR, WHITE);
        mIconColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mIconColor.setSummary(hexColor);
        mIconColor.setDefaultColors(WHITE, VRTOXIN_BLUE);
        mIconColor.setOnPreferenceChangeListener(this);

        PreferenceCategory catColors =
                (PreferenceCategory) findPreference(PREF_CAT_COLORS);
        mActiveTextFrameColor =
                (ColorPickerPreference) findPreference(PREF_ACTIVE_TEXT_FRAME_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_MULTI_USER_SWITCH_ACTIVE_TEXT_COLOR,
                VRTOXIN_BLUE);
        mActiveTextFrameColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mActiveTextFrameColor.setSummary(hexColor);
        mActiveTextFrameColor.setDefaultColors(VRTOXIN_BLUE, VRTOXIN_BLUE);
        mActiveTextFrameColor.setOnPreferenceChangeListener(this);

        mInactiveTextColor =
                (ColorPickerPreference) findPreference(PREF_INACTIVE_TEXT_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_MULTI_USER_SWITCH_INACTIVE_TEXT_COLOR,
                WHITE);
        mInactiveTextColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mInactiveTextColor.setSummary(hexColor);
        mInactiveTextColor.setDefaultColors(WHITE, VRTOXIN_BLUE);
        mInactiveTextColor.setOnPreferenceChangeListener(this);

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

        if (preference == mIconColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_MULTI_USER_SWITCH_ICON_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mActiveTextFrameColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_MULTI_USER_SWITCH_ACTIVE_TEXT_COLOR,
                    intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mInactiveTextColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_MULTI_USER_SWITCH_INACTIVE_TEXT_COLOR,
                    intHex);
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

        StatusBarMultiUserSwitchFragment getOwner() {
            return (StatusBarMultiUserSwitchFragment) getTargetFragment();
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
                                    Settings.System.STATUS_BAR_MULTI_USER_SWITCH_ICON_COLOR,
                                    WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_MULTI_USER_SWITCH_ACTIVE_TEXT_COLOR,
                                    WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_MULTI_USER_SWITCH_INACTIVE_TEXT_COLOR,
                                    WHITE);
                            getOwner().refreshSettings();
                        }
                    })
                    .setPositiveButton(R.string.reset_vrtoxin,
                            new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_MULTI_USER_SWITCH_ICON_COLOR,
                                    VRTOXIN_BLUE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_MULTI_USER_SWITCH_ACTIVE_TEXT_COLOR,
                                    VRTOXIN_BLUE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_MULTI_USER_SWITCH_INACTIVE_TEXT_COLOR,
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
