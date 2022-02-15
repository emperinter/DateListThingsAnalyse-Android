package info.emperinter.DateListThingsAnalyseAndroid;


import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.*;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LinetestActivity extends AppCompatActivity {


    LineChart lc;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.barchart_fragment);
        lc = findViewById(R.id.chart1);


        String[] xx = {
                "2", "4", "6", "8", "10", "12", "14", "16", "18"};
        String[][] yy = {
                {
                        "20", "80", "10", "60", "30", "70", "55", "22", "40"},
                {
                        "18", "22", "99", "33", "12", "64", "99", "16", "34"},
                {
                        "64", "19", "34", "81", "16", "46", "33", "46", "19"}};
        ArrayList<String> xVals = new ArrayList<>();
        for (int i = 0; i < xx.length; i++) {
            xVals.add(xx[i]);
        }

        for (int i = 0; i < 3; i++) {
            ArrayList<Entry> yVals = new ArrayList<>();
            for (int j = 0; j < yy[i].length; j++) {
                yVals.add(new Entry(Float.parseFloat(yy[i][j]), j));
            }
            LineDataSet set1 = new LineDataSet(yVals, "DataSet " + (i + 1));
            set1.setCubicIntensity(0.2f);
            set1.setDrawFilled(true);  //设置包括的范围区域填充颜色
            set1.setDrawCircles(true);  //设置有圆点
            set1.setLineWidth(2f);    //设置线的宽度
            set1.setCircleSize(5f);   //设置小圆的大小

            LineData data = new LineData(set1);

            lc.setData(data);//设置图表数据
        }

    }
}