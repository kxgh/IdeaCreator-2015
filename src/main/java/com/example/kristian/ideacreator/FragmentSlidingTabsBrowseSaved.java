/*
 * Copyright (C) 2013 The Android Open Source Project
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

package com.example.kristian.ideacreator;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.kristian.ideacreator.DbTablesInterfaces.SavedIdeas;
import com.example.kristian.ideacreator.view.SlidingTabLayout;

import java.util.LinkedList;

public class FragmentSlidingTabsBrowseSaved extends Fragment {

    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        return inflater.inflate(R.layout.fragment_sample, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mViewPager.setAdapter(new SamplePagerAdapter());
        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);

    }


    class SamplePagerAdapter extends PagerAdapter implements LoaderManager.LoaderCallbacks<Cursor> {
        private ListView listViewP;
        private ListView listViewH;
        private ListView listViewT;
        private ListView listViewA;

        private SimpleCursorAdapter listViewP_adapter;
        private SimpleCursorAdapter listViewH_adapter;
        private SimpleCursorAdapter listViewT_adapter;
        private SimpleCursorAdapter listViewA_adapter;

        private View[] views = new View[4];



        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return o == view;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0)
                return "Project";
            if (position == 1)
                return "Hobby";
            if (position == 2)
                return "Travel";
            if (position == 3)
                return "All";
            return "Item " + (position + 1);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            // Inflate a new layout from our resources
            View view = null;
            BinderSavedIdeasListView customBinder = new BinderSavedIdeasListView();
            SavedIdeasListViewOnItemLongClickListener onItemLongClickListener = new SavedIdeasListViewOnItemLongClickListener(this);
            String[] from = {SavedIdeas.Idea.IDEA,SavedIdeas.Idea.OBSCURITY,SavedIdeas.Idea.CATEGORY,SavedIdeas.Idea.DATETIME, SavedIdeas.Idea.ISCOMPLETED};

            if (position == 0) {
                view = getActivity().getLayoutInflater().inflate(R.layout.pager_item, container, false);
                views[position]=view;
                listViewP = (ListView) view.findViewById(R.id.listViewSavedIdeasP);
                listViewP.setOnItemLongClickListener(onItemLongClickListener);
                int[] to = {R.id.savedIdeaText,R.id.savedIdeaObscurity,R.id.savedIdeaCategoryIcon,R.id.savedIdeaCompletedText,R.id.savedIdeaCompletedIcon};
                listViewP_adapter = new SimpleCursorAdapter(view.getContext(), R.layout.savedidea, null, from, to, 0);
                listViewP_adapter.setViewBinder(customBinder);
                listViewP.setAdapter(listViewP_adapter);
                getLoaderManager().initLoader(position, Bundle.EMPTY, this);
            } else if (position == 1) {
                view = getActivity().getLayoutInflater().inflate(R.layout.pager_item2, container, false);
                views[position]=view;
                listViewH = (ListView) view.findViewById(R.id.listViewSavedIdeasH);
                listViewH.setOnItemLongClickListener(onItemLongClickListener);
                int[] to = {R.id.savedIdeaText2,R.id.savedIdeaObscurity2,R.id.savedIdeaCategoryIcon2,R.id.savedIdeaCompletedText2,R.id.savedIdeaCompletedIcon2};
                listViewH_adapter = new SimpleCursorAdapter(view.getContext(), R.layout.savedidea2, null, from, to, 0);
                listViewH_adapter.setViewBinder(customBinder);
                listViewH.setAdapter(listViewH_adapter);
                getLoaderManager().initLoader(position, Bundle.EMPTY, this);
            } else if (position == 2) {
                view = getActivity().getLayoutInflater().inflate(R.layout.pager_item3, container, false);
                views[position]=view;
                listViewT = (ListView) view.findViewById(R.id.listViewSavedIdeasT);
                listViewT.setOnItemLongClickListener(onItemLongClickListener);
                int[] to = {R.id.savedIdeaText3,R.id.savedIdeaObscurity3,R.id.savedIdeaCategoryIcon3,R.id.savedIdeaCompletedText3,R.id.savedIdeaCompletedIcon3};
                listViewT_adapter = new SimpleCursorAdapter(view.getContext(), R.layout.savedidea3, null, from, to, 0);
                listViewT_adapter.setViewBinder(customBinder);
                listViewT.setAdapter(listViewT_adapter);
                getLoaderManager().initLoader(position, Bundle.EMPTY, this);
            } else if (position == 3){
                view = getActivity().getLayoutInflater().inflate(R.layout.pager_item4, container, false);
                views[position]=view;
                listViewA = (ListView) view.findViewById(R.id.listViewSavedIdeasA);
                listViewA.setOnItemLongClickListener(onItemLongClickListener);
                int[] to = {R.id.savedIdeaText4,R.id.savedIdeaObscurity4,R.id.savedIdeaCategoryIcon4,R.id.savedIdeaCompletedText4,R.id.savedIdeaCompletedIcon4};
                listViewA_adapter = new SimpleCursorAdapter(view.getContext(), R.layout.savedidea4, null, from, to, 0);
                listViewA_adapter.setViewBinder(customBinder);
                listViewA.setAdapter(listViewA_adapter);
                getLoaderManager().initLoader(position, Bundle.EMPTY, this);
            }

            if (view == null) {
                Log.e(Shared.LogDebug, "instantiateItem is null !!!");

                        view = getActivity().getLayoutInflater().inflate(R.layout.pager_item2, container, false);
                container.addView(view);
                return view;
            }

            //Log.i(Shared.LogDebug, "Instatiate item " + position);
            // Add the newly created View to the ViewPager
            container.addView(view);

            /*// Retrieve a TextView from the inflated View, and update it's text
            TextView title = (TextView) view.findViewById(R.id.item_title);
            title.setText(String.valueOf(position + 1)+" a count je "+count);*/
            return view;
        }

        @Override
        public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
            Log.i(Shared.LogDb, "Creating context in loader on create...");
            Context con = getContext();
            if(con==null)
                Log.wtf(Shared.LogDb, "CONTEXT IS NULL !!!!!!!");
            Log.i(Shared.LogDb, "Done creating context");
            CursorLoader cursorLoader = new CursorLoader(con);
            Log.i(Shared.LogDb, "Loader oncreate executed for id "+id);
            if(id==0){
                cursorLoader.setSelection("category = ?");
                cursorLoader.setSelectionArgs(new String[]{Category.PROJECT.toString()});
            }
            if(id==1){
                cursorLoader.setSelection("category = ?");
                cursorLoader.setSelectionArgs(new String[]{Category.HOBBY.toString()});
            }
            if(id==2){
                cursorLoader.setSelection("category = ?");
                cursorLoader.setSelectionArgs(new String[]{Category.TRAVEL.toString()});
            }
            //Uri uri = Shared.getTableUriWithParams(SavedIdeas.Idea.TABLE_NAME, keysList,valsList);
            Uri uri = SavedIdeas.Idea.CONTENT_URI;
            cursorLoader.setUri(uri);

            Log.i(Shared.LogDb, "Done oncreate in browse, returning");
            return cursorLoader;
        }

        @Override
        public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
            Log.i(Shared.LogDebug, "Swapping data in browse ideas...");
            if(loader.getId()==0 && listViewP_adapter!=null) {
                listViewP_adapter.swapCursor(data);
            }
            if(loader.getId()==1 && listViewH_adapter!=null) {

                listViewH_adapter.swapCursor(data);
            }
            if(loader.getId()==2 && listViewT_adapter!=null) {

                listViewT_adapter.swapCursor(data);
            }
            if(loader.getId()==3 && listViewA_adapter!=null) {

                listViewA_adapter.swapCursor(data);
            }
            Log.i(Shared.LogDebug, "Done swapping data in browse ideas...");
        }

        @Override
        public void notifyDataSetChanged() {
            Log.i(Shared.LogDebug, "Restarting loaders in browse ideas...");
            getLoaderManager().restartLoader(0,null,this);
            getLoaderManager().restartLoader(1,null,this);
            getLoaderManager().restartLoader(2,null,this);
            getLoaderManager().restartLoader(3,null,this);
            Log.i(Shared.LogDebug, "Done restarting loaders in browse ideas...");
        }

        @Override
        public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
            Log.i(Shared.LogDebug, "Resetting loader in browse ideas...");
            loader.reset();
            Log.i(Shared.LogDebug, "Done resetting loader.");
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            Log.i(Shared.LogDebug, "Destroying view in browse ...");
            ((ViewPager) container).removeView((View) object);
            Log.i(Shared.LogDebug, "Destroyed");
        }




     /*   @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            CursorLoader cursorLoader = new CursorLoader(views[id].getContext());
            if(id==0)
                cursorLoader.setUri(ProjectIdeas.Idea.CONTENT_URI);
            else if(id==1)
                cursorLoader.setUri(HobbyIdeas.Idea.CONTENT_URI);
            else if(id==2)
                cursorLoader.setUri(TravelIdeas.Idea.CONTENT_URI);
            return cursorLoader;
        }*/


    }
}
