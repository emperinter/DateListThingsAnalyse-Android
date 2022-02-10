package info.emperinter.DateListThingsAnalyseAndroid;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private Button LoginBtn = null;
    private TextView user,passwd,host;
    private String get_data = "";

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
            Toast.makeText(this, user.getText()+"\n"+passwd.getText()+"\n"+host.getText(), Toast.LENGTH_SHORT).show();
            if(request(host.toString(),1,"2") != ""){
                Intent changeToMain = new Intent(this,MainActivity.class);
                startActivity(changeToMain);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private JSONObject streamToJson(InputStream inputStream) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        String temp;
        StringBuilder stringBuilder = new StringBuilder();
        while ((temp = bufferedReader.readLine()) != null) {
            stringBuilder.append(temp);
        }
        return new JSONObject(stringBuilder.toString().trim());
    }

    //从服务器获取数据（json）
    public String request(final String host,final int m, final String get) { //m = 0 时为默认的更新，m = 1时调用的为查询语句
        //新的线程
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                String strUrl = "";
                try {
                    if (m == 1) {
                        strUrl = "https://" + host + get ;
                    } else if (m == 2) {
                        strUrl = "https://" + host + get ;
                    }
                    URL url = new URL(strUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET"); // 设置请求方式为 GET
                    connection.connect(); // 连接
                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) { // 请求成功
                        InputStream inputStream = connection.getInputStream(); // 得到响应流
                        JSONObject json = null; // 从响应流中提取 JSON
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                            json = streamToJson(inputStream);
                        }
                        get_data = json.getString("things");
                    } else {
                        get_data = "NotFound!";
                    }
                    connection.disconnect();
                } catch (Exception e) {
                    get_data = "<div align ='center'>Connect Error!</div>";
                }
            }
        }).start();
        return get_data;
    }
}

