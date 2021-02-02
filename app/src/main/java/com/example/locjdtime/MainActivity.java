package com.example.locjdtime;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.util.Log;


public class MainActivity extends AppCompatActivity {

    public static  String EXTRA_MESSAGE = "";
    public  static String JD_TIME_URL = "https://a.jd.com//ajax/queryServerData.html";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    /**
     * 获取京东时间
     */
    public void getJdTime(View view) {

        try {
            getServerTime();

        }catch (Exception e){
            System.out.println(e.getMessage());
        }




    }

    protected void getServerTime(){
        // 获取服务器时间
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(JD_TIME_URL).get().build();
        Call call = client.newCall(request);
        new Thread(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat sdf = new SimpleDateFormat();
                sdf.applyPattern("yyyy-MM-dd HH:mm:ss");
                TextView textView1 = findViewById(R.id.localtime);
                TextView textView2 = findViewById(R.id.jdtime);
                TextView textView4 = findViewById(R.id.difftime);
                TextView textView5 = findViewById(R.id.textView10);
                TextView textView6 = findViewById(R.id.delaynum);
                long start = System.currentTimeMillis();
                try {
                    Response response = call.execute();
                    if (!response.isSuccessful()){
                        throw new IOException("请求失败");
                    }
                    String body = response.body().string();
                    System.out.println(body);
                    int has = body.indexOf("serverTime");
                    if (has == -1) {
                        throw new IOException("未获取到时间");
                    }
                    long end = System.currentTimeMillis();
                    // 网络延迟
                    long delayTime = end - start;
                    JSONObject jsonObj = new JSONObject(body);

                    long jdtimestemp = jsonObj.getLong("serverTime");
                    long diffTime = start - jdtimestemp + delayTime/2;
                    boolean isSuc = SystemClock.setCurrentTimeMillis(jdtimestemp+delayTime/2);
                    Log.i("提示","设置系统时间是否成功?"+isSuc);
                    textView4.setText(String.valueOf(diffTime));
                    Date jddate = new Date(jdtimestemp);
                    String jdTime = sdf.format(jddate);
                    Date localdate = new Date(start);
                    String localTime = sdf.format(localdate);
                    // 本地时间
                    textView1.setText(localTime);
                    // 服务器时间
                    textView2.setText(jdTime);
                    textView5.setText(start+"/"+jdtimestemp);
                    textView6.setText(String.valueOf(delayTime));
                } catch (IOException e){
                    e.printStackTrace();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();

    }

}