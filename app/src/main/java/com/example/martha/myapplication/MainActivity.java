package com.example.martha.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Geocoder;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
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
import java.util.Locale;

public class MainActivity extends Activity implements View.OnClickListener{

    private static final int UPDATE_TODAY_WEATHER = 1;

    /*定义控件对象*/
    private ImageView mUpdateBtn;
    private ImageView mCitySelect;
    private TextView cityTv, timeTv, humidityTv, weekTv, pmDataTv, pmQualityTv,
            temperatureTv, climateTv, windTv, city_name_Tv;
    private ImageView weatherImg, pmImg;
    private ProgressBar progressBar;

    /*定义用户位置代理类，监听用户位置，传递当前位置*/
    public LocationClient mLocationClient = null;
    private BDLocationListener myListener = new MyLocationListener();
    String code = null;



    /*调用更新天气的函数，更新界面上的数据*/
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case UPDATE_TODAY_WEATHER:
                    updateTodayWeather((TodayWeather) msg.obj);
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

        /*查询用户位置的函数*/
        startLocate();

        /*获取网络状态的更新控件的ID*/
        /* 监听控件，当控件被点击时通过onclick方法进行响应*/
        mUpdateBtn = (ImageView)findViewById(R.id.title_update_btn);
        mUpdateBtn.setOnClickListener(this);

        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(myListener);

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
        int fengxiangCount=0;
        int fengliCount =0;
        int dateCount=0;
        int highCount =0;
        int lowCount=0;
        int typeCount =0;

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
                            } else if (xmlPullParser.getName().equals("fengli") && fengliCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setFengli(xmlPullParser.getText());
                                fengliCount++;
                            } else if (xmlPullParser.getName().equals("date") && dateCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setDate(xmlPullParser.getText());
                                dateCount++;
                            } else if (xmlPullParser.getName().equals("high") && highCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setHigh(xmlPullParser.getText().substring(2).trim());
                                highCount++;
                            } else if (xmlPullParser.getName().equals("low") && lowCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setLow(xmlPullParser.getText().substring(2).trim());
                                lowCount++;
                            } else if (xmlPullParser.getName().equals("type") && typeCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setType(xmlPullParser.getText());
                                typeCount++;
                            }
                        }
                        break;
                    // 判断当前事件是否为标签元素结束事件
                    case XmlPullParser.END_TAG:
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
        weekTv.setText(todayWeather.getDate());
        temperatureTv.setText(todayWeather.getHigh()+"~"+todayWeather.getLow());
        climateTv.setText(todayWeather.getType());
        windTv.setText("风力:"+todayWeather.getFengli());
        Toast.makeText(MainActivity.this,"更新成功！",Toast.LENGTH_SHORT).show();
    }

    /*查询用户位置的函数*/
    protected void startLocate(){
        mLocationClient = new LocationClient(getApplicationContext());//创建一个用户位置代理的类
        mLocationClient.registerLocationListener(myListener);//注册其监听事件
        LocationClientOption option = new LocationClientOption();//设置一个用户位置代理的选项类
        option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);//设置该模式为省电的模式
        option.setCoorType("bd0911");//设置坐标的类型
        option.setOpenGps(true);//设置是否打开GPS
        option.setLocationNotify(true);//设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setIsNeedAddress(true);//，设置是否需要地址信息，默认不需要！！注意，这个很重要，我们是需要返回地址的
        option.setIsNeedLocationDescribe(true);//设置是否需要位置语义化结果
        option.setIsNeedLocationPoiList(true);//设置是否需要POI结果
        option.setIgnoreKillProcess(false);//默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        mLocationClient.setLocOption(option);//将这些选项加载到用户位置代理类
        mLocationClient.start();//开始位置代理
    }

    private class MyLocationListener implements BDLocationListener{
        @Override
        public void onReceiveLocation(BDLocation location) {
            Log.d("where","city:"+location.getCountry());
            code = location.getCity();
            code = code.replace("市","");
            code = code.replace("省","");
            Log.d("where","定位显示："+code);
        }
    }

}
