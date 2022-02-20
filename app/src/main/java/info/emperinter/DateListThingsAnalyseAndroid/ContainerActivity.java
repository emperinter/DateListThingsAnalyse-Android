package info.emperinter.DateListThingsAnalyseAndroid;


import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;

import java.util.Objects;

public class ContainerActivity extends AppCompatActivity implements LineChartFragment.IOnMessageClick{

    private DataFragment dataFragment = new DataFragment();
    private TextView mTvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);
        Objects.requireNonNull(getSupportActionBar()).hide();// 隐藏ActionBar

        mTvTitle = (TextView) findViewById(R.id.tv_title);


        getFragmentManager().beginTransaction().add(R.id.fl_container,dataFragment,"add").commitAllowingStateLoss();
    }

    //不推荐
    public  void setData(String message){
        mTvTitle.setText(message);
    }

    //推荐给Activity传参的方法
    //实现接口！
    @Override
    public void onClick(String text) {
        mTvTitle.setText(text);
    }


}
