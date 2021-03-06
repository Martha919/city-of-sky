package com.example.martha.myapplication;

import android.content.Context;

import android.support.v4.content.ContextCompat;

import android.support.v4.view.PagerAdapter;

import android.view.View;

import android.view.ViewGroup;

import android.widget.ImageView;



import java.util.List;



/**

 * Created by XiaHaijiao on 2016-11-29.

 */



public class GuideAdapter extends PagerAdapter {

    private List<View> views;

    private Context context;





    public GuideAdapter(List<View> views, Context context) {

        this.views = views;

        this.context = context;

    }





    @Override

    public int getCount() {

        return views.size();

    }



    @Override

    public boolean isViewFromObject(View view, Object object) {

        return view == object;

    }



    @Override

    public void destroyItem(ViewGroup container, int position, Object object) {

        container.removeView(views.get(position));

    }



    @Override

    public Object instantiateItem(ViewGroup container, int position) {

        container.addView(views.get(position));

        return views.get(position);

    }

}