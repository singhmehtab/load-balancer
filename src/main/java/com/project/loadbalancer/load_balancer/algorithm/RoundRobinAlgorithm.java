package com.project.loadbalancer.load_balancer.algorithm;

import com.project.loadbalancer.load_balancer.storage.ILoadBalancerStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service("round-robin")
public class RoundRobinAlgorithm implements ILoadBalancerAlgorithm{

    @Value("${storage.type:in-memory}")
    private String storageType;

    private static final String POINTER_KEY_NAME = "round-robin-pointer-key";

    private ILoadBalancerStorage iLoadBalancerStorage;

    @Autowired
    public void setStorageType(ApplicationContext applicationContext){
        iLoadBalancerStorage = (ILoadBalancerStorage) applicationContext.getBean(storageType);
    }

    @Override
    public synchronized String getUrl() {
        Integer pointer = getPointer();
        if(pointer == null) pointer = 0;
        int storageSize = iLoadBalancerStorage.getNumberOfAvailableUrls();
        if(pointer > storageSize - 1 ) pointer = 0;
        iLoadBalancerStorage.updateValue(POINTER_KEY_NAME, String.valueOf(pointer + 1));
        return iLoadBalancerStorage.getValueFromIndex(pointer);

    }

    private Integer getPointer(){
        String pointer = iLoadBalancerStorage.getValue(POINTER_KEY_NAME);
        if(Objects.isNull(pointer)) return null;
        return Integer.parseInt(pointer);
    }

}
