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
        //Stephan
        LinearLayout stephan = (LinearLayout)v.findViewById(R.id.stephan_card);
        //Brett
        LinearLayout brett = (LinearLayout)v.findViewById(R.id.brett_card);
        //Akhil
        LinearLayout akhil = (LinearLayout)v.findViewById(R.id.akhil_card);
        //Aniket
        LinearLayout aniket = (LinearLayout)v.findViewById(R.id.aniket_card);
        //Dustin
        LinearLayout dustin = (LinearLayout)v.findViewById(R.id.dustin_card);
        //fizban
        LinearLayout fizban = (LinearLayout)v.findViewById(R.id.fizban_card);
        //Josh
        LinearLayout josh = (LinearLayout)v.findViewById(R.id.josh_card);
        //Mario
        LinearLayout mario = (LinearLayout)v.findViewById(R.id.mario_card);
        //Michael
        LinearLayout michael = (LinearLayout)v.findViewById(R.id.michael_card);
        //Quirin
        LinearLayout quirin = (LinearLayout)v.findViewById(R.id.quirin_card);
        //Punk
        LinearLayout punk = (LinearLayout)v.findViewById(R.id.punk_card);
        //Reven
        LinearLayout reven = (LinearLayout)v.findViewById(R.id.reven_card);
        //Rodman01
        LinearLayout rodman = (LinearLayout)v.findViewById(R.id.rodman_card);
        //Sri
        LinearLayout sri = (LinearLayout)v.findViewById(R.id.sri_card);
        //Tommy
        LinearLayout tommy = (LinearLayout)v.findViewById(R.id.tommy_card);
        //Ujwal
        LinearLayout ujwal = (LinearLayout)v.findViewById(R.id.ujwal_card);
        //Vyt
        LinearLayout vyt = (LinearLayout)v.findViewById(R.id.vyt_card);

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

        stephan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent link = new Intent(Intent.ACTION_VIEW);
                Uri url = Uri.parse("https://plus.google.com/u/0/109705439686346848248/about");
                link.setData(url);
                try {
                    startActivity(link);
                } catch (android.content.ActivityNotFoundException e) {
                    noBrowserSnack(v);
                }
            }
        });

        stephan.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent link = new Intent(Intent.ACTION_VIEW);
                Uri url = Uri.parse("https://github.com/NL-BlackDragon");
                link.setData(url);
                try {
                    startActivity(link);
                } catch (android.content.ActivityNotFoundException e) {
                    noBrowserSnack(v);
                }
                return true;
            }
        });

        brett.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent link = new Intent(Intent.ACTION_VIEW);
                Uri url = Uri.parse("https://plus.google.com/+BrettRogersDipDro420/posts/p/pub");
                link.setData(url);
                try {
                    startActivity(link);
                } catch (android.content.ActivityNotFoundException e) {
                    noBrowserSnack(v);
                }
            }
        });

        brett.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent link = new Intent(Intent.ACTION_VIEW);
                Uri url = Uri.parse("https://github.com/rogersb11");
                link.setData(url);
                try {
                    startActivity(link);
                } catch (android.content.ActivityNotFoundException e) {
                    noBrowserSnack(v);
                }
                return true;
            }
        });

        akhil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent link = new Intent(Intent.ACTION_VIEW);
                Uri url = Uri.parse("https://plus.google.com/u/0/+AkhilNarang/about");
                link.setData(url);
                try {
                    startActivity(link);
                } catch (android.content.ActivityNotFoundException e) {
                    noBrowserSnack(v);
                }
            }
        });

        aniket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent link = new Intent(Intent.ACTION_VIEW);
                Uri url = Uri.parse("https://plus.google.com/u/0/+AniketLamba/about");
                link.setData(url);
                try {
                    startActivity(link);
                } catch (android.content.ActivityNotFoundException e) {
                    noBrowserSnack(v);
                }
            }
        });

        dustin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent link = new Intent(Intent.ACTION_VIEW);
                Uri url = Uri.parse("https://plus.google.com/u/0/+DustinRinne/about");
                link.setData(url);
                try {
                    startActivity(link);
                } catch (android.content.ActivityNotFoundException e) {
                    noBrowserSnack(v);
                }
            }
        });

        fizban.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent link = new Intent(Intent.ACTION_VIEW);
                Uri url = Uri.parse("https://plus.google.com/107234820742650236470/posts");
                link.setData(url);
                try {
                    startActivity(link);
                } catch (android.content.ActivityNotFoundException e) {
                    noBrowserSnack(v);
                }
            }
        });

        josh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent link = new Intent(Intent.ACTION_VIEW);
                Uri url = Uri.parse("https://plus.google.com/102661408293205133053/posts");
                link.setData(url);
                try {
                    startActivity(link);
                } catch (android.content.ActivityNotFoundException e) {
                    noBrowserSnack(v);
                }
            }
        });

        mario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent link = new Intent(Intent.ACTION_VIEW);
                Uri url = Uri.parse("https://plus.google.com/u/0/102792209253885577594/about");
                link.setData(url);
                try {
                    startActivity(link);
                } catch (android.content.ActivityNotFoundException e) {
                    noBrowserSnack(v);
                }
            }
        });

        michael.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent link = new Intent(Intent.ACTION_VIEW);
                Uri url = Uri.parse("https://plus.google.com/u/0/+MichaelIfland314/about");
                link.setData(url);
                try {
                    startActivity(link);
                } catch (android.content.ActivityNotFoundException e) {
                    noBrowserSnack(v);
                }
            }
        });

        quirin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent link = new Intent(Intent.ACTION_VIEW);
                Uri url = Uri.parse("https://plus.google.com/u/0/+QuirinG%C3%B6tz/about");
                link.setData(url);
                try {
                    startActivity(link);
                } catch (android.content.ActivityNotFoundException e) {
                    noBrowserSnack(v);
                }
            }
        });

        punk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent link = new Intent(Intent.ACTION_VIEW);
                Uri url = Uri.parse("https://plus.google.com/+BryanHocking/posts");
                link.setData(url);
                try {
                    startActivity(link);
                } catch (android.content.ActivityNotFoundException e) {
                    noBrowserSnack(v);
                }
            }
        });

        reven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent link = new Intent(Intent.ACTION_VIEW);
                Uri url = Uri.parse("https://plus.google.com/u/0/106285148036011828697/about");
                link.setData(url);
                try {
                    startActivity(link);
                } catch (android.content.ActivityNotFoundException e) {
                    noBrowserSnack(v);
                }
            }
        });

        rodman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent link = new Intent(Intent.ACTION_VIEW);
                Uri url = Uri.parse("https://plus.google.com/u/0/+J%C3%B6rnLiske/about");
                link.setData(url);
                try {
                    startActivity(link);
                } catch (android.content.ActivityNotFoundException e) {
                    noBrowserSnack(v);
                }
            }
        });

        sri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent link = new Intent(Intent.ACTION_VIEW);
                Uri url = Uri.parse("https://plus.google.com/u/0/+SriHarsha-srisurya95/about");
                link.setData(url);
                try {
                    startActivity(link);
                } catch (android.content.ActivityNotFoundException e) {
                    noBrowserSnack(v);
                }
            }
        });

        tommy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent link = new Intent(Intent.ACTION_VIEW);
                Uri url = Uri.parse("https://plus.google.com/u/0/107505173068165970676/about");
                link.setData(url);
                try {
                    startActivity(link);
                } catch (android.content.ActivityNotFoundException e) {
                    noBrowserSnack(v);
                }
            }
        });

        ujwal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent link = new Intent(Intent.ACTION_VIEW);
                Uri url = Uri.parse("https://plus.google.com/112381138897217340780/about");
                link.setData(url);
                try {
                    startActivity(link);
                } catch (android.content.ActivityNotFoundException e) {
                    noBrowserSnack(v);
                }
            }
        });

        vyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent link = new Intent(Intent.ACTION_VIEW);
                Uri url = Uri.parse("https://plus.google.com/113793881359227887277/posts");
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
