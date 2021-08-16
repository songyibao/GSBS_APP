package com.songyb.bs.functions;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.alibaba.fastjson.JSONArray;
import com.songyb.bs.classes.grade;
import com.songyb.bs.classes.table;
import com.songyb.bs.detail.GradeDetailActivity;
import com.songyb.bs.utils.Utils;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Handler;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TableCollecter {
    private Map<String,String> today = null;
    private List<Map<String,String>> date_map;
    private int status_code = 0;
    private Handler handler;
    private final Context context;
    private ProgressDialog progressDialog;
    private final String num;
    private final String pass;
    private String table_srting;
    private List<com.songyb.bs.classes.table> table = new ArrayList<>();

    public TableCollecter(String num, String pass, Context context) {
        this.num = num;
        this.pass = pass;
        this.context = context;
    }
    public TableCollecter(String num, String pass, Context context, Handler handler) {
        this.num = num;
        this.pass = pass;
        this.context = context;
        this.handler = handler;
    }
    public void search() {
        String url = "https://api.songyb.xyz/utils/get_grade_by_xh_mm.php?num=" + num + "&pass=" + pass + "&flag=table";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    table_srting = Objects.requireNonNull(response.body()).string();
                    table = JSONArray.parseArray(table_srting, com.songyb.bs.classes.table.class);
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder().url("https://api.songyb.xyz/utils/set_term_start.php").build();
                    Call call2 = client.newCall(request);
                    String response2 = null;
                    try {
                        response2 = Objects.requireNonNull(call2.execute().body()).string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        today = getTodayInfo(response2);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    for(int i=0;i<table.size();i++){
                        assert today != null;
                        table.get(i).setIs_now_week(is_now_week(Integer.parseInt(Objects.requireNonNull(today.get("week"))), table.get(i).getWeek()));
                    }
                    status_code=1;
                }
            }
        });
    }

    public void changeNowWeek(int week){
        for(int i=0;i<table.size();i++){
            table.get(i).setIs_now_week(is_now_week(week, table.get(i).getWeek()));
        }
        today.put("week",String.valueOf(week));
    }
    public List<com.songyb.bs.classes.table> getTable() {
        return table;
    }
    @SuppressLint("SimpleDateFormat")
    public Map<String,String> getTodayInfo(String start) throws ParseException {
        int week_len = 7;
        String [] weekdays = new String[] {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        Map<String,String> week_info = new HashMap<>();
        int oneday = 24*60*60*1000;
        int week_leave;
        int week_start;
        Date today =null;
        long date_diff;
        Date s_date = null;
        try{
            s_date=new SimpleDateFormat("yyyy/MM/dd").parse(start);
        }catch(ParseException e){
            e.printStackTrace();
        }
        assert s_date != null;
        week_start = s_date.getDay();
        week_start = week_start==0?7:week_start;
        week_leave = week_len - week_start;
        today = new Date();
        week_info.put("day",weekdays[today.getDay()]);
        date_diff = today.getTime()-s_date.getTime();
        if(date_diff<0){
            week_info.put("week","0");
            return week_info;
        }
        date_diff = (int)(date_diff/oneday);
        int tmp = (int)Math.ceil((double)(date_diff-week_leave)/week_len)+1;
        week_info.put("week",String.valueOf(tmp));
        return week_info;
    }
    public Map<String,String> getToday(){
        return today;
    }
    public boolean is_now_week(int now_week,String week_info){
        String str = "-"+String.valueOf(now_week)+"-";
        return week_info.contains(str);
    }
    public boolean isDataOk() { return status_code==1;}
}
