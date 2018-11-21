package com.example.martha.myapplication.com.example.martha.bean;

import java.util.List;

/*新建一个类*/
/*利用工具生成get、set、toSring方法*/
public class TodayWeather {
    private String city;
    private String updatetime;
    private String wendu;
    private String shidu;
    private String pm25;
    private String quality;
    private String fengxiang;
    private String fengli;
    private String date;
    private String high;
    private String low;
    private String type;

    //六日天气
    private List<NextDayWeather> nextDayWeatherList;
    public List<NextDayWeather> getNextDayWeatherList() {



        return nextDayWeatherList;



    }







    public void setNextDayWeatherList(List<NextDayWeather> nextDayWeatherList) {



        this.nextDayWeatherList = nextDayWeatherList;



    }




    public String getCity() {
        return city;
    }

    public String getPm25() {
        return pm25;
    }

    public String getFengxiang() {
        return fengxiang;
    }

    public String getQuality() {
        return quality;
    }

    public String getShidu() {
        return shidu;
    }

    public String getUpdatetime() {
        return updatetime;
    }

    public String getFengli() {
        return fengli;
    }

    public String getWendu() {
        return wendu;
    }

    public String getDate() {
        return date;
    }

    public String getHigh() {
        return high;
    }

    public String getLow() {
        return low;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setPm25(String pm25) {
        this.pm25 = pm25;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public void setLow(String low) {
        this.low = low;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setFengxiang(String fengxiang) {
        this.fengxiang = fengxiang;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public void setShidu(String shidu) {
        this.shidu = shidu;
    }

    public void setFengli(String fengli) {
        this.fengli = fengli;
    }

    public void setUpdatetime(String updatetime) {
        this.updatetime = updatetime;
    }

    public void setWendu(String wendu) {
        this.wendu = wendu;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString(){
        return "TodayWeather{" +
                "city='" + city + '\'' +
                ", updatetime='" + updatetime + '\''+
                ", wendu='" + wendu + '\''+
                ", shidu='" + shidu + '\''+
                ", pm25='" + pm25 + '\''+
                ", quality='" + quality + '\''+
                ", fengxiang='" + fengxiang + '\''+
                ", fengli='" + fengli + '\''+
                ", date='" + date + '\''+
                ", high='" + high + '\''+
                ", low='" + low + '\''+
                ", type='" + type + '\''+
                ", nextDayWeatherList=" + nextDayWeatherList.toString() +
                '}';
    }

}
