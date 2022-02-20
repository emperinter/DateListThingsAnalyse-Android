package info.emperinter.DateListThingsAnalyseAndroid.Data;

import org.json.JSONException;

import java.io.IOException;

public interface HttpResponseCallBack {
    void getResponse(String response) throws JSONException, IOException;
}
