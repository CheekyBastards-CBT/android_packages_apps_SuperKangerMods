/*
 * Copyright (C) 2016 The VRToxin Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
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
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.android.vrtoxin.R;
import com.android.vrtoxin.Utils;
import com.android.vrtoxin.preferences.ColorPickerPreference;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class StatusBarClockFragment extends PreferenceFragment
        implements OnPreferenceChangeListener {

    public StatusBarClockFragment(){}

    private static final String TAG = "StatusBarClockFragment";

    private static final String STATUS_BAR_CLOCK_STYLE = "status_bar_clock";
    private static final String STATUS_BAR_AM_PM = "status_bar_am_pm";
    private static final String STATUS_BAR_DATE = "status_bar_date";
    private static final String STATUS_BAR_DATE_STYLE = "status_bar_date_style";
    private static final String PREF_CLOCK_DATE_POSITION = "clock_date_position";
    private static final String STATUS_BAR_DATE_FORMAT = "status_bar_date_format";
    private static final String PREF_COLOR_PICKER = "clock_color";
    private static final String PREF_FONT_STYLE = "statusbar_clock_font_style";
    private static final String PREF_STATUS_BAR_CLOCK_FONT_SIZE  = "statusbar_clock_font_size";
    private static final String PREF_CENTER_CLOCK = "center_clock_hide";
    private static final String PREF_NUMBER_OF_NOTIFICATION_ICONS = "center_clock_number_of_notification_icons";

    private static final int STATUS_BAR_BATTERY_STYLE_HIDDEN = 4;
    private static final int STATUS_BAR_BATTERY_STYLE_TEXT = 6;

    public static final int CLOCK_DATE_STYLE_LOWERCASE = 1;
    public static final int CLOCK_DATE_STYLE_UPPERCASE = 2;
    private static final int CUSTOM_CLOCK_DATE_FORMAT_INDEX = 23;

    private static final int DEFAULT_COLOR = 0xffffffff;
    private static final int VRTOXIN_BLUE = 0xff33b5e5;

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET = 0;

    private ListPreference mStatusBarClock;
    private ListPreference mStatusBarAmPm;
    private ListPreference mStatusBarDate;
    private ListPreference mStatusBarDateStyle;
    private ListPreference mClockDatePosition;
    private ListPreference mStatusBarDateFormat;
    private ListPreference mFontStyle;
    private ListPreference mStatusBarClockFontSize;
    private ColorPickerPreference mColorPicker;
    private SwitchPreference mHideCenterClock;
    private ListPreference mNumberOfNotificationIcons;

    private boolean mCheckPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createCustomView();
    }

    private PreferenceScreen createCustomView() {
        mCheckPreferences = false;
        PreferenceScreen prefSet = getPreferenceScreen();
        if (prefSet != null) {
            prefSet.removeAll();
        }
        addPreferencesFromResource(R.xml.status_bar_clock_settings);

        ContentResolver resolver = getActivity().getContentResolver();

        PackageManager pm = getActivity().getPackageManager();
        Resources systemUiResources;
        try {
            systemUiResources = pm.getResourcesForApplication("com.android.systemui");
        } catch (Exception e) {
            Log.e(TAG, "can't access systemui resources",e);
            return null;
        }

        PreferenceCategory catNotificationIcons =
                (PreferenceCategory) findPreference("center_clock_cat_notification_icons");
        PreferenceCategory mCategory = (PreferenceCategory) findPreference("status_bar");

        mStatusBarClock = (ListPreference) findPreference(STATUS_BAR_CLOCK_STYLE);
        mStatusBarAmPm = (ListPreference) findPreference(STATUS_BAR_AM_PM);
        mStatusBarDate = (ListPreference) findPreference(STATUS_BAR_DATE);
        mStatusBarDateStyle = (ListPreference) findPreference(STATUS_BAR_DATE_STYLE);
        mStatusBarDateFormat = (ListPreference) findPreference(STATUS_BAR_DATE_FORMAT);

        final boolean show = Settings.System.getInt(resolver,
               Settings.System.STATUS_BAR_CLOCK, 1) == 1;
        final boolean hideCenterClock = Settings.System.getInt(resolver,
               Settings.System.STATUS_BAR_CENTER_CLOCK_HIDE, 1) == 1;
        final boolean isHidden = !show;

        int clockStyle = Settings.System.getInt(resolver,
                Settings.System.STATUS_BAR_CLOCK, 1);
        mStatusBarClock.setValue(String.valueOf(clockStyle));
        mStatusBarClock.setSummary(mStatusBarClock.getEntry());
        mStatusBarClock.setOnPreferenceChangeListener(this);

        if (DateFormat.is24HourFormat(getActivity())) {
            mStatusBarAmPm.setEnabled(false);
            mStatusBarAmPm.setSummary(R.string.status_bar_am_pm_info);
        } else {
            int statusBarAmPm = Settings.System.getInt(resolver,
                    Settings.System.STATUS_BAR_AM_PM, 2);
            mStatusBarAmPm.setValue(String.valueOf(statusBarAmPm));
            mStatusBarAmPm.setSummary(mStatusBarAmPm.getEntry());
            mStatusBarAmPm.setOnPreferenceChangeListener(this);
        }

        int showDate = Settings.System.getInt(resolver,
                Settings.System.STATUS_BAR_DATE, 0);
        mStatusBarDate.setValue(String.valueOf(showDate));
        mStatusBarDate.setSummary(mStatusBarDate.getEntry());
        mStatusBarDate.setOnPreferenceChangeListener(this);

        int dateStyle = Settings.System.getInt(resolver,
                Settings.System.STATUS_BAR_DATE_STYLE, 0);
        mStatusBarDateStyle.setValue(String.valueOf(dateStyle));
        mStatusBarDateStyle.setSummary(mStatusBarDateStyle.getEntry());
        mStatusBarDateStyle.setOnPreferenceChangeListener(this);

        mClockDatePosition = (ListPreference) findPreference(PREF_CLOCK_DATE_POSITION);
        mClockDatePosition.setOnPreferenceChangeListener(this);
        mClockDatePosition.setValue(Integer.toString(Settings.System.getInt(getActivity()
                .getContentResolver(), Settings.System.STATUSBAR_CLOCK_DATE_POSITION,
                0)));
        mClockDatePosition.setSummary(mClockDatePosition.getEntry());

        mStatusBarDateFormat.setOnPreferenceChangeListener(this);
        mStatusBarDateFormat.setSummary(mStatusBarDateFormat.getEntry());
        if (mStatusBarDateFormat.getValue() == null) {
            mStatusBarDateFormat.setValue("EEE");
        }

        parseClockDateFormats();

        mColorPicker = (ColorPickerPreference) findPreference(PREF_COLOR_PICKER);
        mColorPicker.setOnPreferenceChangeListener(this);
        int intColor = Settings.System.getInt(getActivity().getContentResolver(),
                    Settings.System.STATUSBAR_CLOCK_COLOR, -2);
        if (intColor == -2) {
            intColor = systemUiResources.getColor(systemUiResources.getIdentifier(
                    "com.android.systemui:color/status_bar_clock_color", null, null));
            mColorPicker.setSummary(getResources().getString(R.string.default_string));
        } else {
            String hexColor = String.format("#%08x", (0xffffffff & intColor));
            mColorPicker.setSummary(hexColor);
            mColorPicker.setAlphaSliderEnabled(true);
        }
        mColorPicker.setNewPreviewColor(intColor);

        mFontStyle = (ListPreference) findPreference(PREF_FONT_STYLE);
        mFontStyle.setOnPreferenceChangeListener(this);
        mFontStyle.setValue(Integer.toString(Settings.System.getInt(getActivity()
                .getContentResolver(), Settings.System.STATUSBAR_CLOCK_FONT_STYLE,
                0)));
        mFontStyle.setSummary(mFontStyle.getEntry());

        mStatusBarClockFontSize = (ListPreference) findPreference(PREF_STATUS_BAR_CLOCK_FONT_SIZE);
        mStatusBarClockFontSize.setOnPreferenceChangeListener(this);
        mStatusBarClockFontSize.setValue(Integer.toString(Settings.System.getInt(getActivity()
                .getContentResolver(), Settings.System.STATUSBAR_CLOCK_FONT_SIZE, 
                14)));
        mStatusBarClockFontSize.setSummary(mStatusBarClockFontSize.getEntry());

        if (isHidden) {
            mHideCenterClock =
                    (SwitchPreference) findPreference(PREF_CENTER_CLOCK);
            mHideCenterClock.setChecked(hideCenterClock);
            mHideCenterClock.setOnPreferenceChangeListener(this);
            if (hideCenterClock) {
                mNumberOfNotificationIcons =
                        (ListPreference) findPreference(PREF_NUMBER_OF_NOTIFICATION_ICONS);
                int numberOfNotificationIcons = Settings.System.getInt(resolver,
                       Settings.System.STATUS_BAR_CENTER_CLOCK_NUMBER_OF_NOTIFICATION_ICONS, 1);
                mNumberOfNotificationIcons.setValue(String.valueOf(numberOfNotificationIcons));
                mNumberOfNotificationIcons.setSummary(mNumberOfNotificationIcons.getEntry());
                mNumberOfNotificationIcons.setOnPreferenceChangeListener(this);
            } else {
                catNotificationIcons.removePreference(findPreference("center_clock_number_of_notification_icons"));
            }
        } else {
            catNotificationIcons.removePreference(findPreference("center_clock_hide"));
            catNotificationIcons.removePreference(findPreference("center_clock_number_of_notification_icons"));
            prefSet.removePreference(findPreference("center_clock_cat_notification_icons"));
        }

        setHasOptionsMenu(true);
        mCheckPreferences = true;
        return prefSet;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Adjust clock position for RTL if necessary
        Configuration config = getResources().getConfiguration();
        if (config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
                mStatusBarClock.setEntries(getActivity().getResources().getStringArray(
                        R.array.status_bar_clock_style_entries_rtl));
                mStatusBarClock.setSummary(mStatusBarClock.getEntry());
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (!mCheckPreferences) {
            return false;
        }
        AlertDialog dialog;
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mStatusBarClock) {
            int clockStyle = Integer.parseInt((String) newValue);
            int index = mStatusBarClock.findIndexOfValue((String) newValue);
            Settings.System.putInt(
                    resolver, STATUS_BAR_CLOCK_STYLE, clockStyle);
            mStatusBarClock.setSummary(mStatusBarClock.getEntries()[index]);
            createCustomView();
            return true;
        } else if (preference == mStatusBarAmPm) {
            int statusBarAmPm = Integer.valueOf((String) newValue);
            int index = mStatusBarAmPm.findIndexOfValue((String) newValue);
            Settings.System.putInt(
                    resolver, STATUS_BAR_AM_PM, statusBarAmPm);
            mStatusBarAmPm.setSummary(mStatusBarAmPm.getEntries()[index]);
            return true;
        } else if (preference == mStatusBarDate) {
            int statusBarDate = Integer.valueOf((String) newValue);
            int index = mStatusBarDate.findIndexOfValue((String) newValue);
            Settings.System.putInt(
                    resolver, STATUS_BAR_DATE, statusBarDate);
            mStatusBarDate.setSummary(mStatusBarDate.getEntries()[index]);
            return true;
        } else if (preference == mStatusBarDateStyle) {
            int statusBarDateStyle = Integer.parseInt((String) newValue);
            int index = mStatusBarDateStyle.findIndexOfValue((String) newValue);
            Settings.System.putInt(
                    resolver, STATUS_BAR_DATE_STYLE, statusBarDateStyle);
            mStatusBarDateStyle.setSummary(mStatusBarDateStyle.getEntries()[index]);
            return true;
        } else if (preference == mClockDatePosition) {
            int val = Integer.parseInt((String) newValue);
            int index = mClockDatePosition.findIndexOfValue((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUSBAR_CLOCK_DATE_POSITION, val);
            mClockDatePosition.setSummary(mClockDatePosition.getEntries()[index]);
            parseClockDateFormats();
            return true;
        } else if (preference ==  mStatusBarDateFormat) {
            int index = mStatusBarDateFormat.findIndexOfValue((String) newValue);
            if (index == CUSTOM_CLOCK_DATE_FORMAT_INDEX) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setTitle(R.string.status_bar_date_string_edittext_title);
                alert.setMessage(R.string.status_bar_date_string_edittext_summary);

                final EditText input = new EditText(getActivity());
                String oldText = Settings.System.getString(
                    getActivity().getContentResolver(),
                    Settings.System.STATUS_BAR_DATE_FORMAT);
                if (oldText != null) {
                    input.setText(oldText);
                }
                alert.setView(input);

                alert.setPositiveButton(R.string.menu_save, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int whichButton) {
                        String value = input.getText().toString();
                        if (value.equals("")) {
                            return;
                        }
                        Settings.System.putString(getActivity().getContentResolver(),
                            Settings.System.STATUS_BAR_DATE_FORMAT, value);

                        return;
                    }
                });

                alert.setNegativeButton(R.string.menu_cancel,
                    new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int which) {
                        return;
                    }
                });
                dialog = alert.create();
                dialog.show();
            } else {
                if ((String) newValue != null) {
                    Settings.System.putString(getActivity().getContentResolver(),
                        Settings.System.STATUS_BAR_DATE_FORMAT, (String) newValue);
                }
            }
            return true;
        } else if (preference == mColorPicker) {
            String hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String
                    .valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUSBAR_CLOCK_COLOR, intHex);
            return true;
        } else if (preference == mFontStyle) {
            int val = Integer.parseInt((String) newValue);
            int index = mFontStyle.findIndexOfValue((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUSBAR_CLOCK_FONT_STYLE, val);
            mFontStyle.setSummary(mFontStyle.getEntries()[index]);
            return true;
        } else if (preference == mStatusBarClockFontSize) {
            int val = Integer.parseInt((String) newValue);
            int index = mStatusBarClockFontSize.findIndexOfValue((String) newValue);
            Settings.System.putInt(resolver,
                    Settings.System.STATUSBAR_CLOCK_FONT_SIZE, val);
            mStatusBarClockFontSize.setSummary(mStatusBarClockFontSize.getEntries()[index]);
            return true;
        } else if (preference == mHideCenterClock) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(resolver,
                    Settings.System.STATUS_BAR_CENTER_CLOCK_HIDE,
                    value ? 1 : 0);
            createCustomView();
            return true;
        } else if (preference == mNumberOfNotificationIcons) {
            int intValue = Integer.valueOf((String) newValue);
            int index = mNumberOfNotificationIcons.findIndexOfValue((String) newValue);
            Settings.System.putInt(resolver,
                    Settings.System.STATUS_BAR_CENTER_CLOCK_NUMBER_OF_NOTIFICATION_ICONS,
                    intValue);
            preference.setSummary(mNumberOfNotificationIcons.getEntries()[index]);
            return true;
        }
        return false;
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

    private void parseClockDateFormats() {
        // Parse and repopulate mClockDateFormats's entries based on current date.
        String[] dateEntries = getResources().getStringArray(R.array.status_bar_date_format_entries_values);
        CharSequence parsedDateEntries[];
        parsedDateEntries = new String[dateEntries.length];
        Date now = new Date();

        int lastEntry = dateEntries.length - 1;
        int dateFormat = Settings.System.getInt(getActivity()
                .getContentResolver(), Settings.System.STATUS_BAR_DATE_STYLE, 0);
        for (int i = 0; i < dateEntries.length; i++) {
            if (i == lastEntry) {
                parsedDateEntries[i] = dateEntries[i];
            } else {
                String newDate;
                CharSequence dateString = DateFormat.format(dateEntries[i], now);
                if (dateFormat == CLOCK_DATE_STYLE_LOWERCASE) {
                    newDate = dateString.toString().toLowerCase();
                } else if (dateFormat == CLOCK_DATE_STYLE_UPPERCASE) {
                    newDate = dateString.toString().toUpperCase();
                } else {
                    newDate = dateString.toString();
                }

                parsedDateEntries[i] = newDate;
            }
        }
        mStatusBarDateFormat.setEntries(parsedDateEntries);
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

        StatusBarClockFragment getOwner() {
            return (StatusBarClockFragment) getTargetFragment();
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
                            Settings.System.putInt(getActivity().getContentResolver(),
                                    Settings.System.STATUS_BAR_CLOCK, 1);
                            Settings.System.putInt(getActivity().getContentResolver(),
                                    Settings.System.STATUS_BAR_AM_PM, 0);
                            Settings.System.putInt(getActivity().getContentResolver(),
                                    Settings.System.STATUS_BAR_DATE, 0);
                            Settings.System.putInt(getActivity().getContentResolver(),
                                    Settings.System.STATUS_BAR_DATE_STYLE, 0);
                            Settings.System.putInt(getActivity().getContentResolver(),
                                    Settings.System.STATUSBAR_CLOCK_DATE_POSITION, 0);
                            Settings.System.putInt(getActivity().getContentResolver(),
                                    Settings.System.STATUS_BAR_DATE_FORMAT, 0);
                            Settings.System.putInt(getActivity().getContentResolver(),
                                    Settings.System.STATUSBAR_CLOCK_COLOR,
                                    DEFAULT_COLOR);
                            Settings.System.putInt(getActivity().getContentResolver(),
                                    Settings.System.STATUSBAR_CLOCK_FONT_STYLE, 0);
                            Settings.System.putInt(getActivity().getContentResolver(),
                                    Settings.System.STATUSBAR_CLOCK_FONT_SIZE, 14);
                            getOwner().createCustomView();
                        }
                    })
                    .setPositiveButton(R.string.reset_vrtoxin,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getActivity().getContentResolver(),
                                    Settings.System.STATUS_BAR_CLOCK, 2);
                            Settings.System.putInt(getActivity().getContentResolver(),
                                    Settings.System.STATUS_BAR_AM_PM, 1);
                            Settings.System.putInt(getActivity().getContentResolver(),
                                    Settings.System.STATUS_BAR_DATE, 0);
                            Settings.System.putInt(getActivity().getContentResolver(),
                                    Settings.System.STATUS_BAR_DATE_STYLE, 0);
                            Settings.System.putInt(getActivity().getContentResolver(),
                                    Settings.System.STATUSBAR_CLOCK_DATE_POSITION, 0);
                            Settings.System.putInt(getActivity().getContentResolver(),
                                    Settings.System.STATUS_BAR_DATE_FORMAT, 0);
                            Settings.System.putInt(getActivity().getContentResolver(),
                                    Settings.System.STATUSBAR_CLOCK_COLOR,
                                    VRTOXIN_BLUE);
                            Settings.System.putInt(getActivity().getContentResolver(),
                                    Settings.System.STATUSBAR_CLOCK_FONT_STYLE, 3);
                            Settings.System.putInt(getActivity().getContentResolver(),
                                    Settings.System.STATUSBAR_CLOCK_FONT_SIZE, 16);
                            getOwner().createCustomView();
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
