package com.songyb.bs.functions;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.alibaba.fastjson.JSONArray;
import com.songyb.bs.classes.grade;
import com.songyb.bs.detail.GradeDetailActivity;
import com.songyb.bs.utils.Utils;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
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

public class GradeCollecter implements Serializable {
    private List<Map<String,String>> date_map;
    private int status_code = 0;
    private Handler handler;
    private final Context context;
    private ProgressDialog progressDialog;
    private final String num;
    private final String pass;
    private String grade_srting;
    private List<grade> grade = new ArrayList<>();
    private List<Map<String, String>> grade_zh = new ArrayList<>();

    public GradeCollecter(String num, String pass, Context context) {
        this.num = num;
        this.pass = pass;
        this.context = context;
    }
    public GradeCollecter(String num, String pass, Context context, Handler handler) {
        this.num = num;
        this.pass = pass;
        this.context = context;
        this.handler = handler;
    }
    public void search(Context context) {
        String url = "https://api.songyb.xyz/utils/get_grade_by_xh_mm.php?num=" + num + "&pass=" + pass + "&flag=grade";
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
                    grade_srting = response.body().string();
                    Map<String,String> grade_info_string = new HashMap();
                    grade_info_string.put("String",grade_srting);
                    Utils.setStorage(context, "GradeInfo",grade_info_string);
                    grade = JSONArray.parseArray(grade_srting, com.songyb.bs.classes.grade.class);
//                    Collections.reverse(grade);
                    grade_zh.clear();
                    for (com.songyb.bs.classes.grade grade_item : grade) {
                        Map<String, String> map_item = new HashMap<>();
                        map_item.put("课程名称", grade_item.getName());
                        map_item.put("学分", grade_item.getCredit());
                        map_item.put("成绩", grade_item.getScore());
                        map_item.put("绩点", grade_item.getGradeScore());
                        grade_zh.add(map_item);
                    }
                    status_code=1;
                }
            }
        });
    }
    public void initData(Context context){
        String this_grade_string = Utils.getStorage(context,"GradeInfo","String");
        if(this_grade_string != null){
            grade = JSONArray.parseArray(this_grade_string, com.songyb.bs.classes.grade.class);
//                    Collections.reverse(grade);
            grade_zh.clear();
            for (com.songyb.bs.classes.grade grade_item : grade) {
                Map<String, String> map_item = new HashMap<>();
                map_item.put("课程名称", grade_item.getName());
                map_item.put("学分", grade_item.getCredit());
                map_item.put("成绩", grade_item.getScore());
                map_item.put("绩点", grade_item.getGradeScore());
                grade_zh.add(map_item);
            }
            status_code=1;
        }else{
            search(context);
        }
    }
    public List<Map<String, String>> getGradeMapByYearAndTerm(int year,int term) {
        List<Map<String, String>> list = new ArrayList<>();
        for (com.songyb.bs.classes.grade grade_item : grade) {
            if(grade_item.getYear() == year && grade_item.getTerm() == term){
                Map<String, String> map_item = new HashMap<>();
                map_item.put("课程名称", grade_item.getName());
                map_item.put("学分", grade_item.getCredit());
                map_item.put("成绩", grade_item.getScore());
                map_item.put("绩点", grade_item.getGradeScore());
                list.add(map_item);
            }
        }
        return list;
    }

    public List<com.songyb.bs.classes.grade> getGrade() {
        return grade;
    }

    public List<Map<String, String>> getGradeMap() {
        return grade_zh;
    }
    public int getStatus_code(){
        return status_code;
    }
    public void clearStatus_code() {
        status_code = 0;
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public List<Map<String,String>> getDate_list(){
        List<String> date_list = new ArrayList<>();
        List<Map<String,String>> date_list_final = new ArrayList<>();
        date_list.add(String.valueOf(grade.get(0).getYear()).concat(String.valueOf(grade.get(0).getTerm())));
        for(grade x:grade){
            if(!date_list.contains(String.valueOf(x.getYear()).concat(String.valueOf(x.getTerm())))){
                date_list.add(String.valueOf(x.getYear()).concat(String.valueOf(x.getTerm())));
            }
        }
        for(String x:date_list){
            Map<String,String> tmp = new HashMap<>();
            tmp.put("year",x.substring(0,4));
            tmp.put("term",x.substring(4));
            date_list_final.add(tmp);
        }
        return date_list_final;
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public int getBiggestTermByYear(int year){
        List<Map<String,String>> date_list_final = new ArrayList<>();
        int tmp = 0;
        date_list_final = getDate_list();
        for(Map<String,String> it:date_list_final){
            if(Objects.equals(it.get("year"), String.valueOf(year))){
                int x = Integer.parseInt(Objects.requireNonNull(it.get("term")));
                if(x>tmp){
                    tmp=x;
                }
            }
        }
        return tmp;
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public int getBottomYear(){
        List<Map<String,String>> date_list_final = new ArrayList<>();
        int tmp = 3000;
        date_list_final = getDate_list();
        for(Map<String,String> it:date_list_final){
            int x = Integer.parseInt(Objects.requireNonNull(it.get("year")));
            if(x<tmp){
                tmp = x;
            }
        }
        return tmp;
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public int getTopYear(){
        List<Map<String,String>> date_list_final = new ArrayList<>();
        int tmp = 0;
        date_list_final = getDate_list();
        for(Map<String,String> it:date_list_final){
            int x = Integer.parseInt(Objects.requireNonNull(it.get("year")));
            if(x>tmp){
                tmp = x;
            }
        }
        return tmp;
    }
    public String getJxb_id(String name,String year,String term){
        for(grade x:grade){
            if(x.getName().equals(name) && String.valueOf(x.getTerm()).equals(term) && String.valueOf(x.getYear()).equals(year)){
                return x.getJxb_id();
            }
        }
        return "none";
    }

    public String getNum() {
        return num;
    }

    public String getPass() {
        return pass;
    }
}
