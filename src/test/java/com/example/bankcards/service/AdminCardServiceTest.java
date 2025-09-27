package com.example.bankcards.service;

import com.example.bankcards.Generator;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.security.CardEncryptor;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.Mockito.atLeastOnce;
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

    private User user;
    private Card activeCard;

    @BeforeEach
    void setUp() {
        user = Generator.generateUser();
        activeCard = Generator.generateCard(user);
    }

    // ---------------- blockCard ----------------
    @Test
    void blockCard_shouldBlockCard_whenCardExists() {
        when(cardRepository.findById(activeCard.getId())).thenReturn(Optional.of(activeCard));

        adminCardService.blockCard(activeCard.getId());

        assertThat(activeCard.getStatus()).isEqualTo("BLOCKED");
        verify(cardRepository).save(activeCard);
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
        when(cardRepository.findByCardNumber(anyString())).thenReturn(Optional.empty());
        when(cardEncryptor.encrypt(anyString())).thenAnswer(inv -> inv.getArgument(0));

        adminCardService.createCard(activeCard);

        verify(cardRepository).save(activeCard);
        verify(cardEncryptor, atLeastOnce()).encrypt(activeCard.getCardNumber());
    }

    @Test
    void createCard_shouldThrowException_whenUserIsNull() {
        Card card = Generator.generateCard(null);

        assertThrows(EntityNotFoundException.class, () -> adminCardService.createCard(card));
    }

    @Test
    void createCard_shouldThrowException_whenCardAlreadyExists() {
        when(cardEncryptor.encrypt(activeCard.getCardNumber())).thenReturn(activeCard.getCardNumber());
        when(cardRepository.findByCardNumber(activeCard.getCardNumber())).thenReturn(Optional.of(activeCard));

        assertThrows(EntityExistsException.class, () -> adminCardService.createCard(activeCard));
    }

    // ---------------- deleteCard ----------------
    @Test
    void deleteCard_shouldDeleteCard_whenCardExists() {
        when(cardRepository.findById(activeCard.getId())).thenReturn(Optional.of(activeCard));

        adminCardService.deleteCard(activeCard.getId());

        verify(cardRepository).delete(activeCard);
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
        Page<Card> page = new PageImpl<>(List.of(activeCard));
        when(cardRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(cardEncryptor.decryptCard(any(Card.class))).thenAnswer(inv -> inv.getArgument(0));

        List<Card> result = adminCardService.getCards(0, 10);

        assertThat(result).hasSize(1).contains(activeCard);
        verify(cardEncryptor).decryptCard(activeCard);
    }

    // ---------------- getToBlockCards ----------------
    @Test
    void getToBlockCards_shouldReturnDecryptedCards_whenCardsExist() {
        Card card = Generator.generateCard(user);
        card.setStatus("TO_BLOCK");
        Page<Card> page = new PageImpl<>(List.of(card));
        when(cardEncryptor.decryptCard(any(Card.class))).thenAnswer(inv -> inv.getArgument(0));
        when(cardRepository.findAllByStatus(eq("TO_BLOCK"), any(Pageable.class))).thenReturn(page);

        List<Card> result = adminCardService.getToBlockCards(0, 10);

        assertThat(result).hasSize(1).contains(card);
        assertThat(result.get(0).getStatus()).isEqualTo("TO_BLOCK");
        verify(cardEncryptor).decryptCard(card);
    }

    // ---------------- updateCard ----------------
    @Test
    void updateCard_shouldSaveUpdatedCard_whenCardExists() {
        Card newCard = Generator.generateCard(user);
        when(cardEncryptor.encrypt(anyString())).thenAnswer(inv -> inv.getArgument(0));
        when(cardRepository.findById(activeCard.getId())).thenReturn(Optional.of(activeCard));

        adminCardService.updateCard(activeCard.getId(), newCard);

        assertThat(newCard.getId()).isEqualTo(activeCard.getId());
        verify(cardRepository).save(newCard);
        verify(cardEncryptor).encrypt(newCard.getCardNumber());
    }

    @Test
    void updateCard_shouldThrowException_whenCardNotFound() {
        UUID id = UUID.randomUUID();
        when(cardRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> adminCardService.updateCard(id, activeCard));
    }

    // ---------------- getCard ----------------
    @Test
    void getCard_shouldReturnDecryptedCard_whenCardExists() {
        when(cardRepository.findById(activeCard.getId())).thenReturn(Optional.of(activeCard));
        when(cardEncryptor.decryptCard(any(Card.class))).thenAnswer(inv -> inv.getArgument(0));

        Card result = adminCardService.getCard(activeCard.getId());

        assertThat(result).isEqualTo(activeCard);
        verify(cardEncryptor).decryptCard(activeCard);
    }

    @Test
    void getCard_shouldThrowException_whenCardNotFound() {
        UUID id = UUID.randomUUID();
        when(cardRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> adminCardService.getCard(id));
    }

}
