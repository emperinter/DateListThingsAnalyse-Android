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
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class LineAnalyseFragment extends Fragment {
    private Button mBtnChange;
    private TextView mTvTitle;
    private TagCloudFragment tagCloudFragment;
    private  IOnMessageClick listener;//申明接口
    private SQLiteDatabase db;
    private DbHelper myDb;
    private int user_id;
    private String username = "";
    private String host = "";
    private String reqGet = "NO";
    private Api api = null;
    private String url;
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
        Log.d("LineAnalyseFragment","-----onCreateView-----");


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
        Log.v("getStream-url",url);

        api = new Api();
        reqGet = api.request(url);

        Log.v("getStream-req-get-cloud",reqGet);
        Log.v("getStream-req-get-cloud",api.get_data);

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

        cartesian.yAxis(0).title("Rank(m/10)");
        cartesian.xAxis(0).labels().padding(5d, 5d, 5d, 5d);

        List<DataEntry> seriesData = new ArrayList<>();
        seriesData.add(new CustomDataEntry("1986", 3.6, 2.3, 2.8));
        seriesData.add(new CustomDataEntry("1987", 7.1, 4.0, 4.1));
        seriesData.add(new CustomDataEntry("1988", 8.5, 6.2, 5.1));
        seriesData.add(new CustomDataEntry("1989", 9.2, 11.8, 6.5));
        seriesData.add(new CustomDataEntry("1990", 10.1, 13.0, 12.5));
        seriesData.add(new CustomDataEntry("1991", 11.6, 13.9, 18.0));
        seriesData.add(new CustomDataEntry("1992", 16.4, 18.0, 21.0));
        seriesData.add(new CustomDataEntry("1993", 18.0, 23.3, 20.3));
        seriesData.add(new CustomDataEntry("1994", 13.2, 24.7, 19.2));
        seriesData.add(new CustomDataEntry("1986", 3.6, 2.3, 2.8));
        seriesData.add(new CustomDataEntry("1987", 7.1, 4.0, 4.1));
        seriesData.add(new CustomDataEntry("1988", 8.5, 6.2, 5.1));
        seriesData.add(new CustomDataEntry("1989", 9.2, 11.8, 6.5));
        seriesData.add(new CustomDataEntry("1990", 10.1, 13.0, 12.5));
        seriesData.add(new CustomDataEntry("1991", 11.6, 13.9, 18.0));
        seriesData.add(new CustomDataEntry("1992", 16.4, 18.0, 21.0));
        seriesData.add(new CustomDataEntry("1993", 18.0, 23.3, 20.3));
        seriesData.add(new CustomDataEntry("1994", 13.2, 24.7, 19.2));
        seriesData.add(new CustomDataEntry("2001",7,8,9));
        seriesData.add(new CustomDataEntry("3001",7,8,9));
        seriesData.add(new CustomDataEntry("4001",7,8,9));
        seriesData.add(new CustomDataEntry("6001",7,8,9));
        seriesData.add(new CustomDataEntry("6002",7,8,9));
        seriesData.add(new CustomDataEntry("6003",7,8,9));
        seriesData.add(new CustomDataEntry("6004",7,8,9));
        seriesData.add(new CustomDataEntry("6005",7,8,9));
        seriesData.add(new CustomDataEntry("6006",7,8,9));
        seriesData.add(new CustomDataEntry("6007",7,8,9));
        seriesData.add(new CustomDataEntry("6008",7,8,9));
        seriesData.add(new CustomDataEntry("6009",7,8,9));
        seriesData.add(new CustomDataEntry("6010",7,8,9));
        seriesData.add(new CustomDataEntry("6011",7,8,9));


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

        return  view;
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
        mBtnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tagCloudFragment == null){
                    tagCloudFragment = new TagCloudFragment();
                }
                //按返回键上一个状态保持原样！Tag:"a"在ContainerActivity中设置;
                Fragment aFragment = getFragmentManager().findFragmentByTag("a");
                if(aFragment != null && !aFragment.isAdded()){
                    mTvTitle.setText("TagCloud");
                    getFragmentManager().beginTransaction().hide(aFragment).add(R.id.fl_container, tagCloudFragment,"b").addToBackStack(null).commitAllowingStateLoss();
                }else {
                    mTvTitle.setText("TagCloud");
                    getFragmentManager().beginTransaction().replace(R.id.fl_container, tagCloudFragment,"b").addToBackStack(null).commitAllowingStateLoss();
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
