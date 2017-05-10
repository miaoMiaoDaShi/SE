package lq.xxp.se.Db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by 钟大爷 on 2016/11/6.
 */

public class DataBaseHelper extends SQLiteOpenHelper {
    private static int VERSION = 1;
    private static String DBNAME = "db_mml";
    public DataBaseHelper(Context context) {
        super(context,DBNAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table tb_collect(_id integer primary key,"+"title varchar(100)," +
                "type varchar(50),blockLink varchar(100),imgUrl varchar(100),num varchar(100)," +
                "time varchar(100))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
