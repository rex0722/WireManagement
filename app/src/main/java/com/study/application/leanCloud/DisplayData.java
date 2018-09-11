package com.study.application.leanCloud;


import java.io.Serializable;

public class DisplayData implements Serializable {


    private final String index;
    private final String user;
    private final String borrowDate;
    private final String estimatedTimeReturn;
    private final String returnDate;
    private final String classification;
    private final String status;
    private final String subscriber;
    private final String subscribeDate;

    DisplayData(String status, String index, String user, String borrowDate, String estimatedTimeReturn, String returnDate,String classification, String subscriber, String subscribeDate) {
        this.status = status;
        this.index = index;
        this.user = user;
        this.borrowDate = borrowDate;
        this.estimatedTimeReturn = estimatedTimeReturn;
        this.returnDate = returnDate;
        this.classification = classification;
        this.subscriber = subscriber;
        this.subscribeDate = subscribeDate;
    }

    public String getStatus() {
        return status;
    }

    public String getIndex() {
        return index;
    }

    public String getUser() {
        return user;
    }

    public String getBorrowDate() {
        return borrowDate;
    }

    public String getEstimatedTimeReturn() {
        return estimatedTimeReturn;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public String getClassification() {
        return classification;
    }

    public String getSubscriber() {
        return subscriber;
    }

    public String getSubscribeDate() {
        return subscribeDate;
    }
}
