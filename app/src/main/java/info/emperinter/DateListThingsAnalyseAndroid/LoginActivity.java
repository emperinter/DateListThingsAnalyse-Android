package info.emperinter.DateListThingsAnalyseAndroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private Button LoginBtn = null;
    private TextView user,passwd,host;
    private String get_data = "";
    public ApiActivity api = new ApiActivity();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginactivity);
        Objects.requireNonNull(getSupportActionBar()).hide();// 隐藏ActionBar


        LoginBtn = findViewById(R.id.login_btn);
        user = findViewById(R.id.username);
        passwd = findViewById(R.id.password);
        host = findViewById(R.id.host);

        LoginBtn.setOnClickListener(view -> {
            Toast.makeText(this, user.getText()+"\n"+passwd.getText()+"\n"+host.getText(), Toast.LENGTH_SHORT).show();
            if(api.request(host.toString(),1,"2") != ""){
                Intent changeToMain = new Intent(this,MainActivity.class);
                startActivity(changeToMain);
            }
        });
    }


}

