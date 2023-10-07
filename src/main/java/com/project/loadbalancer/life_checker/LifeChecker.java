package com.project.loadbalancer.life_checker;

import com.project.loadbalancer.load_balancer.ILoadBalancer;
import com.project.loadbalancer.restAPI.RestApi;
import lombok.extern.slf4j.Slf4j;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Service("life-checker")
@Slf4j
public class LifeChecker {

    @Value("${life.check.healthCheckEndPoint:/}")
    private String healthCheckEndpoint;

    @Value("${health.check.time.interval:10000}")
    private long healthCheckTimeInterval;

    @Value("${print.machine.update.info:false}")
    private Boolean printMachineUpdateInfo;

    @Value("${print.machines.available.info:false}")
    private Boolean printMachinesAvailableInfo;

    private final ExecutorService executorService;

    private final ILoadBalancer iLoadBalancer;

    @Autowired
    public LifeChecker(ILoadBalancer iLoadBalancer,  @Value("${health.check.concurrency.threads:10}") int executorServiceThreads) {
        this.iLoadBalancer = iLoadBalancer;
        executorService = Executors.newFixedThreadPool(executorServiceThreads);
    }

    public void checkLifeAndUpdateUrls(){
        Runnable runnable =  () -> {
            List<Callable<Pair<String, Boolean>>> callableList = getHealthCheckCallables();
            List<Future<Pair<String, Boolean>>> futureList = null;
            try {
                futureList = executorService.invokeAll(callableList);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            for(Future<Pair<String, Boolean>> future : futureList){
                try {
                    Pair<String, Boolean> pair = future.get();
                    if(pair.getValue1() == Boolean.FALSE){
                        if(printMachineUpdateInfo){
                            log.info("Deleting machine with address:: " + pair.getValue0() + " if present");
                        }
                        iLoadBalancer.deleteMachineFromAvailable(pair.getValue0());
                    }
                    else{
                        if(printMachineUpdateInfo){
                            log.info("Adding machine with address:: " + pair.getValue0() + " if deleted");
                        }
                        iLoadBalancer.addMachineToAvailable(pair.getValue0());
                    }
                }
                catch (Exception e){
                    log.warn(e.getMessage());
                }

            }
            if(printMachinesAvailableInfo){
                StringBuilder availableMachines = new StringBuilder();
                for(String url: iLoadBalancer.getAllAvailableMachineUrls()){
                    availableMachines.append(url).append(" ");
                }
                log.info("Currently available machines are " + availableMachines);
            }
            log.info("Schedule Service to check health of machines and update availability completed at " + LocalDateTime.now());
        };
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        log.info("Starting Schedule Service to check health of machines and update availability");
        scheduledExecutorService.scheduleAtFixedRate(runnable,0, healthCheckTimeInterval, TimeUnit.MILLISECONDS);
    }

    private List<Callable<Pair<String, Boolean>>> getHealthCheckCallables() {
        String[] urls = iLoadBalancer.getAllMachineUrls();
        List<Callable<Pair<String, Boolean>>> callableList = new ArrayList<>();
        for(String url: urls){
            Callable<Pair<String, Boolean>> call = () -> {
                try {
                    ResponseEntity responseEntity = RestApi.performRestRequest(RestApi.getUri(url, healthCheckEndpoint), HttpMethod.GET, null, null);
                    Boolean response = responseEntity.getStatusCode().equals(HttpStatus.OK);
                    return new Pair<>(url, response);
                }
                catch (Exception e){
                    return new Pair<>(url, false);
                }

            };
            callableList.add(call);
        }
        return callableList;
    }

}
