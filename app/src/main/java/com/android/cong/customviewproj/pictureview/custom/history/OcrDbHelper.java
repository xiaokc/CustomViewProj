package com.android.cong.customviewproj.pictureview.custom.history;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.android.cong.customviewproj.BaseApplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by xiaokecong on 26/07/2017.
 */

public class OcrDbHelper extends SQLiteOpenHelper {
    private static final String TAG = "OcrDbHelper";
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "ocr.db";
    private static final String TABLE_NAME = "ocr_history";

    private static final String FIELD_ID = "_id";
    private static final String FIELD_FILE_PATH = "file_path";
    private static final String FIELD_LAST_MODIFIED = "last_modified";

    private static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                    + FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + FIELD_FILE_PATH + " TEXT,"
                    + FIELD_LAST_MODIFIED + " INTEGER)";

    private SQLiteDatabase mOcrDb;
    private AtomicInteger mOpenCounter = new AtomicInteger();

    private static OcrDbHelper mInstance;

    private OcrDbHelper(Context context, String name,
                        SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    private OcrDbHelper(Context context) {
        this(context, DB_NAME, null, DB_VERSION);
    }

    private OcrDbHelper() {
        this(BaseApplication.getInstance());
    }

    public static OcrDbHelper getInstance() {
        if (null == mInstance) {
            synchronized (OcrDbHelper.class) {
                if (null == mInstance) {
                    mInstance = new OcrDbHelper();
                }
            }
        }
        return mInstance;
    }

    public synchronized SQLiteDatabase open() {
        if (mOpenCounter.incrementAndGet() == 1) {
            mOcrDb = mInstance.getWritableDatabase();
        }

        return mOcrDb;
    }

    public synchronized void close() {
        if (mOpenCounter.decrementAndGet() == 0) {
            mOcrDb.close();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_TABLE);
        } catch (Exception e) {
            Log.e(TAG, "ocr 数据库创建失败");
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        } catch (Exception e) {
            Log.e(TAG, "ocr onUpgrade failed");
            e.printStackTrace();
        }
    }

    /**
     * 插入一条数据
     *
     * @param item
     */
    public void insertOneItem(OcrHistoryItem item) {
        if (null == item) {
            return;
        }

        try {
            mOcrDb.beginTransaction();
            ContentValues cv = new ContentValues();
            cv.put(FIELD_FILE_PATH, item.getPath());
            cv.put(FIELD_LAST_MODIFIED, item.getLastModifiedTime());
            mOcrDb.insert(TABLE_NAME, null, cv);

            mOcrDb.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "insert one item error");
            e.printStackTrace();
        } finally {
            if (mOcrDb != null) {
                if (mOcrDb.inTransaction()) {
                    mOcrDb.endTransaction();
                }
            }
        }

    }

    /**
     * 删除某个路径的数据
     *
     * @param path
     */
    public boolean deleteItemWithPath(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        try {
            String[] args = {path};
            mOcrDb.delete(TABLE_NAME, FIELD_FILE_PATH + "=?", args);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "delete item with path error,path:" + path);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取当前页的所有item
     *
     * @param currentPage 当前页
     * @param pageSize    每页的记录数
     *
     * @return
     */
    public List<OcrHistoryItem> getOnePageItems(int currentPage, int pageSize) {
        List<OcrHistoryItem> dataList = new ArrayList<>();
        int limit = pageSize;
        int offset = (currentPage - 1) * pageSize;
        if (offset < 0) {
            offset = 0;
        }
        String sql = "select * from " + TABLE_NAME + " limit ? offset ?";

        Cursor cursor = mOcrDb.rawQuery(sql, new String[] {String.valueOf(limit), String.valueOf(offset)});

        try {
            int count = cursor.getCount();
            if (count > 0) {
                while (cursor.moveToNext()) {
                    OcrHistoryItem item = new OcrHistoryItem();
                    String path = cursor.getString(cursor.getColumnIndexOrThrow(FIELD_FILE_PATH));
                    item.setPath(path);
                    dataList.add(item);
                }

            }
        } catch (Exception e) {
            Log.e(TAG, "get one page items error");
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return dataList;

    }

    /**
     * 获取全部记录
     * @return
     */
    public List<OcrHistoryItem> getAllItems() {
        List<OcrHistoryItem> dataList = new ArrayList<>();
        String sql = "select * from " + TABLE_NAME;

        Cursor cursor = mOcrDb.rawQuery(sql, null);

        try {
            int count = cursor.getCount();
            if (count > 0) {
                while (cursor.moveToNext()) {
                    OcrHistoryItem item = new OcrHistoryItem();
                    String path = cursor.getString(cursor.getColumnIndexOrThrow(FIELD_FILE_PATH));
                    item.setPath(path);
                    dataList.add(item);
                }

            }
        } catch (Exception e) {
            Log.e(TAG, "get all items error");
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return dataList;
    }

}
