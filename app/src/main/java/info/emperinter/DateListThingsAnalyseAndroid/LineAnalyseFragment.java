package info.emperinter.DateListThingsAnalyseAndroid;
import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LineAnalyseFragment extends Fragment{
    private Button Madd,Mline,Mtag;
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



    private ArrayList<String> dateList = new ArrayList<String>();
    private ArrayList<Integer> processList = new ArrayList<Integer>();
    private ArrayList<Integer> emotionList = new ArrayList<Integer>();
    private ArrayList<Integer> energyList = new ArrayList<Integer>();

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

                            dateList.add(get_date);
                            processList.add(process);
                            emotionList.add(emotion);
                            energyList.add(energy);
                        }


                        // 弄3个HashMap依次对应相关数值


                        LineChart(view,dateList,processList,emotionList,energyList);

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

    public void LineChart(View view, ArrayList<String> dateList,ArrayList<Integer> processList,ArrayList<Integer> emotionList,ArrayList<Integer> energyList){
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

                cartesian.title("DateListThingsAnalyse");

                cartesian.yAxis(0).title("Rank(10)");
                cartesian.xAxis(0).labels().padding(21d, 21d, 21d, 21d);

                List<DataEntry> seriesData = new ArrayList<>();

                for(int i = 0;i < dateList.size();i++){
                    seriesData.add(new CustomDataEntry(dateList.get(i), processList.get(i), emotionList.get(i), energyList.get(i)));
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
        Madd = (Button) getActivity().findViewById(R.id.add);

        Mline = (Button) getActivity().findViewById(R.id.btn_lineanalyse);
        Mtag = (Button) getActivity().findViewById(R.id.btn_tagcloud);
        Mtag.setEnabled(true);
        Mline.setEnabled(false);
        Madd.setEnabled(true);

        Mline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lineAnalyseFragment == null){
                    lineAnalyseFragment = new LineAnalyseFragment();
                }
//                Fragment addFragment = getFragmentManager().findFragmentByTag("add");
//                Fragment tagFragment = getFragmentManager().findFragmentByTag("tag");
//
//                if((addFragment != null && addFragment.isAdded()) | (tagFragment !=null && tagFragment.isAdded())){
//                    getFragmentManager().beginTransaction().hide(addFragment).hide(tagFragment).add(R.id.fl_container, lineAnalyseFragment,"line").addToBackStack(null).commitAllowingStateLoss();
//                }else {
                    getFragmentManager().beginTransaction().replace(R.id.fl_container, lineAnalyseFragment,"line").addToBackStack(null).commitAllowingStateLoss();
//                }
//                mTvTitle.setText("LineAnalyse");
            }
        });


        Mtag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tagCloudFragment == null){
                    tagCloudFragment = new TagCloudFragment();
                }
//                Fragment addFragment = getFragmentManager().findFragmentByTag("add");
//                Fragment lineFragment = getFragmentManager().findFragmentByTag("line");
//                if((addFragment != null && !addFragment.isAdded()) | (lineFragment !=null && !lineFragment.isAdded())){
                    mTvTitle.setText("TagCloud");
//                    getFragmentManager().beginTransaction().hide(lineFragment).hide(addFragment).add(R.id.fl_container, tagCloudFragment,"tag").addToBackStack(null).commitAllowingStateLoss();
//                }else {
//                    mTvTitle.setText("TagCloud");
                    getFragmentManager().beginTransaction().replace(R.id.fl_container, tagCloudFragment,"tag").addToBackStack(null).commitAllowingStateLoss();
//                }

            }
        });

        Madd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dataFragment == null){
                    dataFragment = new DataFragment();
                }
//                Fragment lineFragment = getFragmentManager().findFragmentByTag("line");
//                Fragment tagFragment = getFragmentManager().findFragmentByTag("tag");
//                if((lineFragment != null && !lineFragment.isAdded()) | (tagFragment != null && !tagFragment.isAdded())){
//                    getFragmentManager().beginTransaction().hide(lineFragment).hide(tagFragment).add(R.id.fl_container, dataFragment,"add").addToBackStack(null).commitAllowingStateLoss();
//                }else {
                    getFragmentManager().beginTransaction().replace(R.id.fl_container, dataFragment,"add").addToBackStack(null).commitAllowingStateLoss();
//                }
                mTvTitle.setText("Data");
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
