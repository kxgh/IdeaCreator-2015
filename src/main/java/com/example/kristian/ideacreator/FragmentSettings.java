package com.example.kristian.ideacreator;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;

/**
 * Created by Kristian on 27/05/2016.
 */
public class FragmentSettings extends PreferenceFragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        Preference pref;

        pref = findPreference(getResources().getString(R.string.stgAddIdea));
        pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivityAddidea();
                return true;
            }
        });

        pref = findPreference(getResources().getString(R.string.stgPacks));
        pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivityPacks();
                return true;
            }
        });

        pref = findPreference(getResources().getString(R.string.stgResetTables));
        pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                resetTables();
                return true;
            }
        });

        pref = findPreference(getResources().getString(R.string.stgName));
        pref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                preference.setSummary(newValue.toString());
                return true;
            }
        });
    }

    private void startActivityAddidea() {
        Intent i = new Intent(getActivity(),ActivityAddIdea.class);
        startActivity(i);
    }
    private void startActivityPacks() {
        Intent i = new Intent(getActivity(),ActivityPacks.class);
        startActivity(i);
    }

    private void resetTables() {
        new AlertDialog.Builder(getActivity())
                .setTitle("Database reset")
                .setMessage("You are about to reset idea database to default. This will remove all saved and added ideas. Continue ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        performReset();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    private void performReset(){
        if(Shared.checkIfUpdating()==false){
            AsyncTaskRollbackAndInsertFromFile
           // async = new AsyncTaskRollback(getActivity().getApplicationContext());
            async2 = new AsyncTaskRollbackAndInsertFromFile(getActivity().getApplicationContext());
            async2.execute();
            Toast.makeText(getActivity(), "Starting rollback...", Toast.LENGTH_SHORT).show();
            return;
            //async.execute();
        }

        Toast.makeText(getActivity(), "Unable to perform rollback at the time", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        Preference preference = findPreference(getResources().getString(R.string.stgName));
        preference.setSummary(preferences.getString(getResources().getString(R.string.stgName),getResources().getString(R.string.stgNameDfl)));

    }
}
