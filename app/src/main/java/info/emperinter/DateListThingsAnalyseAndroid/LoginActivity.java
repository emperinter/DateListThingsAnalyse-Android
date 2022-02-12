package info.emperinter.DateListThingsAnalyseAndroid;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private Button LoginBtn = null;
    private TextView user,passwd,host;
    private String reqGet = "";
    private int userid;
    public Api api = new Api();
    private SQLiteDatabase db;
    private DbHelper dbHelper;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginactivity);
        Objects.requireNonNull(getSupportActionBar()).hide();// 隐藏ActionBar


        LoginBtn = findViewById(R.id.login_btn);
        user = findViewById(R.id.username);
        passwd = findViewById(R.id.password);
        host = findViewById(R.id.host);

        dbHelper = new DbHelper(getBaseContext(), "user.db", null, 1);
        db = dbHelper.getWritableDatabase();

        Cursor cursor = db.query("user", null, null, null, null, null, null);
//        cursor.moveToFirst();
        cursor.moveToLast();
        if (cursor.getCount() > 0) {
            int user_id = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"));
            Log.v("db-getStream-userid",String.valueOf(user_id));
            Intent changeToMain = new Intent(this, ContainerActivity.class);
            startActivity(changeToMain);
            LoginBtn.setEnabled(false);
        }

        LoginBtn.setOnClickListener(view -> {
//          strUrl = "https://plan.emperinter.ga/api/user/query/?format=json&user_name=emperinter&user_passwd=emperinter";
            if (host.getText() != "" && user.getText() != "" && passwd.getText() != ""){
                reqGet = api.request(host.getText()+"/api/user/query/?format=json&user_name="+user.getText()+"&user_passwd="+passwd.getText());
                Log.v("reqGet-getStream",reqGet);
                if(reqGet.contains("user_id")){
                    try {
                        JSONArray user = new JSONArray(reqGet);
                        userid = (int) user.getJSONObject(0).get("user_id");
                        Log.v("reqGet-getStream-userid",String.valueOf(userid));

                        ContentValues set_values = new ContentValues();
                        set_values.put("user_id", userid);
                        db.execSQL("DELETE  FROM user");
                        db.insert("user", null, set_values);

                        Intent changeToMain = new Intent(this, ContainerActivity.class);
                        startActivity(changeToMain);
                        db.close();

                    } catch (JSONException e) {
//                        e.printStackTrace();
                        Toast.makeText(this,e.toString(),Toast.LENGTH_SHORT).show();
                    }
                }else if(reqGet.contains("[]")){
                    Toast.makeText(this,"username or password is wrong !",Toast.LENGTH_SHORT).show();
                }else if(reqGet.contains("HTTP")){
                    Toast.makeText(this," Cleartext HTTP traffic to "+host.getText()+" not permitted",Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(this,"Please Input Your Information !",Toast.LENGTH_SHORT).show();
            }
        });
    }
}

