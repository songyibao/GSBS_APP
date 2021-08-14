package com.songyb.bs.detail;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.flexbox.FlexboxLayout;
import com.songyb.bs.MainActivity;
import com.songyb.bs.R;
import com.songyb.bs.functions.GradeCollecter;
import com.songyb.bs.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GradeDetailActivity extends AppCompatActivity {
    private static final String TAG = "log";
    private FlexboxLayout date_view_con;
    private FlexboxLayout title_con;
    private FlexboxLayout header_container;
    private TextView now_year;
    private TextView now_term;
    private GradeCollecter collecter;
    private FlexboxLayout list_loading;
    private ListView list_view;
    private ListView date_view;
    private List<Map<String,String>> list_map = new ArrayList<>();
    private List<Map<String,String>> date_map = new ArrayList<>();
    private SimpleAdapter list_item;
    private SimpleAdapter date_item;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grade_detail);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        list_loading = (FlexboxLayout) findViewById(R.id.list_loading);
        list_view = (ListView) findViewById(R.id.lv_detail);
        date_view = (ListView) findViewById(R.id.date_view);
        now_year = (TextView) findViewById(R.id.now_year);
        now_term = (TextView) findViewById(R.id.now_term);
        date_view_con = (FlexboxLayout) findViewById(R.id.date_view_con);
        title_con = (FlexboxLayout) findViewById(R.id.title_con);
        header_container = (FlexboxLayout) findViewById(R.id.header_container);
        list_loading.setVisibility(View.VISIBLE);
        list_view.setVisibility(View.GONE);
        collecter = new GradeCollecter(Utils.getStorage(GradeDetailActivity.this, "AcountInfo", "username"),Utils.getStorage(GradeDetailActivity.this, "AcountInfo", "password"),GradeDetailActivity.this);
        collecter.search();
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
    @SuppressLint("SetTextI18n")
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
        now_year.setText(date_map.get(date_map.size()-1).get("year")+"学年");
        now_term.setText("第"+date_map.get(date_map.size()-1).get("term")+"学期");
        date_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                header_container.setVisibility(View.VISIBLE);
                title_con.setVisibility(View.VISIBLE);
                list_view.setVisibility(View.VISIBLE);
                date_view_con.setVisibility(View.GONE);
                Map<String,String> map = new HashMap<>();
                map = date_map.get(position);
                now_year.setText(map.get("year")+"学年");
                now_term.setText("第"+map.get("term")+"学期");
                list_map.clear();
                list_map.addAll(collecter.getGradeMapByYearAndTerm(Integer.parseInt(Objects.requireNonNull(map.get("year"))),Integer.parseInt(Objects.requireNonNull(map.get("term")))));
                list_item.notifyDataSetChanged();
            }
        });
        header_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(date_view_con.getVisibility()==View.VISIBLE){
                    date_view_con.setVisibility(View.GONE);
                }else{
                    header_container.setVisibility(View.GONE);
                    title_con.setVisibility(View.GONE);
                    list_view.setVisibility(View.GONE);
                    date_view_con.setVisibility(View.VISIBLE);
                }
            }
        });
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
        Map<String,String> map = new HashMap<>();
        map = date_map.get(date_map.size()-1);
        list_map.addAll(collecter.getGradeMapByYearAndTerm(Integer.parseInt(Objects.requireNonNull(map.get("year"))),Integer.parseInt(Objects.requireNonNull(map.get("term")))));
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
                    title_con.setVisibility(View.VISIBLE);
                    list_item.notifyDataSetChanged();
                    break;
                case 2:
//                    date_view_con.setVisibility(View.VISIBLE);
                    header_container.setVisibility(View.VISIBLE);
                    date_item.notifyDataSetChanged();
                    break;
            }
        }
    };
}
