package info.emperinter.DateListThingsAnalyseAndroid;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class AFragment extends Fragment {
    private Button mBtnChange;
    private TextView mTvTitle;
    private TagCloudFragment tagCloudFragment;
    private  IOnMessageClick listener;//申明接口

    //传参
    public static  AFragment newInstance(String title){
        AFragment fragment = new AFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title",title);
        fragment.setArguments(bundle);
        return fragment;
    }

    //声明接口，给Activity传参！在ContainerActivity实现该接口！
    public interface IOnMessageClick{
        void onClick(String text);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_a,container,false);  //设置布局文件
        Log.d("AFragment","-----onCreateView-----");
        return  view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTvTitle = (TextView) view.findViewById(R.id.tv_title);
        mBtnChange = (Button) view.findViewById(R.id.btn_change);
        mBtnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tagCloudFragment == null){
                    tagCloudFragment = new TagCloudFragment();
                }
                //按返回键上一个状态保持原样！Tag:"a"在ContainerActivity中设置;
                Fragment aFragment = getFragmentManager().findFragmentByTag("a");
                if(aFragment != null && !aFragment.isAdded()){
                    mTvTitle.setText("A被隐藏，B创建！");
                    getFragmentManager().beginTransaction().hide(aFragment).add(R.id.fl_container, tagCloudFragment,"b").addToBackStack(null).commitAllowingStateLoss();
                }else {
                    mTvTitle.setText("B创建！");
                    getFragmentManager().beginTransaction().replace(R.id.fl_container, tagCloudFragment,"b").addToBackStack(null).commitAllowingStateLoss();
                }

            }
        });


        if(getArguments() != null){
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            listener = (IOnMessageClick) context;   //给Activity传参
        }catch (ClassCastException ex){
            throw  new ClassCastException("Activity 必须实现IOnMessageClick 接口!");
        }

    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //取消异步
    }
}
