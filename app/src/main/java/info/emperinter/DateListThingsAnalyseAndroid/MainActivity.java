package info.emperinter.DateListThingsAnalyseAndroid;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.CategoryValueDataEntry;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.charts.TagCloud;
import com.anychart.scales.OrdinalColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainactivity);
        Objects.requireNonNull(getSupportActionBar()).hide();// 隐藏ActionBar


        AnyChartView anyChartView = findViewById(R.id.any_chart_view);
        anyChartView.setProgressBar(findViewById(R.id.progress_bar));

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
        data.add(new CategoryValueDataEntry("Vietnam", "asia", 92700000));
        data.add(new CategoryValueDataEntry("Germany", "europe", 82800000));
        data.add(new CategoryValueDataEntry("Democratic Republic of the Congo", "africa", 82243000));
        data.add(new CategoryValueDataEntry("Iran", "asia", 80135400));
        data.add(new CategoryValueDataEntry("Turkey", "asia", 79814871));
        data.add(new CategoryValueDataEntry("Thailand", "asia", 68298000));
        data.add(new CategoryValueDataEntry("France", "europe", 67013000));
        data.add(new CategoryValueDataEntry("United Kingdom", "europe", 65110000));
        data.add(new CategoryValueDataEntry("Italy", "europe", 60599936));
        data.add(new CategoryValueDataEntry("Tanzania", "africa", 56878000));
        data.add(new CategoryValueDataEntry("South Africa", "africa", 55908000));
        data.add(new CategoryValueDataEntry("Myanmar", "asia", 54836000));
        data.add(new CategoryValueDataEntry("South Korea", "asia", 51446201));
        data.add(new CategoryValueDataEntry("Colombia", "america", 49224700));
        data.add(new CategoryValueDataEntry("Kenya", "africa", 48467000));
        data.add(new CategoryValueDataEntry("Spain", "europe", 46812000));
        data.add(new CategoryValueDataEntry("Argentina", "america", 43850000));
        data.add(new CategoryValueDataEntry("Ukraine", "europe", 42541633));
        data.add(new CategoryValueDataEntry("Sudan", "africa", 42176000));
        data.add(new CategoryValueDataEntry("Uganda", "africa", 41653000));

        tagCloud.data(data);

        anyChartView.setChart(tagCloud);
    }
}
