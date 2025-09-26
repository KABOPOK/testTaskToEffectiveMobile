package com.example.bankcards.service;

import com.example.bankcards.Generator;
import com.example.bankcards.entity.Card;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.security.CardEncryptor;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AdminCardServiceTest {

    @InjectMocks
    private AdminCardService adminCardService;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private CardEncryptor cardEncryptor;

    // ---------------- blockCard ----------------
    @Test
    void blockCard_shouldUpdateStatusToBlocked_whenCardExists() {
        Card card = Generator.generateCard();
        when(cardRepository.findById(card.getId())).thenReturn(Optional.of(card));
        when(cardRepository.save(any(Card.class))).thenAnswer(inv -> inv.getArgument(0));

        adminCardService.blockCard(card.getId());

        verify(cardRepository).findById(card.getId());
        verify(cardRepository).save(card);
        assertThat(card.getStatus()).isEqualTo("BLOСKED");
    }

    @Test
    void blockCard_shouldThrowException_whenCardNotFound() {
        UUID id = UUID.randomUUID();
        when(cardRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> adminCardService.blockCard(id));
    }

    // ---------------- createCard ----------------
    @Test
    void createCard_shouldSaveNewCard() {
        Card card = Generator.generateCard();
        when(cardEncryptor.encrypt(anyString())).thenAnswer(inv -> inv.getArgument(0));
        when(cardRepository.findByCardNumber(anyString())).thenReturn(Optional.empty());
        when(cardRepository.save(any(Card.class))).thenAnswer(inv -> inv.getArgument(0));

        adminCardService.createCard(card);

        verify(cardRepository).save(card);
    }

    @Test
    void createCard_shouldThrowException_whenUserIsNull() {
        Card card = Generator.generateCard(null);

        assertThrows(EntityNotFoundException.class, () -> adminCardService.createCard(card));
    }

    @Test
    void createCard_shouldThrowException_whenCardAlreadyExists() {
        Card card = Generator.generateCard();
        when(cardEncryptor.encrypt(anyString())).thenAnswer(inv -> inv.getArgument(0));
        when(cardRepository.findByCardNumber(anyString())).thenReturn(Optional.of(card));

        assertThrows(EntityExistsException.class, () -> adminCardService.createCard(card));
    }

    // ---------------- deleteCard ----------------
    @Test
    void deleteCard_shouldDeleteCard_whenCardExists() {
        Card card = Generator.generateCard();
        when(cardRepository.findById(card.getId())).thenReturn(Optional.of(card));
        doNothing().when(cardRepository).delete(card);

        adminCardService.deleteCard(card.getId());

        verify(cardRepository).delete(card);
    }

    @Test
    void deleteCard_shouldThrowException_whenCardNotFound() {
        UUID id = UUID.randomUUID();
        when(cardRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> adminCardService.deleteCard(id));
    }

    // ---------------- getCards ----------------
    @Test
    void getCards_shouldReturnDecryptedCards_whenCardsExist() {
        Card card = Generator.generateCard();
        Page<Card> page = new PageImpl<>(List.of(card));
        when(cardRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(cardEncryptor.decryptCard(any(Card.class))).thenAnswer(inv -> inv.getArgument(0));

        List<Card> result = adminCardService.getCards(0, 10);

        assertThat(result).hasSize(1).contains(card);
    }

    // ---------------- getToBlockCards ----------------
    @Test
    void getToBlockCards_shouldReturnDecryptedCards_whenCardsExist() {
        Card card = Generator.generateCard();
        Page<Card> page = new PageImpl<>(List.of(card));
        when(cardRepository.findAllByStatus(eq("TO_BLOCK"), any(Pageable.class))).thenReturn(page);
        when(cardEncryptor.decryptCard(any(Card.class))).thenAnswer(inv -> inv.getArgument(0));

        List<Card> result = adminCardService.getToBlockCards(0, 10);

        assertThat(result).hasSize(1).contains(card);
    }

    // ---------------- updateCard ----------------
    @Test
    void updateCard_shouldSaveUpdatedCard_whenCardExists() {
        Card oldCard = Generator.generateCard();
        Card newCard = Generator.generateCard();
        when(cardRepository.findById(oldCard.getId())).thenReturn(Optional.of(oldCard));
        when(cardEncryptor.encrypt(anyString())).thenAnswer(inv -> inv.getArgument(0));
        when(cardRepository.save(any(Card.class))).thenAnswer(inv -> inv.getArgument(0));

        adminCardService.updateCard(oldCard.getId(), newCard);

        verify(cardRepository).save(newCard);
        assertThat(newCard.getId()).isEqualTo(oldCard.getId());
    }

    @Test
    void updateCard_shouldThrowException_whenCardNotFound() {
        UUID id = UUID.randomUUID();
        Card newCard = Generator.generateCard();
        when(cardRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> adminCardService.updateCard(id, newCard));
    }

    // ---------------- getCard ----------------
    @Test
    void getCard_shouldReturnDecryptedCard_whenCardExists() {
        Card card = Generator.generateCard();
        when(cardRepository.findById(card.getId())).thenReturn(Optional.of(card));
        when(cardEncryptor.decryptCard(any(Card.class))).thenAnswer(inv -> inv.getArgument(0));

        Card result = adminCardService.getCard(card.getId());

        assertThat(result).isEqualTo(card);
    }

    @Test
    void getCard_shouldThrowException_whenCardNotFound() {
        UUID id = UUID.randomUUID();
        when(cardRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> adminCardService.getCard(id));
    }
}
