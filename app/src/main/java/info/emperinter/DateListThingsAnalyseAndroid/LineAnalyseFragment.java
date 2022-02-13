package info.emperinter.DateListThingsAnalyseAndroid;
import android.app.Fragment;
import android.content.Context;
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
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Line;
import com.anychart.data.Mapping;
import com.anychart.data.Set;
import com.anychart.enums.Anchor;
import com.anychart.enums.MarkerType;
import com.anychart.enums.TooltipPositionMode;
import com.anychart.graphics.vector.Stroke;
import info.emperinter.DateListThingsAnalyseAndroid.API.HttpResponseCallBack;
import info.emperinter.DateListThingsAnalyseAndroid.API.Singleton;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LineAnalyseFragment extends Fragment {
    private Button mBtnChange,Madd,Mline;
    private LineAnalyseFragment lineAnalyseFragment;
    private TextView mTvTitle;
    private TagCloudFragment tagCloudFragment;
    private  IOnMessageClick listener;//申明接口
    private SQLiteDatabase db;
    private DbHelper myDb;
    private int user_id;
    private String host = "";
    private String username;
    private String reqGet = "NO";
    private String url;
    private DataFragment dataFragment;




    private List<DataEntry> data = new ArrayList<>();
    //things_id,value
    private HashMap<Integer, String> dateMap = new HashMap<Integer, String>();
    private HashMap<Integer, Integer> processMap = new HashMap<Integer, Integer>();
    private HashMap<Integer, Integer> emotionMap = new HashMap<Integer, Integer>();
    private HashMap<Integer, Integer> energyMap = new HashMap<Integer, Integer>();

    private int things_id;
    private int process;
    private int emotion;
    private int energy;
    private String get_date;

    //传参
    public static LineAnalyseFragment newInstance(String title){
        LineAnalyseFragment fragment = new LineAnalyseFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title",title);
        fragment.setArguments(bundle);
        return fragment;
    }

    //声明接口，给Activity传参！在ContainerActivity实现该接口！
    public interface IOnMessageClick{
        void onClick(String text);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.lineanalyse_fragment,container,false);  //设置布局文件


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
                            things_id = userJson.getJSONObject(i).getInt("things_id");
                            get_date = userJson.getJSONObject(i).getString("date");
                            process = userJson.getJSONObject(i).getInt("process");
                            emotion = userJson.getJSONObject(i).getInt("emotion");
                            energy = userJson.getJSONObject(i).getInt("energy");

                            dateMap.put(things_id,get_date);
                            processMap.put(things_id,process);
                            emotionMap.put(things_id,emotion);
                            energyMap.put(things_id,energy);
                        }


                        // 弄3个HashMap依次对应相关数值

                        LineChart(view,dateMap,processMap,emotionMap,energyMap);

                    }else if(reqGet.contains("[]")){
                        Toast.makeText(getActivity().getBaseContext(),"username or password is wrong !",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getActivity().getBaseContext(),"SomeThing Wrong !",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return  view;
    }

    public void LineChart(View view, HashMap<Integer,String> dateMap,HashMap<Integer,Integer> processMap,HashMap<Integer,Integer> emotionMap,HashMap<Integer,Integer> energyMap){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AnyChartView anyChartView = view.findViewById(R.id.any_chart_view);
                anyChartView.setProgressBar(view.findViewById(R.id.progress_bar));

                Cartesian cartesian = AnyChart.line();

                cartesian.animation(true);

                cartesian.padding(10d, 20d, 5d, 20d);

                cartesian.crosshair().enabled(true);
                cartesian.crosshair()
                        .yLabel(true)
                        // TODO ystroke
                        .yStroke((Stroke) null, null, null, (String) null, (String) null);

                cartesian.tooltip().positionMode(TooltipPositionMode.POINT);

                cartesian.title("DateListThingsAnalyse ");

                cartesian.yAxis(0).title("Rank(10)");
                cartesian.xAxis(0).labels().padding(5d, 5d, 5d, 5d);

                List<DataEntry> seriesData = new ArrayList<>();

                for (Integer i : dateMap.keySet()) {
                    seriesData.add(new CustomDataEntry(dateMap.get(i), processMap.get(i), emotionMap.get(i), energyMap.get(i)));
                }


                Set set = Set.instantiate();
                set.data(seriesData);
                Mapping series1Mapping = set.mapAs("{ x: 'x', value: 'value' }");
                Mapping series2Mapping = set.mapAs("{ x: 'x', value: 'value2' }");
                Mapping series3Mapping = set.mapAs("{ x: 'x', value: 'value3' }");

                Line series1 = cartesian.line(series1Mapping);
                series1.name("Process");
                series1.hovered().markers().enabled(true);
                series1.hovered().markers()
                        .type(MarkerType.CIRCLE)
                        .size(4d);
                series1.tooltip()
                        .position("right")
                        .anchor(Anchor.LEFT_CENTER)
                        .offsetX(5d)
                        .offsetY(5d);

                Line series2 = cartesian.line(series2Mapping);
                series2.name("Emotion");
                series2.hovered().markers().enabled(true);
                series2.hovered().markers()
                        .type(MarkerType.CIRCLE)
                        .size(4d);
                series2.tooltip()
                        .position("right")
                        .anchor(Anchor.LEFT_CENTER)
                        .offsetX(5d)
                        .offsetY(5d);

                Line series3 = cartesian.line(series3Mapping);
                series3.name("Energy");
                series3.hovered().markers().enabled(true);
                series3.hovered().markers()
                        .type(MarkerType.CIRCLE)
                        .size(4d);
                series3.tooltip()
                        .position("right")
                        .anchor(Anchor.LEFT_CENTER)
                        .offsetX(5d)
                        .offsetY(5d);

                cartesian.legend().enabled(true);
                cartesian.legend().fontSize(13d);
                cartesian.legend().padding(0d, 0d, 10d, 0d);

                anyChartView.setChart(cartesian);
            }
        });
    }

    private class CustomDataEntry extends ValueDataEntry {

        CustomDataEntry(String x, Number value, Number value2, Number value3) {
            super(x, value);
            setValue("value2", value2);
            setValue("value3", value3);
        }

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTvTitle = (TextView) getActivity().findViewById(R.id.tv_title);
        mBtnChange = (Button) getActivity().findViewById(R.id.btn_tagcloud);
        Madd = (Button) getActivity().findViewById(R.id.add);

        Mline = (Button) getActivity().findViewById(R.id.btn_lineanalyse);

        Mline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lineAnalyseFragment == null){
                    lineAnalyseFragment = new LineAnalyseFragment();
                }
                Fragment fragment = getFragmentManager().findFragmentByTag("b");
                if(fragment != null && !fragment.isAdded()){
                    getFragmentManager().beginTransaction().hide(fragment).add(R.id.fl_container, lineAnalyseFragment,"l").addToBackStack(null).commitAllowingStateLoss();
                    mTvTitle.setText("LineAnalyse");
                }else {
                    getFragmentManager().beginTransaction().replace(R.id.fl_container, lineAnalyseFragment,"l").addToBackStack(null).commitAllowingStateLoss();
                    mTvTitle.setText("LineAnalyse");
                }
            }
        });


        mBtnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tagCloudFragment == null){
                    tagCloudFragment = new TagCloudFragment();
                }
                Fragment aFragment = getFragmentManager().findFragmentByTag("l");
                if(aFragment != null && !aFragment.isAdded()){
                    mTvTitle.setText("TagCloud");
                    getFragmentManager().beginTransaction().hide(aFragment).add(R.id.fl_container, tagCloudFragment,"b").addToBackStack(null).commitAllowingStateLoss();
                }else {
                    mTvTitle.setText("TagCloud");
                    getFragmentManager().beginTransaction().replace(R.id.fl_container, tagCloudFragment,"b").addToBackStack(null).commitAllowingStateLoss();
                }

            }
        });

        Madd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dataFragment == null){
                    dataFragment = new DataFragment();
                }
                //按返回键上一个状态保持原样！Tag:"a"在ContainerActivity中设置;
                Fragment fragment = getFragmentManager().findFragmentByTag("l");
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
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            listener = (IOnMessageClick) context;   //给Activity传参
        }catch (ClassCastException ex){
            throw  new ClassCastException("Activity 必须实现IOnMessageClick 接口!");
        }

    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //取消异步
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
