/*
 *  Copyright (C) 2015 The OmniROM Project
 *  Copyright (C) 2016 The VRToxin Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.android.vrtoxin.lockscreen;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.widget.Toast;

import com.android.vrtoxin.R;

import com.android.vrtoxin.preferences.SeekBarPreference;

public class LockscreenWallpaper extends PreferenceFragment
        implements OnPreferenceChangeListener  {

    public LockscreenWallpaper(){}

    public static final int IMAGE_PICK = 1;

    private static final String KEY_WALLPAPER_CAT = "lockscreen_wallpaper";
    private static final String KEY_WALLPAPER_SET = "lockscreen_wallpaper_set";
    private static final String KEY_WALLPAPER_CLEAR = "lockscreen_wallpaper_clear";
    private static final String LOCKSCREEN_SEE_THROUGH  = "lockscreen_see_through";
    private static final String LOCKSCREEN_BLUR_RADIUS  = "lockscreen_blur_radius";

    private PreferenceCategory mWallpaperCat;
    private Preference mSetWallpaper;
    private Preference mClearWallpaper;
    SwitchPreference mSeeThrough;
    private SeekBarPreference mBlurRadius;

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

        addPreferencesFromResource(R.xml.lockscreen_wallpaper);

        PreferenceScreen prefSet = getPreferenceScreen();
        mResolver = getActivity().getContentResolver();

        mWallpaperCat = (PreferenceCategory) findPreference(KEY_WALLPAPER_CAT);
        mSetWallpaper = (Preference) findPreference(KEY_WALLPAPER_SET);
        mClearWallpaper = (Preference) findPreference(KEY_WALLPAPER_CLEAR);

        boolean seeThrough = Settings.System.getInt(mResolver,
            Settings.System.LOCKSCREEN_SEE_THROUGH, 1) == 1;

        mBlurRadius = (SeekBarPreference) findPreference(LOCKSCREEN_BLUR_RADIUS);
        mBlurRadius.setValue(Settings.System.getInt(mResolver,
                Settings.System.LOCKSCREEN_BLUR_RADIUS, 0));
        mBlurRadius.setOnPreferenceChangeListener(this);

        mSeeThrough = (SwitchPreference) findPreference(LOCKSCREEN_SEE_THROUGH);
        mSeeThrough.setChecked(Settings.System.getInt(mResolver,
            Settings.System.LOCKSCREEN_SEE_THROUGH, 0) == 1);
        mSeeThrough.setOnPreferenceChangeListener(this);

        if (seeThrough) {
            mWallpaperCat.removePreference(findPreference("lockscreen_see_through"));
            mWallpaperCat.removePreference(findPreference("lockscreen_wallpaper_set"));
            prefSet.removePreference(mWallpaperCat);
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mSeeThrough) {
            Settings.System.putInt(mResolver,
                    Settings.System.LOCKSCREEN_SEE_THROUGH,
            (Boolean) newValue ? 1 : 0);
            clearKeyguardWallpaper();
            Toast.makeText(getView().getContext(), getString(R.string.reset_lockscreen_wallpaper),
            Toast.LENGTH_LONG).show();
            refreshSettings();
            return true;
        } else if (preference == mBlurRadius) {
            int width = ((Integer)newValue).intValue();
            Settings.System.putInt(mResolver,
                    Settings.System.LOCKSCREEN_BLUR_RADIUS, width);
            return true;
         }
         return false;
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mSetWallpaper) {
            setKeyguardWallpaper();
            return true;
        } else if (preference == mClearWallpaper) {
            clearKeyguardWallpaper();
            Toast.makeText(getView().getContext(), getString(R.string.reset_lockscreen_wallpaper),
            Toast.LENGTH_LONG).show();
            return true;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri uri = data.getData();
                Intent intent = new Intent();
                intent.setClassName("com.android.wallpapercropper", "com.android.wallpapercropper.WallpaperCropActivity");
                intent.putExtra("keyguardMode", "1");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setData(uri);
                startActivity(intent);
            }
        }
    }

    private void setKeyguardWallpaper() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK);
    }

    private void clearKeyguardWallpaper() {
        WallpaperManager wallpaperManager = null;
        wallpaperManager = WallpaperManager.getInstance(getActivity());
        wallpaperManager.clearKeyguardWallpaper();
    }
}
