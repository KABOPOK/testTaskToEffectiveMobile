package com.example.bankcards.service;

import com.example.bankcards.Generator;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.CardEncryptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserCardServiceTest {

    @InjectMocks
    private UserCardService userCardService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private CardEncryptor cardEncryptor;

    private User user;
    private Card activeCard;
    private Card blockedCard;

    @BeforeEach
    void setUp() {
        user = Generator.generateUser();
        activeCard = Generator.generateCard(user);
        activeCard.setStatus("ACTIVE");
        blockedCard = Generator.generateCard(user);
        blockedCard.setStatus("BLOCKED");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(user.getLogin());
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(context);
        when(userRepository.findByLogin(user.getLogin())).thenReturn(Optional.of(user));
    }

    // ---------------- getCardBalance ----------------
    @Test
    void getCardBalance_shouldReturnBalance_whenCardIsActiveAndBelongsToUser() {
        when(cardRepository.findById(activeCard.getId())).thenReturn(Optional.of(activeCard));

        BigDecimal balance = userCardService.getCardBalance(activeCard.getId());

        assertThat(balance).isEqualTo(BigDecimal.valueOf(1000));
    }

    @Test
    void getCardBalance_shouldThrow_whenCardIsBlocked() {
        when(cardRepository.findById(blockedCard.getId())).thenReturn(Optional.of(blockedCard));

        assertThrows(AccessDeniedException.class, () -> userCardService.getCardBalance(blockedCard.getId()));
    }

    // ---------------- getCard ----------------
    @Test
    void getCard_shouldReturnDecryptedCard_whenActiveAndOwnedByUser() {
        when(cardRepository.findById(activeCard.getId())).thenReturn(Optional.of(activeCard));

        Card result = userCardService.getCard(activeCard.getId());

        assertThat(result).isEqualTo(activeCard);
        verify(cardEncryptor).decryptCardAndHidden(activeCard);
    }

    @Test
    void getCard_shouldThrow_whenCardBlocked() {
        when(cardRepository.findById(blockedCard.getId())).thenReturn(Optional.of(blockedCard));

        assertThrows(AccessDeniedException.class, () -> userCardService.getCard(blockedCard.getId()));
    }

    // ---------------- getCardsByCardNumber ----------------
    @Test
    void getCardsByCardNumber_shouldReturnDecryptedCards() {
        when(cardRepository.findAllByCardBinStartingWithAndUser(any(), eq(user), any()))
                .thenReturn(new PageImpl<>(List.of(activeCard)));
        when(cardEncryptor.decryptCardAndHidden(activeCard)).then(inv -> inv.getArgument(0));

        List<Card> result = userCardService.getCardsByCardNumber(0, 10, "123");

        assertThat(result).hasSize(1).contains(activeCard);
        verify(cardEncryptor).decryptCardAndHidden(activeCard);
    }

    // ---------------- requestCardBlock ----------------
    @Test
    void requestCardBlock_shouldUpdateStatusToToBlock() {
        when(cardRepository.findById(activeCard.getId())).thenReturn(Optional.of(activeCard));

        userCardService.requestCardBlock(activeCard.getId());

        assertThat(activeCard.getStatus()).isEqualTo("TO_BLOCK");
        verify(cardRepository).save(activeCard);
    }

    @Test
    void requestCardBlock_shouldThrow_whenCardBlocked() {
        when(cardRepository.findById(blockedCard.getId())).thenReturn(Optional.of(blockedCard));

        assertThrows(AccessDeniedException.class, () -> userCardService.requestCardBlock(blockedCard.getId()));
    }

    // ---------------- transferMoney ----------------
    @Test
    void transferMoney_shouldMoveBalance_whenEnoughMoneyAndCardsBelongToUser() {
        Card toCard = Generator.generateCard(user);
        toCard.setBalance(BigDecimal.valueOf(50));
        when(cardRepository.findById(activeCard.getId())).thenReturn(Optional.of(activeCard));
        when(cardRepository.findById(toCard.getId())).thenReturn(Optional.of(toCard));

        userCardService.transferMoney(activeCard.getId(), toCard.getId(), BigDecimal.valueOf(40));

        assertThat(activeCard.getBalance()).isEqualTo(BigDecimal.valueOf(960));
        assertThat(toCard.getBalance()).isEqualTo(BigDecimal.valueOf(90));
        verify(cardRepository).save(activeCard);
        verify(cardRepository).save(toCard);
    }

    @Test
    void transferMoney_shouldThrow_whenNotEnoughBalance() {
        Card toCard = Generator.generateCard(user);
        toCard.setBalance(BigDecimal.valueOf(50));

        when(cardRepository.findById(activeCard.getId())).thenReturn(Optional.of(activeCard));
        when(cardRepository.findById(toCard.getId())).thenReturn(Optional.of(toCard));

        assertThrows(AccessDeniedException.class,
                () -> userCardService.transferMoney(activeCard.getId(), toCard.getId(), BigDecimal.valueOf(2000)));
        assertThat(activeCard.getBalance()).isEqualTo(BigDecimal.valueOf(1000));
        assertThat(toCard.getBalance()).isEqualTo(BigDecimal.valueOf(50));
    }
}

