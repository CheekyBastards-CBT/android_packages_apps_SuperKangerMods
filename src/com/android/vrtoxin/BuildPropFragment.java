/*
 * Copyright (C) 2015 The Pure Nexus Project
 * credit to Wrdlbrnft also for his Searchable-RecyclerView-Demo
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
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import eu.chainfire.libsuperuser.Shell;

public class BuildPropFragment extends Fragment implements OnQueryTextListener {
    private RecyclerView recyclerView;
    private CoordinatorLayout mCoordLayout;
    private BuildPropRecyclerAdapter mAdapter;
    private ArrayList<Map<String, String>> mProplist;
    private boolean mHasRoom;

    private class LoadProp extends AsyncTask<Void, Void, Void> {
        private ProgressDialog dialog = null;
        private Context context = null;
        private RecyclerView mList = null;
        private CoordinatorLayout mLayout;
        private boolean mTryCatchFail;
        private boolean mIsRestore;
        private Properties prop;
        private String[] pTitle;
        private ArrayList<Map<String, String>> proplist;
        private BuildPropRecyclerAdapter mAdapter;
        private boolean suAvailable = false;

        public LoadProp setInits(Context context, CoordinatorLayout layout, RecyclerView list, Boolean restore) {
            this.context = context;
            mLayout = layout;
            mList = list;
            mIsRestore = restore;
            return this;
        }

        @Override
        protected void onPreExecute() {
            // The progress dialog here is so the user will wait until the prop stuff has loaded

            dialog = new ProgressDialog(context);
            dialog.setTitle(getString(R.string.wait_title));
            dialog.setMessage(getString(R.string.wait_message));
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);

            // A semi-hack way to prevent FCs when orientation changes during progress dialog showing
            VRToxinActivity.lockCurrentOrientation((VRToxinActivity) context);

            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            suAvailable = Shell.SU.available();

            mTryCatchFail = false;
            // copy over backup if from restore trigger
            if (mIsRestore) {
                try {
                    restorefile();
                } catch (Exception e) {
                    mTryCatchFail = true;
                }
            }

            final String fileloc = createTempFile();
            // this loads up the listview - can take a sec
            if (fileloc.equalsIgnoreCase("error")) {
                mTryCatchFail = true;
            }

            prop = new Properties();
            File file = new File(fileloc);
            try {
                prop.load(new FileInputStream(file));

                pTitle = (String[])prop.keySet().toArray(new String[prop.keySet().size()]);

                final List<String> pDesc = new ArrayList<String>();
                for (int i = 0; i < pTitle.length; i++) {
                    pDesc.add(prop.getProperty(pTitle[i]));
                }

                proplist = buildData(pTitle, pDesc);
            } catch (IOException e) {
                mTryCatchFail = true;
            }

            //an attempt at sorting the build.prop mess by title
            if (!mTryCatchFail) {
                Collections.sort(proplist, new Comparator<Map<String, String>>() {
                    @Override
                    public int compare(Map<String, String> m1, Map<String, String> m2) {
                        return m1.get("title").compareTo(m2.get("title"));
                    }
                });
            }
            
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //if it worked - set up adapter and onitemclick
            if (!mTryCatchFail) {
                LinearLayoutManager llm = new LinearLayoutManager(context);
                llm.setOrientation(LinearLayoutManager.VERTICAL);
                mList.setLayoutManager(llm);

                mAdapter = new BuildPropRecyclerAdapter(proplist);
                mList.setAdapter(mAdapter);

                // toss these up to the fragment so we can use them
                setAdapterItems(mAdapter, proplist);
            }

            ((VRToxinActivity) context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
            dialog.dismiss();

            String filepath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/build.prop.bak";

            if (!suAvailable) {
                Snackbar.make(mLayout, getString(R.string.no_root_error), Snackbar.LENGTH_SHORT).show();
            } else if (mTryCatchFail) {
                Snackbar.make(mLayout, getString(R.string.general_error), Snackbar.LENGTH_SHORT).show();
            } else if (mIsRestore){
                Snackbar.make(mLayout, String.format(getString(R.string.restore_loc), filepath), Snackbar.LENGTH_LONG).show();
            }

            if (EditPropFragment.isProcessing) {
                if (EditPropFragment.isProcessingError) {
                    Snackbar.make(mLayout, getString(R.string.general_error), Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(mLayout, String.format(getString(R.string.edit_backup_loc), filepath), Snackbar.LENGTH_LONG).show();
                }
            }

            EditPropFragment.isProcessing = false;
            EditPropFragment.isProcessingError = false;

            VRToxinActivity.isRoot = suAvailable;
        }
    }

    private class BuildPropItem extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected TextView titleText;
        protected TextView descriptionText;

        public BuildPropItem(View itemView) {
            super(itemView);
            titleText = (TextView) itemView.findViewById(R.id.prop_title);
            descriptionText = (TextView) itemView.findViewById(R.id.prop_desc);
            LinearLayout cardClick = (LinearLayout) itemView.findViewById(R.id.clicklayout);
            cardClick.setOnClickListener(this);
        }

        // make each card item clickable and launch edit
        @Override
        public void onClick(View v) {
            showEdit(titleText.getText().toString(), descriptionText.getText().toString());
        }
    }

    private class BuildPropRecyclerAdapter extends RecyclerView.Adapter<BuildPropItem> {
        private ArrayList<Map<String, String>> proplist;

        public BuildPropRecyclerAdapter(List<Map<String, String>> data) {
            proplist = new ArrayList<Map<String, String>>(data);
        }

        @Override
        public BuildPropItem onCreateViewHolder(ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.propeditlistitem, viewGroup, false);

            return new BuildPropItem(itemView);
        }

        @Override
        public void onBindViewHolder(BuildPropItem buildPropItem, int i) {
            buildPropItem.titleText.setText(proplist.get(i).get("title"));
            buildPropItem.descriptionText.setText(proplist.get(i).get("description"));
        }

        @Override
        public int getItemCount() {
            return proplist.size();
        }

        //queues up the removal, addition, and rearranging based on search query
        public void animateTo(ArrayList<Map<String, String>> models) {
            applyAndAnimateRemovals(models);
            applyAndAnimateAdditions(models);
            applyAndAnimateMovedItems(models);
        }

        //remove items from main list that arent in filtered list - start from bottom to not mess w/ index
        private void applyAndAnimateRemovals(ArrayList<Map<String, String>> newModels) {
            for (int i = proplist.size() - 1; i >= 0; i--) {
                Map<String, String> model = proplist.get(i);
                if (!newModels.contains(model)) {
                    removeItem(i);
                }
            }
        }

        //add items from filtered list that arent in main (edited-by-search) list - start from top
        private void applyAndAnimateAdditions(ArrayList<Map<String, String>> newModels) {
            for (int i = 0, count = newModels.size(); i < count; i++) {
                Map<String, String> model = newModels.get(i);
                if (!proplist.contains(model)) {
                    addItem(i, model);
                }
            }
        }

        //iterate through filter List from bottom and check index of each item to main list - move items in main list to match filter if needed.
        private void applyAndAnimateMovedItems(ArrayList<Map<String, String>> newModels) {
            for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
                Map<String, String> model = newModels.get(toPosition);
                int fromPosition = proplist.indexOf(model);
                if (fromPosition >= 0 && fromPosition != toPosition) {
                    moveItem(fromPosition, toPosition);
                }
            }
        }

        // remove item from main list
        public Map<String, String> removeItem(int position) {
            Map<String, String> model = proplist.remove(position);
            notifyItemRemoved(position);
            return model;
        }

        // add item to main list
        public void addItem(int position, Map<String, String> model) {
            proplist.add(position, model);
            notifyItemInserted(position);
        }

        // move item in main list to match filter list
        public void moveItem(int fromPosition, int toPosition) {
            Map<String, String> model = proplist.remove(fromPosition);
            proplist.add(toPosition, model);
            notifyItemMoved(fromPosition, toPosition);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        return (View) inflater.inflate(R.layout.propeditmain, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCoordLayout = (CoordinatorLayout) view.findViewById(R.id.buildpropcoord);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerList);

        // Attempt to flag instances where writing to build.prop
        // is not possible due to no space
        String filepath = "/system";
        File file = new File(filepath);

        long freeSpace = file.getFreeSpace();
        //long useSpace = file.getUsableSpace();

        if (file.getFreeSpace() == 0) {
            mHasRoom = false;
            Snackbar.make(mCoordLayout, getString(R.string.no_room_error), Snackbar.LENGTH_SHORT).show();
        } else {
            mHasRoom = true;
        }

        (new LoadProp()).setInits(getActivity(), mCoordLayout, recyclerView, false).execute();

        FloatingActionButton fabAdd = (FloatingActionButton) view.findViewById(R.id.fab);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mHasRoom && VRToxinActivity.isRoot) {
                    ((VRToxinActivity) getActivity()).displayEditProp(null, null);
                } else {
                    Snackbar.make(mCoordLayout, (mHasRoom ? "" : getString(R.string.no_room_error)) + (!mHasRoom && !VRToxinActivity.isRoot ? "\n" : "") + (VRToxinActivity.isRoot ? "" : getString(R.string.no_root_error)), Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // grab the searchview actionview stuff, add query listener
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextChange(String query) {
        // pass filtered list to method to create filtered list
        ArrayList<Map<String, String>> filteredModelList = filter(mProplist, query);
        // pass filtered list to adapter method for animating add/removal
        mAdapter.animateTo(filteredModelList);
        // go back to beginning of list
        recyclerView.scrollToPosition(0);
        return true;
    }

    private ArrayList<Map<String, String>> filter(ArrayList<Map<String, String>> models, String query) {
        // force lowercase for ease
        query = query.toLowerCase();

        // create filtered list from main list based on query
        ArrayList<Map<String, String>> filteredModelList = new ArrayList<Map<String, String>>();
        for (int i = 0; i < models.size(); i++) {
            Map<String, String> propEntry = models.get(i);
            String text = models.get(i).get("title").toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(propEntry);
            }
        }
        return filteredModelList;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_restore:
                if (mHasRoom && VRToxinActivity.isRoot) {
                    (new LoadProp()).setInits(getActivity(), mCoordLayout, recyclerView, true).execute();
                } else {
                    Snackbar.make(mCoordLayout, (mHasRoom ? "" : getString(R.string.no_room_error)) + (!mHasRoom && !VRToxinActivity.isRoot ? "\n" : "") + (VRToxinActivity.isRoot ? "" : getString(R.string.no_root_error)), Snackbar.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_backup:
                // Might as well allow backups even if no room or no root
                backup();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public BuildPropFragment() {}

    public void setAdapterItems(BuildPropRecyclerAdapter adapter, ArrayList<Map<String, String>> proplist) {
        // need these for search functions
        mAdapter = adapter;
        mProplist = proplist;
    }

    public ArrayList<Map<String, String>> buildData(String[] t, List<String> d) {
        ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();

        for (int i = 0; i < t.length; ++i) {
            if (t[i] != null) {
                list.add(putData(t[i], d.get(i)));
            }
        }

        return list;
    }

    public HashMap<String, String> putData(String title, String description) {
        HashMap<String, String> item = new HashMap<String, String>();

        item.put("title", title);
        item.put("description", description);

        return item;
    }

    public void restorefile() {
        String filepath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/build.prop.bak";
        File file = new File(filepath);
        if (file.exists()) {
            try {
                Shell.SU.run("mount -o remount,rw  /system");
                Shell.SU.run("mv -f /system/build.prop " + filepath + ".tmp");
                Shell.SU.run("mv -f " + filepath +" /system/build.prop");
                Shell.SU.run("chmod 644 /system/build.prop");
                Shell.SU.run("mount -o remount,ro  /system");
            } catch (Exception e) {
                Snackbar.make(mCoordLayout, getString(R.string.general_error), Snackbar.LENGTH_SHORT).show();
            }
        } else {
            Snackbar.make(mCoordLayout, getString(R.string.no_backup_error), Snackbar.LENGTH_SHORT).show();
        }
    }

    public void backup() {
        String filepath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/build.prop.bak";
        try {
            // Should let backup be created even if root denied
            Shell.SH.run("cp -f /system/build.prop " + filepath);
            Snackbar.make(mCoordLayout, String.format(getString(R.string.backup_loc), filepath), Snackbar.LENGTH_LONG).show();
        } catch (Exception e) {
            Snackbar.make(mCoordLayout, getString(R.string.general_error), Snackbar.LENGTH_SHORT).show();
        }
    }

    public void showEdit(String name, String key) {
        if (mHasRoom && VRToxinActivity.isRoot) {
            ((VRToxinActivity)getActivity()).displayEditProp(name, key);
        } else {
                    Snackbar.make(mCoordLayout, (mHasRoom ? "" : getString(R.string.no_room_error)) + (!mHasRoom && !VRToxinActivity.isRoot ? "\n" : "") + (VRToxinActivity.isRoot ? "" : getString(R.string.no_root_error)), Snackbar.LENGTH_SHORT).show();
        }
    }

    public String createTempFile() {
        try {
            // Seemed better to opt for SH here in case root denied for whatever reason
            Shell.SH.run("cp -f /system/build.prop " + Environment.getExternalStorageDirectory().getAbsolutePath() + "/buildprop.tmp");
            // The below doesn't really seem necessary...
            //Shell.SU.run("chmod 777 " + Environment.getExternalStorageDirectory().getAbsolutePath() + "/buildprop.tmp");
            return (String) Environment.getExternalStorageDirectory().getAbsolutePath() + "/buildprop.tmp";
        } catch (Exception e) {
            return (String) "error";
        }
    }
}

