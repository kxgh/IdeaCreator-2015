package com.example.kristian.ideacreator;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.widget.GridView;
import android.support.v7.app.AppCompatActivity;

import com.example.kristian.ideacreator.DbTablesInterfaces.PacksInfo;

import java.util.LinkedList;

public class ActivityPacks extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>  {


    private GridView gridPacksView;
    private SimpleCursorAdapter gridViewPacksAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_packs);

        BinderPacksGridView binder = new BinderPacksGridView();

        String[] from = { PacksInfo.Pack.PACKNAME, PacksInfo.Pack.ISINSTALLED};
        gridPacksView = (GridView) findViewById(R.id.packsGridView);

        int[] to = { R.id.packsGridPackName, R.id.packsGridPackInstalled  };//R.id.savedIdeaTextMain,R.id.savedIdeaObscurityMain,R.id.savedIdeaCategoryIconMain,R.id.savedIdeaCompletedTextMain,R.id.savedIdeaCompletedIconMain};
        gridViewPacksAdapter = new SimpleCursorAdapter(this, R.layout.packs_grid, null, from, to, 0);
        PacksGridOnItemLongClickListener listener = new PacksGridOnItemLongClickListener();
        gridPacksView.setOnItemLongClickListener(listener);
        gridViewPacksAdapter.setViewBinder(binder);
        gridPacksView.setAdapter(gridViewPacksAdapter);
        getLoaderManager().initLoader(Shared.LOADER_ID, Bundle.EMPTY,this );
        getLoaderManager().initLoader(Shared.LOADER_CHECKER, Bundle.EMPTY,this );
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader a = new CursorLoader(this);
        Uri uri = PacksInfo.Pack.CONTENT_URI;
        if(id == Shared.LOADER_CHECKER){

            LinkedList<String> keys = new LinkedList<>();
            LinkedList<String> vals = new LinkedList<>();
            keys.add("check");
            vals.add("true");
            uri = Shared.getTableUriWithParams(PacksInfo.Pack.TABLE_NAME,keys,vals);
        }
        a.setUri(uri);
        a.setSortOrder(PacksInfo.Pack.PACKID);
        Log.i(Shared.LogDb, "Done oncreate in packs, returning");
        return a;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data!=null) {
            Log.i(Shared.LogDebug, "Swapping data in packs screen...");
            gridViewPacksAdapter.swapCursor(data);
            Log.i(Shared.LogDebug, "Done swapping data in packs screen...");
        }

    }

    @Override
    public void onContentChanged() {
        Log.i(Shared.LogDebug, "On content change called in packs screen...");
        getLoaderManager().restartLoader(Shared.LOADER_ID,null,this);
        getLoaderManager().restartLoader(Shared.LOADER_CHECKER,null,this);
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        getLoaderManager().restartLoader(Shared.LOADER_ID,null,this);
        getLoaderManager().restartLoader(Shared.LOADER_CHECKER,null,this);
    }
}
