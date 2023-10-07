package com.project.loadbalancer.load_balancer.storage;

import java.util.LinkedList;
import java.util.List;

public interface ILoadBalancerStorage {

    String getValue(String key);

    void updateValue(String key, String value);

    String getValueFromIndex(int pointer);

    void addNewUrl(String url);

    void deleteUrl(String url);

    void addUrls(List<String> urls);

    int getNumberOfAvailableUrls();

    boolean urlPresent(String Url);

    List<String> getAllAvailableMachineUrls();
}
