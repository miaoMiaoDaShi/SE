package lq.xxp.se.Db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;

import lq.xxp.se.Bean.DbBean;

/**
 * Created by 钟大爷 on 2016/9/28.
 */
public class DbInit {
    private DataBaseHelper db;
    private SQLiteDatabase sql;
    private List<DbBean> list;
    private static DbInit dataBaseinit = null;
    private ContentValues contentValues = new ContentValues();
    private Cursor cursor;

    private DbInit(Context context) {
        db = new DataBaseHelper(context);
        sql = db.getWritableDatabase();
    }



    public static DbInit DbInitInstance(Context context){
        if (dataBaseinit==null){
            return new DbInit(context);
        }
        return dataBaseinit;
    }

    public void addData(String tb_Name, ContentValues contentValues) {
        this.contentValues = contentValues;
        sql.insert(tb_Name, null, contentValues);
    }

    public void delData(String tb_Name, String where) {
        sql.delete(tb_Name, "_id=?", new String[]{where});
    }

    public void upData(String tb_Name, ContentValues contentValues, String possion) {
        sql.update(tb_Name, contentValues, "_id=?", new String[]{possion});
    }

    public List<DbBean> giveData(String tb_Name) {
        list = new ArrayList<DbBean>();

        cursor = sql.query(tb_Name,null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            while (!cursor.isAfterLast()) {
                DbBean dbBean = new DbBean(cursor.getString(1), cursor.getString(2), cursor.getString(3),
                        cursor.getString(4),cursor.getString(5),cursor.getString(6));
                list.add(dbBean);
                cursor.moveToNext();
            }
        }
        return list;
    }

    public SparseArray<Integer> getDbId(String tb_Name){
        SparseArray<Integer> sparseArray = new SparseArray<Integer>();
        cursor = sql.query(tb_Name,null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            int i = 0;
            while (!cursor.isAfterLast()){
                sparseArray.put(i,cursor.getInt(0));
                i++;
                cursor.moveToNext();
            }
        }
        Log.i("mmds","大学艾欧唯"+sparseArray.size());
        return sparseArray;
    }


}
