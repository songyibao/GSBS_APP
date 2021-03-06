package com.songyb.bs;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;

import android.content.Context;
import android.content.Intent;
import android.graphics.Outline;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;


import android.widget.BaseAdapter;

import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;

import com.songyb.bs.classes.Func;
import com.songyb.bs.classes.Userinfo;
import com.songyb.bs.detail.ExamDetailActivity;
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
import java.util.Objects;

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

        initListView();
        initSwiper();
        initListener();
    }

    public void initListener() {
        //????????????????????????
        login_btn = findViewById(R.id.login_button);
        login_btn.setOnClickListener(v -> {
            if (Utils.getStorage(MainActivity.this, "AccountInfo", "username") != null && Utils.getStorage(MainActivity.this, "AccountInfo", "password") != null) {
                Toast.makeText(MainActivity.this, "????????????", Toast.LENGTH_SHORT).show();
            } else {
                // ???bnt1????????????????????????
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                //??????
                startActivity(intent);
            }
        });
        list_view.setOnItemClickListener((parent, view, position, id) -> {
            Func func_item = funcs.get(position);
//                Toast.makeText(MainActivity.this,func_item.getName(),Toast.LENGTH_SHORT).show();
            if (Utils.getStorage(MainActivity.this, "AccountInfo", "username") == null || Utils.getStorage(MainActivity.this, "AccountInfo", "password") == null) {
                Toast.makeText(MainActivity.this, "????????????", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            } else {
                switch (func_item.getName()) {
                    case "????????????":
                        startActivity(new Intent(MainActivity.this, GradeDetailActivity.class));
                        break;
                    case "????????????":
                        startActivity(new Intent(MainActivity.this, TableDetailActivity.class));
                        break;
                    case "????????????":
                        startActivity(new Intent(MainActivity.this, ExamDetailActivity.class));
                        break;
                }
            }

        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void initSwiper() {
        mBanner = findViewById(R.id.mBanner);
        //????????????
        int[] imageUrls = new int[]{R.drawable.library, R.drawable.flower, R.drawable.science};
        List<Integer> imageList = new ArrayList<>();
        for (int imageUrl : imageUrls) {
            imageList.add(imageUrl);
        }
        //??????????????????????????????Glide????????????
        mBanner.setImageLoader(new ImageLoader() {
            @Override
            public void displayImage(Context context, Object path, ImageView imageView) {
                Glide.with(MainActivity.this).load(path).into(imageView);
            }
        });
        //??????????????????????????????????????????????????????????????????GitHub???????????????
        //????????????
        mBanner.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), 30);
            }
        });

        mBanner.setClipToOutline(true);

        mBanner.setBannerAnimation(Transformer.Accordion);
        mBanner.setImages(imageList);
        mBanner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);//??????banner????????????
        mBanner.setIndicatorGravity(BannerConfig.CENTER);//?????????????????????
        mBanner.setDelayTime(2000);//????????????????????????
//        mBanner.setOnBannerListener((OnBannerListener) this);//????????????
        mBanner.start();//????????????Banner??????
    }

    private void initListView() {
        list_view = findViewById(R.id.lv_holder);
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
                if (response.isSuccessful()) {
                    String str = Objects.requireNonNull(response.body()).string();
                    List<Func> new_funcs = JSONArray.parseArray(str, Func.class);
                    for (int i = 0; i < new_funcs.size(); i++) {
                        if (new_funcs.get(i).getTag().equals("function") && new_funcs.get(i).getAvailable().equals("yes")) {
                            funcs.add(new_funcs.get(i));
                        }
                    }
                    handler.sendEmptyMessageDelayed(1, 100);
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
                convertView = getLayoutInflater().inflate(R.layout.listview_main, null);
                viewHolder.func_name = convertView.findViewById(R.id.func_name);
                viewHolder.func_icon = convertView.findViewById(R.id.func_icon);

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
        if (Utils.isLogin(MainActivity.this)) {
            new Thread(() -> {
                HttpGet get_avatar = new HttpGet();
                String userinfo = "";
                try {
                    userinfo = get_avatar.run("https://api.songyb.xyz/user_api/get_userinfo_by_num.php?num=" + Utils.getStorage(MainActivity.this, "AccountInfo", "username"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Userinfo info = JSONObject.parseObject(userinfo, Userinfo.class);
                Message msg = handler.obtainMessage();
                msg.what = 2;
                msg.obj = info.getAvatar();
                handler.sendMessage(msg);
            }).start();

        } else {
            Toast.makeText(MainActivity.this, "?????????", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mBanner.stopAutoPlay();
    }

}