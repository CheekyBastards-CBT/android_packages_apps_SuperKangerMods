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

package com.android.vrtoxin.lockscreen;

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
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.vrtoxin.R;

import com.android.vrtoxin.preferences.ColorPickerPreference;

public class LockScreenVisualizerSettings extends PreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    public LockScreenVisualizerSettings(){}

    private static final String PREF_SHOW =
            "visualizer_show";
    private static final String PREF_USE_CUSTOM_COLOR =
            "visualizer_use_custom_color";
    private static final String PREF_COLOR =
            "visualizer_color";

    private static final int WHITE           = 0xffffffff;
    private static final int VRTOXIN_BLUE    = 0xff1976D2;

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET  = 0;

    private SwitchPreference mShow;
    private SwitchPreference mUseCustomColor;
    private ColorPickerPreference mColor;

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

        addPreferencesFromResource(R.xml.lock_screen_visualizer_settings);
        PreferenceScreen prefSet = getPreferenceScreen();
        mResolver = getActivity().getContentResolver();

        final boolean show = Settings.System.getInt(mResolver,
               Settings.System.LOCK_SCREEN_VISUALIZER_SHOW, 0) == 1;

        final boolean useCustomColor = Settings.System.getInt(mResolver,
               Settings.System.LOCK_SCREEN_VISUALIZER_USE_CUSTOM_COLOR, 0) == 1;

        mShow = (SwitchPreference) findPreference(PREF_SHOW);
        mShow.setChecked(show);
        mShow.setOnPreferenceChangeListener(this);

        if (show) {
            mUseCustomColor = (SwitchPreference) findPreference("visualizer_use_custom_color");
            mUseCustomColor.setChecked(useCustomColor);
            mUseCustomColor.setOnPreferenceChangeListener(this);
        } else {
            prefSet.removePreference(mUseCustomColor);
        }

        if (show && useCustomColor) {
            mColor =
                    (ColorPickerPreference) findPreference(PREF_COLOR);
            int intColor = Settings.System.getInt(mResolver,
                    Settings.System.LOCK_SCREEN_VISUALIZER_CUSTOM_COLOR, WHITE);
            mColor.setNewPreviewColor(intColor);
            String hexColor = String.format("#%08x", (0xffffffff & intColor));
            mColor.setSummary(hexColor);
            mColor.setDefaultColors(WHITE, VRTOXIN_BLUE);
            mColor.setOnPreferenceChangeListener(this);
        } else {
            prefSet.removePreference(findPreference("visualizer_color"));
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

        if (preference == mShow) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.LOCK_SCREEN_VISUALIZER_SHOW,
                    value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mUseCustomColor) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.LOCK_SCREEN_VISUALIZER_USE_CUSTOM_COLOR,
                    value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.LOCK_SCREEN_VISUALIZER_CUSTOM_COLOR, intHex);
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

        LockScreenVisualizerSettings getOwner() {
            return (LockScreenVisualizerSettings) getTargetFragment();
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
                                    Settings.System.LOCK_SCREEN_VISUALIZER_SHOW, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.LOCK_SCREEN_VISUALIZER_USE_CUSTOM_COLOR, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.LOCK_SCREEN_VISUALIZER_CUSTOM_COLOR,
                                    WHITE);
                            getOwner().refreshSettings();
                        }
                    })
                    .setPositiveButton(R.string.reset_vrtoxin,
                            new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.LOCK_SCREEN_VISUALIZER_SHOW, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.LOCK_SCREEN_VISUALIZER_USE_CUSTOM_COLOR, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.LOCK_SCREEN_VISUALIZER_CUSTOM_COLOR,
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
