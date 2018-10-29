package com.example.martha.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.martha.myapplication.com.example.martha.MyApplication;
import com.example.martha.myapplication.com.example.martha.bean.City;

import java.util.ArrayList;
import java.util.List;

/*增加选择城市的Acticity*/
public class SelectCity extends AppCompatActivity implements View.OnClickListener{

    private ImageView mBackBtn;
    private TextView city_name_Tv;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.select_city);

        /*实现城市列表的展示*/
        /*获取城市名称的ID，并用setText方法储存*/
        final SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
        city_name_Tv = (TextView) findViewById(R.id.title_name);
        city_name_Tv.setText(sharedPreferences.getString("main_city_name", "北京"));
        List<City> cityList = ((MyApplication) getApplication()).getCityList();
        final ArrayList<String> cityNameList = new ArrayList<>(cityList.size());
        final ArrayList<String> cityCodeList = new ArrayList<>(cityList.size());
        for (City c : cityList) {
            cityNameList.add(c.getCity());
            cityCodeList.add(c.getNumber());
        }
        ListView mlistView = (ListView) findViewById(R.id.list_view);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, cityNameList);
        mlistView.setAdapter(adapter);

        mBackBtn = (ImageView) findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(this);

        /*把选择结果返回给主界面*/
        mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String cityCode = cityCodeList.get(i);
                String cityName = cityNameList.get(i);
                SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("main_city_code", cityCode);
                editor.putString("main_city_name", cityName);
                editor.commit();
                Intent intent = new Intent();
                intent.putExtra("cityCode", cityCode);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.title_back:
                Intent i = new Intent();
                i.putExtra("cityCode", "101160101");
                i.putExtra("citycode","101010300");
                setResult(RESULT_OK, i);
                finish();
                break;
            default:
                break;
        }
    }


}
