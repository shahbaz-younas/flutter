package com.qearner.quiz.model;

import java.util.ArrayList;

public class Category {
    private String id, name, image,  noOfCate, message, date, ttlQues;

    public boolean adsShow,isPlayed;
    int imgRes;

    public Category() {
    }

    public Category(String name, int imgRes) {
        this.name = name;
        this.imgRes = imgRes;
    }

    public Category(String id, String name, String image) {
        this.id = id;
        this.name = name;
        this.image = image;
    }

    public boolean isPlayed() {
        return isPlayed;
    }

    public void setPlayed(boolean played) {
        isPlayed = played;
    }

    public int getImgRes() {
        return imgRes;
    }

    public void setImgRes(int imgRes) {
        this.imgRes = imgRes;
    }

    public Category(boolean adsShow) {
        this.adsShow = adsShow;
    }

    public boolean isAdsShow() {
        return adsShow;
    }

    public void setAdsShow(boolean adsShow) {
        this.adsShow = adsShow;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNoOfCate() {
        return noOfCate;
    }

    public void setNoOfCate(String noOfCate) {
        this.noOfCate = noOfCate;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImage(String image) {
        this.image = image;
    }


    public String getTtlQues() {
        return ttlQues;
    }

    public void setTtlQues(String ttlQues) {
        this.ttlQues = ttlQues;
    }


}
