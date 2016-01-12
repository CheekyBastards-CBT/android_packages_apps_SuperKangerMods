/*
 * Copyright (C) 2015 AICP
 * Copyright (C) 2015 The Vrtoxin Project
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
import android.content.res.Resources;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
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
import android.text.Spannable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.vrtoxin.R;
import com.android.vrtoxin.preferences.ColorPickerPreference;

public class StatusLogoFragment extends PreferenceFragment
        implements OnPreferenceChangeListener {

    public StatusLogoFragment(){}

    private static final String TAG = "StatusLogoFragment";

    private static final String STATUS_BAR_VRTOXIN_LOGO_SHOW = "status_bar_vrtoxin_logo_show";
    private static final String KEY_VRTOXIN_LOGO_STYLE = "status_bar_vrtoxin_logo_style";
    private static final String KEY_VRTOXIN_LOGO_COLOR = "status_bar_vrtoxin_logo_color";
    private static final String PREF_NUMBER_OF_NOTIFICATION_ICONS = "logo_number_of_notification_icons";
    private static final String PREF_HIDE_LOGO = "logo_hide_logo";

    private static final int DEFAULT_COLOR = 0xffffffff;
    private static final int VRTOXIN_BLUE = 0xff33b5e5;

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET = 0;

    private ListPreference mShowVRToxinLogo;
    private ListPreference mVRToxinLogoStyle;
    private ColorPickerPreference mVRToxinLogoColor;
    private SwitchPreference mHideLogo;
    private ListPreference mNumberOfNotificationIcons;

    private boolean mCheckPreferences;
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

        addPreferencesFromResource(R.xml.status_bar_logo);
        mResolver = getActivity().getContentResolver();

        mShowVRToxinLogo = (ListPreference) findPreference(STATUS_BAR_VRTOXIN_LOGO_SHOW);
        int showVRToxinLogo = Settings.System.getIntForUser(mResolver,
                Settings.System.STATUS_BAR_VRTOXIN_LOGO_SHOW, 3,
                UserHandle.USER_CURRENT);
        mShowVRToxinLogo.setValue(String.valueOf(showVRToxinLogo));
        mShowVRToxinLogo.setSummary(mShowVRToxinLogo.getEntry());
        mShowVRToxinLogo.setOnPreferenceChangeListener(this);

        mVRToxinLogoStyle = (ListPreference) findPreference(KEY_VRTOXIN_LOGO_STYLE);
        int VRToxinLogoStyle = Settings.System.getIntForUser(mResolver,
                Settings.System.STATUS_BAR_VRTOXIN_LOGO_STYLE, 0,
                UserHandle.USER_CURRENT);
        mVRToxinLogoStyle.setValue(String.valueOf(VRToxinLogoStyle));
        mVRToxinLogoStyle.setSummary(mVRToxinLogoStyle.getEntry());
        mVRToxinLogoStyle.setOnPreferenceChangeListener(this);

        mHideLogo =
                (SwitchPreference) findPreference(PREF_HIDE_LOGO);
        mHideLogo.setChecked(Settings.System.getInt(mResolver,
               Settings.System.STATUS_BAR_VRTOXIN_LOGO_HIDE_LOGO, 1) == 1);
        mHideLogo.setOnPreferenceChangeListener(this);

        mNumberOfNotificationIcons =
                (ListPreference) findPreference(PREF_NUMBER_OF_NOTIFICATION_ICONS);
        int numberOfNotificationIcons = Settings.System.getInt(mResolver,
               Settings.System.STATUS_BAR_VRTOXIN_LOGO_NUMBER_OF_NOTIFICATION_ICONS, 1);
        mNumberOfNotificationIcons.setValue(String.valueOf(numberOfNotificationIcons));
        mNumberOfNotificationIcons.setSummary(mNumberOfNotificationIcons.getEntry());
        mNumberOfNotificationIcons.setOnPreferenceChangeListener(this);

        // VRToxin logo color
        mVRToxinLogoColor =
            (ColorPickerPreference) findPreference(KEY_VRTOXIN_LOGO_COLOR);
        mVRToxinLogoColor.setOnPreferenceChangeListener(this);
        int intColor = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_VRTOXIN_LOGO_COLOR, 0xffffffff);
        String hexColor = String.format("#%08x", (0xffffffff & intColor));
            mVRToxinLogoColor.setSummary(hexColor);
            mVRToxinLogoColor.setNewPreviewColor(intColor);
            mVRToxinLogoColor.setAlphaSliderEnabled(true);

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
        if (preference == mShowVRToxinLogo) {
            int showVRToxinLogo = Integer.valueOf((String) newValue);
            int index = mShowVRToxinLogo.findIndexOfValue((String) newValue);
            Settings.System.putInt(
                    mResolver, Settings.System.STATUS_BAR_VRTOXIN_LOGO_SHOW, showVRToxinLogo);
            mShowVRToxinLogo.setSummary(mShowVRToxinLogo.getEntries()[index]);
            return true;
        } else if (preference == mVRToxinLogoStyle) {
            int VRToxinLogoStyle = Integer.valueOf((String) newValue);
            int index = mVRToxinLogoStyle.findIndexOfValue((String) newValue);
            Settings.System.putIntForUser(
                    mResolver, Settings.System.STATUS_BAR_VRTOXIN_LOGO_STYLE, VRToxinLogoStyle,
                    UserHandle.USER_CURRENT);
            mVRToxinLogoStyle.setSummary(
                    mVRToxinLogoStyle.getEntries()[index]);
            return true;
        } else if (preference == mHideLogo) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_VRTOXIN_LOGO_HIDE_LOGO,
                    value ? 1 : 0);
            return true;
        } else if (preference == mNumberOfNotificationIcons) {
            int intValue = Integer.valueOf((String) newValue);
            int index = mNumberOfNotificationIcons.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_VRTOXIN_LOGO_NUMBER_OF_NOTIFICATION_ICONS,
                    intValue);
            preference.setSummary(mNumberOfNotificationIcons.getEntries()[index]);
            return true;
        } else if (preference == mVRToxinLogoColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_VRTOXIN_LOGO_COLOR, intHex);
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

        StatusLogoFragment getOwner() {
            return (StatusLogoFragment) getTargetFragment();
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
                                    Settings.System.STATUS_BAR_VRTOXIN_LOGO_SHOW, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_VRTOXIN_LOGO_STYLE, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_VRTOXIN_LOGO_HIDE_LOGO, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_VRTOXIN_LOGO_NUMBER_OF_NOTIFICATION_ICONS, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_VRTOXIN_LOGO_COLOR,
                                    DEFAULT_COLOR);
                            getOwner().refreshSettings();
                        }
                    })
                    .setPositiveButton(R.string.reset_vrtoxin,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_VRTOXIN_LOGO_SHOW, 3);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_VRTOXIN_LOGO_STYLE, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_VRTOXIN_LOGO_HIDE_LOGO, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_VRTOXIN_LOGO_NUMBER_OF_NOTIFICATION_ICONS, 4);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_VRTOXIN_LOGO_COLOR,
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
