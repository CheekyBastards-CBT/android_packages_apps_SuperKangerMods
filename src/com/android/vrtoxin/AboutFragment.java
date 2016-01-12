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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


public class AboutFragment extends Fragment {

    Context context;
    private int clickCount;

    public AboutFragment() {
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
        View v = inflater.inflate(R.layout.about_frag_card, container, false);

        final LinearLayout logo = (LinearLayout)v.findViewById(R.id.logo_card);

        //VRToxin website
        LinearLayout link1 = (LinearLayout)v.findViewById(R.id.link1_card);
        //Google+
        LinearLayout link2 = (LinearLayout)v.findViewById(R.id.link2_card);
        //Twitter
        LinearLayout link3 = (LinearLayout)v.findViewById(R.id.link3_card);
        //Donate
        LinearLayout link4 = (LinearLayout)v.findViewById(R.id.link4_card);

        link1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent link = new Intent(Intent.ACTION_VIEW);
                Uri url = Uri.parse(getString(R.string.vrtoxin_site));
                link.setData(url);
                try {
                    startActivity(link);
                } catch (android.content.ActivityNotFoundException e) {
                    noBrowserSnack(v);
                }
            }
        });

        link2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent link = new Intent(Intent.ACTION_VIEW);
                Uri url = Uri.parse(getString(R.string.gplus_data));
                link.setData(url);
                try {
                    startActivity(link);
                } catch (android.content.ActivityNotFoundException e) {
                    noBrowserSnack(v);
                }
            }
        });

        link3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent link = new Intent(Intent.ACTION_VIEW);
                Uri url = Uri.parse(getString(R.string.twit_data));
                link.setData(url);
                try {
                    startActivity(link);
                } catch (android.content.ActivityNotFoundException e) {
                    noBrowserSnack(v);
                }
            }
        });

        link4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent link = new Intent(Intent.ACTION_VIEW);
                Uri url = Uri.parse("https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=CDAAXFJCLA43A");
                link.setData(url);
                try {
                    startActivity(link);
                } catch (android.content.ActivityNotFoundException e) {
                    noBrowserSnack(v);
                }
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
