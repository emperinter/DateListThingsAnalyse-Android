package info.emperinter.DateListThingsAnalyseAndroid;

import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import info.emperinter.DateListThingsAnalyseAndroid.Data.DbHelper;
import info.emperinter.DateListThingsAnalyseAndroid.Data.HttpResponseCallBack;
import info.emperinter.DateListThingsAnalyseAndroid.Data.Singleton;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;


public class LineChartFragment extends Fragment implements OnChartGestureListener, OnChartValueSelectedListener {


    private Button Madd,Mline,Mtag;
    private LineChartFragment lineAnalyseFragment;
    private TagCloudFragment tagCloudFragment;
    private SQLiteDatabase db;
    private DbHelper myDb;
    private int user_id;
    private String host = "";
    private String username;
    private String reqGet = "NO";
    private String url;
    private DataFragment dataFragment;
    private ProgressBar processBar;
    private TextView info;

    private LineChart chart;



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
    public static LineChartFragment newInstance(String title){
        LineChartFragment fragment = new LineChartFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title",title);
        fragment.setArguments(bundle);
        return fragment;
    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.linechart_fragment,container,false);  //设置布局文件


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
                @RequiresApi(api = Build.VERSION_CODES.Q)
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

                        LineChart(view,dateList,processList,emotionList,energyList);

                        // 弄3个HashMap依次对应相关数值

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


    //设置线的颜色
    private final int[] colors = new int[] {
            Color.rgb(252,3,3),
//            Color.rgb(252,244,3),
            Color.rgb(187,134,252),
            Color.rgb(36,252,3),
    };


    public void LineChart(View view, ArrayList<String> dateList, ArrayList<Integer> processList, ArrayList<Integer> emotionList, ArrayList<Integer> energyList){

                processBar = (ProgressBar) view.findViewById(R.id.progress_bar);
                info = (TextView) view.findViewById(R.id.info);
                chart = (LineChart) view.findViewById(R.id.chart1);
                chart.getDescription().setEnabled(false);
                chart.setNoDataText("Loading...");
                chart.invalidate();

                chart.setNoDataTextColor(Color.rgb(0,0,0));

                chart.setOnChartValueSelectedListener(this);

                //双击缩放禁止
                chart.setDoubleTapToZoomEnabled(true);

                //灰色背景
//                chart.setDrawGridBackground(true);
                //chart整个背景颜色
//                chart.setBackgroundColor(Color.rgb(0,0,0));

//                chart.setLeftTopRightBottom(100,100,100,100);

                chart.getDescription().setEnabled(false);
                chart.setDrawBorders(false);
                //左边y轴启用
                chart.getAxisLeft().setEnabled(true);
                chart.getAxisLeft().setDrawAxisLine(false);
                //右边y轴禁止
                chart.getAxisRight().setEnabled(false);
                chart.getAxisRight().setDrawAxisLine(false);

                //x 轴朝下
                chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

                // enable touch gestures
                chart.setTouchEnabled(true);

                // enable scaling and dragging
                chart.setDragEnabled(true);
                //禁止缩放
                chart.setScaleEnabled(false);


                // if disabled, scaling can be done on x- and y-axis separately
                // 手指只能分开控制x和y轴的操作
                chart.setPinchZoom(false);


                ArrayList<ILineDataSet> dataSets = new ArrayList<>();

                String[] tag = new  String[3];
                tag[0] = "process";
                tag[1] = "emotion";
                tag[2] = "energy";

                int thingsCount = dateList.size();

                ArrayList<String> xLable = new ArrayList<>();
                for (int m = 0;m < thingsCount;m++ ){
                    xLable.add(dateList.get(m));
                }
                XAxis xAxis = chart.getXAxis();

                xAxis.setValueFormatter(new IndexAxisValueFormatter(xLable));


                //这里的3代表了3条线
                for (int z = 0; z < 3; z++) {

                    ArrayList<Entry> values = new ArrayList<>();

                    if(z == 0){
                        for (int i = 0; i < thingsCount; i++) {
//                            double val = (Math.random() * 6) + 3;
                            values.add(new Entry(i, processList.get(i)));
                        }
                    }else if (z == 1){
                        for (int i = 0; i < thingsCount; i++) {
//                            double val = (Math.random() * 6) + 3;
                            values.add(new Entry(i, emotionList.get(i)));
                        }
                    }else if (z == 2){
                        for (int i = 0; i < thingsCount; i++) {
//                            double val = (Math.random() * 6) + 3;
                            values.add(new Entry(i, energyList.get(i)));
                        }
                    }


                    LineDataSet d = new LineDataSet(values, tag[z]);
                    d.setLineWidth(2f);
                    d.setCircleRadius(5f);
                    d.setHighlightLineWidth(3);
//                    d.setHighLightColor(Color.rgb(0,0,0));


                    //选择那种配色
                    // 0 % 3
                    // 1 % 3
                    int color = colors[z % colors.length];
                    d.setColor(color);
                    d.setCircleColor(color);
                    d.setHighLightColor(color);

                    if (d.isDrawFilledEnabled())
                        d.setDrawFilled(false);
                    else
                        d.setDrawFilled(true);
                    d.setFillColor(color);

                    dataSets.add(d);
                }


                for (ILineDataSet iSet : dataSets) {

                    LineDataSet set = (LineDataSet) iSet;
                    set.setDrawValues(true);
                    set.setValueTextSize(15f);
                    set.setDrawVerticalHighlightIndicator(true);
                    set.setMode(set.getMode() == LineDataSet.Mode.HORIZONTAL_BEZIER
                            ? LineDataSet.Mode.LINEAR
                            :  LineDataSet.Mode.HORIZONTAL_BEZIER);
                }

                LineData data = new LineData(dataSets);

                //消失
                processBar.setVisibility(View.INVISIBLE);
//                info.setText("Data Dealing Successful !");
                info.setVisibility(View.INVISIBLE);

                chart.setData(data);
                //当前界面最多显示10个x轴的数据
                chart.setVisibleXRangeMaximum(7);
                //移到某个位置
                chart.moveViewToX(thingsCount);
                chart.invalidate();

                //设置chart说明
                Legend l = chart.getLegend();
                //靠上
                l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
                //靠右
        //        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
                l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
                //垂直
        //        l.setOrientation(Legend.LegendOrientation.VERTICAL);
                l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
                //图像里
                l.setDrawInside(false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
                    lineAnalyseFragment = new LineChartFragment();
                }
                getFragmentManager().beginTransaction().replace(R.id.fl_container, lineAnalyseFragment,"line").addToBackStack(null).commitAllowingStateLoss();

            }
        });


        Mtag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tagCloudFragment == null){
                    tagCloudFragment = new TagCloudFragment();
                }

                getFragmentManager().beginTransaction().replace(R.id.fl_container, tagCloudFragment,"tag").addToBackStack(null).commitAllowingStateLoss();

            }
        });

        Madd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dataFragment == null){
                    dataFragment = new DataFragment();
                }
                getFragmentManager().beginTransaction().replace(R.id.fl_container, dataFragment,"add").addToBackStack(null).commitAllowingStateLoss();
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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



    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        Log.i("Gesture", "START, x: " + me.getX() + ", y: " + me.getY());
    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        Log.i("Gesture", "END, lastGesture: " + lastPerformedGesture);

        // un-highlight values after the gesture is finished and no single-tap
        if(lastPerformedGesture != ChartTouchListener.ChartGesture.SINGLE_TAP)
            chart.highlightValues(null); // or highlightTouch(null) for callback to onNothingSelected(...)
    }

    @Override
    public void onChartLongPressed(MotionEvent me) {
        Log.i("LongPress", "Chart long pressed.");
    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {
        Log.i("DoubleTap", "Chart double-tapped.");
    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {
        Log.i("SingleTap", "Chart single-tapped.");
    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
        Log.i("Fling", "Chart fling. VelocityX: " + velocityX + ", VelocityY: " + velocityY);
    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
        Log.i("Scale / Zoom", "ScaleX: " + scaleX + ", ScaleY: " + scaleY);
    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {
        Log.i("Translate / Move", "dX: " + dX + ", dY: " + dY);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("VAL SELECTED",
                "Value: " + e.getY() + ", xIndex: " + e.getX()
                        + ", DataSet index: " + h.getDataSetIndex());

    }

    @Override
    public void onNothingSelected() {

    }
}
