package tech.doujiang.launcher.model;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import tech.doujiang.launcher.database.MyDatabaseHelper;

public class MyContentProvider extends ContentProvider {

    private static UriMatcher uriMathcer;
    private MyDatabaseHelper dbHelper;

    public static final String AUTHORITY = "tech.doujiang.provider";

    static {
        uriMathcer = new UriMatcher(UriMatcher.NO_MATCH);
        uriMathcer.addURI(AUTHORITY, "KeyTable", 0);
    }

    public MyContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        dbHelper = new MyDatabaseHelper(getContext(), MyDatabaseHelper.DB_NAME, null, MyDatabaseHelper.DB_VERSION);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // TODO: Implement this to handle query requests from clients.
        Log.d("Call the provider: ", "query");
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Log.d("Query para",
                String.format("%s %s %s %s",
                        projection[0],selection, selectionArgs[0], sortOrder ));
        Cursor cursor = null;
        switch (uriMathcer.match(uri)){
            case 0:
                cursor = db.query("KeyTable", projection, selection, selectionArgs, null, null,
                        sortOrder);
                break;
            default:
                break;
        }
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
