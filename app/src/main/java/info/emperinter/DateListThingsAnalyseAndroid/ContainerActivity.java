package info.emperinter.DateListThingsAnalyseAndroid;


import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Objects;

public class ContainerActivity extends AppCompatActivity{

    private DataFragment dataFragment = new DataFragment();
    private FirebaseAnalytics mFirebaseAnalytics;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(ContainerActivity.this);
        mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);
        Bundle bundle = new Bundle();
        bundle.putString("start","yes");
        mFirebaseAnalytics.logEvent("ContainerActivity",bundle);

        Objects.requireNonNull(getSupportActionBar()).hide();// 隐藏ActionBar
        getFragmentManager().beginTransaction().replace(R.id.fl_container,dataFragment,"add").commitAllowingStateLoss();
    }
}
