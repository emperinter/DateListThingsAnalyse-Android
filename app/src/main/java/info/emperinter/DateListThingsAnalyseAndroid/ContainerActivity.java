package info.emperinter.DateListThingsAnalyseAndroid;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

public class ContainerActivity extends AppCompatActivity implements LineAnalyseFragment.IOnMessageClick{

    private TagCloudFragment tagFragment;

    private TextView mTvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);
        Objects.requireNonNull(getSupportActionBar()).hide();// 隐藏ActionBar

        mTvTitle = (TextView) findViewById(R.id.tv_title);

        tagFragment = new TagCloudFragment().newInstance("TagCloud!"); //传参

        getFragmentManager().beginTransaction().add(R.id.fl_container,tagFragment,"tag").commitAllowingStateLoss();
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
