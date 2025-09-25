package com.example.bankcards.service;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class UserCardService extends DefaultService {
    private final UserRepository userRepository;
    private final CardRepository cardRepository;

    private void doesCardBelongToUser(Card card, User user){
        if(!user.getId().equals(card.getUser().getId())){
            throw new AccessDeniedException(
                    format("The card with id %s does not belong to user with id %s",
                            card.getUser().getId(), user.getId()));
        }
    }

    private void doesCardBlocked(Card card){
        if(!card.getStatus().equals("ACTIVE")){
            throw new AccessDeniedException(format("The card with id %s is blocked", card.getId()));
        }
    }

    private User getCurrentUser() {
        String userLogin = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByLogin(userLogin)
                .orElseThrow(() -> new InternalError("user not exist"));
    }

    public BigDecimal getCardBalance(UUID id) {
        Card card = getOrThrow(id, cardRepository::findById);
        User user = getCurrentUser();
        doesCardBelongToUser(card, user);
        doesCardBlocked(card);
        return card.getBalance();
    }

    public Card getCard(UUID id) {
        Card card = getOrThrow(id, cardRepository::findById);
        User user = getCurrentUser();
        doesCardBlocked(card);
        doesCardBelongToUser(card, user);
        return card;
    }

    public List<Card> getCardsByCardNumber(Integer page, Integer size, String cardNumber) {
        Card card = cardRepository.findByCardNumber(cardNumber).orElseThrow(
                () -> new EntityNotFoundException(format("Card with cardNumber %s not exist", cardNumber)));
        User user = getCurrentUser();
        doesCardBelongToUser(card, user);
        doesCardBlocked(card);

        Pageable pageable = PageRequest.of(page, size);
        Page<Card> pageOfCards = cardRepository.findAllByCardNumberStartingWithAndUser(cardNumber, user, pageable);
        return pageOfCards.getContent();
    }

    public void requestCardBlock(UUID id) {
        Card card = getOrThrow(id, cardRepository::findById);
        User user = getCurrentUser();
        doesCardBelongToUser(card, user);
        doesCardBlocked(card);

        card.setStatus("TO_BLOCK");
        cardRepository.save(card);
    }

    @Transactional
    public void transferMoney(UUID fromCardId, UUID toCardId, BigDecimal amount) {
        Card fromCard = getOrThrow(fromCardId, cardRepository::findById);
        Card toCard = getOrThrow(toCardId, cardRepository::findById);
        User user = getCurrentUser();
        doesCardBelongToUser(fromCard, user);
        doesCardBelongToUser(toCard, user);
        doesCardBlocked(fromCard);
        doesCardBlocked(toCard);
        BigDecimal reminder =  fromCard.getBalance().subtract(amount);
        if(reminder.compareTo(BigDecimal.ZERO) < 0) {
            throw new AccessDeniedException(format("Not enough money on the card with id %s", fromCardId));
        }

        fromCard.setBalance(reminder);
        toCard.setBalance(toCard.getBalance().add(amount));
        cardRepository.save(fromCard);
        cardRepository.save(toCard);
    }
}
