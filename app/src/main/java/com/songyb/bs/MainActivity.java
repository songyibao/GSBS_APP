package com.songyb.bs;



import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Outline;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.songyb.bs.classes.Func;
import com.songyb.bs.classes.Userinfo;
import com.songyb.bs.detail.GradeDetailActivity;
import com.songyb.bs.detail.TableDetailActivity;
import com.songyb.bs.login.LoginActivity;
import com.songyb.bs.utils.HttpGet;
import com.songyb.bs.utils.MyImageView;
import com.songyb.bs.utils.Utils;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.loader.ImageLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private Banner mBanner;
    private ArrayList<Func> funcs = new ArrayList<>();
    private ListView list_view;
    private Mybaseadapter list_item;
    private MyImageView login_btn;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
//        Toast.makeText(MainActivity.this,String.valueOf(width)+String.valueOf(height), Toast.LENGTH_SHORT).show();
//        Toast.makeText(MainActivity.this,"username:"+Utils.getStorage(MainActivity.this,"AcountInfo","username")+",password:"+Utils.getStorage(MainActivity.this,"AcountInfo","password"),Toast.LENGTH_SHORT).show();
        initListView();
        initSwiper();
        initListener();
    }
    public void initListener(){
        //登录按钮点击事件
        login_btn = (MyImageView) findViewById(R.id.login_button);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Utils.getStorage(MainActivity.this,"AcountInfo","username")!= null && Utils.getStorage(MainActivity.this,"AcountInfo","password")!= null){
                    Toast.makeText(MainActivity.this,"您已登录",Toast.LENGTH_SHORT).show();
                }else{
                    // 给bnt1添加点击响应事件
                    Intent intent =new Intent(MainActivity.this, LoginActivity.class);
                    //启动
                    startActivity(intent);
                }
            }
        });
        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Func func_item = funcs.get(position);
//                Toast.makeText(MainActivity.this,func_item.getName(),Toast.LENGTH_SHORT).show();
                if(func_item.getName().equals("成绩查询")){
                    if(Utils.getStorage(MainActivity.this,"AcountInfo","username")==null || Utils.getStorage(MainActivity.this,"AcountInfo","password")==null){
                        Toast.makeText(MainActivity.this,"请先登录",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this,LoginActivity.class));
                    }else{
                        startActivity(new Intent(MainActivity.this, GradeDetailActivity.class));
                    }
                }else if(func_item.getName().equals("课表查询")){
                    if(Utils.getStorage(MainActivity.this,"AcountInfo","username")==null || Utils.getStorage(MainActivity.this,"AcountInfo","password")==null){
                        Toast.makeText(MainActivity.this,"请先登录",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this,LoginActivity.class));
                    }else{
                        startActivity(new Intent(MainActivity.this, TableDetailActivity.class));
                    }
                }
            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void initSwiper(){
        mBanner = findViewById(R.id.mBanner);
        //图片资源
        int[] imageUrls = new int[]{R.drawable.library,R.drawable.flower,R.drawable.science};
        List<Integer> imageList = new ArrayList<>();
        for(int i =0;i<imageUrls.length;i++){
            imageList.add(imageUrls[i]);
        }
        //设置图片加载其，通过Glide加载图片
        mBanner.setImageLoader(new ImageLoader() {
            @Override
            public void displayImage(Context context, Object path, ImageView imageView) {
                Glide.with(MainActivity.this).load(path).into(imageView);
            }
        });
        //设置轮播的动画效果，里面有很多种特效，可以到GitHub上查看文档
        //设置圆角
        mBanner.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), 30);
            }
        });

        mBanner.setClipToOutline(true);

        mBanner.setBannerAnimation(Transformer.Accordion);
        mBanner.setImages(imageList);
        mBanner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);//设置banner显示样式
        mBanner.setIndicatorGravity(BannerConfig.CENTER);//设置指示器位置
        mBanner.setDelayTime(2000);//设置轮播切换时间
//        mBanner.setOnBannerListener((OnBannerListener) this);//设置监听
        mBanner.start();//开始进行Banner渲染
    }
    private void initListView() {
        list_view = (ListView) findViewById(R.id.lv_holder);
        list_item = new Mybaseadapter();
        list_view.setAdapter(list_item);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("https://api.songyb.xyz/title_api/get_all_titles.php").build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    String str = response.body().string();
                    List<Func> new_funcs = JSONArray.parseArray(str,Func.class);
                    for(int i=0;i<new_funcs.size();i++){
                        if(new_funcs.get(i).getTag().equals("function") && new_funcs.get(i).getAvailable().equals("yes")){
                            funcs.add(new_funcs.get(i));
                        }
                    }
                    handler.sendEmptyMessageDelayed(1,100);
                }
            }
        });
    }
    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    list_item.notifyDataSetChanged();
                    break;
                case 2:
                    login_btn.setMyUrl((String) msg.obj);
                    break;
            }
        }
    };
    public class Mybaseadapter extends BaseAdapter {

        @Override
        public int getCount() {
            return funcs.size();
        }

        @Override
        public Object getItem(int position) {
            return funcs.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = new ViewHolder();

            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.listview_main,null);
                viewHolder.func_name = (TextView) convertView.findViewById(R.id.func_name);
                viewHolder.func_icon = (MyImageView) convertView.findViewById(R.id.func_icon);

                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.func_name.setText(funcs.get(position).getName());
            viewHolder.func_icon.setMyUrl(funcs.get(position).getIcon());

            return convertView;
        }
    }
    final static class ViewHolder {
        MyImageView func_icon;
        TextView func_name;
    }
    @Override
    protected void onStart() {
        super.onStart();
        mBanner.startAutoPlay();
        if(Utils.isLogin(MainActivity.this)){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    HttpGet get_avatar = new HttpGet();
                    String userinfo = "";
                    try {
                        userinfo=get_avatar.run("https://api.songyb.xyz/user_api/get_userinfo_by_num.php?num="+Utils.getStorage(MainActivity.this,"AcountInfo","username"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Userinfo info = JSONObject.parseObject(userinfo,Userinfo.class);
                    Message msg = handler.obtainMessage();
                    msg.what = 2;
                    msg.obj = info.getAvatar();
                    handler.sendMessage(msg);
                }
            }).start();

        }else{
            Toast.makeText(MainActivity.this,"未登录",Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onStop(){
        super.onStop();
        mBanner.stopAutoPlay();
    }

}