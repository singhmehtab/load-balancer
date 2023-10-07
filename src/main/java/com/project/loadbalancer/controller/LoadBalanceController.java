package com.project.loadbalancer.controller;

import com.project.loadbalancer.load_balancer.ILoadBalancer;
import com.project.loadbalancer.restAPI.RestApi;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Enumeration;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class LoadBalanceController {

    private final ILoadBalancer iLoadBalancer;

    @Autowired
    public LoadBalanceController(ILoadBalancer iLoadBalancer){
        this.iLoadBalancer = iLoadBalancer;
    }

    @RequestMapping(value = "/**")
    public Object serveRequest(HttpServletRequest httpServletRequest){
        String requestBody = null;
        try {
            requestBody = httpServletRequest.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        try {
            ResponseEntity<?> responseEntity = RestApi.performRestRequest(getUri(httpServletRequest), HttpMethod.valueOf(httpServletRequest.getMethod()), getHeaders(httpServletRequest), requestBody);
            return responseEntity.getBody();
        }
        catch (Exception e){
            return e.getMessage();
        }
    }

    private HttpHeaders getHeaders(HttpServletRequest httpServletRequest){
        HttpHeaders headers = new HttpHeaders();
        Enumeration<String> enumeration = httpServletRequest.getHeaderNames();
        while (enumeration.hasMoreElements()){
            String nextElement = enumeration.nextElement();
            headers.add(nextElement, httpServletRequest.getHeader(nextElement));
        }
        return headers;
    }

    private String getUri(HttpServletRequest httpServletRequest){
        String url = iLoadBalancer.getAvailableMachineUrl();
        return "http://" + url + httpServletRequest.getRequestURI() + "?" + httpServletRequest.getQueryString();
    }

}
