package info.emperinter.DateListThingsAnalyseAndroid;


import android.annotation.SuppressLint;
import android.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
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
import info.emperinter.DateListThingsAnalyseAndroid.API.HttpResponseCallBack;
import info.emperinter.DateListThingsAnalyseAndroid.API.Singleton;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TagCloudFragment extends Fragment {

    private TextView mTvTitle;
    private Button Mline,Madd;
    private LineAnalyseFragment lineAnalyseFragment;
    private DataFragment dataFragment;
    private SQLiteDatabase db;
    private DbHelper myDb;
    private String username = "";
    private String host = "";
    private String reqGet = "NO";
    private String url;

    // keyword的HashMap
    HashMap<String, Integer> KeyMap = new HashMap<String, Integer>();
    private int user_id;
    private String key = "";
    private List<DataEntry> data = new ArrayList<>();

    //传参
    public static  TagCloudFragment newInstance(String title){
        TagCloudFragment fragment = new TagCloudFragment();
        Bundle bundle = new Bundle();
        bundle.putString("getdata",title);
        fragment.setArguments(bundle);
        return fragment;
    }


    @SuppressLint("LongLogTag")
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


        //json获取
        url = host+"/api/thing/query/?format=json&userid="+user_id;

        try {
            Singleton.getInstance().doGetRequest(url, new HttpResponseCallBack() {
                @Override
                public void getResponse(String response) throws JSONException {
                    reqGet = response;
                    if(reqGet.contains("things_id")){
                        JSONArray userJson = new JSONArray(reqGet);
                        for (int i = 0;i < userJson.length();i++){
                            key = userJson.getJSONObject(i).getString("key");
                            if(KeyMap.containsKey(key)){
                                KeyMap.put(key,KeyMap.get(key) + 1);
                            }else if(!key.contains("nan") && key != ""){
                                KeyMap.put(key,1);
                            }
                        }

                        TagChart(view,KeyMap);

                    }else if(reqGet.contains("[]")){
                        Toast.makeText(getActivity().getBaseContext(),"username or password is wrong !",Toast.LENGTH_SHORT).show();
                    }else if(reqGet.contains("HTTP")){
                        Toast.makeText(getActivity().getBaseContext()," Cleartext HTTP traffic to not permitted",Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(getActivity().getBaseContext(),"Please Input Your Information !"+reqGet,Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }



        return  view;
    }

    public void TagChart(View view,HashMap<String,Integer> KeyMap){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 图标基本设置
                AnyChartView anyChartView = view.findViewById(R.id.any_chart_view);
                anyChartView.setProgressBar(view.findViewById(R.id.progress_bar));
                TagCloud tagCloud = AnyChart.tagCloud();
                tagCloud.title(username + "TagCloud");
                OrdinalColor ordinalColor = OrdinalColor.instantiate();
                ordinalColor.colors(new String[] {
                        "#26959f", "#f18126", "#3b8ad8", "#60727b", "#e24b26","#FF03DAC5"
                });
                tagCloud.colorScale(ordinalColor);
                tagCloud.angles(new Double[] {-90d, 0d, 90d});

                tagCloud.colorRange().enabled(true);
                tagCloud.colorRange().colorLineSize(15d);

                for (String i : KeyMap.keySet()) {
                    data.add(new CategoryValueDataEntry(i,String.valueOf(KeyMap.get(i)),KeyMap.get(i)));
                }
                tagCloud.data(data);
                anyChartView.setChart(tagCloud);
            }
        });
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTvTitle = (TextView) getActivity().findViewById(R.id.tv_title);

        Mline = (Button) getActivity().findViewById(R.id.btn_lineanalyse);
        Madd = (Button) getActivity().findViewById(R.id.add);

        Mline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lineAnalyseFragment == null){
                    lineAnalyseFragment = new LineAnalyseFragment();
                }
                Fragment tagFragment = getFragmentManager().findFragmentByTag("tag");
                if(tagFragment != null && !tagFragment.isAdded()){
                    getFragmentManager().beginTransaction().hide(tagFragment).add(R.id.fl_container, lineAnalyseFragment).addToBackStack(null).commitAllowingStateLoss();
                    mTvTitle.setText("LineAnalyse");
                }else {
                    getFragmentManager().beginTransaction().replace(R.id.fl_container, lineAnalyseFragment).addToBackStack(null).commitAllowingStateLoss();
                    mTvTitle.setText("LineAnalyse");
                }
            }
        });

        Madd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dataFragment == null){
                    dataFragment = new DataFragment();
                }
                Fragment fragment = getFragmentManager().findFragmentByTag("b");
                if(fragment != null && !fragment.isAdded()){
                    getFragmentManager().beginTransaction().hide(fragment).add(R.id.fl_container, dataFragment).addToBackStack(null).commitAllowingStateLoss();
                    mTvTitle.setText("Data");
                }else {
                    getFragmentManager().beginTransaction().replace(R.id.fl_container, dataFragment).addToBackStack(null).commitAllowingStateLoss();
                    mTvTitle.setText("Data");
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
