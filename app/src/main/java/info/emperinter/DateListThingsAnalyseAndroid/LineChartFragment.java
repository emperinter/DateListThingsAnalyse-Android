package info.emperinter.DateListThingsAnalyseAndroid;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.*;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class LineChartFragment extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, OnChartGestureListener, OnChartValueSelectedListener {


    private LineChart chart;
    private SeekBar seekBarX, seekBarY;
    private TextView tvX, tvY;


    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.barchart_fragment);
        chart = findViewById(R.id.chart1);

        setTitle("MultiLineChartActivity");

        tvX = findViewById(R.id.tvXMax);
        tvY = findViewById(R.id.tvYMax);

        seekBarX = findViewById(R.id.seekBar1);
        seekBarX.setOnSeekBarChangeListener(this);

        seekBarY = findViewById(R.id.seekBar2);
        seekBarY.setOnSeekBarChangeListener(this);

        chart = findViewById(R.id.chart1);
        chart.setOnChartValueSelectedListener(this);

        //双击缩放禁止
        chart.setDoubleTapToZoomEnabled(false);

        //灰色背景
//        chart.setDrawGridBackground(true);
        //chart整个背景颜色
//        chart.setBackgroundColor(Color.rgb(0,0,0));

        chart.setLeftTopRightBottom(100,100,100,100);

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

//        chart.setHorizontalScrollBarEnabled(true);

//        chart.getAxisRight().setDrawGridLines(true);
//        chart.getXAxis().setDrawAxisLine(true);
//        chart.getXAxis().setDrawGridLines(true);

        //最大可见值
        //当点的数量大于该值时则不显示每个点具体的数值
//        chart.setMaxVisibleValueCount(10);

        //当前界面最多显示10个x轴的数据
        chart.setVisibleXRangeMaximum(21);

        //图像水平位移
//        chart.setX(1000);

        // enable touch gestures
        chart.setTouchEnabled(true);

        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);

//        chart.setKeepPositionOnRotation(true);


        // if disabled, scaling can be done on x- and y-axis separately
        // 手指只能分开控制x和y轴的操作
        chart.setPinchZoom(false);


        seekBarX.setProgress(20);
        seekBarY.setProgress(100);

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

//设置线的颜色
    private final int[] colors = new int[] {
//            ColorTemplate.VORDIPLOM_COLORS[0],
//            ColorTemplate.VORDIPLOM_COLORS[1],
//            ColorTemplate.VORDIPLOM_COLORS[2]
            Color.rgb(252,3,3),
            Color.rgb(36,252,3),
            Color.rgb(252,244,3),
    };

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        chart.resetTracking();

//        chart.setMinOffset(20);

//        chart.setMaxVisibleValueCount(10);


        progress = seekBarX.getProgress();

        tvX.setText(String.valueOf(seekBarX.getProgress()));
        tvY.setText(String.valueOf(seekBarY.getProgress()));

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();

        //这里的3代表了3条线
        for (int z = 0; z < 3; z++) {

            ArrayList<Entry> values = new ArrayList<>();

            Date dNow = new Date();   //当前时间
            Date dBefore = new Date();
            Calendar calendar = Calendar.getInstance(); //得到日历
            calendar.setTime(dNow);//把当前时间赋给日历


            for (int i = 0; i < progress; i++) {
                double val = (Math.random() * seekBarY.getProgress()) + 3;
                values.add(new Entry(i, (float) val));
            }

            LineDataSet d = new LineDataSet(values, "DataSet " + (z + 1));
            d.setLineWidth(1f);
            d.setCircleRadius(3f);

            //选择那种配色
            // 0 % 3
            // 1 % 3
            int color = colors[z % colors.length];

            d.setColor(color);
            d.setCircleColor(color);
            dataSets.add(d);
        }


        for (ILineDataSet iSet : dataSets) {

            LineDataSet set = (LineDataSet) iSet;
            set.setDrawValues(true);
            set.setMode(set.getMode() == LineDataSet.Mode.HORIZONTAL_BEZIER
                    ? LineDataSet.Mode.LINEAR
                    :  LineDataSet.Mode.HORIZONTAL_BEZIER);

        }

        LineData data = new LineData(dataSets);

        chart.setVisibleXRangeMaximum(18);

        chart.setData(data);
        chart.invalidate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.line, menu);
        menu.removeItem(R.id.actionToggleIcons);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.viewGithub: {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://github.com/PhilJay/MPAndroidChart/blob/master/MPChartExample/src/com/xxmassdeveloper/mpchartexample/MultiLineChartActivity.java"));
                startActivity(i);
                break;
            }
            case R.id.actionToggleValues: {
                List<ILineDataSet> sets = chart.getData()
                        .getDataSets();

                for (ILineDataSet iSet : sets) {
                    LineDataSet set = (LineDataSet) iSet;
//                    set.setDrawValues(!set.isDrawValuesEnabled());
                    set.setDrawValues(true);

                }

                chart.invalidate();
                break;
            }
            /*
            case R.id.actionToggleIcons: { break; }
             */
            case R.id.actionTogglePinch: {
                if (chart.isPinchZoomEnabled())
                    chart.setPinchZoom(false);
                else
                    chart.setPinchZoom(true);

                chart.invalidate();
                break;
            }
            case R.id.actionToggleAutoScaleMinMax: {
                chart.setAutoScaleMinMaxEnabled(!chart.isAutoScaleMinMaxEnabled());
                chart.notifyDataSetChanged();
                break;
            }
            case R.id.actionToggleHighlight: {
                if(chart.getData() != null) {
                    chart.getData().setHighlightEnabled(!chart.getData().isHighlightEnabled());
                    chart.invalidate();
                }
                break;
            }
            case R.id.actionToggleFilled: {
                List<ILineDataSet> sets = chart.getData()
                        .getDataSets();

                for (ILineDataSet iSet : sets) {

                    LineDataSet set = (LineDataSet) iSet;
                    if (set.isDrawFilledEnabled())
                        set.setDrawFilled(false);
                    else
                        set.setDrawFilled(true);
                }
                chart.invalidate();
                break;
            }
            case R.id.actionToggleCircles: {
                List<ILineDataSet> sets = chart.getData()
                        .getDataSets();

                for (ILineDataSet iSet : sets) {

                    LineDataSet set = (LineDataSet) iSet;
                    if (set.isDrawCirclesEnabled())
                        set.setDrawCircles(false);
                    else
                        set.setDrawCircles(true);
                }
                chart.invalidate();
                break;
            }
            case R.id.actionToggleCubic: {
                List<ILineDataSet> sets = chart.getData()
                        .getDataSets();

                for (ILineDataSet iSet : sets) {

                    LineDataSet set = (LineDataSet) iSet;
                    set.setMode(set.getMode() == LineDataSet.Mode.CUBIC_BEZIER
                            ? LineDataSet.Mode.LINEAR
                            :  LineDataSet.Mode.CUBIC_BEZIER);
                }
                chart.invalidate();
                break;
            }
            case R.id.actionToggleStepped: {
                List<ILineDataSet> sets = chart.getData()
                        .getDataSets();

                for (ILineDataSet iSet : sets) {

                    LineDataSet set = (LineDataSet) iSet;
                    set.setMode(set.getMode() == LineDataSet.Mode.STEPPED
                            ? LineDataSet.Mode.LINEAR
                            :  LineDataSet.Mode.STEPPED);
                }
                chart.invalidate();
                break;
            }
            case R.id.actionToggleHorizontalCubic: {
                List<ILineDataSet> sets = chart.getData()
                        .getDataSets();

                for (ILineDataSet iSet : sets) {

                    LineDataSet set = (LineDataSet) iSet;
                    set.setMode(set.getMode() == LineDataSet.Mode.HORIZONTAL_BEZIER
                            ? LineDataSet.Mode.LINEAR
                            :  LineDataSet.Mode.HORIZONTAL_BEZIER);
                }
                chart.invalidate();
                break;
            }
            case R.id.actionSave: {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//                    saveToGallery();
                } else {
//                    requestStoragePermission(chart);
                }
                break;
            }
            case R.id.animateX: {
                chart.animateX(2000);
                break;
            }
            case R.id.animateY: {
                chart.animateY(2000);
                break;
            }
            case R.id.animateXY: {
                chart.animateXY(2000, 2000);
                break;
            }
        }
        return true;
    }

//    @Override
//    protected void saveToGallery() {
//        saveToGallery(chart, "MultiLineChartActivity");
//    }

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
    public void onNothingSelected() {}

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}
}