package com.project.loadbalancer.restAPI;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Objects;

public class RestApi {

    public static ResponseEntity<?> performRestRequest(String uri, HttpMethod httpMethod, HttpHeaders headers, String requestBody){
        RestTemplate restTemplate = new RestTemplateBuilder().setConnectTimeout(Duration.ofMillis(500)).setReadTimeout(Duration.ofMillis(500)).build();
        if(Objects.isNull(headers)) headers = new HttpHeaders();
        HttpEntity<String> entity;
        if(Objects.isNull(requestBody) || StringUtils.isEmpty(requestBody)) {
            entity = new HttpEntity<>(headers);
        }
        else{
            entity = new HttpEntity<>(requestBody, headers);
        }
        ResponseEntity responseEntity = restTemplate.exchange(uri, httpMethod, entity, String.class);
        return responseEntity;
    }

    public static String getUri(String host, String endPoint){
        return "http://" + host + "/" + endPoint;
    }

}
