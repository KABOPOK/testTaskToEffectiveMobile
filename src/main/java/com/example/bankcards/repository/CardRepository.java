package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CardRepository extends JpaRepository<Card, UUID> {
    Optional<Card> findById(UUID id);
    Page<Card> findAll(Pageable pageable);
    Page<Card> findAllByStatus(String status, Pageable pageable);

    Page<Card> findAllByCardBinStartingWithAndUser(String cardNumber, User user, Pageable pageable);

    Optional<Card> findByCardNumber(String cardNumber);

    List<Card> findAllByExpirationDateBeforeAndStatusNot(LocalDate now, String expired);
    List<Card> findCardByUser(User user);
}
