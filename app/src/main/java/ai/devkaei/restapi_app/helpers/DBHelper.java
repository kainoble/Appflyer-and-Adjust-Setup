package ai.devkaei.restapi_app.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import ai.devkaei.restapi_app.AppConfig;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "mydatabase.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "database";
    private static final String COLUMN_ID = "column_id";
    private static final String COLUMN_VERSION = "column_version";
    private static final String COLUMN_LINK = "column_link";
    private static final String COLUMN_POLICY = "column_policy";
    private static final String COLUMN_STATUS = "column_status";
    private static final String COLUMN_COUNTRY = "column_country";

    public DBHelper(Context mContext) {
        super(mContext, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_COUNTRY + " TEXT DEFAULT '', " +
                COLUMN_VERSION + " INTEGER DEFAULT 0, " +
                COLUMN_LINK + " TEXT DEFAULT '', " +
                COLUMN_POLICY + " TEXT DEFAULT '', " +
                COLUMN_STATUS + " INTEGER DEFAULT 0)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public int getDBVersion() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_VERSION + " FROM " + TABLE_NAME, null);
        int version = 0;
        if (cursor.moveToFirst()) {
            version = cursor.getInt(0);
        }
        cursor.close();
        return version;
    }

    public Cursor getDBContent() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }

    public void updateLocalDB(int column_version, String column_country, String column_link, String column_policylink,  int column_status) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues dbValues = new ContentValues();
            dbValues.put(COLUMN_VERSION, column_version);
            dbValues.put(COLUMN_COUNTRY, column_country);
            dbValues.put(COLUMN_LINK, column_link);
            dbValues.put(COLUMN_POLICY, column_policylink);
            dbValues.put(COLUMN_STATUS, column_status);
            db.update(TABLE_NAME, dbValues, null, null);
            Log.d(AppConfig.LOG_TAG, "Local DB Updated");
        } catch (Exception e) {
            Log.e(AppConfig.LOG_TAG, "Error updating local DB", e);
        }
    }
}
