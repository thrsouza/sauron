package com.github.thrsouza.sauron.infrastructure.integration.http.adapter;

import org.springframework.stereotype.Component;

import com.github.thrsouza.sauron.domain.customer.CustomerScoreService;

@Component
public class CustomerScoreServiceHttpAdapter implements CustomerScoreService {
    
    @Override
    public int getScoreByDocument(String document) {

        // Simulate a delay of 3 seconds
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return 500 + (int) (Math.random() * 500);
    }
}
