package info.emperinter.DateListThingsAnalyseAndroid;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.CategoryValueDataEntry;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.charts.TagCloud;
import com.anychart.scales.OrdinalColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TagCloudActivity extends AppCompatActivity {
    private SQLiteDatabase db;
    private DbHelper myDb;
    private int user_id;

    @SuppressLint("Range")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainactivity);
        Objects.requireNonNull(getSupportActionBar()).hide();// 隐藏ActionBar

        myDb = new DbHelper(getBaseContext(), "user.db", null, 1);
        db = myDb.getWritableDatabase();
        Cursor cursor = db.query("user", null, null, null, null, null, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user_id = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"));;
        }
        db.close();

        Log.v("getStream-userid",String.valueOf(user_id));

        AnyChartView anyChartView = findViewById(R.id.any_chart_view);
        anyChartView.setProgressBar(findViewById(R.id.progress_bar));

        TagCloud tagCloud = AnyChart.tagCloud();

        tagCloud.title(user_id + "\'s DateListThings KeyWords");

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
//        data.add(new CategoryValueDataEntry("China", "asia", 1383220000));
//        data.add(new CategoryValueDataEntry("India", "asia", 1316000000));
//        data.add(new CategoryValueDataEntry("United States", "america", 324982000));
//        data.add(new CategoryValueDataEntry("Indonesia", "asia", 263510000));
//        data.add(new CategoryValueDataEntry("Brazil", "america", 207505000));
//        data.add(new CategoryValueDataEntry("Pakistan", "asia", 196459000));
//        data.add(new CategoryValueDataEntry("Nigeria", "africa", 191836000));
//        data.add(new CategoryValueDataEntry("Bangladesh", "asia", 162459000));
//        data.add(new CategoryValueDataEntry("Russia", "europe", 146804372));
//        data.add(new CategoryValueDataEntry("Japan", "asia", 126790000));
//        data.add(new CategoryValueDataEntry("Mexico", "america", 123518000));
//        data.add(new CategoryValueDataEntry("Ethiopia", "africa", 104345000));
//        data.add(new CategoryValueDataEntry("Philippines", "asia", 104037000));
//        data.add(new CategoryValueDataEntry("Egypt", "africa", 93013300));

        tagCloud.data(data);

        anyChartView.setChart(tagCloud);
    }
}
