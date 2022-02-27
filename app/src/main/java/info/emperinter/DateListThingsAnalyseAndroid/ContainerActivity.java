package info.emperinter.DateListThingsAnalyseAndroid;


import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;

import java.util.Objects;

public class ContainerActivity extends AppCompatActivity{

    private DataFragment dataFragment = new DataFragment();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);
        Objects.requireNonNull(getSupportActionBar()).hide();// 隐藏ActionBar
        getFragmentManager().beginTransaction().replace(R.id.fl_container,dataFragment,"add").commitAllowingStateLoss();
    }
}
