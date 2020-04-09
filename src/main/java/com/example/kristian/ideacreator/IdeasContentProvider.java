package com.example.kristian.ideacreator;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.kristian.ideacreator.DbTablesInterfaces.HobbyIdeas;
import com.example.kristian.ideacreator.DbTablesInterfaces.IdeasPrototype;
import com.example.kristian.ideacreator.DbTablesInterfaces.PacksInfo;
import com.example.kristian.ideacreator.DbTablesInterfaces.ProjectIdeas;
import com.example.kristian.ideacreator.DbTablesInterfaces.SavedIdeas;
import com.example.kristian.ideacreator.DbTablesInterfaces.TravelIdeas;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;
import java.util.Scanner;

public class IdeasContentProvider extends ContentProvider {


    private IdeasDatabaseOpenHelper dbHelper;

    public IdeasContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String tableName = null;
        if (uri.getPath().compareTo(SavedIdeas.Idea.CONTENT_URI.getPath()) == 0)
            tableName = SavedIdeas.Idea.TABLE_NAME;
        if (uri.getPath().compareTo(ProjectIdeas.Idea.CONTENT_URI.getPath()) == 0)
            tableName = ProjectIdeas.Idea.TABLE_NAME;
        if (uri.getPath().compareTo(HobbyIdeas.Idea.CONTENT_URI.getPath()) == 0)
            tableName = HobbyIdeas.Idea.TABLE_NAME;
        if (uri.getPath().compareTo(TravelIdeas.Idea.CONTENT_URI.getPath()) == 0)
            tableName = TravelIdeas.Idea.TABLE_NAME;
        if (uri.getPath().compareTo(PacksInfo.Pack.CONTENT_URI.getPath()) == 0)
            tableName = PacksInfo.Pack.TABLE_NAME;

        if (tableName == null) {
            Log.wtf(Shared.LogDb, "Unknown table name in delete in content provider");
            return -1;
        }
        return db.delete(tableName, selection, selectionArgs);
    }

    @Override
    public String getType(Uri uri) { // toto sa uz nepouziva
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        //String path = uri.getPath().substring(1,uri.getPath().length());
        SQLiteDatabase db = dbHelper.getReadableDatabase();


        if (uri.compareTo(HobbyIdeas.Idea.CONTENT_URI) == 0) {
            db.insert(HobbyIdeas.Idea.TABLE_NAME, null, values);
        } else if (uri.compareTo(ProjectIdeas.Idea.CONTENT_URI) == 0) {
            db.insert(ProjectIdeas.Idea.TABLE_NAME, null, values);
        } else if (uri.compareTo(TravelIdeas.Idea.CONTENT_URI) == 0) {
            db.insert(TravelIdeas.Idea.TABLE_NAME, null, values);
        } else {
            if (uri.compareTo(SavedIdeas.Idea.CONTENT_URI) == 0) {
                db.insert(SavedIdeas.Idea.TABLE_NAME, null, values);
            } else {

                if (uri.compareTo(PacksInfo.Pack.CONTENT_URI) == 0) {
                    db.insert(PacksInfo.Pack.TABLE_NAME, null, values);
                } else {
                    Log.e(Shared.LogDb, "Content provider error inserting: no such uri. The uri is: " + uri.toString());
                    Log.e(Shared.LogDb, "But required for example: " + HobbyIdeas.Idea.CONTENT_URI.toString());
                    return null;
                }
            }
        }
        return uri;
    }

    @Override
    public boolean onCreate() { // tu treba co najmenej veci, co najrychlejsie to musi zbehnut
        Log.i(Shared.LogDb, "IDeasContent provider oncreate.");
        if (getContext() != null) {
            this.dbHelper = dbHelperFactory.INSTANCE.getDbHelperInstance(getContext());
            return true;
        } else Log.wtf(Shared.LogDb, "IDeasContent provider NULL CONTEXT.");
        return false;
    }

    private int updateFromNet(String tableName, Uri uri) throws Exception {
        URL url = new URL(Shared.getPacksServerUrl());
        URLConnection urlConnection = url.openConnection();
        HttpURLConnection http = (HttpURLConnection) urlConnection;
        InputStream inputStream = http.getInputStream();
        Scanner sc = new Scanner(inputStream);
        try{
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            Scanner s = new Scanner(line);
            try {
                s.useDelimiter(";");
                String packId = s.next();
                String packName = s.next();
                String packUrl = s.next();
                ContentValues cv = new ContentValues();
                cv.put(PacksInfo.Pack.PACKID, packId);
                cv.put(PacksInfo.Pack.PACKNAME, packName);
                cv.put(PacksInfo.Pack.PACKURL, packUrl);
                int updated = update(uri, cv, PacksInfo.Pack.PACKID + " = ?", new String[]{packId});
                if (updated == 0) {
                    cv.put(PacksInfo.Pack.ISINSTALLED, Shared.Pack.PACK_NOT_INSTALLED);
                    insert(uri, cv);
                }
            }catch(Exception e){
                Log.e(Shared.LogDb, "Conent provider updateFromNet exception1: "+e.getMessage());
                s.close();
                break;
            }

        }}catch(Exception e){
            Log.e(Shared.LogDb, "Conent provider updateFromNet exception1: "+e.getMessage());
        }

        sc.close();
        inputStream.close();
        return 0;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Log.i(Shared.LogDb, "Performing query:" + uri.toString());
        SQLiteDatabase db = dbHelper.getReadableDatabase(); // toto nedavat do instancnej
        String tableName = "";
        String uriPath = uri.getPath();
        if (HobbyIdeas.Idea.CONTENT_URI.getPath().compareTo(uriPath) == 0) {
            tableName = HobbyIdeas.Idea.TABLE_NAME;
        } else if (ProjectIdeas.Idea.CONTENT_URI.getPath().compareTo(uriPath) == 0) {
            tableName = ProjectIdeas.Idea.TABLE_NAME;
        } else if (TravelIdeas.Idea.CONTENT_URI.getPath().compareTo(uriPath) == 0) {
            tableName = TravelIdeas.Idea.TABLE_NAME;
        } else if (SavedIdeas.Idea.CONTENT_URI.getPath().compareTo(uriPath) == 0) {

            Cursor query;
            tableName = SavedIdeas.Idea.TABLE_NAME;
            String selectedCategory = uri.getQueryParameter("category");

            String groupBy = null;
            String having = null;
            String limit = null;
            query = db.query(tableName, projection, selection, selectionArgs, groupBy, having, sortOrder, limit);
            return query;
        } else {
            if (PacksInfo.Pack.CONTENT_URI.getPath().compareTo(uriPath) == 0) {

                // check if there are packs with flag 'currently installing' that are not really currently installing
                // if so, set flag to 'not installed', and remove all ideas with those ids
                String check = uri.getQueryParameter("check");
                if (check != null && check.equals("true")) {

                    try {
                        Cursor tempCurs = query(PacksInfo.Pack.CONTENT_URI, new String[]{PacksInfo.Pack._ID}, String.format("%s = ?", PacksInfo.Pack.ISINSTALLED), new String[]{Shared.Pack.PACK_CURRENTLY_INSTALLING + ""}, null);
                        if (tempCurs != null) {
                            for (tempCurs.moveToFirst(); !tempCurs.isAfterLast(); tempCurs.moveToNext()) {
                                long idOfInvalidInstallingState = tempCurs.getLong(tempCurs.getColumnIndex(PacksInfo.Pack._ID));
                                if (!Shared.idsOfPacksCurrentlyInstalling.contains(idOfInvalidInstallingState)) {
                                    Bundle b = new Bundle();
                                    b.putLong("id", idOfInvalidInstallingState);
                                    call("setPackMode", Shared.Pack.PACK_NOT_INSTALLED + "", b);
                                    delete(ProjectIdeas.Idea.CONTENT_URI,ProjectIdeas.Idea.PACKID+"=?",new String[]{idOfInvalidInstallingState+""});
                                    delete(TravelIdeas.Idea.CONTENT_URI,TravelIdeas.Idea.PACKID+"=?",new String[]{idOfInvalidInstallingState+""});
                                    delete(HobbyIdeas.Idea.CONTENT_URI,HobbyIdeas.Idea.PACKID+"=?",new String[]{idOfInvalidInstallingState+""});
                                }
                            }
                            tempCurs.close();
                        }
                    } catch (Exception e) {
                        Log.e(Shared.LogDebug, "Unexpected exception at content provider Pack Loader checker query: " + e.getMessage());
                    }
                    return null;
                }

                tableName = PacksInfo.Pack.TABLE_NAME;
                if (selection == null) {
                    try {
                        updateFromNet(tableName, uri);
                    } catch (Exception e) {
                        Log.e(Shared.LogDb, "Error in content provider while querying for Packs: " + e.getMessage());
                    }
                } else {
                    Log.e(Shared.LogDb, "Content provider: NOT UPDATING. Selection is null ");
                }
                Cursor curs = db.query(tableName, projection, selection, selectionArgs, null, null, sortOrder, null);
                curs.setNotificationUri(getContext().getContentResolver(), PacksInfo.Pack.CONTENT_URI);
                return curs;
            } else {

                Log.e(Shared.LogDb, "Content provider error inserting: no such uri. The uri is: " + uri.toString());
                Log.e(Shared.LogDb, "But required for example: " + HobbyIdeas.Idea.CONTENT_URI.toString());
            }
        }
        //return db.query(tableName,projection,selection,selectionArgs,null,null,sortOrder,limit);
        return db.query(tableName, null, null, null, null, null, null);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String tableName = null;
        if (uri.getPath().compareTo(SavedIdeas.Idea.CONTENT_URI.getPath()) == 0)
            tableName = SavedIdeas.Idea.TABLE_NAME;
        if (uri.getPath().compareTo(ProjectIdeas.Idea.CONTENT_URI.getPath()) == 0)
            tableName = ProjectIdeas.Idea.TABLE_NAME;
        if (uri.getPath().compareTo(HobbyIdeas.Idea.CONTENT_URI.getPath()) == 0)
            tableName = HobbyIdeas.Idea.TABLE_NAME;
        if (uri.getPath().compareTo(TravelIdeas.Idea.CONTENT_URI.getPath()) == 0)
            tableName = TravelIdeas.Idea.TABLE_NAME;
        if (uri.getPath().compareTo(PacksInfo.Pack.CONTENT_URI.getPath()) == 0)
            tableName = PacksInfo.Pack.TABLE_NAME;

        if (tableName == null) {
            Log.wtf(Shared.LogDb, "Unknown table name in delete in content provider");
            return -1;
        }
        return db.update(tableName, values, selection, selectionArgs);
    }

    @Nullable
    @Override
    public Bundle call(@NonNull String method, String arg, Bundle extras) {
        if (method.equals("generateIdea")) {
            return generateIdea(method, arg, extras);
        }
        if (method.equals("saveIdea")) {
            return saveIdea(method, arg, extras);
        }
        if (method.equals("markRealized")) {
            return markRealized(method, arg, extras);
        }
        if (method.equals("removeSaved")) {
            return removeSaved(method, arg, extras);
        }
        if (method.equals("downloadPack")) {
            return downloadPack(method, arg, extras);
        }
        if (method.equals("setPackMode")) { // set pack is installed? mode in arg!
            return setPackMode(method, arg, extras);
        }
        /*
        * Content provider onCreate hack, DONT REMOVE
         */
        if (method.equals("mock")) {
            return mock(method, arg, extras);
        }

        return null;
    }

    private Bundle setPackMode(String method, String arg, Bundle extras) {
        long id = extras.getLong("id");
        int mode = 0;
        try {
            mode = Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            Log.e(Shared.LogDb, "Content provider setpack mode received unparseable arg:" + arg);
            return null;
        }
        ContentValues cv = new ContentValues();
        cv.put(PacksInfo.Pack.ISINSTALLED, mode);
        update(PacksInfo.Pack.CONTENT_URI, cv, "_id=?", new String[]{"" + id});
        getContext().getContentResolver().notifyChange(PacksInfo.Pack.CONTENT_URI, null);
        return new Bundle();
    }

    private Bundle downloadPack(String method, String arg, Bundle extras) {
        if (!Shared.isInternetAvailable())
            return null;
        long id = extras.getLong("id");
        Log.i(Shared.LogDb, "Content provider downloadPack got id: " + id);
        Cursor query = query(PacksInfo.Pack.CONTENT_URI, new String[]{PacksInfo.Pack.PACKURL}, "_id=?", new String[]{"" + id}, null);
        if (query == null)
            return null;
        query.moveToFirst();
        Log.i(Shared.LogDb, "Content provider downloadPack column id: " + query.getColumnIndex(PacksInfo.Pack.PACKURL) + " Column count: " + query.getColumnCount());
        String url = query.getString(query.getColumnIndex(PacksInfo.Pack.PACKURL));
        query.close();
        Bundle result = new Bundle();
        int numberInserted = 0;
        try {
            numberInserted = parseAndDownloadPack(url,id);
        } catch (Exception e) {
            numberInserted = -1;
            Log.e(Shared.LogDb, "Error while reading ideas from " + url + ". Exception message: " + e.getMessage());
        }
        if (numberInserted > 0)
            result.putBoolean("result", true);
        else
            result.putBoolean("result", false);
        result.putInt("count", numberInserted);
        return result;
    }

    private int parseAndDownloadPack(String url, long packId) throws Exception {
        int count = 0;

        if (url != null) {

            URL ad = new URL(url);
            URLConnection urlConnection = ad.openConnection();
            HttpURLConnection http = (HttpURLConnection) urlConnection;
            http.setReadTimeout(6000);
            InputStream inputStream = http.getInputStream();
            Scanner scanner = new Scanner(inputStream);
            while (scanner.hasNextLine()) {
                ContentValues contentValues = new ContentValues();
                String line = scanner.nextLine();
                Scanner sc = new Scanner(line);
                //Log.i(Shared.LogDb,line);
                sc.useDelimiter(Shared.fileDelimiter);
                String ctg = sc.next();
                byte obsc = sc.nextByte();
                String ideaa = sc.next();
                if (ctg.equals(Shared.fileCtgHobby))
                    if (ideaa.charAt(ideaa.length() - 1) == 'g' && ideaa.charAt(ideaa.length() - 2) == 'n' && ideaa.charAt(ideaa.length() - 3) == 'i') {
                        ideaa = "try " + ideaa;
                    }
                if (ctg.equals(Shared.fileCtgProject)) {
                    contentValues.put(ProjectIdeas.Idea.IDEA, ideaa);
                    contentValues.put(ProjectIdeas.Idea.OBSCURITY, obsc);
                    contentValues.put(ProjectIdeas.Idea.PACKID,packId);
                    insert(ProjectIdeas.Idea.CONTENT_URI, contentValues);
                } else if (ctg.equals(Shared.fileCtgHobby)) {
                    contentValues.put(HobbyIdeas.Idea.IDEA, ideaa);
                    contentValues.put(HobbyIdeas.Idea.OBSCURITY, obsc);
                    contentValues.put(HobbyIdeas.Idea.PACKID,packId);
                    insert(HobbyIdeas.Idea.CONTENT_URI, contentValues);
                } else if (ctg.equals(Shared.fileCtgTravel)) {
                    contentValues.put(TravelIdeas.Idea.IDEA, ideaa);
                    contentValues.put(TravelIdeas.Idea.OBSCURITY, obsc);
                    contentValues.put(TravelIdeas.Idea.PACKID,packId);
                    insert(TravelIdeas.Idea.CONTENT_URI, contentValues);
                }
                count++;
                if (count % 100 == 0) {
                    Log.i(Shared.LogDb, line);
                }
                sc.close();
            }

            scanner.close();
            inputStream.close();
            return count;
        }
        return -1;
    }

    private Bundle mock(String method, String arg, Bundle extras) {
        SQLiteDatabase readableDatabase = dbHelper.getReadableDatabase();
        return new Bundle();
    }

    private Bundle removeSaved(String method, String arg, Bundle extras) {
        String tableName = SavedIdeas.Idea.TABLE_NAME;
        Long id = extras.getLong("id");
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SavedIdeas.Idea.ISCOMPLETED, 1);
        contentValues.put(SavedIdeas.Idea.DATETIME, System.currentTimeMillis());
        int delete = db.delete(tableName, "_id = ?", new String[]{id.toString()});

        Bundle result = new Bundle();
        result.putInt("removed", delete);
        return result;
    }

    private Bundle markRealized(String method, String arg, Bundle extras) {
        String tableName = SavedIdeas.Idea.TABLE_NAME;
        Long id = extras.getLong("id");
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SavedIdeas.Idea.ISCOMPLETED, 1);
        contentValues.put(SavedIdeas.Idea.DATETIME, System.currentTimeMillis());
        int update = db.update(tableName, contentValues, "_id = ?", new String[]{id.toString()});
        Bundle result = new Bundle();
        result.putInt("updated", update);
        return result;
    }

    private Bundle saveIdea(String method, String arg, Bundle extras) {
        /*SQLiteDatabase readableDatabase = dbHelper.getReadableDatabase();
        String tableName = SavedIdeas.Idea.TABLE_NAME;*/
        IdeaObject ideaToBeSaved = (IdeaObject) extras.getSerializable("idea");
        Log.i(Shared.LogDb, "idem ukladat ideau " + ideaToBeSaved.getText());
        ContentValues contentValues = new ContentValues();
        contentValues.put(SavedIdeas.Idea.IDEA, ideaToBeSaved.getText());
        contentValues.put(SavedIdeas.Idea.OBSCURITY, (int) ideaToBeSaved.getObscurity());
        contentValues.put(SavedIdeas.Idea.CATEGORY, ideaToBeSaved.getCtg().toString());
        contentValues.put(SavedIdeas.Idea.ISCOMPLETED, 0);
        contentValues.put(SavedIdeas.Idea.DATETIME, 0);
        Log.i(Shared.LogDb, "Ukladam " + contentValues.toString());
        Bundle result = new Bundle();
        if (insert(SavedIdeas.Idea.CONTENT_URI, contentValues).compareTo(SavedIdeas.Idea.CONTENT_URI) == 0)
            result.putBoolean("success", true);
        else
            result.putBoolean("success", false);
        return result;
    }

    private Bundle generateIdea(@NonNull String method, String arg, Bundle extras) {
        SQLiteDatabase readableDatabase = dbHelper.getReadableDatabase();
        String tableName = "error in ideas content provider. No fitting table name";
        Category ctg = Category.HOBBY;
        if (arg.equals(Category.HOBBY.toString())) {
            tableName = HobbyIdeas.Idea.TABLE_NAME;
            ctg = Category.HOBBY;
        }
        if (arg.equals(Category.PROJECT.toString())) {
            tableName = ProjectIdeas.Idea.TABLE_NAME;
            ctg = Category.PROJECT;
        }
        if (arg.equals(Category.TRAVEL.toString())) {
            tableName = TravelIdeas.Idea.TABLE_NAME;
            ctg = Category.TRAVEL;
        }
        String sql = "SELECT %s,%s,%s FROM %s WHERE %s >= %d AND %s <= %d ORDER BY RANDOM() LIMIT 1";
        Cursor cursor = readableDatabase.rawQuery(String.format(Locale.US, sql, IdeasPrototype._ID, IdeasPrototype.IDEA, IdeasPrototype.OBSCURITY, tableName, IdeasPrototype.OBSCURITY, (int) extras.getByte("obscMin"), IdeasPrototype.OBSCURITY, (int) extras.getByte("obscMax")), null);


        Bundle generated = new Bundle();
        if (cursor.getCount() == 0)
            return generated;
        IdeaObject ideaObject = new IdeaObject();
        ideaObject.setCtg(ctg);
        //generated.putString("category", ctg.toString());;
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndex(IdeasPrototype._ID));
            ideaObject.setId(id);
            //generated.putLong("id", id);
            String data = cursor.getString(cursor.getColumnIndex(IdeasPrototype.IDEA));
            //generated.putString("idea", ",," + Shared.getPrefix(ctg) + data + "\"");


            ideaObject.setText(Shared.getPrefix(ctg) + data + "\"");
            byte obsc = (byte) cursor.getInt(cursor.getColumnIndex(IdeasPrototype.OBSCURITY));
            //generated.putByte("obsc", obsc);
            ideaObject.setObscurity(obsc);
            break;
        }
        generated.putSerializable("idea", ideaObject);
        cursor.close();
        return generated;
    }
}
