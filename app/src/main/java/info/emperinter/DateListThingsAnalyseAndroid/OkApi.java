package info.emperinter.DateListThingsAnalyseAndroid;
import java.io.IOException;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkApi extends AppCompatActivity {

    final OkHttpClient client = new OkHttpClient();
    public String getData = "";

    public void getData(String url){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Request request = new Request.Builder()
                        .url(url)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    getData = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        TagCloudFragment tagFragment = new TagCloudFragment().newInstance(getData); //传参
        //把AFragment添加到Activity中，记得调用commit;"a"是在按返回键上一个状态保持原样！方便后续可以找到！
        getFragmentManager().beginTransaction().add(R.id.fl_container,tagFragment,"tag").commitAllowingStateLoss();
    }
}
