package com.songyb.bs.detail;

import android.annotation.SuppressLint;

import android.graphics.Color;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.util.DisplayMetrics;

import android.view.Gravity;

import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.flexbox.FlexboxLayout;

import com.songyb.bs.R;
import com.songyb.bs.functions.ExamCollecter;
import com.songyb.bs.utils.Utils;


import java.io.Serializable;
import java.util.ArrayList;

import java.util.List;
import java.util.Map;
import java.util.Objects;



public class ExamDetailActivity extends AppCompatActivity implements Serializable {
    private static final int REFRESH_OK = 4;
    private static final int EXAM_DETAIL_OK = 3;
//    private String grade_detail;
//    private TextView grade_detail_view;
    private FlexboxLayout date_view_con;
    private FlexboxLayout title_con;
    private FlexboxLayout header_container;
    private TextView now_year;
    private TextView now_term;
    private ExamCollecter collecter;
    private FlexboxLayout list_loading;
    private ListView list_view;
    private ListView date_view;
    private List<Map<String, String>> list_map = new ArrayList<>();
    private List<Map<String, String>> date_map = new ArrayList<>();
    private SimpleAdapter list_item;
    private SimpleAdapter date_item;

    private int width;
    private int height;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exam_detail);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setWidthAndHeight();
        matchView();
        initData();
    }
    public void matchView(){
        list_loading = findViewById(R.id.list_loading);
        list_view = findViewById(R.id.lv_detail);
        date_view = findViewById(R.id.date_view);
        now_year = findViewById(R.id.now_year);
        now_term = findViewById(R.id.now_term);
        date_view_con = findViewById(R.id.date_view_con);
        title_con = findViewById(R.id.title_con);
        header_container = findViewById(R.id.header_container);
        list_loading.setVisibility(View.VISIBLE);
        list_view.setVisibility(View.GONE);
    }
    public void setWidthAndHeight(){
        WindowManager wm = getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        int screenWidth = outMetrics.widthPixels;
        int screenHeight = outMetrics.heightPixels;
        int statusBarHeight = getResources().getDimensionPixelSize(getResources().getIdentifier("status_bar_height", "dimen", "android"));
        width = screenWidth;
        height = screenHeight - statusBarHeight;
    }
    public void initData(){
        collecter = new ExamCollecter(Utils.getStorage(ExamDetailActivity.this, "AccountInfo", "username"), Utils.getStorage(ExamDetailActivity.this, "AccountInfo", "password"), ExamDetailActivity.this);
        collecter.initData(ExamDetailActivity.this);
        new Thread(() -> {
            while (collecter.getStatus_code() != 1) {
            }
            handler.sendEmptyMessage(0);
        }).start();
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @SuppressLint("RtlHardcoded")
    public void showRefreshButton(){
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.leftMargin = (int) Math.round(width*0.86);
        params.topMargin = (int) Math.round(height*0.90);
        params.height = 150;
        params.width = 150;
        CardView button = new CardView(ExamDetailActivity.this);
        button.setCardBackgroundColor(Color.parseColor("#ffffff"));
        button.setRadius(75);
        button.setOnClickListener(v -> {
            list_map.clear();
            date_map.clear();
            list_item.notifyDataSetChanged();
            date_item.notifyDataSetChanged();
            Toast.makeText(ExamDetailActivity.this,"正在查询，请稍后",Toast.LENGTH_SHORT).show();
            collecter.search(ExamDetailActivity.this);
            collecter.clearStatus_code();
            new Thread(() -> {
                while (collecter.getStatus_code() != 1) {
                }
                handler.sendEmptyMessage(REFRESH_OK);
            }).start();
        });
        ImageView image = new ImageView(ExamDetailActivity.this);
        image.setImageResource(R.drawable.refresh);
        LinearLayout.LayoutParams p2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        image.setLayoutParams(p2);
        button.addView(image);
        addContentView(button,params);
    }
    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void initDateView() {
        date_item = new SimpleAdapter(
                this,
                date_map,
                R.layout.listview_date,
                new String[]{"year", "term"},
                new int[]{R.id.year, R.id.term}
        );
        date_view.setAdapter(date_item);
        date_view.setOnItemClickListener((parent, view, position, id) -> {
            header_container.setVisibility(View.VISIBLE);
            title_con.setVisibility(View.VISIBLE);
            list_view.setVisibility(View.VISIBLE);
            date_view_con.setVisibility(View.GONE);
            Map<String, String> map;
            map = date_map.get(position);
            now_year.setText(map.get("year") + "学年");
            now_term.setText("第" + map.get("term") + "学期");
            list_map.clear();
            list_map.addAll(collecter.getExamMapByYearAndTerm(String.valueOf(map.get("year")), Integer.parseInt(Objects.requireNonNull(map.get("term")))));
            list_item.notifyDataSetChanged();
        });
        header_container.setOnClickListener(v -> {
            if (date_view_con.getVisibility() == View.VISIBLE) {
                date_view_con.setVisibility(View.GONE);
            } else {
                header_container.setVisibility(View.GONE);
                title_con.setVisibility(View.GONE);
                list_view.setVisibility(View.GONE);
                date_view_con.setVisibility(View.VISIBLE);
            }
        });
        setDateView();
    }
    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void setDateView(){
        date_map.clear();
        date_map.addAll(collecter.getDate_list());
        now_year.setText(date_map.get(date_map.size() - 1).get("year") + "学年");
        now_term.setText("第" + date_map.get(date_map.size() - 1).get("term") + "学期");
        handler.sendEmptyMessage(2);
    }

    private void initListView() {
        list_item = new SimpleAdapter(
                this,
                list_map,
                R.layout.listview_exam,
                new String[]{"课程名称", "考试时间", "考试地点"},
                new int[]{R.id.name, R.id.time, R.id.place}
        );
        list_view.setAdapter(list_item);
        setListView();
        handler.sendEmptyMessage(1);
    }
    public void setListView(){
        Map<String, String> map;
        map = date_map.get(date_map.size() - 1);
        list_map.clear();
        list_map.addAll(collecter.getExamMapByYearAndTerm(String.valueOf(map.get("year")), Integer.parseInt(Objects.requireNonNull(map.get("term")))));
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
                    showRefreshButton();
                    break;
                case 1:
                    list_loading.setVisibility(View.GONE);
                    list_view.setVisibility(View.VISIBLE);
                    title_con.setVisibility(View.VISIBLE);
                    list_item.notifyDataSetChanged();
                    break;
                case 2:
//                    date_view_con.setVisibility(View.VISIBLE);
                    header_container.setVisibility(View.VISIBLE);
                    date_item.notifyDataSetChanged();
                    break;
                case REFRESH_OK:
                    setDateView();
                    setListView();
                    list_item.notifyDataSetChanged();
                    date_item.notifyDataSetChanged();
                    Toast.makeText(ExamDetailActivity.this,"已刷新",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

}
