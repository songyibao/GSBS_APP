package com.songyb.bs.detail;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.flexbox.FlexboxLayout;
import com.songyb.bs.R;
import com.songyb.bs.classes.table;
import com.songyb.bs.functions.TableCollecter;
import com.songyb.bs.utils.Utils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class TableDetailActivity extends AppCompatActivity {
    private final int DATA_OK = 0;
    private FlexboxLayout top;
    private FlexboxLayout left;
    private FlexboxLayout right;
    private CardView corner;
    private ListView left_list;
    private ListView right_list;
    private TableCollecter collecter;
    private int width;
    private int height;
    private double content_height;
    private double content_width;
    private double left_length;
    private double top_length;
    private double row_height;
    private double column_width;
    private final String[] color_arrays = new String[]{"#85B8CF", "#90C652", "#D8AA5A", "#FC9F9D", "#0A9A84", "#61BC69", "#12AEF3", "#E29AAD"};
    @SuppressLint({"UseCompatLoadingForDrawables", "RtlHardcoded"})
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.table_detail);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setWidthAndHeight();
        matchView();
        initData();
        Toast.makeText(this,String.valueOf(height)+"x"+String.valueOf(width),Toast.LENGTH_LONG).show();
    }
    public void matchView(){
        top = (FlexboxLayout) findViewById(R.id.top);
        left =(FlexboxLayout) findViewById(R.id.left);
        right =(FlexboxLayout) findViewById(R.id.right);
        corner = (CardView) findViewById(R.id.corner);
        ViewGroup.LayoutParams params = corner.getLayoutParams();
        params.width = (int) Math.round(left_length);
        corner.setLayoutParams(params);
//        right_list = findViewById(R.id.right_list);
    }
    public void initData(){
        collecter = new TableCollecter(Utils.getStorage(TableDetailActivity.this, "AcountInfo", "username"),Utils.getStorage(TableDetailActivity.this, "AcountInfo", "password"),TableDetailActivity.this);
        collecter.search();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(!collecter.isDataOk()){}
                handler.sendEmptyMessage(DATA_OK);
            }
        }).start();
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
        content_height = height*0.92;
        content_width = width*0.9;
        left_length = width*0.1;
        top_length = height*0.08;
        row_height = content_height/12;
        column_width = content_width/7;
    }
    public void setWeek(){
        String day = collecter.getToday().get("day");
        CardView day_card =null;
        switch (Objects.requireNonNull(day)){
            case "周一":
                day_card = (CardView) findViewById(R.id.mon);
                day_card.setCardBackgroundColor(Color.parseColor("#E6EDF3"));
                break;
            case "周二":
                day_card = (CardView) findViewById(R.id.tue);
                day_card.setCardBackgroundColor(Color.parseColor("#E6EDF3"));
                break;
            case "周三":
                day_card = (CardView) findViewById(R.id.wed);
                day_card.setCardBackgroundColor(Color.parseColor("#E6EDF3"));
                break;
            case "周四":
                day_card = (CardView) findViewById(R.id.thu);
                day_card.setCardBackgroundColor(Color.parseColor("#E6EDF3"));
                break;
            case "周五":
                day_card = (CardView) findViewById(R.id.fri);
                day_card.setCardBackgroundColor(Color.parseColor("#E6EDF3"));
                break;
            case "周六":
                day_card = (CardView) findViewById(R.id.sat);
                day_card.setCardBackgroundColor(Color.parseColor("#E6EDF3"));
                break;
            case "周日":
                day_card = (CardView) findViewById(R.id.sun);
                day_card.setCardBackgroundColor(Color.parseColor("#E6EDF3"));
                break;
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void addAllClass(){
        List<table> table_other_week = collecter.getTable().stream().filter(s->!s.isIs_now_week()).collect(Collectors.toList());
        List<table> table_now_week = collecter.getTable().stream().filter(table::isIs_now_week).collect(Collectors.toList());
        table x;
        int i;
        for(i=0;i<table_other_week.size();i++){
            x = table_other_week.get(i);
            addClass(x.getName(),x.getDate(),x.getOrder(),x.getLength(),x.isIs_now_week(),color_arrays[i%8]);
        }
        int j;
        for(j=0;j<table_now_week.size();j++){
            x = table_now_week.get(j);
            addClass(x.getName(),x.getDate(),x.getOrder(),x.getLength(),x.isIs_now_week(),color_arrays[i++%8]);
        }



    }
    @SuppressLint({"RtlHardcoded", "SetTextI18n"})
    public void addClass(String name, int date, int order, int length, boolean is_now_week,String color){
        FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        TextView text = new TextView(this);
        text.setGravity(Gravity.CENTER);

        if(!is_now_week){
            text.setText("[非本周]"+name);
            text.setTextColor(Color.parseColor("#BBC9D6"));
            text.setBackgroundColor(Color.parseColor("#E9EEF4"));
        }else{
            text.setText(name);
            text.setBackgroundColor(Color.parseColor(color));
            text.setTextColor(Color.parseColor("#FFFFFF"));
        }

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity=Gravity.TOP|Gravity.LEFT;
        params.leftMargin = (int)Math.round(left_length+column_width*(date-1));
        params.topMargin = (int)Math.round(top_length+row_height*(order-1));
        params.height = (int)Math.round(length*row_height);
        params.width =(int)Math.round(column_width);
        CardView holder=new CardView(this);
        holder.setContentPadding(7,7,7,7);
        holder.setBackgroundColor(Color.TRANSPARENT);

        FrameLayout.LayoutParams params3 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        CardView middle_holder = new CardView(this);
        middle_holder.setRadius(20);


        middle_holder.addView(text,0,params2);
        holder.addView(middle_holder,0,params3);
        addContentView(holder, params);
    }
    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler(){
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DATA_OK:
                    setWeek();
                    addAllClass();
                    break;
            }
        }
    };

}