package com.project.loadbalancer.load_balancer.storage;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

@Service("in-memory")
public class InMemoryLoadBalancerStorage implements ILoadBalancerStorage{

    private static final HashMap<String, String> generalHashMap = new HashMap<>();
    private static final LinkedList<String> availableMachineUrls = new LinkedList<>();

    private static final HashSet<String> availableMachineUrlsHashSet = new HashSet<>();

    @Override
    public String getValue(String key) {
        return generalHashMap.get(key);
    }

    @Override
    public void updateValue(String key, String value) {
        generalHashMap.put(key, value);
    }

    @Override
    public String getValueFromIndex(int pointer) {
       return availableMachineUrls.get(pointer);
    }

    @Override
    public void addNewUrl(String url) {
        availableMachineUrlsHashSet.add(url);
        availableMachineUrls.addLast(url);
    }

    @Override
    public void deleteUrl(String url) {
        availableMachineUrlsHashSet.remove(url);
        availableMachineUrls.remove(url);
    }

    @Override
    public void addUrls(List<String> urls) {
        availableMachineUrlsHashSet.addAll(urls);
        availableMachineUrls.addAll(urls);
    }

    @Override
    public int getNumberOfAvailableUrls() {
        return availableMachineUrls.size();
    }

    @Override
    public boolean urlPresent(String url) {
        return availableMachineUrlsHashSet.contains(url);
    }

    @Override
    public List<String> getAllAvailableMachineUrls() {
        return availableMachineUrls;
    }

}
