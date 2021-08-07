package com.songyb.bs.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.aware.DiscoverySession;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyImageView extends androidx.appcompat.widget.AppCompatImageView {
    protected static final int SUCCESS = 0;
    protected static final int ERROR = 1;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        public void handleMessage(Message msg) {

            switch (msg.what) {
                case SUCCESS:
                    Bitmap bitmap = (Bitmap) msg.obj;
                    setImageBitmap(bitmap);
                    break;
                case ERROR:
                    Toast.makeText(getContext(),"失败",Toast.LENGTH_LONG).show();
                    break;

                default:
                    break;
            }

        }

        ;
    };
    public MyImageView(@NonNull @NotNull Context context) {
        super(context);
    }

    public MyImageView(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyImageView(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setMyUrl(String Url) {
        new Thread() {
            public void run() {
                try {
                    URL url = new URL(Url);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(5000);
                    conn.setRequestMethod("GET");
                    int code = conn.getResponseCode();
                    if (code == 200) {
                        InputStream in = conn.getInputStream();
                        Bitmap bitmap = BitmapFactory.decodeStream(in);
                        //发送消息，通知handler更细UI
                        Message msg = Message.obtain();
                        msg.what = SUCCESS;
                        msg.obj = bitmap;
                        mHandler.sendMessage(msg);
                    }

                } catch (Exception e) {
                    e.printStackTrace();

                    Message msg = Message.obtain();
                    msg.what = ERROR;
                    mHandler.sendMessage(msg);
                }

            }

            ;
        }.start();
    }
}
