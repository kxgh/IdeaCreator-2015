package com.example.kristian.ideacreator;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;


public class ActivityBrowseIdeas extends FragmentActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_browse_ideas);
        if(getActionBar()!=null)
            getActionBar().hide();

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            FragmentSlidingTabsBrowseSaved fragment = new FragmentSlidingTabsBrowseSaved();
            transaction.replace(R.id.browse_content_fragment, fragment);
            transaction.commit();
        }
    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem logToggle = menu.findItem(R.id.menu_toggle_log);
//        logToggle.setVisible(findViewById(R.id.sample_output) instanceof ViewAnimator);
        //logToggle.setTitle(mLogShown ? R.string.sample_hide_log : R.string.sample_show_log);

        return super.onPrepareOptionsMenu(menu);
    }*/
}
