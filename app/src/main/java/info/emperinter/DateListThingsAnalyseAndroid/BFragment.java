package info.emperinter.DateListThingsAnalyseAndroid;


import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.CategoryValueDataEntry;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.charts.TagCloud;
import com.anychart.scales.OrdinalColor;

import java.util.ArrayList;
import java.util.List;

public class BFragment extends Fragment {

    private TextView mTvTitle;
    private Button Mret;
    private AFragment aFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_b,container,false);  //设置布局文件

        AnyChartView anyChartView = view.findViewById(R.id.any_chart_view);
        anyChartView.setProgressBar(view.findViewById(R.id.progress_bar));

        TagCloud tagCloud = AnyChart.tagCloud();

        tagCloud.title("DateListThings KeyWords");

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
        mTvTitle = (TextView) view.findViewById(R.id.tv_title);

        Mret = (Button) view.findViewById(R.id.ret);

        Mret.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(aFragment == null){
                    aFragment = new AFragment();
                }
                //按返回键上一个状态保持原样！Tag:"a"在ContainerActivity中设置;
                Fragment fragment = getFragmentManager().findFragmentByTag("b");
                if(fragment != null && !fragment.isAdded()){
                    getFragmentManager().beginTransaction().hide(fragment).add(R.id.fl_container,aFragment).addToBackStack(null).commitAllowingStateLoss();
                    mTvTitle.setText("B被隐藏，A创建！");
                }else {
                    getFragmentManager().beginTransaction().replace(R.id.fl_container,aFragment).addToBackStack(null).commitAllowingStateLoss();
                    mTvTitle.setText("A创建！");
                }

            }
        });

    }
}
