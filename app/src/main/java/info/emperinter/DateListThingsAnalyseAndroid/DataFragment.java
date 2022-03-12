package info.emperinter.DateListThingsAnalyseAndroid;

import android.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import android.util.Log;
import android.widget.*;
import com.google.firebase.analytics.FirebaseAnalytics;
import info.emperinter.DateListThingsAnalyseAndroid.Data.DbHelper;
import info.emperinter.DateListThingsAnalyseAndroid.Data.HttpResponseCallBack;
import info.emperinter.DateListThingsAnalyseAndroid.Data.Singleton;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.IOException;

public class DataFragment extends Fragment {
    private Button Mdata,mBtnAdd,Mtag,Mline;
    private TagCloudFragment tagCloudFragment;
    private LineChartFragment lineFragment;
    private SQLiteDatabase db;
    private DbHelper myDb;
    private int user_id;
    private String host = "";
    private String reqGet = "NO";
    private String url;
    private int getProcess,getEmotion,getEnergy,getYear,getMonth,getDay;
    private String getKeyWords;

    private String inputJson = "";
    private FirebaseAnalytics mFirebaseAnalytics;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.data_fragment,container,false);  //设置布局文件
//       软键盘弹出时，整个界面不会变动，输入框也不会顶起，这种设置

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams. SOFT_INPUT_ADJUST_NOTHING);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity().getBaseContext());
        mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);
        Bundle bundle = new Bundle();
        bundle.putString("start","yes");
        mFirebaseAnalytics.logEvent("Data",bundle);

        //user_id 获取
        myDb = new DbHelper(getContext().getApplicationContext(),"user.db", null, 1);
        db = myDb.getWritableDatabase();
        Cursor cursor = db.query("user", null, null, null, null, null, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user_id = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"));;
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

        Mtag = (Button) getActivity().findViewById(R.id.btn_tagcloud);
        Mdata = (Button) getActivity().findViewById(R.id.add);
        Mline = (Button) getActivity().findViewById(R.id.btn_lineanalyse);

        Mtag.setEnabled(true);
        Mdata.setEnabled(false);
        Mline.setEnabled(true);

        mBtnAdd = view.findViewById(R.id.btn_add);

        NumberPicker numberProcess = view.findViewById(R.id.processpicker);
        NumberPicker numberEmotion = view.findViewById(R.id.emotionpicker);
        NumberPicker numberEnergy = view.findViewById(R.id.energypicker);
        EditText getKey = view.findViewById(R.id.inputkey);
        DatePicker getDate = view.findViewById(R.id.mydate);

        TextView infoText = view.findViewById(R.id.info);


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

                Log.v("origin_update_input",inputJson);

                try {
                    Singleton.getInstance().doPostRequest(host+"/api/things/",inputJson, new HttpResponseCallBack() {
                        @Override
                        public void getResponse(String response) throws JSONException, IOException {
                            reqGet = response;
                            Log.v("get_data_json",reqGet);
                            if(reqGet.contains("things_id")) {
                                infoText.setText("insert successful!");
                            }else if(reqGet.contains("exists")){
                                getKey.setHint("list things with this date already exists.");
                                infoText.setText("list things with this date already exists.!");
                                try {
                                    //已存在则查到thing_id并更新
                                    Singleton.getInstance().doGetRequest(host + "/api/thing/query/?userid=" + user_id, new HttpResponseCallBack() {
                                        @Override
                                        public void getResponse(String response) throws JSONException, IOException {
                                            reqGet = response;
                                            JSONArray userJson = new JSONArray(reqGet);
                                            int i = 0;
                                            while (i < userJson.length()) {
                                                int things_id = userJson.getJSONObject(i).getInt("things_id");
                                                String getdate = userJson.getJSONObject(i).getString("date");
                                                infoText.setText("found the same data with things_id: " + things_id + " and update it");
                                                Log.v("update_getdate",getdate);
                                                String getday = "";
                                                String getmon = "";
                                                if(getDay < 10){
                                                    getday  = "0"+ getDay;
                                                }else {
                                                    getday = String.valueOf(getDay);
                                                }
                                                if(getMonth < 10){
                                                    getmon = "0" + getMonth;
                                                }else {
                                                    getmon = String.valueOf(getMonth);
                                                }
                                                if (getdate.contains(getYear+"-"+getmon+"-"+getday) ){
                                                    //查到things_id 并更新
                                                    inputJson =
                                                            "{\"things_id\":" + things_id + "," +
                                                            "\"userid\":\"" + host + "/api/users/" + user_id + "/\"," +
                                                            "\"date\":\"" + getYear + "-" + getMonth + "-" + getDay + "\"," +
                                                            "\"process\":" + getProcess + "," +
                                                            "\"emotion\":" + getEmotion + "," +
                                                            "\"energy\":" + getEnergy + "," +
                                                            "\"key\":\"" + getKeyWords + "\"}";
                                                    try {
                                                        Singleton.getInstance().doPutRequest(host + "/api/things/"+things_id+"/", inputJson, new HttpResponseCallBack() {
                                                            @Override
                                                            public void getResponse(String response) throws JSONException, IOException {
                                                                reqGet = response;
                                                                if (reqGet.contains(String.valueOf(things_id))) {
                                                                    infoText.setText("update successful with the same date!");
                                                                } else {
                                                                    infoText.setText("update failed for some unknow things !");
                                                                }
                                                            }
                                                        });
                                                    }catch (Exception e){
                                                        infoText.setText(e.toString());
                                                    }
                                                    break;
                                                }
                                                i += 1;
                                            }
                                        }
                                    });
                                }catch (IOException e){
                                    infoText.setText(e.toString());
                                }

                            }else if(reqGet.contains("This field may not be blank.") && reqGet.contains("key")){
                                Toast.makeText(getActivity().getBaseContext(),"This field may not be blank !",Toast.LENGTH_SHORT).show();
                                infoText.setText("KeyWord may not be blank !");
                            }else{
                                Toast.makeText(getActivity().getBaseContext(),"SomeThing Wrong !",Toast.LENGTH_SHORT).show();
                                infoText.setText("Something Wrong !");
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.v("get_data",getKeyWords+"\t"+String.valueOf(getEmotion));
            }
        });


        //TagCloudBtn
        Mtag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tagCloudFragment == null){
                    tagCloudFragment = new TagCloudFragment();
                }
                    getFragmentManager().beginTransaction().replace(R.id.fl_container, tagCloudFragment,"tag").addToBackStack(null).commitAllowingStateLoss();
            }
        });

        Mline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(lineFragment == null){
                    lineFragment = new LineChartFragment();
                }
                    getFragmentManager().beginTransaction().replace(R.id.fl_container, lineFragment,"line").addToBackStack(null).commitAllowingStateLoss();
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
                    Toast.makeText(getActivity(), getString(R.string.exit), Toast.LENGTH_SHORT).show();
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
