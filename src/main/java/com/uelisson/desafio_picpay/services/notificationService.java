package com.uelisson.desafio_picpay.services;

import com.uelisson.desafio_picpay.domain.User.User;
import com.uelisson.desafio_picpay.dto.NotificationDTO;
import com.uelisson.desafio_picpay.dto.TransactionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class notificationService {

    @Autowired
    private RestTemplate restTemplate;

    public void sendNotification(User user, String message) {
        try {
            Map<String, Object> payload = Map.of(
                    "email", user.getEmail(),
                    "message", message
            );
            restTemplate.postForEntity("https://util.devi.tools/api/v1/notify", payload, String.class);
            System.out.println("Notificação enviada para " + user.getEmail());
        } catch (Exception e) {
            System.out.println("Erro ao enviar notificação: " + e.getMessage());
        }
    }
}
