package com.example.martha.myapplication.com.example.martha.bean;

public class NextDayWeather {
    private String date;



    private String high;



    private String low;



    private String typeDay;//白天天气 多云 晴



    private String typeNight;



    private String fengliDay;



    private String fengliNight;







    public NextDayWeather() {







    }







    public NextDayWeather(String date, String high, String low, String typeDay, String fengli) {



        this.date = date;



        this.high = high;



        this.low = low;



        this.typeDay = typeDay;



        this.fengliDay = fengli;



    }







    public String getDate() {



        return date;



    }







    public void setDate(String date) {



        this.date = date;



    }







    public String getHigh() {



        return high;



    }







    public void setHigh(String high) {



        this.high = high;



    }







    public String getLow() {



        return low;



    }







    public void setLow(String low) {



        this.low = low;



    }







    public String getTypeDay() {



        return typeDay;



    }







    public void setTypeDay(String typeDay) {



        this.typeDay = typeDay;



    }







    public String getFengliDay() {



        return fengliDay;



    }







    public void setFengliDay(String fengliDay) {



        this.fengliDay = fengliDay;



    }







    public String getTypeNight() {



        return typeNight;



    }







    public void setTypeNight(String typeNight) {



        this.typeNight = typeNight;



    }







    public String getFengliNight() {



        return fengliNight;



    }







    public void setFengliNight(String fengliNight) {



        this.fengliNight = fengliNight;



    }







    @Override



    public String toString() {



        return "NextDayWeather{" +



                "date='" + date + '\'' +



                ", high='" + high + '\'' +



                ", low='" + low + '\'' +



                ", typeDay='" + typeDay + '\'' +



                ", typeNight='" + typeNight + '\'' +



                ", fengliDay='" + fengliDay + '\'' +



                ", fengliNight='" + fengliNight + '\'' +



                '}';



    }
}
