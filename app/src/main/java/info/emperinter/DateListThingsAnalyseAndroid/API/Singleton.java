package info.emperinter.DateListThingsAnalyseAndroid.API;

import android.util.Log;
import okhttp3.*;

import java.io.IOException;

public class Singleton {
    private static Singleton INSTANCE = null;
    private Singleton() {};

    OkHttpClient client = new OkHttpClient();

    public static Singleton getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Singleton();
        }
        return(INSTANCE);
    }

    public void doPostRequest(String url,final HttpResponseCallBack responseCallBack) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String strResponse = response.body().string();
                Log.v("getStream-strResponse",strResponse);
                try {
                    responseCallBack.getResponse(strResponse);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}