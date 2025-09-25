package com.example.bankcards.service;

import com.example.bankcards.entity.Card;
import com.example.bankcards.repository.CardRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class AdminCardService extends DefaultService {
    private final CardRepository cardRepository;

    public void blockCard(UUID id) {
        Card card = getOrThrow(id, cardRepository::findById);
        card.setStatus("BLOKED");
        card.setUpdatedAt(Instant.now());
        cardRepository.save(card);
    }

    public void createCard(Card card) {
        if(card.getUser() == null) throw new EntityNotFoundException(format(ENTITY_NOT_FOUND, "you send is"));
        //if user is blocked
        Card existedCard = cardRepository.findByCardNumber(card.getCardNumber()).orElse(null);
        if (existedCard != null) throw new EntityExistsException(format(ENTITY_ALREADY_EXIST,card.getId()));
        card.setCreatedAt(Instant.now());
        card.setUpdatedAt(Instant.now());
        cardRepository.save(card);
    }

    public void deleteCard(UUID id) {
        Card card = getOrThrow(id, cardRepository::findById);
        cardRepository.delete(card);
    }

    public List<Card> getCards(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Card> pageOfCards = cardRepository.findAll(pageable);
        return pageOfCards.getContent();
    }

    public List<Card> getToBlockCards(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Card> pageOfCards = cardRepository.findAllByStatus("TO_BLOCK", pageable);
        return pageOfCards.getContent();
    }

    public void updateCard(UUID id, Card updatedCard){
        Card card = getOrThrow(id, cardRepository::findById);
        updatedCard.setId(card.getId());
        updatedCard.setCreatedAt(card.getCreatedAt());
        updatedCard.setUpdatedAt(Instant.now());
        cardRepository.save(updatedCard);
    }

    public Card getCard(UUID id) {
        return getOrThrow(id, cardRepository::findById);
    }

}
