package com.study.application.leanCloud;

public interface SubscribeCallback {
    void canSubscribeItem(String [] itemList);
    void checkItemEstimatedTimeReturn(Long estimatedTimeReturnNum);
}
