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

package com.android.vrtoxin.statusbar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.vrtoxin.R;
import com.android.vrtoxin.preferences.ColorPickerPreference;

public class ExpandedHeaderNetworkIconsFragment extends PreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    public ExpandedHeaderNetworkIconsFragment(){}

    private static final String PREF_NO_SIM =
            "header_network_icons_no_sim_color";
    private static final String PREF_AIRPLANE_MODE =
            "header_network_icons_airplane_mode_color";

    private static final int WHITE           = 0xffffffff;
    private static final int RED_500         = 0xfff44336;
    private static final int VRTOXIN_BLUE    = 0xff1976D2;

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET  = 0;

    private ColorPickerPreference mNoSim;
    private ColorPickerPreference mAirplaneMode;

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

        addPreferencesFromResource(R.xml.expanded_header_network_icons_settings);
        mResolver = getActivity().getContentResolver();

        int intColor;
        String hexColor;

        mNoSim =
                (ColorPickerPreference) findPreference(PREF_NO_SIM);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_EXPANDED_NETWORK_ICONS_NO_SIM_COLOR, WHITE);
        mNoSim.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mNoSim.setSummary(hexColor);
        mNoSim.setDefaultColors(WHITE, VRTOXIN_BLUE);
        mNoSim.setOnPreferenceChangeListener(this);

        mAirplaneMode =
                (ColorPickerPreference) findPreference(PREF_AIRPLANE_MODE);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_EXPANDED_NETWORK_ICONS_AIRPLANE_MODE_COLOR,
                WHITE);
        mAirplaneMode.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mAirplaneMode.setSummary(hexColor);
        mAirplaneMode.setDefaultColors(WHITE, VRTOXIN_BLUE);
        mAirplaneMode.setOnPreferenceChangeListener(this);

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

        if (preference == mNoSim) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_EXPANDED_NETWORK_ICONS_NO_SIM_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mAirplaneMode) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_EXPANDED_NETWORK_ICONS_AIRPLANE_MODE_COLOR,
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

        ExpandedHeaderNetworkIconsFragment getOwner() {
            return (ExpandedHeaderNetworkIconsFragment) getTargetFragment();
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
                                    Settings.System.STATUS_BAR_EXPANDED_NETWORK_ICONS_NO_SIM_COLOR,
                                    WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EXPANDED_NETWORK_ICONS_AIRPLANE_MODE_COLOR,
                                    WHITE);
                            getOwner().refreshSettings();
                        }
                    })
                    .setPositiveButton(R.string.reset_vrtoxin,
                            new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EXPANDED_NETWORK_ICONS_NO_SIM_COLOR,
                                    VRTOXIN_BLUE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EXPANDED_NETWORK_ICONS_AIRPLANE_MODE_COLOR,
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
