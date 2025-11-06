package com.uelisson.desafio_picpay.services;

import com.uelisson.desafio_picpay.domain.User.User;
import com.uelisson.desafio_picpay.domain.User.UserType;
import com.uelisson.desafio_picpay.dto.UserDTO;
import com.uelisson.desafio_picpay.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public void validateTransaction(User sender, BigDecimal amount) throws Exception {
        if(sender.getUserType() == UserType.MERCHANT){
            throw new Exception("Usiario não está autorizado a enviar dinheiro!");
        }
        if(sender.getBalance().compareTo(amount) < 0){
            throw new Exception("O saldo insuficiente!");
        }
    }

    public User findUserById(Long id) throws Exception {
        return userRepository.findById(id).orElseThrow(() -> new Exception("Usuário não encontrado"));
    }

    public void saveUser(User user) throws Exception {
        this.userRepository.save(user);
    }

    public User createUser(UserDTO data) throws Exception {
        User newUser = new User(data);
        this.saveUser(newUser);
        return newUser;
    }

    public List<User> getAllUsers(){
        return this.userRepository.findAll();
    }
}
