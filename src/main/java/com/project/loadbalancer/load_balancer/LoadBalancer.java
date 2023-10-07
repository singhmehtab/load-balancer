package com.project.loadbalancer.load_balancer;

import com.project.loadbalancer.load_balancer.algorithm.ILoadBalancerAlgorithm;
import com.project.loadbalancer.load_balancer.storage.ILoadBalancerStorage;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class LoadBalancer implements ILoadBalancer {

    @Value("${load.balancer.urls}")
    private String[] loadBalancerUrls;

    @Value("${storage.type:in-memory}")
    private String storageType;

    @Value("${load.balancing.type:round-robin}")
    private String loadBalancingType;

    @Autowired
    public void setStorageType(ApplicationContext applicationContext){
        this.iLoadBalancerStorage = (ILoadBalancerStorage) applicationContext.getBean(storageType);
    }

    @Autowired
    public void setLoadBalancingType(ApplicationContext applicationContext){
        this.iLoadBalancerAlgorithm = (ILoadBalancerAlgorithm) applicationContext.getBean(loadBalancingType);
    }

    private ILoadBalancerStorage iLoadBalancerStorage;

    private ILoadBalancerAlgorithm iLoadBalancerAlgorithm;

    @PostConstruct()
    private void postConstruct(){
        iLoadBalancerStorage.addUrls(Arrays.stream(loadBalancerUrls).toList());
    }

    @Override
    public synchronized String getAvailableMachineUrl() {
        return iLoadBalancerAlgorithm.getUrl();
    }

    @Override
    public String[] getAllMachineUrls() {
        return loadBalancerUrls;
    }

    @Override
    public void addMachineToAvailable(String url) {
        if(!iLoadBalancerStorage.urlPresent(url)){
            iLoadBalancerStorage.addNewUrl(url);
        }
    }

    @Override
    public void deleteMachineFromAvailable(String url) {
        if(iLoadBalancerStorage.urlPresent(url)){
            iLoadBalancerStorage.deleteUrl(url);
        }
    }

    @Override
    public List<String> getAllAvailableMachineUrls() {
        return iLoadBalancerStorage.getAllAvailableMachineUrls();
    }
}
