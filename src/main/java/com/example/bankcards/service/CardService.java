package com.example.bankcards.service;

import com.example.bankcards.entity.Card;
import com.example.bankcards.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CardService extends DefaultService {
    private final CardRepository cardRepository;

    public void blockCard(UUID id) {
        Card card = getOrThrow(id, cardRepository::findById);
        card.setStatus("BLOKED");
        cardRepository.save(card);
    }
}
