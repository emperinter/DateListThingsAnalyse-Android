package info.emperinter.DateListThingsAnalyseAndroid;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
    public DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, "user.db", null, 1);
    }

    @Override
    //数据库第一次创建时被调用
    public void onCreate(SQLiteDatabase db) {
        String DB_CREATE_TABLE_NOTE = "CREATE TABLE user (user_id INTEGER,user_name VARCHAR(12),host VARCHAR(32));";
        db.execSQL(DB_CREATE_TABLE_NOTE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
//        sqLiteDatabase.execSQL("ALTER TABLE user ADD phone VARCHAR(12)");
    }
}
