package info.emperinter.DateListThingsAnalyseAndroid;


import android.graphics.Color;
import android.graphics.Typeface;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.List;

public class MLineChart {

    private String learnEmotion;
    private LineChart mLineChart;

    public MLineChart(String learnEmotion, LineChart mLineChart) {
        this.learnEmotion = learnEmotion;
        this.mLineChart = mLineChart;
    }


    /**
     * 将类接收的linechart进行样式调整并回传
     * @return mLineChart
     */
    public LineChart getLineChart(){
        mLineChart.setBackgroundColor(Color.parseColor("#EDF3F3"));//设置整个lienchart的背景颜色
        //设置折线本身样式
        mLineChart.getDescription().setEnabled(false);//设置折线图的描述，一般不需要
        mLineChart.setTouchEnabled(true);//设置是否能点击
        mLineChart.setDragEnabled(true);//设置是否可以拖拽
        mLineChart.setDragDecelerationEnabled(false);//设置手拖拽放开后图是否继续滚动
        mLineChart.setDragDecelerationFrictionCoef(0.9f);//设置继续滚动的速度
        mLineChart.setScaleEnabled(true);//设置是否可以缩放
        mLineChart.setDrawGridBackground(false);//设置图表的背景颜色
        mLineChart.setHighlightPerDragEnabled(true);//能否拖拽高亮线(数据点与坐标的提示线)，默认是true
        mLineChart.setPinchZoom(true);//设置是否能扩大缩小

        //设置折线图标签样式
        Legend l = mLineChart.getLegend();//legend是设置折线图的标签的样式，不是设置折线图本身
        l.setForm(Legend.LegendForm.CIRCLE);//设置标签样式为圆形
        l.setTypeface(Typeface.DEFAULT_BOLD);//设置使用何种字体
        l.setTextSize(11f);//设置字体尺寸
        l.setTextColor(Color.WHITE);//设置字体颜色
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);//设置标签上下位置
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);//设置标签左右位置
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);//设置标签排布方式
        l.setDrawInside(false);

        //设置x和y轴的样式
        YAxis rightAxis = mLineChart.getAxisRight();
        rightAxis.setEnabled(false);
        //设置图表左边的y轴禁用
        YAxis leftAxis = mLineChart.getAxisLeft();
        leftAxis.setEnabled(false);
        //设置x轴
        XAxis xAxis = mLineChart.getXAxis();
        xAxis.setTextColor(Color.parseColor("#333333"));
        xAxis.setTextSize(11f);
        xAxis.setAxisMinimum(0f);
        xAxis.setDrawAxisLine(true);//是否绘制轴线
        xAxis.setDrawGridLines(true);//设置x轴上每个点对应的线
        xAxis.setDrawLabels(true);//绘制标签  是否显示x轴的标签尺度
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//设置x轴的显示位置
        xAxis.setGranularity(1f);//禁止放大后x轴标签重绘

        //设置x轴的值
        xAxis.setGranularity(1f);//缩放的时候有用，比如放大的时候，我不想把横轴的月份再细分
        IAxisValueFormatter formatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                //设置 xAxis.setGranularity(1);后 value是从0开始的，每次加1，
                List<String> xAxisList = getXaxisList();
                int v = (int) value;
                if (v <= xAxisList.size() && v >= 0) {
                    String st = xAxisList.get(v);
                    return st;
                } else {
                    return null;
                }
            }
        };
        xAxis.setValueFormatter((ValueFormatter) formatter);
        return mLineChart;
    }


    /**
     * 为设置x轴的日期，生成对应格式的数据
     * @param
     * @return mXaxisList
     */
    private List<String> getXaxisList(){
        List<String> mXaxisList = new ArrayList<>();
//        for (int i=0;i<learnEmotion.getXaxis().getData().size();i++){
//            mXaxisList.add(learnEmotion.getXaxis().getData().get(i));
//        }
        return mXaxisList;
    }


    /**
     * 返回linechart所需要的格式的数据，将后端传回的数据进行格式转换
     * @return mLineData
     */
    public LineData getData(){

        ArrayList<Entry> poetryList = new ArrayList<Entry>();
//        for (int i=0;i<learnEmotion.getSeries().get(0).getData().size();i++){
//            poetryList.add(new Entry(i,Float.valueOf(learnEmotion.getSeries().get(0).getData().get(i))));
//        }

        ArrayList<Entry> articleList = new ArrayList<Entry>();
//        for (int i=0;i<learnEmotion.getSeries().get(1).getData().size();i++){
//            articleList.add(new Entry(i,Float.valueOf(learnEmotion.getSeries().get(1).getData().get(i))));
//        }

        ArrayList<Entry> sentenceList = new ArrayList<Entry>();
//        for (int i=0;i<learnEmotion.getSeries().get(2).getData().size();i++){
//            sentenceList.add(new Entry(i,Float.valueOf(learnEmotion.getSeries().get(2).getData().get(i))));
//        }

        LineDataSet poetryData, articleData, sentenceData;

        poetryData = new LineDataSet(poetryList, "aa");
        articleData = new LineDataSet(articleList, "bb");
        sentenceData = new LineDataSet(sentenceList, "cc");

        LineData mLineData = new LineData(poetryData, articleData, sentenceData);

        return mLineData;
    }


}