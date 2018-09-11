package com.study.application.leanCloud;


import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.avos.avoscloud.AVCloudQueryResult;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.CloudQueryCallback;
import com.study.application.ui.BorrowActivity;
import com.study.application.ui.SearchActivity;

import java.util.ArrayList;

public class Reader {

    private final ArrayList<DisplayData> conditionDataArrayList = new ArrayList<>();
    private final ArrayList<String> itemArrayList = new ArrayList<>();
    public static ArrayList<ObjectIdData> objectIdDataArrayList = new ArrayList<>();
    private static String isItemReturn = "";
    private static int isSameAsSubscriber = BorrowActivity.NO_SUBSCRIBER;
    private BorrowCheckCallback borrowCheckCallback;
    private ReturnCheckCallback returnCheckCallback;
    private SubscribeCallback subscribeCallback;

    public Reader(Context context, int identification){
        switch (identification){
            case ActivityID.BORROW_ACTIVITY:
                borrowCheckCallback = (BorrowCheckCallback) context;
                break;
            case ActivityID.RETURN_ACTIVITY:
                returnCheckCallback = (ReturnCheckCallback) context;
                break;
            case ActivityID.SUBSCRIBE_ACTIVITY:
                subscribeCallback = (SubscribeCallback) context;
                break;
        }
    }

    public Reader(){}

    public void spinnerElementSearch(String selectItem) {

        String keyWord = keyWordJudge(selectItem);
        itemArrayList.clear();
        String cql = "SELECT " + keyWord + " FROM Record";

        AVQuery.doCloudQueryInBackground(cql, new CloudQueryCallback<AVCloudQueryResult>() {
            @Override
            public void done(AVCloudQueryResult avCloudQueryResult, AVException e) {

                for (int i = 0; i < avCloudQueryResult.getResults().size(); i++){
                    if (!itemArrayList.contains(avCloudQueryResult.getResults().get(i).getString(keyWord)))
                        itemArrayList.add(avCloudQueryResult.getResults().get(i).getString(keyWord));
                }

                Intent intent = new Intent("SpinnerItemElement");
                intent.putExtra("SpinnerItemElementArray", itemArrayList.toArray(new String[itemArrayList.size()]));
                SearchActivity.searchContext.sendBroadcast(intent);
            }
        });
    }

    public void conditionSearch(String word, String value) {
        conditionDataArrayList.clear();
        String keyWord = keyWordJudge(word);
        String cql = "SELECT * FROM Record WHERE " + keyWord + " = " + "'" + value + "'";

        AVQuery.doCloudQueryInBackground(cql, new CloudQueryCallback<AVCloudQueryResult>() {
            @Override
            public void done(AVCloudQueryResult result, AVException e) {

                for(int i = 0; i < result.getResults().size(); i++){
                conditionDataArrayList.add(new DisplayData(
                        result.getResults().get(i).getString("status"),
                        result.getResults().get(i).getString("item"),
                        result.getResults().get(i).getString("user"),
                        result.getResults().get(i).getString("borrowDate"),
                        result.getResults().get(i).getString("estimatedTimeReturn"),
                        result.getResults().get(i).getString("returnDate"),
                        result.getResults().get(i).getString("classification"),
                        result.getResults().get(i).getString("subscriber"),
                        result.getResults().get(i).getString("subscribeDate")
                    ));
                }

                Intent intent = new Intent("DelverConditionData");
                intent.putExtra("conditionData", conditionDataArrayList);
                SearchActivity.searchContext.sendBroadcast(intent);
            }
        });

    }

    public static void setObjectID(){

        String cql = "SELECT item FROM Record";

        AVQuery.doCloudQueryInBackground(cql, new CloudQueryCallback<AVCloudQueryResult>() {
            @Override
            public void done(AVCloudQueryResult result, AVException e) {

                for (int i = 0; i < result.getResults().size(); i++){
                    objectIdDataArrayList.add(new ObjectIdData(
                            result.getResults().get(i).getObjectId(),
                            result.getResults().get(i).getString("item")
                    ));

                    Log.w("TAG", "id: " + objectIdDataArrayList.get(i).getObjectId() +
                            " item: " + objectIdDataArrayList.get(i).getItem());
                }
            }
        });
    }

    public void itemStatusCheck(String item, int target){
        isItemReturn = "";
        String cql = "SELECT status FROM Record WHERE item = " + '"' + item + '"';

        AVQuery.doCloudQueryInBackground(cql, new CloudQueryCallback<AVCloudQueryResult>() {
            @Override
            public void done(AVCloudQueryResult result, AVException e) {

                if (result.getResults().get(0).getString("status") != null)
                    isItemReturn = result.getResults().get(0).getString("status");


                if (target == ActivityID.BORROW_ACTIVITY)
                    borrowCheckCallback.isItemReturn(isItemReturn);
                else
                    returnCheckCallback.isItemCanReturn(isItemReturn);
            }
        });
    }

    public void subscriberCheck(String item,String user){
        isSameAsSubscriber = BorrowActivity.NO_SUBSCRIBER;
        String cql = "SELECT subscriber FROM Record WHERE item = " + '"' + item + '"';

        AVQuery.doCloudQueryInBackground(cql, new CloudQueryCallback<AVCloudQueryResult>() {
            @Override
            public void done(AVCloudQueryResult result, AVException e) {
                Log.w("TAG", cql);
                Log.w("TAG", "size:" + result.getResults().size());
                Log.e("TAG", "Reader.Java----134 content: " + result.getResults().get(0).getString("subscriber"));
                if (result.getResults().get(0).getString("subscriber") != null &&
                    !result.getResults().get(0).getString("subscriber").equals("")){
                    if (result.getResults().get(0).getString("subscriber").equals(user)){
                        isSameAsSubscriber = BorrowActivity.SAME_AS_SUBSCRIBER;
                        Log.d("TAG", isSameAsSubscriber + " Reader.JAVA ====== 139");
                    }else
                        isSameAsSubscriber = BorrowActivity.NOT_SAME_AS_SUBSCRIBER;
                }else{
                    Log.e("TAG", "Reader.Java----143 content: " + result.getResults().get(0).getString("subscriber"));
                    isSameAsSubscriber = BorrowActivity.NO_SUBSCRIBER;
                }


                borrowCheckCallback.isSubscriberSameAsUser(isSameAsSubscriber);
                Log.d("TAG",isSameAsSubscriber + " Reader.JAVA ====== 149");
            }
        });

        Log.d("TAG", isSameAsSubscriber + " Reader.JAVA ====== 153");
    }

    public void checkReturnerSameAsBorrower(String item, String user){
        String cql = "SELECT user FROM Record WHERE item = " + '"' + item + '"';
        AVQuery.doCloudQueryInBackground(cql, new CloudQueryCallback<AVCloudQueryResult>() {
            @Override
            public void done(AVCloudQueryResult result, AVException e) {
                if (result.getResults().get(0).getString("user").equals(user))
                    returnCheckCallback.isReturnerSameAsBorrower(true);
                else
                    returnCheckCallback.isReturnerSameAsBorrower(false);
            }
        });

    }

    public void checkSubscribeItem(){
        String cql = "SELECT item FROM Record WHERE subscriber = '' OR subscriber = NULL";
        AVQuery.doCloudQueryInBackground(cql, new CloudQueryCallback<AVCloudQueryResult>() {
            @Override
            public void done(AVCloudQueryResult result, AVException e) {
                ArrayList<String> item = new ArrayList<>();

                if (result != null){
                    Log.d("TAG","checkSubscribeItem not null");
                    for (int i = 0; i < result.getResults().size(); i++){
                        item.add(result.getResults().get(i).getString("item"));
                    }
                }else
                    Log.i("TAG","checkSubscribeItem null");

                subscribeCallback.canSubscribeItem(item.toArray(new String[item.size()]));
            }
        });
    }

    public void checkSubscribeDate(){

    }

    private String keyWordJudge(String word) {

        String keyWord = "";

        switch (word) {
            case "品項":
                keyWord = "classification";
                break;
            case "使用者":
                keyWord = "user";
                break;
            case "狀態":
                keyWord = "status";
                break;
        }

        return keyWord;
    }

}
