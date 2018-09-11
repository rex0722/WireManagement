package com.study.application.leanCloud;


class Record {

    private final long dateTime;
    private final String user;
    private final String date;
    private final String subdate;
    private final String classification;
    private final String status;

    public Record(String status, long dateTime, String user, String date, String subdate, String classification){
        this.status = status;
        this.dateTime = dateTime;
        this.user = user;
        this.date = date;
        this.subdate = subdate;
        this.classification = classification;
    }

    public String getStatus(){
        return status;
    }

    public long getDateTime(){
        return dateTime;
    }

    public String getUser(){
        return user;
    }

    public String getDate(){
        return date;
    }

    public String getSubdate(){return subdate;}

    public  String getClassification(){
        return classification;
    }

}
