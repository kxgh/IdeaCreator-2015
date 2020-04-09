package com.example.kristian.ideacreator;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;

public class ActivitySettings extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager()
                .beginTransaction()
                .add(android.R.id.content, new FragmentSettings())
                .commit();
    }

}
