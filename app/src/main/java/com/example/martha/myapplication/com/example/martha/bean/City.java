package com.example.martha.myapplication.com.example.martha.bean;

/*增加选择城市的Acticity*/
public class City {
    private String province;
    private String city;
    private String number;
    private String firstPY;
    private String allPY;
    private String allFristPY;

    /*增加构造函数City*/
    public City(String province, String city, String number, String
            firstPY, String allPY, String allFristPY) {
        this.province = province;
        this.city = city;
        this.number = number;
        this.firstPY = firstPY;
        this.allPY = allPY;
        this.allFristPY = allFristPY;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setAllFristPY(String allFristPY) {
        this.allFristPY = allFristPY;
    }

    public void setAllPY(String allPY) {
        this.allPY = allPY;
    }

    public void setFirstPY(String firstPY) {
        this.firstPY = firstPY;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public String getFirstPY() {
        return firstPY;
    }

    public String getAllFristPY() {
        return allFristPY;
    }

    public String getNumber() {
        return number;
    }

    public String getAllPY() {
        return allPY;
    }

    public String getProvince() {
        return province;
    }

}
