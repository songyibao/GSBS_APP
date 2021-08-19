package com.songyb.bs.functions;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.alibaba.fastjson.JSONArray;
import com.songyb.bs.classes.exam;

import com.songyb.bs.utils.Utils;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Handler;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ExamCollecter implements Serializable {
    private int status_code = 0;
    private Handler handler;
    private final Context context;
    private final String num;
    private final String pass;
    private String exam_srting;
    private List<exam> exam = new ArrayList<>();
    private List<Map<String, String>> exam_zh = new ArrayList<>();

    public ExamCollecter(String num, String pass, Context context) {
        this.num = num;
        this.pass = pass;
        this.context = context;
    }
    public void search(Context context) {
        String url = "https://api.songyb.xyz/utils/get_grade_by_xh_mm.php?num=" + num + "&pass=" + pass + "&flag=exam";
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
                    exam_srting = Objects.requireNonNull(response.body()).string();
                    Map<String,String> exam_info_string = new HashMap();
                    exam_info_string.put("String",exam_srting);
                    Utils.setStorage(context, "ExamInfo",exam_info_string);
                    exam = JSONArray.parseArray(exam_srting, com.songyb.bs.classes.exam.class);
                    Collections.reverse(exam);
                    exam_zh.clear();
                    for (com.songyb.bs.classes.exam exam_item : exam) {
                        Map<String, String> map_item = new HashMap<>();
                        map_item.put("课程名称", exam_item.getName());
                        map_item.put("考试时间", exam_item.getTime());
                        map_item.put("考试地点", exam_item.getPlace());
                        exam_zh.add(map_item);
                    }
                    status_code=1;
                }
            }
        });
    }
    public void initData(Context context){
        String this_exam_string = Utils.getStorage(context,"ExamInfo","String");
        if(this_exam_string != null){
            exam = JSONArray.parseArray(this_exam_string, com.songyb.bs.classes.exam.class);
            Collections.reverse(exam);
            exam_zh.clear();
            for (com.songyb.bs.classes.exam exam_item : exam) {
                Map<String, String> map_item = new HashMap<>();
                map_item.put("课程名称", exam_item.getName());
                map_item.put("考试时间", exam_item.getTime());
                map_item.put("考试地点", exam_item.getPlace());
                exam_zh.add(map_item);
            }
            status_code=1;
        }else{
            search(context);
        }
    }
    public List<Map<String, String>> getExamMapByYearAndTerm(String year,int term) {
        List<Map<String, String>> list = new ArrayList<>();
        for (com.songyb.bs.classes.exam exam_item : exam) {
            if(exam_item.getYear().equals(year) && exam_item.getTerm() == term){
                Map<String, String> map_item = new HashMap<>();
                map_item.put("课程名称", exam_item.getName());
                map_item.put("考试时间", exam_item.getTime());
                map_item.put("考试地点", exam_item.getPlace());
                list.add(map_item);
            }
        }
        return list;
    }

    public List<com.songyb.bs.classes.exam> getExam() {
        return exam;
    }

    public List<Map<String, String>> getExamMap() {
        return exam_zh;
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
        date_list.add(String.valueOf(exam.get(0).getYear()).concat(String.valueOf(exam.get(0).getTerm())));
        for(exam x:exam){
            if(!date_list.contains(String.valueOf(x.getYear()).concat(String.valueOf(x.getTerm())))){
                date_list.add(String.valueOf(x.getYear()).concat(String.valueOf(x.getTerm())));
            }
        }
        for(String x:date_list){
            Map<String,String> tmp = new HashMap<>();
            tmp.put("year",x.substring(0,9));
            tmp.put("term",x.substring(9));
            date_list_final.add(tmp);
        }
        return date_list_final;
    }

    public String getNum() {
        return num;
    }

    public String getPass() {
        return pass;
    }
}
