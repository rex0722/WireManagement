package com.study.application.leanCloud;

public class ObjectIdData {

    private String objectId;
    private String item;

    public ObjectIdData(String objectId, String item){
        this.objectId = objectId;
        this.item = item;
    }

    public String getObjectId(){
        return objectId;
    }

    public String getItem(){
        return item;
    }

}
