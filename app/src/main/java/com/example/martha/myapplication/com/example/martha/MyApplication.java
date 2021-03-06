package com.example.martha.myapplication.com.example.martha;

import android.app.Application;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.example.martha.myapplication.com.example.martha.bean.City;
import com.example.martha.myapplication.com.example.martha.db.CityDB;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.baidu.mapapi.SDKInitializer.initialize;

public class MyApplication extends Application{
        private static final String TAG = "MyApp";

        private static MyApplication myApplication;
        private CityDB mCityDB;
        private List<City> mCityList;

        @Override
        public void onCreate(){
            super.onCreate();
            Log.d(TAG,"MyApplication->Oncreate");

            myApplication = this;
            mCityDB = openCityDB();
            initCityList();

            //在使用SDK各组件之前初始化context信息，传入ApplicationContext


        }

        /*初始化城市列表*/
        private void initCityList(){
            mCityList = new ArrayList<City>();
            new Thread(new Runnable() {
            @Override
            public void run() {
            // TODO Auto-generated method stub
                prepareCityList();
            }
        }).start();
        }

        private boolean prepareCityList() {
            mCityList = mCityDB.getAllCity();
            int i=0;
            for (City city : mCityList) {
                i++;
                String cityName = city.getCity();
                String cityCode = city.getNumber();
                Log.d(TAG,cityCode+":"+cityName);
            }
            Log.d(TAG,"i="+i);
            return true;
    }
        public List<City> getCityList() {
        return mCityList;
    }


    /*调用getInstance方法得到对象*/
    public static MyApplication getInstance(){
            return myApplication; }

        /*创建打开数据库的方法*/
        private CityDB openCityDB() {
        String path = "/data"
                + Environment.getDataDirectory().getAbsolutePath()
                + File.separator + getPackageName()
                + File.separator + "databases1"
                + File.separator
                + CityDB.CITY_DB_NAME;
        File db = new File(path);
        Log.d(TAG,path);
        if (!db.exists()) {
            String pathfolder = "/data"
                    + Environment.getDataDirectory().getAbsolutePath()
                    + File.separator + getPackageName()
                    + File.separator + "databases1"
                    + File.separator;

            File dirFirstFolder = new File(pathfolder);
            if(!dirFirstFolder.exists()){
                dirFirstFolder.mkdirs();
                Log.i("MyApp","mkdirs");
            }
            Log.i("MyApp","db is not exists");
            try {
                InputStream is = getAssets().open("city.db");
                FileOutputStream fos = new FileOutputStream(db);
                int len = -1;
                byte[] buffer = new byte[1024];
                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                    fos.flush();
                }
                fos.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
        return new CityDB(this, path);
    }
}
