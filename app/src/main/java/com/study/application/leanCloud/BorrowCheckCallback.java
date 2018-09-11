package com.study.application.leanCloud;

public interface BorrowCheckCallback {
    void isItemReturn(String result);
    void isSubscriberSameAsUser(int result);
}
