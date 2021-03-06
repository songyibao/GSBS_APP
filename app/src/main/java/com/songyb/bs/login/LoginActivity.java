package com.songyb.bs.login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSONArray;
import com.songyb.bs.R;
import com.songyb.bs.classes.grade;
import com.songyb.bs.utils.Utils;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText account;
    private EditText password;
    private Button login_btn;
    private ProgressBar loading;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.activity_login);
        //获取用户名的id
        account = findViewById(R.id.username);
        //获取密码的id
        password = findViewById(R.id.password);
        login_btn = findViewById(R.id.login);
        loading =findViewById(R.id.loading);
        initListener();
    }

    public void initListener(){
        //监听输入变化事件
        login_btn.setOnClickListener(v -> {
            try {
                LoginClient();
            } catch (IOException e) {
                Toast.makeText(LoginActivity.this,e.toString(),Toast.LENGTH_SHORT).show();
            }
        });
        account.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(password.getText().length() != 0){
                    login_btn.setEnabled(s.length() == 10);
                }
            }
        });
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(account.getText().length() == 10){
                    login_btn.setEnabled(s.length() != 0);
                }
            }
        });

    }
    public void LoginClient() throws IOException {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(password.getWindowToken(), 0) ;
        loading.setVisibility(View.VISIBLE);
        //获取用户名的值
        String name= URLEncoder.encode(account.getText().toString(),"UTF-8");
        //获取密码的值
        String pass=URLEncoder.encode(password.getText().toString(),"UTF-8");
        //获取网络上的servlet路径
        String url="https://api.songyb.xyz/utils/get_grade_by_xh_mm.php?num="+name+"&pass="+pass+"&flag=grade";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    String result = Objects.requireNonNull(response.body()).string();
                    //处理UI需要切换到UI线程处理
                    List<grade> grade = new ArrayList<>();
                    try{
                        grade = JSONArray.parseArray(result, com.songyb.bs.classes.grade.class);
                    }catch (Exception e){
                        Looper.prepare();
                        Toast.makeText(LoginActivity.this, "登陆失败，请检查用户名和密码或者稍后重试", Toast.LENGTH_SHORT).show();
                        finish();
                        Looper.loop();
                        return;
                    }

                    if(grade.size()>0){
                        Map<String,String> userinfo = new HashMap<>();
                        userinfo.put("username",name);
                        userinfo.put("password",pass);
                        boolean status = Utils.setStorage(LoginActivity.this,"AccountInfo",userinfo);
                        setResult(status?RESULT_OK:0);
                        if(status){
                            handler.sendEmptyMessage(1);
                        }
                        finish();
                    }else{
                        Looper.prepare();
                        Toast.makeText(LoginActivity.this, "无数据", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Looper.prepare();
                Toast.makeText(LoginActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        });
    }
    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case 1:
                    loading.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this,"登陆成功",Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    };
}
