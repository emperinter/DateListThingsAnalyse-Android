package info.emperinter.DateListThingsAnalyseAndroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private Button LoginBtn = null;
    private TextView user,passwd,host;
    private String reqGet = "";
    private int userid;
    public ApiActivity api = new ApiActivity();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginactivity);
        Objects.requireNonNull(getSupportActionBar()).hide();// 隐藏ActionBar


        LoginBtn = findViewById(R.id.login_btn);
        user = findViewById(R.id.username);
        passwd = findViewById(R.id.password);
        host = findViewById(R.id.host);

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
                        Intent changeToMain = new Intent(this, ContainerActivity.class);
                        startActivity(changeToMain);
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

