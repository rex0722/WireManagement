package com.study.application.leanCloud;

import android.util.Log;

import com.avos.avoscloud.AVObject;
import com.study.application.ui.BorrowActivity;


public class Writer{

    private final static String dataTable = "Record";
    private AVObject avObject;

    public void writeBorrowDataToDatabase(String borrowDate, String estimatedTimeReturn, String user, String item, String status, long borrowDateNum, long estimatedTimeReturnNum, int isSubscriberSameAsUser){
        avObject = AVObject.createWithoutData(dataTable, getObjectId(item));

        if (isSubscriberSameAsUser == BorrowActivity.SAME_AS_SUBSCRIBER){
            Log.d("TAG", "Writer.JAVA ===== 35   Subscriber is same as user");
            avObject.put("subscriber", "");
            avObject.put("subscribeDate", "");
            avObject.put("subscribeDateReturn", "");
            avObject.put("subscribeDateNum", 0);
            avObject.put("subscribeDateReturnNum", 0);
        }
        Log.v("TAG", "");
        avObject.put("borrowDate",borrowDate);
        avObject.put("estimatedTimeReturn", estimatedTimeReturn);
        avObject.put("user", user);
        avObject.put("status", status);
        avObject.put("borrowDateNum", borrowDateNum);
        avObject.put("estimatedTimeReturnNum", estimatedTimeReturnNum);
        avObject.put("returnDate","");
        avObject.put("returnDateNum",0);

        avObject.saveInBackground();
    }

    public void writeReturnDataToDatabase(String returnDate, String user, String item, String status, long returnDateNum){
        avObject = AVObject.createWithoutData(dataTable, getObjectId(item));

        avObject.put("returnDate", returnDate);
        avObject.put("user", user);
        avObject.put("status", status);
        avObject.put("returnDateNum", returnDateNum);
        avObject.put("estimatedTimeReturn", "");
        avObject.put("estimatedTimeReturnNum", 0);
        avObject.saveInBackground();
    }

    public void writeSubscriptionDataToDatabase(String subscribeDate, String subscribeDateReturn, String subscriber, String item, long subscribeDateNum, long subscribeDateReturnNum){
        avObject = AVObject.createWithoutData(dataTable, getObjectId(item));

        avObject.put("subscribeDate", subscribeDate);
        avObject.put("subscribeDateReturn", subscribeDateReturn);
        avObject.put("subscriber", subscriber);
        avObject.put("subscribeDateNum", subscribeDateNum);
        avObject.put("subscribeDateReturnNum", subscribeDateReturnNum);
        avObject.saveInBackground();
    }

    public void writeCancelSubscriptionDataToDatabase(String item){
        avObject = AVObject.createWithoutData(dataTable, getObjectId(item));

        avObject.put("subscribeDate", "");
        avObject.put("subscribeDateReturn", "");
        avObject.put("subscriber", "");
        avObject.put("subscribeDateNum", 0);
        avObject.put("subscribeDateReturnNum", 0);
        avObject.saveInBackground();
    }

    private String getObjectId(String item){
        int index;

        for (index = 0; index < Reader.objectIdDataArrayList.size(); index++){
            if (Reader.objectIdDataArrayList.get(index).getItem().equals(item))
                break;
        }

        Log.d("TAG", "Writer.JAVA ===== 83  " + Reader.objectIdDataArrayList.get(index).getObjectId());
        return Reader.objectIdDataArrayList.get(index).getObjectId();
    }

}
