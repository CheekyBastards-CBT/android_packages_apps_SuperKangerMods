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
import android.os.Bundle;
import android.os.UserHandle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.vrtoxin.R;

import com.android.vrtoxin.preferences.SeekBarPreference;
import com.android.vrtoxin.preferences.ColorPickerPreference;

public class CarrierLabelFragment extends PreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    public CarrierLabelFragment(){}

    private static final String PREF_CARRIER_LABEL_SHOW =
            "carrier_label_show";
    private static final String PREF_CARRIER_LABEL_SHOW_ON_LOCK_SCREEN =
            "carrier_label_show_on_lock_screen";
    private static final String PREF_CARRIER_LABEL_USE_CUSTOM =
            "carrier_label_use_custom";
    private static final String PREF_CARRIER_LABEL_CUSTOM_LABEL =
            "carrier_label_custom_label";
    private static final String PREF_HIDE_LABEL =
            "carrier_label_hide_label";
    private static final String PREF_NUMBER_OF_NOTIFICATION_ICONS =
            "carrier_label_number_of_notification_icons";
    private static final String PREF_COLOR =
            "carrier_label_color";
    private static final String STATUS_BAR_CARRIER_FONT_SIZE  =
            "status_bar_carrier_font_size";
    private static final String PREF_STATUS_BAR_CARRIER_FONT_STYLE =
            "status_bar_carrier_font_style";
    private static final String STATUS_BAR_CARRIER_SPOT =
            "status_bar_carrier_spot";
    private static final String PREF_CAT_NOTIFICATIONS =
            "carrier_label_cat_notification_icons";
    private static final String PREF_CAT_COLOR =
            "carrier_label_cat_color";
    private static final String PREF_CAT_CUSTOM =
            "carrier_label_cat_custom";

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET = 0;

    private static final int WHITE = 0xffffffff;
    private static final int VRTOXIN_BLUE = 0xff1976D2;

    private SwitchPreference mShow;
    private SwitchPreference mShowOnLockScreen;
    private SwitchPreference mUseCustom;
    private EditTextPreference mCustomLabel;
    private SwitchPreference mHideLabel;
    private ListPreference mNumberOfNotificationIcons;
    private ColorPickerPreference mColor;
    private SeekBarPreference mStatusBarCarrierSize;
    private ListPreference mStatusBarCarrierFontStyle;
    private ListPreference mStatusBarCarrierSpot;
    private PreferenceCategory mNotifIcoCat;
    private PreferenceCategory mColorCat;
    private PreferenceCategory mCustomLabelCat;

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

        addPreferencesFromResource(R.xml.carrier_label);
        mResolver = getActivity().getContentResolver();
        PreferenceScreen prefSet = getPreferenceScreen();

        final boolean show = Settings.System.getInt(mResolver,
               Settings.System.STATUS_BAR_CARRIER_LABEL_SHOW, 0) == 1;
        final boolean showOnLockScreen = Settings.System.getInt(mResolver,
               Settings.System.STATUS_BAR_CARRIER_LABEL_SHOW_ON_LOCK_SCREEN, 1) == 1;
        final boolean useCustom = Settings.System.getInt(mResolver,
               Settings.System.STATUS_BAR_CARRIER_LABEL_USE_CUSTOM, 0) == 1;
        final boolean hideLabel = Settings.System.getInt(mResolver,
               Settings.System.STATUS_BAR_CARRIER_LABEL_HIDE_LABEL, 1) == 1;
        final boolean isHidden = !show && !showOnLockScreen;

        mNotifIcoCat = (PreferenceCategory) findPreference(PREF_CAT_NOTIFICATIONS);
        mColorCat = (PreferenceCategory) findPreference(PREF_CAT_COLOR);
        mCustomLabelCat = (PreferenceCategory) findPreference(PREF_CAT_CUSTOM);

        mShow = (SwitchPreference) findPreference(PREF_CARRIER_LABEL_SHOW);
        mShow.setChecked(show);
        mShow.setOnPreferenceChangeListener(this);

        mShowOnLockScreen = (SwitchPreference) findPreference(PREF_CARRIER_LABEL_SHOW_ON_LOCK_SCREEN);
        mShowOnLockScreen.setChecked(showOnLockScreen);
        mShowOnLockScreen.setOnPreferenceChangeListener(this);

        mStatusBarCarrierSize =
                (SeekBarPreference) findPreference(STATUS_BAR_CARRIER_FONT_SIZE);
        mStatusBarCarrierSize.setValue(Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.STATUS_BAR_CARRIER_FONT_SIZE, 14));
        mStatusBarCarrierSize.setOnPreferenceChangeListener(this);

        mStatusBarCarrierFontStyle = (ListPreference) findPreference(PREF_STATUS_BAR_CARRIER_FONT_STYLE);
        mStatusBarCarrierFontStyle.setOnPreferenceChangeListener(this);
        mStatusBarCarrierFontStyle.setValue(Integer.toString(Settings.System.getInt(getActivity()
                .getContentResolver(), Settings.System.STATUS_BAR_CARRIER_FONT_STYLE, 0)));
        mStatusBarCarrierFontStyle.setSummary(mStatusBarCarrierFontStyle.getEntry());

        mStatusBarCarrierSpot = (ListPreference) findPreference(STATUS_BAR_CARRIER_SPOT);
        int StatusBarCarrierSpot = Settings.System.getIntForUser(mResolver,
                Settings.System.STATUS_BAR_CARRIER_SPOT, 0,
                UserHandle.USER_CURRENT);
        mStatusBarCarrierSpot.setValue(String.valueOf(StatusBarCarrierSpot));
        mStatusBarCarrierSpot.setSummary(mStatusBarCarrierSpot.getEntry());
        mStatusBarCarrierSpot.setOnPreferenceChangeListener(this);

        if (!isHidden) {
            mUseCustom = (SwitchPreference) findPreference(PREF_CARRIER_LABEL_USE_CUSTOM);
            mUseCustom.setChecked(useCustom);
            mUseCustom.setOnPreferenceChangeListener(this);

            if (useCustom) {
                mCustomLabel = (EditTextPreference) findPreference(PREF_CARRIER_LABEL_CUSTOM_LABEL);
                mCustomLabel.getEditText().setHint(getResources().getString(
                        com.android.internal.R.string.default_custom_label));
                mCustomLabel.setOnPreferenceChangeListener(this);
                updateCustomLabelPreference();
            } else {
                prefSet.removePreference(mCustomLabelCat);
            }

            if (show) {
                mHideLabel =
                        (SwitchPreference) findPreference(PREF_HIDE_LABEL);
                mHideLabel.setChecked(hideLabel);
                mHideLabel.setOnPreferenceChangeListener(this);
                if (hideLabel) {
                    mNumberOfNotificationIcons =
                            (ListPreference) findPreference(PREF_NUMBER_OF_NOTIFICATION_ICONS);
                    int numberOfNotificationIcons = Settings.System.getInt(mResolver,
                           Settings.System.STATUS_BAR_CARRIER_LABEL_NUMBER_OF_NOTIFICATION_ICONS, 1);
                    mNumberOfNotificationIcons.setValue(String.valueOf(numberOfNotificationIcons));
                    mNumberOfNotificationIcons.setSummary(mNumberOfNotificationIcons.getEntry());
                    mNumberOfNotificationIcons.setOnPreferenceChangeListener(this);
                } else {
                    prefSet.removePreference(mNumberOfNotificationIcons);
                }
            } else {
                prefSet.removePreference(mHideLabel);
                prefSet.removePreference(mNumberOfNotificationIcons);
                prefSet.removePreference(mNotifIcoCat);
            }

            mColor =
                    (ColorPickerPreference) findPreference(PREF_COLOR);
            int intColor = Settings.System.getInt(mResolver,
                    Settings.System.STATUS_BAR_CARRIER_LABEL_COLOR,
                    WHITE); 
            mColor.setNewPreviewColor(intColor);
            String hexColor = String.format("#%08x", (0xffffffff & intColor));
            mColor.setSummary(hexColor);
            mColor.setOnPreferenceChangeListener(this);
        } else {
            prefSet.removePreference(mUseCustom);
            prefSet.removePreference(mCustomLabel);
            prefSet.removePreference(mHideLabel);
            prefSet.removePreference(mNumberOfNotificationIcons);
            prefSet.removePreference(mColor);
            prefSet.removePreference(mNotifIcoCat);
            prefSet.removePreference(mColorCat);
            prefSet.removePreference(mStatusBarCarrierSize);
            prefSet.removePreference(mStatusBarCarrierFontStyle);
            prefSet.removePreference(mStatusBarCarrierSpot);
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
                    Settings.System.STATUS_BAR_CARRIER_LABEL_SHOW,
                    value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mShowOnLockScreen) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_CARRIER_LABEL_SHOW_ON_LOCK_SCREEN,
                    value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mStatusBarCarrierSize) {
            int width = ((Integer)newValue).intValue();
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUS_BAR_CARRIER_FONT_SIZE, width);
            return true;
        } else if (preference == mStatusBarCarrierFontStyle) {
            int val = Integer.parseInt((String) newValue);
            int index = mStatusBarCarrierFontStyle.findIndexOfValue((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUS_BAR_CARRIER_FONT_STYLE, val);
            mStatusBarCarrierFontStyle.setSummary(mStatusBarCarrierFontStyle.getEntries()[index]);
            return true;
        } else if (preference == mStatusBarCarrierSpot) {
            int StatusBarCarrierSpot = Integer.valueOf((String) newValue);
            int index = mStatusBarCarrierSpot.findIndexOfValue((String) newValue);
            Settings.System.putIntForUser(
                    mResolver, Settings.System.STATUS_BAR_CARRIER_SPOT, StatusBarCarrierSpot,
                    UserHandle.USER_CURRENT);
            mStatusBarCarrierSpot.setSummary(
                    mStatusBarCarrierSpot.getEntries()[index]);
            return true;
        } else if (preference == mUseCustom) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_CARRIER_LABEL_USE_CUSTOM,
                    value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mCustomLabel) {
            String label = (String) newValue;
            Settings.System.putString(mResolver,
                    Settings.System.STATUS_BAR_CARRIER_LABEL_CUSTOM_LABEL, label);
            updateCustomLabelPreference();
        } else if (preference == mHideLabel) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_CARRIER_LABEL_HIDE_LABEL,
                    value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mNumberOfNotificationIcons) {
            int intValue = Integer.valueOf((String) newValue);
            int index = mNumberOfNotificationIcons.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_CARRIER_LABEL_NUMBER_OF_NOTIFICATION_ICONS,
                    intValue);
            preference.setSummary(mNumberOfNotificationIcons.getEntries()[index]);
            return true;
        } else if (preference == mColor) {
            String hex = ColorPickerPreference.convertToARGB(
                Integer.valueOf(String.valueOf(newValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.STATUS_BAR_CARRIER_LABEL_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        }
        return false;
    }

    private void updateCustomLabelPreference() {
        String customLabelText = Settings.System.getString(mResolver,
                Settings.System.STATUS_BAR_CARRIER_LABEL_CUSTOM_LABEL);
        String customLabelDefaultSummary = getResources().getString(
                    com.android.internal.R.string.default_custom_label);
        if (customLabelText == null) {
            customLabelText = "";
        }
        mCustomLabel.setText(customLabelText);
        mCustomLabel.setSummary(
                customLabelText.isEmpty() ? customLabelDefaultSummary : customLabelText);
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

        CarrierLabelFragment getOwner() {
            return (CarrierLabelFragment) getTargetFragment();
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
                                    Settings.System.STATUS_BAR_CARRIER_LABEL_COLOR,
                                    WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_CARRIER_FONT_SIZE, 14);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_CARRIER_FONT_STYLE, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_CARRIER_SPOT, 0);
                            getOwner().refreshSettings();
                        }
                    })
                    .setPositiveButton(R.string.reset_vrtoxin,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_CARRIER_LABEL_COLOR,
                                    VRTOXIN_BLUE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_CARRIER_FONT_SIZE, 14);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_CARRIER_FONT_STYLE, 3);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_CARRIER_SPOT, 0);
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
