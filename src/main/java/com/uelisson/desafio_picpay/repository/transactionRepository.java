package com.uelisson.desafio_picpay.repository;

import com.uelisson.desafio_picpay.domain.transaction.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface transactionRepository extends JpaRepository<Transaction, Long> {
}
