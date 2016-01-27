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
import com.android.vrtoxin.preferences.SeekBarPreference;

public class StatusBarTickerFragment extends PreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String SHOW_TICKER =
            "status_bar_show_ticker";
    private static final String SHOW_COUNT =
            "status_bar_notif_count";
    private static final String CAT_COLORS =
            "ticker_colors";
    private static final String CAT_NOTIF_COLORS =
            "notif_colors";
    private static final String TEXT_COLOR =
            "status_bar_ticker_text_color";
    private static final String ICON_COLOR =
            "status_bar_ticker_icon_color";
    private static final String COUNT_ICON_COLOR =
            "status_bar_notif_count_icon_color";
    private static final String COUNT_TEXT_COLOR =
            "status_bar_notif_count_text_color";
    private static final String PREF_TICKER_ICON_COLOR_DARK =
            "status_bar_ticker_icon_color_dark_mode";
    private static final String PREF_TICKER_TEXT_COLOR_DARK =
            "status_bar_ticker_text_color_dark_mode";
    private static final String STATUS_BAR_TICKER_FONT_SIZE  =
            "status_bar_ticker_font_size";

    private static final int BLACK                  = 0xff000000;
    private static final int WHITE                  = 0xffffffff;
    private static final int VRTOXIN_BLUE           = 0xff1976D2;

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET  = 0;

    private SwitchPreference mShowCount;
    private SwitchPreference mShowTicker;
    private ColorPickerPreference mTextColor;
    private ColorPickerPreference mIconColor;
    private ColorPickerPreference mIconColorDark;
    private ColorPickerPreference mCountIconColor;
    private ColorPickerPreference mCountTextColor;
    private ColorPickerPreference mTextColorDark;
    private SeekBarPreference mTickerFontSize;

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

        addPreferencesFromResource(R.xml.status_bar_ticker);
        mResolver = getActivity().getContentResolver();
        PreferenceScreen prefSet = getPreferenceScreen();

        int intColor;
        String hexColor;

        boolean showTicker = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_SHOW_TICKER, 0) == 1;
        boolean showCount = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_NOTIF_COUNT, 0) == 1;

        mShowTicker =
                (SwitchPreference) findPreference(SHOW_TICKER);
        mShowTicker.setChecked(showTicker);
        mShowTicker.setOnPreferenceChangeListener(this);

        mShowCount =
                (SwitchPreference) findPreference(SHOW_COUNT);
        mShowCount.setChecked(showCount);
        mShowCount.setOnPreferenceChangeListener(this);

        if (showTicker) {
            mTickerFontSize =
                    (SeekBarPreference) findPreference(STATUS_BAR_TICKER_FONT_SIZE);
            mTickerFontSize.setValue(Settings.System.getInt(mResolver,
                    Settings.System.STATUS_BAR_TICKER_FONT_SIZE, 14));
            mTickerFontSize.setOnPreferenceChangeListener(this);

            mTextColor =
                    (ColorPickerPreference) findPreference(TEXT_COLOR);
            intColor = Settings.System.getInt(mResolver,
                    Settings.System.STATUS_BAR_TICKER_TEXT_COLOR,
                    WHITE); 
            mTextColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mTextColor.setSummary(hexColor);
            mTextColor.setDefaultColors(WHITE, VRTOXIN_BLUE);
            mTextColor.setOnPreferenceChangeListener(this);

            mTextColorDark =
                    (ColorPickerPreference) findPreference(PREF_TICKER_TEXT_COLOR_DARK);
            intColor = Settings.System.getInt(mResolver,
                    Settings.System.STATUS_BAR_TICKER_TEXT_COLOR_DARK_MODE,
                    BLACK);
            mTextColorDark.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mTextColorDark.setSummary(hexColor);
            mTextColorDark.setDefaultColors(BLACK, BLACK);
            mTextColorDark.setOnPreferenceChangeListener(this);

            mIconColor =
                    (ColorPickerPreference) findPreference(ICON_COLOR);
            intColor = Settings.System.getInt(mResolver,
                    Settings.System.STATUS_BAR_TICKER_ICON_COLOR,
                    WHITE); 
            mIconColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mIconColor.setSummary(hexColor);
            mIconColor.setDefaultColors(WHITE, WHITE);
            mIconColor.setOnPreferenceChangeListener(this);

            mIconColorDark =
                    (ColorPickerPreference) findPreference(PREF_TICKER_ICON_COLOR_DARK);
            intColor = Settings.System.getInt(mResolver,
                    Settings.System.STATUS_BAR_TICKER_ICON_COLOR_DARK_MODE,
                    BLACK);
            mIconColorDark.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mIconColorDark.setSummary(hexColor);
            mIconColorDark.setDefaultColors(BLACK, BLACK);
            mIconColorDark.setOnPreferenceChangeListener(this);
        } else {
            prefSet.removePreference(findPreference("ticker_colors"));
        }

        if (showCount) {
            mCountIconColor =
                    (ColorPickerPreference) findPreference(COUNT_ICON_COLOR);
            intColor = Settings.System.getInt(mResolver,
                    Settings.System.STATUS_BAR_NOTIF_COUNT_ICON_COLOR,
                    WHITE); 
            mCountIconColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mCountIconColor.setSummary(hexColor);
            mCountIconColor.setDefaultColors(WHITE, VRTOXIN_BLUE);
            mCountIconColor.setOnPreferenceChangeListener(this);

            mCountTextColor =
                    (ColorPickerPreference) findPreference(COUNT_TEXT_COLOR);
            intColor = Settings.System.getInt(mResolver,
                    Settings.System.STATUS_BAR_NOTIF_COUNT_TEXT_COLOR,
                    WHITE); 
            mCountTextColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mCountTextColor.setSummary(hexColor);
            mCountTextColor.setDefaultColors(WHITE, WHITE);
            mCountTextColor.setOnPreferenceChangeListener(this);
        } else {
            prefSet.removePreference(findPreference("notif_colors"));
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
        String hex;
        int intHex;

        if (preference == mShowCount) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_NOTIF_COUNT,
                    value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mShowTicker) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_SHOW_TICKER,
                    value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mTextColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_TICKER_TEXT_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mIconColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_TICKER_ICON_COLOR,
                    intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mIconColorDark) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_TICKER_ICON_COLOR_DARK_MODE,
                    intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mTextColorDark) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_TICKER_TEXT_COLOR_DARK_MODE,
                    intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mCountIconColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_NOTIF_COUNT_ICON_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mCountTextColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_NOTIF_COUNT_TEXT_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mTickerFontSize) {
            int width = ((Integer)newValue).intValue();
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_TICKER_FONT_SIZE, width);
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

        StatusBarTickerFragment getOwner() {
            return (StatusBarTickerFragment) getTargetFragment();
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
                                    Settings.System.STATUS_BAR_NOTIF_COUNT, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_SHOW_TICKER, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_TICKER_TEXT_COLOR,
                                    WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_TICKER_ICON_COLOR,
                                    WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_NOTIF_COUNT_ICON_COLOR,
                                    WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_NOTIF_COUNT_TEXT_COLOR,
                                    BLACK);
                            getOwner().refreshSettings();
                        }
                    })
                    .setPositiveButton(R.string.reset_vrtoxin,
                            new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_NOTIF_COUNT, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_SHOW_TICKER, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_TICKER_TEXT_COLOR,
                                    VRTOXIN_BLUE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_TICKER_ICON_COLOR,
                                    VRTOXIN_BLUE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_NOTIF_COUNT_ICON_COLOR,
                                    VRTOXIN_BLUE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_NOTIF_COUNT_TEXT_COLOR,
                                    0xff00ff00);
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
