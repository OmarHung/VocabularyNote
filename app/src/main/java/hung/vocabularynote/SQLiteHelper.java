package hung.vocabularynote;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Hung on 2016/11/1.
 */

public class SQLiteHelper extends SQLiteOpenHelper {
    private SQLiteDatabase db;
    private Context context;
    //static final String File_Name_Of_Prefrences = "VocabularyNotePrefrences";
    //static final String PREFS_BACKUP_KEY = "backup";
    private static SQLiteDatabase database;
    public static final int VERSION = 1;
    public static final String FILE_DIR = "/hung.vocabularynote.database";
    public static final String DATABASE_NAME = "vocabulary.db";
    public static final String TABLE_NAME = "vocabulary";
    public static final String KEY_ID = "_id";
    public static final String ENGLISH_COLUMN = "english";
    public static final String CHINESE_COLUMN = "chinese";
    public static final String EXAMPLE_COLUMN = "example";
    public static final String STAR_COLUMN = "star";
    public final static String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME
            + " (" + KEY_ID + " INTEGER PRIMARY KEY,"
            + ENGLISH_COLUMN + " TEXT,"
            + CHINESE_COLUMN + " TEXT,"
            + EXAMPLE_COLUMN + " TEXT,"
            + STAR_COLUMN + " TEXT)";

    public SQLiteHelper(Context context) {
        super(context, Environment.getExternalStorageDirectory() + FILE_DIR + File.separator + DATABASE_NAME, null, VERSION);
        this.context=context;
        db = getDatabase(context);
    }
    public SQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);

    }
    // 需要資料庫的元件呼叫這個方法，這個方法在一般的應用都不需要修改
    public static SQLiteDatabase getDatabase(Context context) {
        if (database == null || !database.isOpen()) {
            database = new SQLiteHelper(context, Environment.getExternalStorageDirectory() + FILE_DIR + File.separator + DATABASE_NAME, null, VERSION).getWritableDatabase();
        }
        return database;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 刪除原有的表格
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        // 呼叫onCreate建立新版的表格
        onCreate(db);
    }

    // 關閉資料庫，一般的應用都不需要修改
    public void close() {
        db.close();
    }
    public void update_insert(long _id, String enlish, String chinese, String example, String star) {
        if(!update(_id,enlish,chinese,example,star)) insert(enlish,chinese,example,star);
    }
    // 新增參數指定的物件
    public long insert(String enlish, String chinese, String example, String star) {
        // 建立準備新增資料的ContentValues物件
        ContentValues cv = new ContentValues();
        cv.put(ENGLISH_COLUMN, enlish);
        cv.put(CHINESE_COLUMN, chinese);
        cv.put(EXAMPLE_COLUMN, example);
        cv.put(STAR_COLUMN, star);
        long id = db.insert(TABLE_NAME, null, cv);
        return id;
    }

    // 修改參數指定的物件
    public boolean update(long _id, String enlish, String chinese, String example, String star) {
        // 建立準備修改資料的ContentValues物件
        ContentValues cv = new ContentValues();
        cv.put(ENGLISH_COLUMN, enlish);
        cv.put(CHINESE_COLUMN, chinese);
        cv.put(EXAMPLE_COLUMN, example);
        cv.put(STAR_COLUMN, star);
        String where = KEY_ID + "=" + _id;
        // 執行修改資料並回傳修改的資料數量是否成功
        return db.update(TABLE_NAME, cv, where, null) > 0;
    }
    public boolean updateStar(long _id, String star) {
        // 建立準備修改資料的ContentValues物件
        ContentValues cv = new ContentValues();
        cv.put(STAR_COLUMN, star);
        String where = KEY_ID + "=" + _id;
        // 執行修改資料並回傳修改的資料數量是否成功
        return db.update(TABLE_NAME, cv, where, null) > 0;
    }

    // 刪除參數指定編號的資料
    public boolean delete(long _id) {
        // 設定條件為編號，格式為「欄位名稱=資料」
        String where = KEY_ID + "=" + _id;
        // 刪除指定編號資料並回傳刪除是否成功
        return db.delete(TABLE_NAME, where , null) > 0;
    }
    public List<Map<String, Object>> getData() {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        Cursor cursor = db.query(
                TABLE_NAME, null, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            result.add(getRecord(cursor));
        }

        cursor.close();
        return result;
    }
    public List<Map<String, Object>> getMarkData() {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        Cursor cursor = db.query(
                TABLE_NAME, null, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            if(cursor.getString(4).equals("1"))
                result.add(getRecord(cursor));
        }
        cursor.close();
        return result;
    }
    public List<Map<String, Object>> getSingleData(String[] _id) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        Cursor cursor = db.query(TABLE_NAME, null, "_id=?", _id, null, null, null, null);
        while (cursor.moveToNext()) {
            result.add(getRecord(cursor));
        }

        cursor.close();
        return result;
    }
    // 把Cursor目前的資料包裝為物件
    public Map<String, Object> getRecord(Cursor cursor) {
        // 準備回傳結果用的物件
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("_id",cursor.getString(0));
        result.put("English", cursor.getString(1));
        result.put("Chinese", cursor.getString(2));
        result.put("Example",cursor.getString(3));
        result.put("Star",cursor.getString(4));
        // 回傳結果
        return result;
    }
    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
    /*
    public void saveToSharedPreferences(List<Map<String, Object>> mData) {
        try {
            synchronized (MainActivity.sDataLock) {
                SharedPreferences sharedPreferences = context.getSharedPreferences(File_Name_Of_Prefrences,0);
                for(int i=0;i<mData.size();i++) {
                    String _id = mData.get(i).get("_id").toString();
                    String english = mData.get(i).get("English").toString();
                    String chinese = mData.get(i).get("Chinese").toString();
                    String example = mData.get(i).get("Example").toString();
                    String star = mData.get(i).get("Star").toString();
                    String item = _id+"|"+english+"|"+chinese+"|"+example+"|"+star;
                    sharedPreferences.edit().putString(_id,item).commit();
                }
            }
        } catch (Exception e) {
            Log.e("SQLiteHelper", "Unable to write to file");
        }
    }*/
}
