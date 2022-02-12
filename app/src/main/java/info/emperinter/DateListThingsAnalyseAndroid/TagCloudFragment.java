package info.emperinter.DateListThingsAnalyseAndroid;


import android.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.CategoryValueDataEntry;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.charts.TagCloud;
import com.anychart.scales.OrdinalColor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TagCloudFragment extends Fragment {

    private TextView mTvTitle;
    private Button Mret;
    private LineAnalyseFragment lineAnalyseFragment;
    private SQLiteDatabase db;
    private DbHelper myDb;
    private int user_id;
    private String username = "";
    private String host = "";
    private String reqGet = "NO";
    private Api api = null;
    private String url;
    OkHttpClient client = new OkHttpClient();


    //传参
    public static  TagCloudFragment newInstance(String title){
        TagCloudFragment fragment = new TagCloudFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title",title);
        fragment.setArguments(bundle);
        return fragment;
    }

    String getResponse(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tagcloud_fragment,container,false);  //设置布局文件

        //user_id 获取
        myDb = new DbHelper(getContext().getApplicationContext(),"user.db", null, 1);
        db = myDb.getWritableDatabase();
        Cursor cursor = db.query("user", null, null, null, null, null, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user_id = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"));;
            username = cursor.getString(cursor.getColumnIndexOrThrow("user_name"));
            host = cursor.getString(cursor.getColumnIndexOrThrow("host"));
        }
        db.close();

//                    strUrl = "https://plan.emperinter.ga/api/thing/query/?format=json&userid=10";
        //json获取
        url = host+"/api/thing/query/?format=json&userid="+user_id;
        Log.v("getStream-url",url);



//        try {
//            getResponse(url);
//            Log.v("ok-http-getStream",reqGet);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        api = new Api();
        reqGet = api.request(url);



        Log.v("getStream-req-get-cloud",reqGet);

        if(reqGet.contains("things_id")){
            try {
                JSONArray userJson = new JSONArray(reqGet);
                Log.v("getStream-userjson", String.valueOf(userJson.length()));
//                userid = (int) userJson.getJSONObject(0).get("user_id");
//                Log.v("reqGet-getStream-userid",String.valueOf(userid));
            } catch (JSONException e) {
//                Toast.makeText(this,e.toString(),Toast.LENGTH_SHORT).show();
                Toast.makeText(getActivity().getBaseContext(),e.toString(),Toast.LENGTH_SHORT).show();
            }
        }else if(reqGet.contains("[]")){
            Toast.makeText(getActivity().getBaseContext(),"username or password is wrong !",Toast.LENGTH_SHORT).show();
        }else if(reqGet.contains("HTTP")){
            Toast.makeText(getActivity().getBaseContext()," Cleartext HTTP traffic to not permitted",Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(getActivity().getBaseContext(),"Please Input Your Information !"+reqGet,Toast.LENGTH_SHORT).show();
        }


        AnyChartView anyChartView = view.findViewById(R.id.any_chart_view);
        anyChartView.setProgressBar(view.findViewById(R.id.progress_bar));

        TagCloud tagCloud = AnyChart.tagCloud();

        tagCloud.title(username + "TagCloud");
        OrdinalColor ordinalColor = OrdinalColor.instantiate();
        ordinalColor.colors(new String[] {
                "#26959f", "#f18126", "#3b8ad8", "#60727b", "#e24b26"
        });
        tagCloud.colorScale(ordinalColor);
        tagCloud.angles(new Double[] {-90d, 0d, 90d});

        tagCloud.colorRange().enabled(true);
        tagCloud.colorRange().colorLineSize(15d);

//      对于我这个项目来说，category最好就是不同的数量值
        List<DataEntry> data = new ArrayList<>();
        data.add(new CategoryValueDataEntry("China", "asia", 1383220000));
        data.add(new CategoryValueDataEntry("India", "asia", 1316000000));
        data.add(new CategoryValueDataEntry("United States", "america", 324982000));
        data.add(new CategoryValueDataEntry("Indonesia", "asia", 263510000));
        data.add(new CategoryValueDataEntry("Brazil", "america", 207505000));
        data.add(new CategoryValueDataEntry("Pakistan", "asia", 196459000));
        data.add(new CategoryValueDataEntry("Nigeria", "africa", 191836000));
        data.add(new CategoryValueDataEntry("Bangladesh", "asia", 162459000));
        data.add(new CategoryValueDataEntry("Russia", "europe", 146804372));
        data.add(new CategoryValueDataEntry("Japan", "asia", 126790000));
        data.add(new CategoryValueDataEntry("Mexico", "america", 123518000));
        data.add(new CategoryValueDataEntry("Ethiopia", "africa", 104345000));
        data.add(new CategoryValueDataEntry("Philippines", "asia", 104037000));
        data.add(new CategoryValueDataEntry("Egypt", "africa", 93013300));

        tagCloud.data(data);

        anyChartView.setChart(tagCloud);

        return  view;
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTvTitle = (TextView) getActivity().findViewById(R.id.tv_title);

        Mret = (Button) getActivity().findViewById(R.id.btn_lineanalyse);

        Mret.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lineAnalyseFragment == null){
//                    api = new Api();
//                    reqGet = api.request(url);

                    Log.v("getStream-req-get-cloud",reqGet);
                    lineAnalyseFragment = new LineAnalyseFragment();
                }
                //按返回键上一个状态保持原样！Tag:"a"在ContainerActivity中设置;
                Fragment fragment = getFragmentManager().findFragmentByTag("b");
                if(fragment != null && !fragment.isAdded()){
                    getFragmentManager().beginTransaction().hide(fragment).add(R.id.fl_container, lineAnalyseFragment).addToBackStack(null).commitAllowingStateLoss();
                    mTvTitle.setText("LineAnalyse");
                }else {
                    getFragmentManager().beginTransaction().replace(R.id.fl_container, lineAnalyseFragment).addToBackStack(null).commitAllowingStateLoss();
                    mTvTitle.setText("LineAnalyse");
                }

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_BACK){
                    Toast.makeText(getActivity(), "exit!", Toast.LENGTH_SHORT).show();
                    getActivity().moveTaskToBack(true);
                    getActivity().finish();
                    System.exit(0);
                    return true;
                }
                return false;
            }
        });
    }
}
