package com.example.martha.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.martha.myapplication.com.example.martha.bean.NextDayWeather;
import com.example.martha.myapplication.com.example.martha.bean.TodayWeather;
import com.example.martha.myapplication.com.example.martha.myapplication.util.NetUtil;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener,
        ViewPager.OnPageChangeListener {

    private static final int UPDATE_TODAY_WEATHER = 1;

    /*定义控件对象*/
    private ImageView mUpdateBtn;
    private ImageView mCitySelect;
    private TextView cityTv, timeTv, humidityTv, weekTv, pmDataTv, pmQualityTv,
            temperatureTv, climateTv, windTv, city_name_Tv;
    private ImageView weatherImg, pmImg;
    private ProgressBar progressBar;
    /*六天天气信息展示*/
    private GuideAdapter vpAdapter;
    private ViewPager vp;
    private List<View> views;
    /*小圆点*/
    private ImageView[] dots;
    private int[] ids = {R.id.iv1,R.id.iv2};


    /*定义用户位置代理类，监听用户位置，传递当前位置*/




    /*调用更新天气的函数，更新界面上的数据*/
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case UPDATE_TODAY_WEATHER:
                    updateTodayWeather((TodayWeather) msg.obj);
                    updateNextDaysWeather((TodayWeather) msg.obj);
                    break;
                default:
                    break;
            }
        }
    };



    @Override
    /*oncreate方法是最先重载的方法，表示一个窗口正在生成*/
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        /*获取网络状态的更新控件的ID*/
        /* 监听控件，当控件被点击时通过onclick方法进行响应*/
        mUpdateBtn = (ImageView)findViewById(R.id.title_update_btn);
        mUpdateBtn.setOnClickListener(this);



        /*检测网络是否连接*/
        if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
            Log.d("myWeather", "网络OK");
            Toast.makeText(MainActivity.this,"网络OK！", Toast.LENGTH_LONG).show();
        }else
        {
            Log.d("myWeather", "网络挂了");
            Toast.makeText(MainActivity.this,"网络挂了！", Toast.LENGTH_LONG).show();
        }

        /*初始化更新按钮*/
        progressBar = (ProgressBar) findViewById(R.id.loading);

        /*为城市选择这个Imageview增加onclick事件*/
        /*获取城市选择的更新控件的ID*/
        mCitySelect = (ImageView) findViewById(R.id.title_city_manager);
        mCitySelect.setOnClickListener(this);

        /*初始化滑动页面、小圆点*/
        initViews();
        initDots();

        /*在Oncreate方法中调用init函数*/
        initView();

    }

    /*初始化控件内容*/
    void initView(){
        city_name_Tv = (TextView) findViewById(R.id.title_city_name);
        cityTv = (TextView) findViewById(R.id.city);
        timeTv = (TextView) findViewById(R.id.time);
        humidityTv = (TextView) findViewById(R.id.humidity);
        weekTv = (TextView) findViewById(R.id.week_today);
        pmDataTv = (TextView) findViewById(R.id.pm_data);
        pmQualityTv = (TextView) findViewById(R.id.pm2_5_quality);
        pmImg = (ImageView) findViewById(R.id.pm2_5_img);
        temperatureTv = (TextView) findViewById(R.id.temperature);
        climateTv = (TextView) findViewById(R.id.climate);
        windTv = (TextView) findViewById(R.id.wind);
        weatherImg = (ImageView) findViewById(R.id.weather_img);
        city_name_Tv.setText("N/A");
        cityTv.setText("N/A");
        timeTv.setText("N/A");
        humidityTv.setText("N/A");
        pmDataTv.setText("N/A");
        pmQualityTv.setText("N/A");
        weekTv.setText("N/A");
        temperatureTv.setText("N/A");
        climateTv.setText("N/A");
        windTv.setText("N/A");
        //
        //更新六日天气
        LayoutInflater inflater = LayoutInflater.from(this);
        views = new ArrayList<>();
        views.add(inflater.inflate(R.layout.sixday1, null));
        views.add(inflater.inflate(R.layout.sixday2, null));
        for (int page = 0; page < 2; page++) {
            //日期
            TextView dateTV = (TextView) views.get(page).findViewById(R.id.week_today);
            dateTV.setText("N/A");
            dateTV = (TextView) views.get(page).findViewById(R.id.week_today1);
            dateTV.setText("N/A");
            dateTV = (TextView) views.get(page).findViewById(R.id.week_today2);
            dateTV.setText("N/A");
            //气温
            TextView tempTv = (TextView) views.get(page).findViewById(R.id.temperature);
            tempTv.setText("N/A");
            tempTv = (TextView) views.get(page).findViewById(R.id.temperature1);
            tempTv.setText("N/A");
            tempTv = (TextView) views.get(page).findViewById(R.id.temperature2);
            tempTv.setText("N/A");
            //天气情况：多云转晴
            TextView cliTx = (TextView) views.get(page).findViewById(R.id.climate);
            cliTx.setText("N/A");
            cliTx = (TextView) views.get(page).findViewById(R.id.climate1);
            cliTx.setText("N/A");
            cliTx = (TextView) views.get(page).findViewById(R.id.climate2);
            cliTx.setText("N/A");
            //风力
            TextView winTx = (TextView) views.get(page).findViewById(R.id.wind);
            winTx.setText("N/A");
            winTx = (TextView) views.get(page).findViewById(R.id.wind1);
            winTx.setText("N/A");
            winTx = (TextView) views.get(page).findViewById(R.id.wind2);
            winTx.setText("N/A");
            }
        vpAdapter = new GuideAdapter(views,this);
        vp = (ViewPager) findViewById(R.id.viewpager);
        vp.setAdapter(vpAdapter);

        //

    }

    @Override
    public void onClick(View view){

        /*在onClick事件中，通过view.getId()方法，获得Id号*/
        if(view.getId() == R.id.title_city_manager){
            Intent i = new Intent(this, SelectCity.class);
            // startActivity(i);
            startActivityForResult(i,1);
        }

        /*构造更新图标的动画函数Animation*/
        /*调用Interpolator方法设置动画放映速度*/


        if(view.getId() == R.id.title_update_btn){

            /*更新图标的动画开始*/
            /*设置ProgressBar可见，ImageView不可见*/
            mUpdateBtn.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);

            /*通过sharedpreferences方法读取城市ID，无定义可缺省为北京市*/
            SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
            String cityCode = sharedPreferences.getString("main_city_code","101010100");
            Log.d("myWeather",cityCode);

            /*调用queryWeatherCode方法获取网络数据*/
            if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
                Log.d("myWeather", "网络OK");
                queryWeatherCode(cityCode);
            }else
            {
                Log.d("myWeather", "网络挂了");
                Toast.makeText(MainActivity.this,"网络挂了！",Toast.LENGTH_LONG).show();
            }
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String newCityCode= data.getStringExtra("cityCode");
            Log.d("myWeather", "选择的城市代码为"+newCityCode);
            if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
                Log.d("myWeather", "网络OK");
                queryWeatherCode(newCityCode);
            } else {
                Log.d("myWeather", "网络挂了");
                Toast.makeText(MainActivity.this, "网络挂了！", Toast.LENGTH_LONG).show();
            }
        }
    }


    /**
     *
     * @param cityCode
     */
    private void queryWeatherCode(String cityCode) {
        final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + cityCode;
        Log.d("myWeather", address);


        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection con = null;
                TodayWeather todayWeather = null;
                try{
                    URL url = new URL(address);
                    con = (HttpURLConnection)url.openConnection();
                    con.setRequestMethod("GET");
                    con.setConnectTimeout(8000);
                    con.setReadTimeout(8000);
                    InputStream in = con.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String str;
                    while((str=reader.readLine()) != null){
                        response.append(str);
                        Log.d("myWeather", str);
                    }
                    String responseStr=response.toString();
                    Log.d("myWeather", responseStr);

                    /*调用解析函数，并返回TodayWeather对象*/
                    todayWeather = parseXML(responseStr);
                    if(todayWeather != null){
                        Log.d("myweather",todayWeather.toString());
                    }

                    Message msg =new Message();
                    msg.what = UPDATE_TODAY_WEATHER;
                    msg.obj=todayWeather;
                    mHandler.sendMessage(msg);

                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    if(con != null){
                        con.disconnect();
                    }
                }
            }
        }).start();
    }

    /*解析函数：解析城市名称、时间等信息*/
    private TodayWeather parseXML(String xmldata){

        /*将解析的数据存入TodayWeather对象中*/
        TodayWeather todayWeather = null;
        //
        NextDayWeather nextDayWeather = null;
        List<NextDayWeather> nextDayWeatherList = new ArrayList<>(6);
        int fengxiangCount=-1;
        int fengliCount =-1;
        int dateCount=-1;
        int highCount =-1;
        int lowCount=-1;
        int typeCount =-1;

        try {
            XmlPullParserFactory fac = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = fac.newPullParser();
            xmlPullParser.setInput(new StringReader(xmldata));
            int eventType = xmlPullParser.getEventType();
            Log.d("myWeather", "parseXML");
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    // 判断当前事件是否为文档开始事件
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    // 判断当前事件是否为标签元素开始事件
                    case XmlPullParser.START_TAG:
                        if(xmlPullParser.getName().equals("resp"
                        )){
                            todayWeather= new TodayWeather();
                        }
                        if (xmlPullParser.getName().equals("weather")) {
                            nextDayWeather = new NextDayWeather();
                        }
                        if (xmlPullParser.getName().equals("yesterday")) {
                            nextDayWeather = new NextDayWeather();
                        }
                        if (todayWeather != null) {
                            if (xmlPullParser.getName().equals("city")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setCity(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("updatetime")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setUpdatetime(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("shidu")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setShidu(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("wendu")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setWendu(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("pm25")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setPm25(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("quality")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setQuality(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setFengxiang(xmlPullParser.getText());
                                fengxiangCount++;
                            }
                            else if (xmlPullParser.getName().equals("fengli") || xmlPullParser.getName().equals("fl_1")) {
                                eventType = xmlPullParser.next();
                                if (fengliCount == -1) {
                                    todayWeather.setFengli(xmlPullParser.getText());
                                }
                                if (fengliCount > -1 && fengliCount % 2 == 0) {
                                    nextDayWeather.setFengliDay(xmlPullParser.getText());
                                }
                                if (fengliCount > -1 && fengliCount % 2 == 1) {
                                    nextDayWeather.setFengliNight(xmlPullParser.getText());
                                }
                                fengliCount++;
                            }
                            else if (xmlPullParser.getName().equals("date")|| xmlPullParser.getName().equals("date_1"))  {
                                eventType = xmlPullParser.next();
                                if (dateCount == 0) {
                                    todayWeather.setDate(xmlPullParser.getText().split("日")[1]);
                                }
                                nextDayWeather.setDate(xmlPullParser.getText().split("日")[1]);
                                dateCount++;
                            }
                            else if (xmlPullParser.getName().equals("high")|| xmlPullParser.getName().equals("high_1")){
                                eventType = xmlPullParser.next();
                                if (highCount == 0) {
                                    todayWeather.setHigh(xmlPullParser.getText().substring(2).trim());
                                }
                                nextDayWeather.setHigh(xmlPullParser.getText().substring(2).trim());
                                highCount++;
                            }
                            else if (xmlPullParser.getName().equals("low")|| xmlPullParser.getName().equals("low_1")) {
                                eventType = xmlPullParser.next();
                                if (lowCount == 0) {
                                    todayWeather.setLow(xmlPullParser.getText().substring(2).trim());
                                    }
                                nextDayWeather.setLow(xmlPullParser.getText().substring(2).trim());
                                lowCount++;
                            }
                            else if (xmlPullParser.getName().equals("type")||
                                    xmlPullParser.getName().equals("type_1")){
                                eventType = xmlPullParser.next();
                                if (typeCount == 2) {
                                    todayWeather.setType(xmlPullParser.getText());
                                }
                                if (typeCount % 2 == 0) {
                                    nextDayWeather.setTypeDay(xmlPullParser.getText());
                                }
                                if (typeCount % 2 == 1) {
                                    nextDayWeather.setTypeNight(xmlPullParser.getText());
                                }
                                typeCount++;
                            }
                        }
                        break;
                    // 判断当前事件是否为标签元素结束事件
                    case XmlPullParser.END_TAG:
                        if (xmlPullParser.getName().equals("weather") && nextDayWeather.getDate() != null) {
                            nextDayWeatherList.add(nextDayWeather);
                            }

                        if (xmlPullParser.getName().equals("yesterday") && nextDayWeather.getDate() != null) {
                            nextDayWeatherList.add(nextDayWeather);
                        }
                        break;
                }
                // 进入下一个元素并触发相应事件
                eventType = xmlPullParser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (NextDayWeather next : nextDayWeatherList) {



            System.out.println(next.toString());



        }



        todayWeather.setNextDayWeatherList(nextDayWeatherList);



        System.out.println(todayWeather.toString());
        return todayWeather;
    }


    /*编写更新天气的函数*/
    void updateTodayWeather(TodayWeather todayWeather){

        /*设置ImageView可见，ProgressBar不可见*/
        if(mUpdateBtn.getVisibility()==View.GONE && progressBar.getVisibility()==View.VISIBLE){
            progressBar.setVisibility(View.GONE);
            mUpdateBtn.setVisibility(View.VISIBLE);
        }

        city_name_Tv.setText(todayWeather.getCity()+"天气");
        cityTv.setText(todayWeather.getCity());
        timeTv.setText(todayWeather.getUpdatetime()+ "发布");
        humidityTv.setText("湿度："+todayWeather.getShidu());
        pmDataTv.setText(todayWeather.getPm25());
        pmQualityTv.setText(todayWeather.getQuality());
        weekTv.setText("今天 "+todayWeather.getDate());
        temperatureTv.setText(todayWeather.getHigh()+"~"+todayWeather.getLow());
        climateTv.setText(todayWeather.getType());
        windTv.setText("风力:"+todayWeather.getFengli());

        Toast.makeText(MainActivity.this,"更新成功！",Toast.LENGTH_SHORT).show();
    }


    /*更新六日天气*/
    void updateNextDaysWeather(TodayWeather todayWeather) {
        List<NextDayWeather> nextDayWeatherList = todayWeather.getNextDayWeatherList();
        System.out.println("nextDayWeatherList.size:" + nextDayWeatherList.size());

        for (int page = 0; page < 2; page++) {
            int day = page * 3;
            System.out.println("day：" + day);
            //日期
            TextView dateTV = (TextView) views.get(page).findViewById(R.id.week_today);
            dateTV.setText(nextDayWeatherList.get(day).getDate());
            //气温
            TextView tempTv = (TextView) views.get(page).findViewById(R.id.temperature);
            tempTv.setText(nextDayWeatherList.get(day).getHigh() + "~" + nextDayWeatherList.get(day).getLow());
            //天气情况：多云转晴
            TextView cliTx = (TextView) views.get(page).findViewById(R.id.climate);
            cliTx.setText(addZhuan(nextDayWeatherList.get(day).getTypeDay(), nextDayWeatherList.get(day).getTypeNight()));
            //风力
            TextView winTx = (TextView) views.get(page).findViewById(R.id.wind);
            winTx.setText(nextDayWeatherList.get(day).getFengliDay());
            day++;
            System.out.println("day：" + day);
            dateTV = (TextView) views.get(page).findViewById(R.id.week_today1);
            dateTV.setText(nextDayWeatherList.get(day).getDate());
            tempTv = (TextView) views.get(page).findViewById(R.id.temperature1);
            tempTv.setText(nextDayWeatherList.get(day).getHigh() + "~" + nextDayWeatherList.get(day).getLow());
            cliTx = (TextView) views.get(page).findViewById(R.id.climate1);
            cliTx.setText(addZhuan(nextDayWeatherList.get(day).getTypeDay(), nextDayWeatherList.get(day).getTypeNight()));
            winTx = (TextView) views.get(page).findViewById(R.id.wind1);
            winTx.setText(nextDayWeatherList.get(day).getFengliDay());
            day++;
            System.out.println("day：" + day);
            dateTV = (TextView) views.get(page).findViewById(R.id.week_today2);
            dateTV.setText(nextDayWeatherList.get(day).getDate());
            tempTv = (TextView) views.get(page).findViewById(R.id.temperature2);
            tempTv.setText(nextDayWeatherList.get(day).getHigh() + "~" + nextDayWeatherList.get(day).getLow());
            cliTx = (TextView) views.get(page).findViewById(R.id.climate2);
            cliTx.setText(addZhuan(nextDayWeatherList.get(day).getTypeDay(), nextDayWeatherList.get(day).getTypeNight()));
            winTx = (TextView) views.get(page).findViewById(R.id.wind2);
            winTx.setText(nextDayWeatherList.get(day).getFengliDay());
            vpAdapter = new GuideAdapter(views, this);
            vp = (ViewPager) findViewById(R.id.viewpager);
            vp.setAdapter(vpAdapter);
        }

    }


    private String addZhuan(String a, String b){

        if (a.equals(b)|| b==null) {
            return a;
        }
        else return a + "转" + b;

    }


    void initDots(){
        dots = new ImageView[views.size()];
        for(int i =0;i<views.size();i++){
            dots[i]=(ImageView)findViewById(ids[i]);
        }
    }
    private void initViews(){
        LayoutInflater inflater = LayoutInflater.from(this);
        views = new ArrayList<View>();
        views.add(inflater.inflate(R.layout.sixday1,null));
        views.add(inflater.inflate(R.layout.sixday2,null));
        vpAdapter = new GuideAdapter(views,this);
        vp = (ViewPager) findViewById(R.id.viewpager);
        vp.setAdapter(vpAdapter);
        vp.setOnPageChangeListener(this);
    }
        @Override
        public void onPageScrolled(int position, float positionOffset, int
                positionOffsetPixels) {
        }
    @Override
    public void onPageSelected(int position) {
        for (int a = 0;a<ids.length;a++){
            if(a==position){
                dots[a].setImageResource(R.drawable.page_indicator_focused);
            }else {
                dots[a].setImageResource(R.drawable.page_indicator_unfocused);
            }
        }
    }
    @Override
    public void onPageScrollStateChanged(int state) {
    }

}
