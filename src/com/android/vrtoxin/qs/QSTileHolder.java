/*
 * Copyright (C) 2015 The CyanogenMod Project
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
package com.android.vrtoxin.qs;

import android.content.Context;

import com.android.internal.util.vrtoxin.QSConstants;
import com.android.internal.util.vrtoxin.QSUtils;
import com.android.vrtoxin.R;

/**
 * This class holds the icon, the name - or the string the user sees,
 * and the value which will be stored
 */
public class QSTileHolder {
    public static final String TILE_ADD_DELETE = "";

    public final String resourceName;
    public final String value;
    public final String name;

    public QSTileHolder(String resourceName, String value, String name) {
        this.resourceName = resourceName;
        this.value = value;
        this.name = name;
    }

    public static QSTileHolder from(Context context, String tileType) {
        String resourceName = null;
        int stringId = -1;

        if (!TILE_ADD_DELETE.equals(tileType) &&
                !QSUtils.getAvailableTiles(context).contains(tileType)) {
            return null;
        }

        switch (tileType) {
            case TILE_ADD_DELETE:
                break;
            case QSConstants.TILE_AMBIENT_DISPLAY:
                resourceName = "ic_qs_ambientdisplay_on";
                stringId = R.string.qs_tile_ambient_display;
                break;
            case QSConstants.TILE_AIRPLANE:
                resourceName = "ic_signal_airplane_enable_animation";
                stringId = R.string.airplane_mode;
                break;
            case QSConstants.TILE_BLUETOOTH:
                resourceName = "ic_qs_bluetooth_on";
                stringId = R.string.bluetooth_settings_title;
                break;
            case QSConstants.TILE_BRIGHTNESS:
                resourceName = "ic_qs_brightness_auto_on_alpha";
                stringId = R.string.qs_tile_brightness;
                break;
            case QSConstants.TILE_CAST:
                resourceName = "ic_qs_cast_on";
                stringId = R.string.cast_screen;
                break;
            case QSConstants.TILE_CELLULAR:
                resourceName = "ic_qs_signal_full_4";
                stringId = R.string.cellular_data_title;
                break;
            case QSConstants.TILE_INVERSION:
                resourceName = "ic_qs_inversion_on";
                stringId = R.string.accessibility_display_inversion_preference_title;
                break;
            case QSConstants.TILE_EXPANDED_DESKTOP:
                resourceName = "ic_qs_expanded_desktop";
                stringId = R.string.qs_tile_expanded_desktop;
                break;
            case QSConstants.TILE_FLASHLIGHT:
                resourceName = "ic_signal_flashlight_enable_animation";
                stringId = R.string.power_flashlight;
                break;
            case QSConstants.TILE_HEADSUP:
                resourceName = "ic_qs_heads_up_on";
                stringId = R.string.qs_tile_heads_up;
                break;
            case QSConstants.TILE_HOTSPOT:
                resourceName = "ic_qs_hotspot_on";
                stringId = R.string.hotspot;
                break;
            case QSConstants.TILE_LOCATION:
                resourceName = "ic_signal_location_enable_animation";
                stringId = R.string.location_title;
                break;
            case QSConstants.TILE_MUSIC:
                resourceName = "ic_qs_media_play";
                stringId = R.string.qs_tile_music_play;
                break;
            case QSConstants.TILE_NFC:
                resourceName = "ic_qs_nfc_on";
                stringId = R.string.qs_tile_nfc;
                break;
            case QSConstants.TILE_REBOOT:
                resourceName = "ic_qs_reboot";
                stringId = R.string.qs_tile_reboot;
                break;
            case QSConstants.TILE_ROTATION:
                resourceName = "ic_portrait_to_auto_rotate_animation";
                stringId = R.string.qs_tile_rotation;
                break;
            case QSConstants.TILE_SCREENOFF:
                resourceName = "ic_qs_power";
                stringId = R.string.qs_tile_screen_off;
                break;
            case QSConstants.TILE_SCREENSHOT:
                resourceName = "ic_qs_screenshot";
                stringId = R.string.qs_tile_screenshot;
                break;
            case QSConstants.TILE_SCREEN_TIMEOUT:
                resourceName = "ic_qs_screen_timeout_vector";
                stringId = R.string.qs_tile_screen_timeout;
                break;
            case QSConstants.TILE_SYNC:
                resourceName = "ic_qs_sync_on";
                stringId = R.string.qs_tile_sync;
                break;
            case QSConstants.TILE_USB_TETHERING:
                resourceName = "ic_qs_usb_tether_on";
                stringId = R.string.qs_tile_usb_tethering;
                break;
            case QSConstants.TILE_VOLUME:
                resourceName = "ic_qs_volume_panel";
                stringId = R.string.qs_tile_volume_panel;
                break;
            case QSConstants.TILE_VRTOXIN:
                resourceName = "ic_qs_vrtoxin";
                stringId = R.string.qs_tile_vrtoxin;
                break;
            case QSConstants.TILE_WIFI:
                resourceName = "ic_qs_wifi_full_4";
                stringId = R.string.wifi_quick_toggle_title;
                break;
            case QSConstants.TILE_BATTERY_SAVER:
                resourceName = "ic_qs_battery_saver_on";
                stringId = R.string.qs_tile_batterysaver;
                break;
            case QSConstants.TILE_COMPASS:
                resourceName = "ic_qs_compass_on";
                stringId = R.string.qs_tile_compass;
                break;
            case QSConstants.TILE_ADB_NETWORK:
                resourceName = "ic_qs_network_adb_on";
                stringId = R.string.qs_tile_adb;
                break;
            case QSConstants.TILE_LOCKSCREEN:
                resourceName = "ic_qs_lock_screen_on";
                stringId = R.string.qs_tile_lockscreen;
                break;
            case QSConstants.TILE_DND:
                resourceName = "ic_qs_dnd_on";
                stringId = R.string.qs_tile_dnd;
                break;
            case QSConstants.TILE_THEMES:
                resourceName = "ic_settings_themes_alpha";
                stringId = R.string.qs_tile_themes;
                break;
            case QSConstants.TILE_SLIMPIE:
                resourceName = "ic_qs_pie_global_on";
                stringId = R.string.qs_tile_slimpie;
                break;
            case QSConstants.TILE_GESTURE_ANYWHERE:
                resourceName = "ic_qs_gestures_on";
                stringId = R.string.qs_tile_gesture_anywhere;
                break;
            case QSConstants.TILE_NAVBAR:
                resourceName = "ic_qs_navbar_on";
                stringId = R.string.qs_tile_navbar;
                break;
            case QSConstants.TILE_HWKEYS:
                resourceName = "ic_qs_buttons_on";
                stringId = R.string.qs_tile_hwkeys;
                break;
            case QSConstants.TILE_APPCIRCLEBAR:
                resourceName = "ic_qs_appcirclebar_on";
                stringId = R.string.qs_tile_appcirclebar;
                break;
            case QSConstants.TILE_APPSIDEBAR:
                resourceName = "ic_qs_sidebar_off";
                stringId = R.string.qs_tile_appsidebar;
                break;
            case QSConstants.TILE_FLOATING_WINDOWS:
                resourceName = "ic_qs_floating_on";
                stringId = R.string.qs_tile_floating_windows;
                break;
            case QSConstants.TILE_SLIM_FLOATS:
                resourceName = "ic_qs_floating_on";
                stringId = R.string.qs_tile_slim_floats;
                break;
            case QSConstants.TILE_KERNEL_ADIUTOR:
                resourceName = "ic_qs_kernel";
                stringId = R.string.qs_tile_kernel_adiutor;
                break;
            case QSConstants.TILE_LED:
                resourceName = "ic_qs_led_on";
                stringId = R.string.qs_tile_led;
                break;
            default:
                return null;
        }

        String name = stringId != -1 ? context.getString(stringId) : null;
        return new QSTileHolder(resourceName, tileType, name);
    }
}
