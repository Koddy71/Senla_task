package com.example.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.model.Account;

import jakarta.persistence.LockModeType;

public interface AccountRepository extends JpaRepository<Account, Long>{    //интерфейс, потому что используется динамическое создание
                                                                            // реализаций CRUD методов для работы с БД
    @Lock(LockModeType.PESSIMISTIC_WRITE)       // другие не смогут изменить эту запись, пока текущая транзакция не завершится
    @Query("select a from Account a where a.id = :id")
    Optional<Account> findByIdForUpdate(@Param("id") Long id);  // Optional помогает избежать NullPointerException
} 
