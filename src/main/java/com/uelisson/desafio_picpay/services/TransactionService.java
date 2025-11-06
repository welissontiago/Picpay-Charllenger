package com.uelisson.desafio_picpay.services;

import com.uelisson.desafio_picpay.domain.User.User;
import com.uelisson.desafio_picpay.domain.transaction.Transaction;
import com.uelisson.desafio_picpay.dto.TransactionDTO;
import com.uelisson.desafio_picpay.repository.transactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class TransactionService {

    @Autowired
    private UserService userService;

    @Autowired
    transactionRepository transactionRepository;

    @Autowired
    private notificationService notificationService;

    @Autowired
    private RestTemplate restTemplate;

    public Transaction createTransaction(TransactionDTO transaction) throws Exception {
        User sender = this.userService.findUserById(transaction.senderId());
        User receiver = this.userService.findUserById(transaction.receiverId());

        userService.validateTransaction(sender, transaction.value());

        boolean isAuthorized = this.authorizedTransaction(sender, transaction.value());
        if(!isAuthorized) {
            throw new Exception("Transaction is not authorized");
        }

        Transaction newTransaction = new Transaction();
        newTransaction.setAmount(transaction.value());
        newTransaction.setSender(sender);
        newTransaction.setReceiver(receiver);
        newTransaction.setTimestamp(LocalDateTime.now());

        sender.setBalance(sender.getBalance().subtract(transaction.value()));
        receiver.setBalance(receiver.getBalance().add(transaction.value()));

        this.transactionRepository.save(newTransaction);
        this.userService.saveUser(sender);
        this.userService.saveUser(receiver);
        this.notificationService.sendNotification(sender, "Transaction request successful");
        this.notificationService.sendNotification(receiver, "New transaction successful");
        return newTransaction;
    }

    public boolean authorizedTransaction(User sender, BigDecimal value) {
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(
                    "https://util.devi.tools/api/v2/authorize", Map.class
            );
            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> body = response.getBody();
                if (body != null && body.containsKey("data")) {
                    Map<String, Object> data = (Map<String, Object>) body.get("data");
                    Object auth = data.get("authorization");
                    return Boolean.TRUE.equals(auth) || "true".equalsIgnoreCase(String.valueOf(auth));
                }
            }
            return false;

        } catch (HttpClientErrorException.Forbidden e) {
            System.out.println("Transação não autorizada pelo serviço externo");
            return false;

        } catch (Exception e) {
            System.out.println("Erro ao consultar autorização: " + e.getMessage());
            return false;
        }
    }

}
