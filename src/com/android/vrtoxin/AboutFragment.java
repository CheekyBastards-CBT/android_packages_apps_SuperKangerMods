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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class AboutFragment extends Fragment {

    Context context;
    private int clickCount;

    public AboutFragment() {
    }

    private TextView mDownloadSummary;
    private TextView mChangelogSummary;

    private String mStrFileNameNew;
    private String mStrFileURLNew;
    private String mStrCurFile;
    private String mStrDevice;

    private final int STARTUP_DIALOG = 1;
    protected ArrayAdapter<String> adapter;

    private boolean su = false;
    private boolean startup = true;
    private static final String FILENAME_PROC_VERSION = "/proc/version";
    private static final String LOG_TAG = "DeviceInfoSettings";
    private static Intent IRC_INTENT = new Intent(Intent.ACTION_VIEW, Uri.parse("ccircslim:1"));

    public File path;
    public String zipfile;
    public String logfile;
    public String last_kmsgfile;
    public String kmsgfile;
    public String systemfile;
    Process superUser;
    DataOutputStream ds;
    byte[] buf = new byte[1024];

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

        LinearLayout download = (LinearLayout)v.findViewById(R.id.download_card);
        mDownloadSummary = (TextView) v.findViewById(R.id.short_cut_download_summary);

        LinearLayout changelog = (LinearLayout)v.findViewById(R.id.changelog_card);
        mChangelogSummary = (TextView) v.findViewById(R.id.short_cut_changelog_summary);

        //Gapps button
        LinearLayout gapps = (LinearLayout)v.findViewById(R.id.gapps_card);

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

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mStrFileURLNew != null
                        && mStrFileURLNew != "") {
                    launchUrl(mStrFileURLNew);
                } else {
                    launchUrl(getString(R.string.download_url));
                }
            }
        });

        changelog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchUrl(getString(R.string.changelog_url));
            }
        });

        changelog.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                ((VRToxinActivity) getActivity()).displayCurrentChangelog();

                return true;
            }
        });

        gapps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent link = new Intent(Intent.ACTION_VIEW);
                Uri url = Uri.parse("http://opengapps.org");
                link.setData(url);
                try {
                    startActivity(link);
                } catch (android.content.ActivityNotFoundException e) {
                    noBrowserSnack(v);
                }
            }
        });

        gapps.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent link = new Intent(Intent.ACTION_VIEW);
                Uri url = Uri.parse("http://vrtoxin.net/downloads/download.php?file=files/GApps/TBO_GAPPS_12-19.zip");
                link.setData(url);
                try {
                    startActivity(link);
                } catch (android.content.ActivityNotFoundException e) {
                    noBrowserSnack(v);
                }
                return true;
            }
        });

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

        try {
            FileInputStream fstream = new FileInputStream("/system/build.prop");
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                String[] line = strLine.split("=");
                if (line[0].equals("ro.vrtoxin.version")) {
                    mStrCurFile = line[1];
                }
            }
            in.close();
        } catch (Exception e) {
            Toast.makeText(getActivity().getBaseContext(), getString(R.string.system_prop_error),
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        SharedPreferences shPrefs = getActivity().getSharedPreferences("UpdateChecker", 0);
        mStrFileNameNew = shPrefs.getString("Filename", "");
        mStrFileURLNew = shPrefs.getString("DownloadUrl", "");

        updateView();

        return v;
    }

    private void launchUrl(String url) {
        Uri uriUrl = Uri.parse(url);
        Intent urlIntent = new Intent(Intent.ACTION_VIEW, uriUrl);
        urlIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getActivity().startActivity(urlIntent);
    }

    public void updateView() {
        if (!mStrFileNameNew.equals("") && !(mStrFileNameNew.compareToIgnoreCase(mStrCurFile)<=0)) {
            mDownloadSummary.setTextColor(0xff009688);
            mChangelogSummary.setTextColor(0xff009688);

            mDownloadSummary.setText(getString(R.string.short_cut_download_summary_update_available));
            mChangelogSummary.setText(getString(R.string.short_cut_changelog_summary_update_available));
        }
    }

    private void toast(String text) {
        // easy toasts for all!
        Toast toast = Toast.makeText(getView().getContext(), text,
                Toast.LENGTH_SHORT);
        toast.show();
    }

    private boolean isCallable(Intent intent) {
        List<ResolveInfo> list = getActivity().getPackageManager().queryIntentActivities(
                intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    public short sdAvailable() {
        // check if sdcard is available
        // taken from developer.android.com
        short mExternalStorageAvailable = 0;
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write the media
            mExternalStorageAvailable = 2;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // We can only read the media
            mExternalStorageAvailable = 1;
        } else {
            // Something else is wrong. It may be one of many other states, but
            // all we need
            // to know is we can neither read nor write
            mExternalStorageAvailable = 0;
        }
        return mExternalStorageAvailable;
    }
}
