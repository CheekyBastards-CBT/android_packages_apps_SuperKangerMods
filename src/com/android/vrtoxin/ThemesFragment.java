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

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.UiModeManager;
import android.app.IUiModeManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.vrtoxin.R;

public class ThemesFragment extends PreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    public ThemesFragment(){}

    private static final String TAG = "ThemesFragment";

    private static final String NIGHT_MODE = "night_mode";
    private static final String NIGHT_AUTO_MODE = "night_auto_mode";
    private static final String OVERRIDE_CUSTOM_COLORS = "override_custom_colors";

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET = 0;

    private ListPreference mNightModePreference;
    private ListPreference mNightAutoMode;
    private SwitchPreference mOverrideCustomColor;

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

        addPreferencesFromResource(R.xml.themes_fragment);

        PreferenceScreen prefSet = getPreferenceScreen();
        mResolver = getActivity().getContentResolver();

        int mNightAutoModeOn = Settings.Secure.getInt(mResolver,
                  Settings.Secure.UI_NIGHT_AUTO_MODE, 0);

        mNightAutoMode = (ListPreference) prefSet.findPreference(NIGHT_AUTO_MODE);
        mNightAutoMode.setValue(String.valueOf(
                Settings.Secure.getInt(mResolver,
                Settings.Secure.UI_NIGHT_AUTO_MODE, 0)));
        mNightAutoMode.setSummary(mNightAutoMode.getEntry());

        mNightAutoMode.setOnPreferenceChangeListener(
            new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference,
                        Object newValue) {
                String val = (String) newValue;
                Settings.Secure.putInt(mResolver,
                    Settings.Secure.UI_NIGHT_AUTO_MODE,
                    Integer.valueOf(val));
                int index = mNightAutoMode.findIndexOfValue(val);
                mNightAutoMode.setSummary(
                    mNightAutoMode.getEntries()[index]);
                return true;
            }
        });

        mNightModePreference = (ListPreference) prefSet.findPreference(NIGHT_MODE);
        if (mNightAutoModeOn == 0) {
            final UiModeManager uiManager = (UiModeManager) getActivity().getSystemService(
                    Context.UI_MODE_SERVICE);
            final int currentNightMode = uiManager.getNightMode();
            mNightModePreference.setValue(String.valueOf(currentNightMode));
            mNightModePreference.setOnPreferenceChangeListener(this);
        } else {
            prefSet.removePreference(mNightModePreference);
        }

        mOverrideCustomColor =
                (SwitchPreference) prefSet.findPreference(OVERRIDE_CUSTOM_COLORS);
        mOverrideCustomColor.setChecked((Settings.System.getInt(mResolver,
                Settings.System.OVERRIDE_CUSTOM_COLORS, 0) == 1));
        mOverrideCustomColor.setOnPreferenceChangeListener(this);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add(0, MENU_RESET, 0, R.string.reset)
                .setIcon(R.drawable.ic_action_reset)
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

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mOverrideCustomColor) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.OVERRIDE_CUSTOM_COLORS, value ? 1 : 0);
        }
        if (preference == mNightModePreference) {
            try {
                final int value = Integer.parseInt((String) newValue);
                final UiModeManager uiManager = (UiModeManager) getActivity().getSystemService(
                        Context.UI_MODE_SERVICE);
                uiManager.setNightMode(value);
            } catch (NumberFormatException e) {
                Log.e(TAG, "could not persist night mode setting", e);
            }
        }
        return true;
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

        ThemesFragment getOwner() {
            return (ThemesFragment) getTargetFragment();
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
                            Settings.Secure.putInt(getOwner().mResolver,
                                    Settings.Secure.UI_NIGHT_AUTO_MODE, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.OVERRIDE_CUSTOM_COLORS, 0);
                            final IUiModeManager uiModeManagerService = IUiModeManager.Stub.asInterface(
                                    ServiceManager.getService(Context.UI_MODE_SERVICE));
                            try {
                                uiModeManagerService.setNightMode(
                                        UiModeManager.MODE_NIGHT_NO);
                            } catch (RemoteException e) {
                            }
                            getOwner().refreshSettings();
                        }
                    })
                    .setPositiveButton(R.string.reset_vrtoxin,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.Secure.putInt(getOwner().mResolver,
                                    Settings.Secure.UI_NIGHT_AUTO_MODE, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.OVERRIDE_CUSTOM_COLORS, 0);
                            final IUiModeManager uiModeManagerService = IUiModeManager.Stub.asInterface(
                                    ServiceManager.getService(Context.UI_MODE_SERVICE));
                            try {
                                uiModeManagerService.setNightMode(
                                        UiModeManager.MODE_NIGHT_YES);
                            } catch (RemoteException e) {
                            }
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
