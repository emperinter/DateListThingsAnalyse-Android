package info.emperinter.DateListThingsAnalyseAndroid;

import android.os.Build;
import android.support.annotation.RequiresApi;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ApiActivity {
    private String get_data = "";

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
