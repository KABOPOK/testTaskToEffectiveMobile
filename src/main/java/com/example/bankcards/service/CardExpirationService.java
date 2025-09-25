package com.example.bankcards.service;

import com.example.bankcards.entity.Card;
import com.example.bankcards.repository.CardRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CardExpirationService {

    private final CardRepository cardRepository;

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void blockExpiredCards() {
        List<Card> expiredCards = cardRepository.findAllByExpirationDateBeforeAndStatusNot(
                LocalDate.now(), "EXPIRED");
        for (Card card : expiredCards) {
            card.setStatus("EXPIRED");
        }
        cardRepository.saveAll(expiredCards);
    }

}
