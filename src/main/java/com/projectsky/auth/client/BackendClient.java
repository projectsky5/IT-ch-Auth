package com.projectsky.auth.client;

import com.projectsky.auth.dto.UserCreateRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class BackendClient {

    private final RestTemplate restTemplate;

    public BackendClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Value("${itbackend.url}")
    private String backendUrl;

    public void createUserInBackend(UserCreateRequest request){
        String url = backendUrl + "/api/internal/users";
        restTemplate.postForEntity(url, new HttpEntity<>(request), Void.class);
    }
}
