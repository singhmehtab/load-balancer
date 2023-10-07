package com.project.loadbalancer.load_balancer;

import java.util.List;

public interface ILoadBalancer {

    String getAvailableMachineUrl();

    String[] getAllMachineUrls();

    void addMachineToAvailable(String url);

    void deleteMachineFromAvailable(String url);

    List<String> getAllAvailableMachineUrls();

}
