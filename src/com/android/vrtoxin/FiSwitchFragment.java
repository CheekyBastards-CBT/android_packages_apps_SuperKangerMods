/*
 * Copyright (C) 2015 The Pure Nexus Project
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
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class FiSwitchFragment extends Fragment {
    private SharedPreferences prefs;
    private FloatingActionButton fabAdd;
    static String FI_FAB_ENABLED = "fi_fab_enabled";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fiswitch, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefs = this.getActivity().getSharedPreferences(getActivity().getPackageName(), Context.MODE_PRIVATE);

        fabAdd = (FloatingActionButton) view.findViewById(R.id.fab);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchRadioInfo();
            }
        });

        setFabEnabled(isFabEnabled());

        LinearLayout link1 = (LinearLayout)view.findViewById(R.id.dialcode1);
        LinearLayout link2 = (LinearLayout)view.findViewById(R.id.dialcode2);
        LinearLayout link3 = (LinearLayout)view.findViewById(R.id.dialcode3);
        LinearLayout link4 = (LinearLayout)view.findViewById(R.id.dialcode4);
        LinearLayout link5 = (LinearLayout)view.findViewById(R.id.dialcode5);
        LinearLayout link6 = (LinearLayout)view.findViewById(R.id.dialcode6);

        link1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // -- Below strategy requires android.permission.CONTROL_INCALL_EXPERIENCE to not be a system permission, or app to reside in system
                getActivity().sendBroadcast(new Intent("android.provider.Telephony.SECRET_CODE", Uri.parse("android_secret_code://34777")));
            }
        });

        link2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // -- Below strategy requires android.permission.CONTROL_INCALL_EXPERIENCE to not be a system permission, or app to reside in system
                getActivity().sendBroadcast(new Intent("android.provider.Telephony.SECRET_CODE", Uri.parse("android_secret_code://34866")));
            }
        });

        link3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // -- Below strategy requires android.permission.CONTROL_INCALL_EXPERIENCE to not be a system permission, or app to reside in system
                getActivity().sendBroadcast(new Intent("android.provider.Telephony.SECRET_CODE", Uri.parse("android_secret_code://346398")));
            }
        });

        link4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // -- Below strategy requires android.permission.CONTROL_INCALL_EXPERIENCE to not be a system permission, or app to reside in system
                getActivity().sendBroadcast(new Intent("android.provider.Telephony.SECRET_CODE", Uri.parse("android_secret_code://342886")));
            }
        });

        link5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // -- Below strategy requires android.permission.CONTROL_INCALL_EXPERIENCE to not be a system permission, or app to reside in system
                getActivity().sendBroadcast(new Intent("android.provider.Telephony.SECRET_CODE", Uri.parse("android_secret_code://34963")));
            }
        });

        link6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // -- Below strategy requires android.permission.CONTROL_INCALL_EXPERIENCE to not be a system permission, or app to reside in system
                getActivity().sendBroadcast(new Intent("android.provider.Telephony.SECRET_CODE", Uri.parse("android_secret_code://344636")));
            }
        });

        getActivity().invalidateOptionsMenu();
    }

    private void launchRadioInfo() {
        Intent link = new Intent(Intent.ACTION_MAIN);
        ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.RadioInfo");
        link.setComponent(cn);
        startActivity(link);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.findItem(R.id.action_fabhide).setChecked(isFabEnabled());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_fabhide:
                boolean checked = item.isChecked();
                item.setChecked(!checked);
                setFabEnabled(!checked);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setFabEnabled(boolean enabled) {
        if (fabAdd != null) {
            fabAdd.setVisibility(enabled ? View.VISIBLE: View.GONE);
        }
        prefs.edit().putBoolean(FI_FAB_ENABLED, enabled).apply();
    }

    public boolean isFabEnabled() {
        return fabAdd != null && prefs.getBoolean(FI_FAB_ENABLED, fabAdd.getVisibility() != View.GONE);
    }

    public FiSwitchFragment() {};
}

