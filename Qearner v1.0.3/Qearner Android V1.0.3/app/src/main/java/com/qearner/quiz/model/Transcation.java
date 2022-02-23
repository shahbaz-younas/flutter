package com.qearner.quiz.model;

public class Transcation {

    String Coin,type,type_two,coinStatus,date;

    public String getCoin() {
        return Coin;
    }

    public void setCoin(String coin) {
        Coin = coin;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCoinStatus() {
        return coinStatus;
    }

    public void setCoinStatus(String coinStatus) {
        this.coinStatus = coinStatus;
    }

    public String getDate() {
        return date;
    }

    public String getType_two() {
        return type_two;
    }

    public void setType_two(String type_two) {
        this.type_two = type_two;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
