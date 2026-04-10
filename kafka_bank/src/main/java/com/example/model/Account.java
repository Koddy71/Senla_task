package com.example.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "accounts")
public class Account {
    @Id
    private Long id;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    public Account(){};

    public Account(Long id, BigDecimal balance){
        this.id=id;
        this.balance=balance;
    }

    public void setId(Long id){
        this.id=id;
    }

    public Long getId(){
        return id;
    }

    public void setBalance(BigDecimal balance){
        this.balance=balance;
    }

    public BigDecimal getBalance(){
        return balance;
    }
}
