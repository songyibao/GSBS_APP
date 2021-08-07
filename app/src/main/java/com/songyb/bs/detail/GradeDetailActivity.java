package com.songyb.bs.detail;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSONArray;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.songyb.bs.MainActivity;
import com.songyb.bs.R;
import com.songyb.bs.classes.Func;
import com.songyb.bs.classes.grade;
import com.songyb.bs.functions.GradeCollecter;
import com.songyb.bs.utils.HttpGet;
import com.songyb.bs.utils.MyImageView;
import com.songyb.bs.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GradeDetailActivity extends AppCompatActivity {
    private TextView now_year;
    private TextView now_item;
    private GradeCollecter collecter;
    private ProgressBar list_loading;
    private ListView list_view;
    private ListView date_view;
    private List<grade> list = new ArrayList<>();
    private List<Map<String,String>> list_map = new ArrayList<>();
    private List<Map<String,String>> date_map = new ArrayList<>();
    private SimpleAdapter list_item;
    private SimpleAdapter date_item;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        list_loading = findViewById(R.id.list_loading);
        list_view = (ListView) findViewById(R.id.lv_detail);
        date_view = (ListView) findViewById(R.id.date_view);
        list_loading.setVisibility(View.VISIBLE);
        list_view.setVisibility(View.GONE);
        collecter = new GradeCollecter(Utils.getStorage(GradeDetailActivity.this, "AcountInfo", "username"),Utils.getStorage(GradeDetailActivity.this, "AcountInfo", "password"),GradeDetailActivity.this);
        collecter.search();
        Map<String,String> map_item = new HashMap<>();
        map_item.put("课程名称","课程名称");
        map_item.put("学分","学分");
        map_item.put("成绩","成绩");
        map_item.put("绩点","绩点");
        list_map.add(map_item);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                    handler.sendEmptyMessage(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void initDateView(){
        date_item = new SimpleAdapter(
                this,
                date_map,
                R.layout.listview_date,
                new String[] {"year","term"},
                new int[] {R.id.year,R.id.term}
        );
        date_view.setAdapter(date_item);

        date_map.addAll(collecter.getDate_list());
        handler.sendEmptyMessage(2);
    }
    private void initListView() {
        list_item = new SimpleAdapter(
                this,
                list_map,
                R.layout.listview_grade,
                new String[]{"课程名称","学分","成绩","绩点"},
                new int[] {R.id.name,R.id.credit,R.id.score,R.id.gradeScore}
        );
        list_view.setAdapter(list_item);

        list_map.addAll(collecter.getGradeMap());
        handler.sendEmptyMessage(1);
    }
    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler() {

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    initDateView();
                    initListView();
                    break;
                case 1:
                    list_loading.setVisibility(View.GONE);
                    list_view.setVisibility(View.VISIBLE);
                    list_item.notifyDataSetChanged();
                    break;
                case 2:
                    date_view.setVisibility(View.VISIBLE);
                    date_item.notifyDataSetChanged();
                    break;
            }
        }
    };
}
