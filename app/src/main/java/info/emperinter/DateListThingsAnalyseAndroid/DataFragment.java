package info.emperinter.DateListThingsAnalyseAndroid;

import android.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import info.emperinter.DateListThingsAnalyseAndroid.API.HttpResponseCallBack;
import info.emperinter.DateListThingsAnalyseAndroid.API.Singleton;
import okhttp3.*;
import org.json.JSONException;
import java.io.IOException;

public class DataFragment extends Fragment {
    private Button mBtnChange,Mdata,mBtnAdd;
    private TextView mTvTitle;
    private TagCloudFragment tagCloudFragment;
    private DataFragment dataFragment;
    private LineAnalyseFragment.IOnMessageClick listener;//申明接口
    private SQLiteDatabase db;
    private DbHelper myDb;
    private int user_id;
    private String host = "";
    private String username;
    private String reqGet = "NO";
    private String url;
    private int getProcess,getEmotion,getEnergy,getYear,getMonth,getDay;
    private String getKeyWords;

    private String inputJson = "";


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.data_fragment,container,false);  //设置布局文件


        //user_id 获取
        myDb = new DbHelper(getContext().getApplicationContext(),"user.db", null, 1);
        db = myDb.getWritableDatabase();
        Cursor cursor = db.query("user", null, null, null, null, null, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user_id = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"));;
            username = cursor.getString(cursor.getColumnIndexOrThrow("user_name"));
            host = cursor.getString(cursor.getColumnIndexOrThrow("host"));
        }
        db.close();

        //json获取
        url = host+"/api/thing/query/?format=json&userid="+user_id;


        try {
            Singleton.getInstance().doGetRequest(url, new HttpResponseCallBack() {
                @Override
                public void getResponse(String response) throws JSONException {
                    reqGet = response;
                    if(reqGet.contains("things_id")){
                    }else if(reqGet.contains("[]")){
                        Toast.makeText(getActivity().getBaseContext(),"username or password is wrong !",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getActivity().getBaseContext(),"SomeThing Wrong !",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTvTitle = (TextView) getActivity().findViewById(R.id.tv_title);
        mBtnChange = (Button) getActivity().findViewById(R.id.btn_tagcloud);
        Mdata = (Button) getActivity().findViewById(R.id.add);
        mBtnAdd = view.findViewById(R.id.btn_add);

        NumberPicker numberProcess = view.findViewById(R.id.processpicker);
        NumberPicker numberEmotion = view.findViewById(R.id.emotionpicker);
        NumberPicker numberEnergy = view.findViewById(R.id.energypicker);
        EditText getKey = view.findViewById(R.id.inputkey);
        DatePicker getDate = view.findViewById(R.id.mydate);


        numberProcess.setMaxValue(10);
        numberEmotion.setMaxValue(10);
        numberEnergy.setMaxValue(10);
        numberProcess.setMinValue(0);
        numberEmotion.setMinValue(0);
        numberEnergy.setMinValue(0);

        //user_id 获取
        myDb = new DbHelper(getActivity().getBaseContext(),"user.db", null, 1);
        db = myDb.getWritableDatabase();
        Cursor cursor = db.query("user", null, null, null, null, null, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user_id = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"));;
            username = cursor.getString(cursor.getColumnIndexOrThrow("user_name"));
            host = cursor.getString(cursor.getColumnIndexOrThrow("host"));
        }
        db.close();

        //json获取

        mBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getKeyWords = getKey.getText().toString();
                getEmotion = numberEmotion.getValue();
                getEnergy = numberEnergy.getValue();
                getProcess = numberProcess.getValue();
                getYear = getDate.getYear();
                getMonth = getDate.getMonth() + 1;
                getDay = getDate.getDayOfMonth();

                inputJson =
                        "{\"userid\":\""+host+"/api/users/"+user_id+"/\"," +
                        "\"date\":\""+getYear+"-"+getMonth+"-"+getDay+"\"," +
                        "\"process\":"+getProcess+","+
                        "\"emotion\":"+getEmotion+"," +
                        "\"energy\":"+getEnergy+"," +
                        "\"key\":\""+getKeyWords+"\"}";

                Log.v("get_data_input",inputJson);

                try {
                    Singleton.getInstance().doPostRequest(host+"/api/things/",inputJson, new HttpResponseCallBack() {
                        @Override
                        public void getResponse(String response) throws JSONException {
                            reqGet = response;
                            Log.v("get_data_json",reqGet);
                            if(reqGet.contains("things_id")) {
                                Toast.makeText(getActivity().getBaseContext(), "👌ok!", Toast.LENGTH_SHORT).show();
                            }else if(reqGet.contains("exists")){
                                Toast.makeText(getActivity().getBaseContext(), "list things with this date already exists.", Toast.LENGTH_SHORT).show();
                                getKey.setHint("list things with this date already exists.");
                                getKey.setTextColor(0xff3b3b3b);
                            }else if(reqGet.contains("This field may not be blank.") && reqGet.contains("key")){
                                Toast.makeText(getActivity().getBaseContext(),"This field may not be blank !",Toast.LENGTH_SHORT).show();
                                getKey.setHint("This field may not be blank.");
                            }else{
                                Toast.makeText(getActivity().getBaseContext(),"SomeThing Wrong !",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.v("get_data",getKeyWords+"\t"+String.valueOf(getEmotion));
            }
        });



        mBtnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tagCloudFragment == null){
                    tagCloudFragment = new TagCloudFragment();
                }
                //按返回键上一个状态保持原样！Tag:"a"在ContainerActivity中设置;
                Fragment aFragment = getFragmentManager().findFragmentByTag("a");
                if(aFragment != null && !aFragment.isAdded()){
                    mTvTitle.setText("TagCloud");
                    getFragmentManager().beginTransaction().hide(aFragment).add(R.id.fl_container, tagCloudFragment,"b").addToBackStack(null).commitAllowingStateLoss();
                }else {
                    mTvTitle.setText("TagCloud");
                    getFragmentManager().beginTransaction().replace(R.id.fl_container, tagCloudFragment,"b").addToBackStack(null).commitAllowingStateLoss();
                }

            }
        });

        Mdata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dataFragment == null){
                    dataFragment = new DataFragment();
                }
                //按返回键上一个状态保持原样！Tag:"a"在ContainerActivity中设置;
                Fragment fragment = getFragmentManager().findFragmentByTag("d");
                if(fragment != null && !fragment.isAdded()){
                    getFragmentManager().beginTransaction().hide(fragment).add(R.id.fl_container, dataFragment).addToBackStack(null).commitAllowingStateLoss();
                    mTvTitle.setText("Data");
                }else {
                    getFragmentManager().beginTransaction().replace(R.id.fl_container, dataFragment).addToBackStack(null).commitAllowingStateLoss();
                    mTvTitle.setText("Data");
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_BACK){
                    Toast.makeText(getActivity(), "exit!", Toast.LENGTH_SHORT).show();
                    getActivity().moveTaskToBack(true);
                    getActivity().finish();
                    System.exit(0);
                    return true;
                }
                return false;
            }
        });
    }
}
