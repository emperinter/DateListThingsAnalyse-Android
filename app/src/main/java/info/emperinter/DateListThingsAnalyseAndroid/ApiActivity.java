package info.emperinter.DateListThingsAnalyseAndroid;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ApiActivity {
    private String get_data = "";

    //    HTTP响应流转换成JSON
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private JSONArray streamToJson(InputStream inputStream) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        String temp;
        StringBuilder stringBuilder = new StringBuilder();
        while ((temp = bufferedReader.readLine()) != null) {
            stringBuilder.append(temp);
        }
        return new JSONArray(stringBuilder.toString().trim());
    }
    
    //从服务器获取数据（json）
    public String request(final String strUrl) {
        //新的线程
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                try {
//                    strUrl = "https://plan.emperinter.ga/api/user/query/?format=json&user_name=emperinter&user_passwd=emperinter";
//                    strUrl = "https://plan.emperinter.ga/api/thing/query/?format=json&userid=10";
                    Log.v("HTTP-getStream",strUrl);
                    URL url = new URL(strUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET"); // 设置请求方式为 GET
                    connection.connect(); // 连接
                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) { // 请求成功
                        InputStream inputStream = connection.getInputStream(); // 得到响应流
                        Log.v("inputStream-getStream:" , inputStream.toString());
                        JSONArray json = null;
                        json = streamToJson(inputStream);

//                        System.out.printf("\n####################################################");
//                        System.out.printf("getStream-things_id:\t"+json.getJSONObject(0).get("things_id") + "\t" + String.valueOf(json.length()) );
//                        System.out.printf("\n####################################################");
                        get_data = json.toString();
                    } else {
                        get_data = "NotFound!";
                    }
                    connection.disconnect();
                } catch (Exception e) {
                    Log.v("Exception-getStream",e.toString());
                    get_data = e.toString();
                }
            }
        }).start();
        return get_data;
    }
}
