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

import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.text.Html;
import android.view.IWindowManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManagerPolicyControl;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.vrtoxin.utils.Helpers;

public class HomeFragment extends Fragment {

    Context context;
    private int clickCount;

    public HomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.context = getActivity().getApplicationContext();
        clickCount = 0;
    }

    private void noBrowserSnack(View v) {
        Snackbar.make(v, getString(R.string.no_browser_error), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.home_frag_card, container, false);

        final LinearLayout logo = (LinearLayout)v.findViewById(R.id.logo_card);
        LinearLayout fullScreen = (LinearLayout)v.findViewById(R.id.full_screen_card);
        LinearLayout overviewSettings = (LinearLayout)v.findViewById(R.id.overview_settings_card);
        LinearLayout statusBarSettings = (LinearLayout)v.findViewById(R.id.status_bar_card);
        LinearLayout restartSystemUI = (LinearLayout)v.findViewById(R.id.restart_systemui_card);
        LinearLayout powerMenu = (LinearLayout)v.findViewById(R.id.power_menu_card);
        LinearLayout otaFrag = (LinearLayout)v.findViewById(R.id.ota_card);

        fullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentResolver mResolver = context.getContentResolver();
                String value = Settings.Global.getString(mResolver, Settings.Global.POLICY_CONTROL);
                boolean isExpanded = "immersive.full=*".equals(value);
                Settings.Global.putString(mResolver, Settings.Global.POLICY_CONTROL,
                        isExpanded ? "" : "immersive.full=*");
                if (isExpanded)
                    WindowManagerPolicyControl.reloadFromSetting(context);
            }
        });

        overviewSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setClassName("com.android.settings",
                    "com.android.settings.Settings$MainSettingsActivity");
                startActivity(intent);
            }
        });

        statusBarSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((VRToxinActivity)getActivity()).displaySubFrag(getString(R.string.status_bar_frag_title));
            }
        });

        restartSystemUI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helpers.restartSystemUI();
            }
        });

        powerMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final IWindowManager windowManagerService = IWindowManager.Stub.asInterface(
                        ServiceManager.getService(Context.WINDOW_SERVICE));
                if (windowManagerService == null) {
                    return;
                }
                try {
                    windowManagerService.toggleGlobalMenu();
                } catch (RemoteException e) {
                }
            }
        });

        powerMenu.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ((VRToxinActivity)getActivity()).displaySubFrag(getString(R.string.powermenu_frag_title));
                return true;
            }
        });

        otaFrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((VRToxinActivity)getActivity()).displaySubFrag(getString(R.string.ota_updater_frag_title));
            }
        });

        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCount++;
                if (clickCount == 5) {
                    Snackbar.make(v, getString(R.string.click1), Snackbar.LENGTH_SHORT).show();
                }
                if (clickCount > 5 && clickCount < 10) {
                    Snackbar.make(v, String.format(getString(R.string.click2), clickCount), Snackbar.LENGTH_SHORT).show();
                }
                if (clickCount == 10) {
                    Snackbar.make(v, String.format(getString(R.string.click3), clickCount), Snackbar.LENGTH_LONG).show();
                    clickCount = 0;
                }
            }
        });

        logo.setClickable(false);
        return v;
    }
}
