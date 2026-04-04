package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.model.MoneyTransfer;

public interface MoneyTransferRepository extends JpaRepository<MoneyTransfer, String> {
}
