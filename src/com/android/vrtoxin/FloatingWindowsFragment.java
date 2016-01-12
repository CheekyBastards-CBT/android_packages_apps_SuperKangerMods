/*
 * Copyright (C) 2016 VRToxin Project
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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.UserHandle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.vrtoxin.R;

import java.util.List;

public class FloatingWindowsFragment extends PreferenceFragment
            implements OnPreferenceChangeListener  {

    public FloatingWindowsFragment(){}

    private static final String FLOATING_WINDOW_MODE = "floating_window_mode";
    private static final String GESTURE_ANYWHERE_FLOATING = "gesture_anywhere_floating";
    private static final String SLIM_ACTION_FLOATS = "slim_action_floats";

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET = 0;

    SwitchPreference mFloatingWindowMode;
    SwitchPreference mGestureAnywhereFloatingWindow;
    SwitchPreference mSlimActionFloatingWindow;

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

        addPreferencesFromResource(R.xml.floating_windows_fragment);
        mResolver = getActivity().getContentResolver();
        PreferenceScreen prefSet = getPreferenceScreen();

        mFloatingWindowMode = (SwitchPreference) prefSet.findPreference(FLOATING_WINDOW_MODE);
        mFloatingWindowMode.setChecked(Settings.System.getInt(mResolver,
            Settings.System.FLOATING_WINDOW_MODE, 0) == 1);
        mFloatingWindowMode.setOnPreferenceChangeListener(this);

        mGestureAnywhereFloatingWindow = (SwitchPreference) prefSet.findPreference(GESTURE_ANYWHERE_FLOATING);
        mGestureAnywhereFloatingWindow.setChecked(Settings.System.getInt(mResolver,
            Settings.System.GESTURE_ANYWHERE_FLOATING, 0) == 1);
        mGestureAnywhereFloatingWindow.setOnPreferenceChangeListener(this);

        mSlimActionFloatingWindow = (SwitchPreference) prefSet.findPreference(SLIM_ACTION_FLOATS);
        mSlimActionFloatingWindow.setChecked(Settings.System.getInt(mResolver,
            Settings.System.SLIM_ACTION_FLOATS, 0) == 1);
        mSlimActionFloatingWindow.setOnPreferenceChangeListener(this);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add(0, MENU_RESET, 0, R.string.reset)
                .setIcon(R.drawable.ic_settings_backup_restore)
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
        if (preference == mFloatingWindowMode) {
            Settings.System.putInt(mResolver,
                    Settings.System.FLOATING_WINDOW_MODE,
            (Boolean) newValue ? 1 : 0);
            return true;
       } else if (preference == mGestureAnywhereFloatingWindow) {
            Settings.System.putInt(mResolver,
                    Settings.System.GESTURE_ANYWHERE_FLOATING,
            (Boolean) newValue ? 1 : 0);
            return true;
        } else if (preference == mSlimActionFloatingWindow) {
            Settings.System.putInt(mResolver,
                    Settings.System.SLIM_ACTION_FLOATS,
            (Boolean) newValue ? 1 : 0);
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

        FloatingWindowsFragment getOwner() {
            return (FloatingWindowsFragment) getTargetFragment();
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
                                    Settings.System.FLOATING_WINDOW_MODE, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.GESTURE_ANYWHERE_FLOATING, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.SLIM_ACTION_FLOATS, 0);
                            getOwner().refreshSettings();
                        }
                    })
                    .setPositiveButton(R.string.reset_vrtoxin,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.FLOATING_WINDOW_MODE, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.GESTURE_ANYWHERE_FLOATING, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.SLIM_ACTION_FLOATS, 1);
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
