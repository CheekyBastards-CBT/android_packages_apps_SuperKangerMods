/*
 * Copyright (C) 2014 DarkKat
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
/*
package com.android.vrtoxin;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.preference.SlimSeekBarPreference;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.vrtoxin.R;
import com.android.vrtoxin.preferences.ColorPickerPreference;

public class NavigationBarButtonAdvanced extends PreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    public NavigationBarButtonAdvanced(){}

    private static final String PREF_CAT_MENU_BUTTON =
            "navigation_bar_cat_menu_button";
    private static final String PREF_CAT_COLORS =
            "navigation_bar_cat_colors";
    private static final String PREF_SLIM_DIM =
            "dim_nav_buttons_cat";
    private static final String PREF_SHOW_IME_ARROWS =
            "navigation_bar_show_ime_arrows";
    private static final String PREF_MENU_VISIBILITY =
            "navigation_bar_menu_visibility";
    private static final String PREF_MENU_LOCATION =
            "navigation_bar_menu_location";
    private static final String DIM_NAV_BUTTONS =
            "dim_nav_buttons";
    private static final String DIM_NAV_BUTTONS_TOUCH_ANYWHERE =
            "dim_nav_buttons_touch_anywhere";
    private static final String DIM_NAV_BUTTONS_TIMEOUT =
            "dim_nav_buttons_timeout";
    private static final String DIM_NAV_BUTTONS_ALPHA =
            "dim_nav_buttons_alpha";
    private static final String DIM_NAV_BUTTONS_ANIMATE =
            "dim_nav_buttons_animate";
    private static final String DIM_NAV_BUTTONS_ANIMATE_DURATION =
            "dim_nav_buttons_animate_duration";
    private static final String PREF_ICON_COLOR_MODE =
            "navigation_bar_icon_color_mode";
    private static final String PREF_RIPPLE_COLOR_MODE =
            "navigation_bar_button_ripple_color_mode";
    private static final String PREF_ICON_COLOR =
            "navigation_bar_icon_color";
    private static final String PREF_RIPPLE_COLOR =
            "navigation_bar_button_ripple_color";

    private static final int WHITE = 0xffffffff;
    private static final int VRTOXIN_BLUE = 0xff33b5e5;

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET = 0;

    private SwitchPreference mShowImeArrows;
    private ListPreference mMenuVisibility;
    private ListPreference mMenuLocation;
    private ListPreference mIconColorMode;
    private ListPreference mRippleColorMode;
    private ColorPickerPreference mIconColor;
    private ColorPickerPreference mRippleColor;

    SwitchPreference mDimNavButtons;
    SwitchPreference mDimNavButtonsTouchAnywhere;
    SlimSeekBarPreference mDimNavButtonsTimeout;
    SlimSeekBarPreference mDimNavButtonsAlpha;
    SwitchPreference mDimNavButtonsAnimate;
    SlimSeekBarPreference mDimNavButtonsAnimateDuration;

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

        addPreferencesFromResource(R.xml.navigation_bar_button_advanced);
        PreferenceScreen prefSet = getPreferenceScreen();
        mResolver = getActivity().getContentResolver();
        int intValue;
        int intColor;
        String hexColor;

        boolean isMenuButtonVisible = Settings.System.getInt(mResolver,
                Settings.System.NAVIGATION_BAR_MENU_VISIBILITY, 0) != 2;
        boolean colorizeIcon = Settings.System.getInt(mResolver,
               Settings.System.NAVIGATION_BAR_ICON_COLOR_MODE, 0) != 0;
        boolean colorizeRipple = Settings.System.getInt(mResolver,
               Settings.System.NAVIGATION_BAR_BUTTON_RIPPLE_COLOR_MODE, 2) == 1;
        boolean isSlimDimVisible = Settings.System.getInt(mResolver,
                Settings.System.DIM_NAV_BUTTONS, 0) == 1;

        PreferenceCategory catMenuButton =
                (PreferenceCategory) findPreference(PREF_CAT_MENU_BUTTON);
        PreferenceCategory catColors =
                (PreferenceCategory) findPreference(PREF_CAT_COLORS);
        PreferenceCategory catSlimDim =
                (PreferenceCategory) findPreference(PREF_SLIM_DIM);

        mShowImeArrows = (SwitchPreference) findPreference(PREF_SHOW_IME_ARROWS);
        mShowImeArrows.setChecked(Settings.System.getInt(mResolver,
                Settings.System.NAVIGATION_BAR_SHOW_IME_ARROWS, 0) == 1);
        mShowImeArrows.setOnPreferenceChangeListener(this);

        mMenuVisibility =
                (ListPreference) findPreference(PREF_MENU_VISIBILITY);
        intValue = Settings.System.getInt(mResolver,
                Settings.System.NAVIGATION_BAR_MENU_VISIBILITY, 0);
        mMenuVisibility.setValue(String.valueOf(intValue));
        mMenuVisibility.setSummary(mMenuVisibility.getEntry());
        mMenuVisibility.setOnPreferenceChangeListener(this);

        if (isMenuButtonVisible) {
            mMenuLocation =
                    (ListPreference) findPreference(PREF_MENU_LOCATION);
            intValue = Settings.System.getInt(mResolver,
                    Settings.System.NAVIGATION_BAR_MENU_LOCATION, 0);
            mMenuLocation.setValue(String.valueOf(intValue));
            mMenuLocation.setSummary(mMenuLocation.getEntry());
            mMenuLocation.setOnPreferenceChangeListener(this);
        } else {
            catMenuButton.removePreference(findPreference(PREF_MENU_LOCATION));
        }

        mDimNavButtons = (SwitchPreference) findPreference(DIM_NAV_BUTTONS);
        mDimNavButtons.setChecked(Settings.System.getInt(mResolver,
                Settings.System.DIM_NAV_BUTTONS, 0) == 1);
        mDimNavButtons.setOnPreferenceChangeListener(this);

        if (isSlimDimVisible) {
            mDimNavButtonsTouchAnywhere = (SwitchPreference) findPreference(DIM_NAV_BUTTONS_TOUCH_ANYWHERE);
            mDimNavButtonsTouchAnywhere.setChecked(Settings.System.getInt(mResolver,
                    Settings.System.DIM_NAV_BUTTONS_TOUCH_ANYWHERE, 0) == 1);
            mDimNavButtonsTouchAnywhere.setOnPreferenceChangeListener(this);

            mDimNavButtonsTimeout = (SlimSeekBarPreference) findPreference(DIM_NAV_BUTTONS_TIMEOUT);
            final int dimTimeout = Settings.System.getInt(mResolver,
                    Settings.System.DIM_NAV_BUTTONS_TIMEOUT, 3000);
            mDimNavButtonsTimeout.setDefault(3000);
            mDimNavButtonsTimeout.isMilliseconds(true);
            mDimNavButtonsTimeout.setInterval(1);
            mDimNavButtonsTimeout.minimumValue(100);
            mDimNavButtonsTimeout.multiplyValue(100);
            // minimum 100 is 1 interval of the 100 multiplier
            mDimNavButtonsTimeout.setInitValue((dimTimeout / 100) - 1);
            mDimNavButtonsTimeout.setOnPreferenceChangeListener(this);

            mDimNavButtonsAlpha = (SlimSeekBarPreference) findPreference(DIM_NAV_BUTTONS_ALPHA);
            int alphaScale = Settings.System.getInt(mResolver,
                    Settings.System.DIM_NAV_BUTTONS_ALPHA, 50);
            mDimNavButtonsAlpha.setDefault(50);
            mDimNavButtonsAlpha.setInterval(1);
            mDimNavButtonsAlpha.setInitValue(alphaScale);
            mDimNavButtonsAlpha.setOnPreferenceChangeListener(this);

            mDimNavButtonsAnimate = (SwitchPreference) findPreference(DIM_NAV_BUTTONS_ANIMATE);
            mDimNavButtonsAnimate.setChecked(Settings.System.getInt(mResolver,
                    Settings.System.DIM_NAV_BUTTONS_ANIMATE, 0) == 1);
            mDimNavButtonsAnimate.setOnPreferenceChangeListener(this);

            mDimNavButtonsAnimateDuration = (SlimSeekBarPreference) findPreference(DIM_NAV_BUTTONS_ANIMATE_DURATION);
            final int animateDuration = Settings.System.getInt(mResolver,
                    Settings.System.DIM_NAV_BUTTONS_ANIMATE_DURATION, 2000);
            mDimNavButtonsAnimateDuration.setDefault(2000);
            mDimNavButtonsAnimateDuration.isMilliseconds(true);
            mDimNavButtonsAnimateDuration.setInterval(1);
            mDimNavButtonsAnimateDuration.minimumValue(100);
            mDimNavButtonsAnimateDuration.multiplyValue(100);
            // minimum 100 is 1 interval of the 100 multiplier
            mDimNavButtonsAnimateDuration.setInitValue((animateDuration / 100) - 1);
            mDimNavButtonsAnimateDuration.setOnPreferenceChangeListener(this);
        } else {
            catSlimDim.removePreference(findPreference(DIM_NAV_BUTTONS_TIMEOUT));
            catSlimDim.removePreference(findPreference(DIM_NAV_BUTTONS_ALPHA));
            catSlimDim.removePreference(findPreference(DIM_NAV_BUTTONS_ANIMATE));
            catSlimDim.removePreference(findPreference(DIM_NAV_BUTTONS_ANIMATE_DURATION));
        }

        mIconColorMode =
                (ListPreference) findPreference(PREF_ICON_COLOR_MODE);
        intValue = Settings.System.getInt(mResolver,
                Settings.System.NAVIGATION_BAR_ICON_COLOR_MODE, 1);
        mIconColorMode.setValue(String.valueOf(intValue));
        mIconColorMode.setSummary(mIconColorMode.getEntry());
        mIconColorMode.setOnPreferenceChangeListener(this);

        mRippleColorMode =
                (ListPreference) findPreference(PREF_RIPPLE_COLOR_MODE);
        intValue = Settings.System.getInt(mResolver,
                Settings.System.NAVIGATION_BAR_BUTTON_RIPPLE_COLOR_MODE, 2);
        mRippleColorMode.setValue(String.valueOf(intValue));
        mRippleColorMode.setSummary(mRippleColorMode.getEntry());
        mRippleColorMode.setOnPreferenceChangeListener(this);

        if (colorizeIcon) {
            mIconColor =
                    (ColorPickerPreference) findPreference(PREF_ICON_COLOR);
            intColor = Settings.System.getInt(mResolver,
                    Settings.System.NAVIGATION_BAR_ICON_COLOR, WHITE); 
            mIconColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mIconColor.setSummary(hexColor);
            mIconColor.setOnPreferenceChangeListener(this);
        } else {
            catColors.removePreference(findPreference(PREF_ICON_COLOR));
        }

        if (colorizeRipple) {
            mRippleColor =
                    (ColorPickerPreference) findPreference(PREF_RIPPLE_COLOR);
            intColor = Settings.System.getInt(mResolver,
                    Settings.System.NAVIGATION_BAR_BUTTON_RIPPLE_COLOR, WHITE); 
            mRippleColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mRippleColor.setSummary(hexColor);
            mRippleColor.setOnPreferenceChangeListener(this);
        } else {
            catColors.removePreference(findPreference(PREF_RIPPLE_COLOR));
        }

        if (!colorizeIcon && !colorizeRipple) {
            prefSet.removePreference(catColors);
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
        int intValue;
        int index;
        String hex;
        int intHex;

        if (preference == mShowImeArrows) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.NAVIGATION_BAR_SHOW_IME_ARROWS, value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mMenuVisibility) {
            intValue = Integer.valueOf((String) newValue);
            index = mMenuVisibility.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                Settings.System.NAVIGATION_BAR_MENU_VISIBILITY, intValue);
            preference.setSummary(mMenuVisibility.getEntries()[index]);
            refreshSettings();
            return true;
        } else if (preference == mMenuLocation) {
            intValue = Integer.valueOf((String) newValue);
            index = mMenuLocation.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                Settings.System.NAVIGATION_BAR_MENU_LOCATION, intValue);
            preference.setSummary(mMenuLocation.getEntries()[index]);
            return true;
        } else if (preference == mDimNavButtons) {
            Settings.System.putInt(mResolver,
                Settings.System.DIM_NAV_BUTTONS,
                    ((Boolean) newValue) ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mDimNavButtonsTouchAnywhere) {
            Settings.System.putInt(mResolver,
                Settings.System.DIM_NAV_BUTTONS_TOUCH_ANYWHERE,
                    ((Boolean) newValue) ? 1 : 0);
            return true;
        } else if (preference == mDimNavButtonsTimeout) {
            Settings.System.putInt(mResolver,
                Settings.System.DIM_NAV_BUTTONS_TIMEOUT, Integer.parseInt((String) newValue));
            return true;
        } else if (preference == mDimNavButtonsAlpha) {
            Settings.System.putInt(mResolver,
                Settings.System.DIM_NAV_BUTTONS_ALPHA, Integer.parseInt((String) newValue));
            return true;
        } else if (preference == mDimNavButtonsAnimate) {
            Settings.System.putInt(mResolver,
                Settings.System.DIM_NAV_BUTTONS_ANIMATE,
                    ((Boolean) newValue) ? 1 : 0);
            return true;
        } else if (preference == mDimNavButtonsAnimateDuration) {
            Settings.System.putInt(mResolver,
                Settings.System.DIM_NAV_BUTTONS_ANIMATE_DURATION,
                Integer.parseInt((String) newValue));
            return true;
        } else if (preference == mIconColorMode) {
            intValue = Integer.valueOf((String) newValue);
            index = mIconColorMode.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                Settings.System.NAVIGATION_BAR_ICON_COLOR_MODE, intValue);
            preference.setSummary(mIconColorMode.getEntries()[index]);
            refreshSettings();
            return true;
        } else if (preference == mRippleColorMode) {
            intValue = Integer.valueOf((String) newValue);
            index = mIconColorMode.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                Settings.System.NAVIGATION_BAR_BUTTON_RIPPLE_COLOR_MODE, intValue);
            preference.setSummary(mRippleColorMode.getEntries()[index]);
            refreshSettings();
            return true;
        } else if (preference == mIconColor) {
            hex = ColorPickerPreference.convertToARGB(
                Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.NAVIGATION_BAR_ICON_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mRippleColor) {
            hex = ColorPickerPreference.convertToARGB(
                Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.NAVIGATION_BAR_BUTTON_RIPPLE_COLOR, intHex);
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

        NavigationBarButtonAdvanced getOwner() {
            return (NavigationBarButtonAdvanced) getTargetFragment();
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
                                    Settings.System.NAVIGATION_BAR_SHOW_IME_ARROWS, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.NAVIGATION_BAR_MENU_VISIBILITY, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.NAVIGATION_BAR_MENU_LOCATION, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.DIM_NAV_BUTTONS, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.DIM_NAV_BUTTONS_TOUCH_ANYWHERE, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.DIM_NAV_BUTTONS_TIMEOUT, 3000);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.DIM_NAV_BUTTONS_ALPHA, 50);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.DIM_NAV_BUTTONS_ANIMATE, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.DIM_NAV_BUTTONS_ANIMATE_DURATION, 2000);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.NAVIGATION_BAR_ICON_COLOR_MODE, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.NAVIGATION_BAR_BUTTON_RIPPLE_COLOR_MODE, 2);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.NAVIGATION_BAR_ICON_COLOR,
                                    WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.NAVIGATION_BAR_BUTTON_RIPPLE_COLOR,
                                    WHITE);
                            getOwner().refreshSettings();
                        }
                    })
                    .setPositiveButton(R.string.reset_vrtoxin,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.NAVIGATION_BAR_SHOW_IME_ARROWS, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.NAVIGATION_BAR_MENU_VISIBILITY, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.NAVIGATION_BAR_MENU_LOCATION, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.DIM_NAV_BUTTONS, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.DIM_NAV_BUTTONS_TOUCH_ANYWHERE, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.DIM_NAV_BUTTONS_TIMEOUT, 3000);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.DIM_NAV_BUTTONS_ALPHA, 30);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.DIM_NAV_BUTTONS_ANIMATE, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.DIM_NAV_BUTTONS_ANIMATE_DURATION, 2000);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.NAVIGATION_BAR_ICON_COLOR_MODE, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.NAVIGATION_BAR_BUTTON_RIPPLE_COLOR_MODE, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.NAVIGATION_BAR_ICON_COLOR,
                                    VRTOXIN_BLUE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.NAVIGATION_BAR_BUTTON_RIPPLE_COLOR,
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
}*/
