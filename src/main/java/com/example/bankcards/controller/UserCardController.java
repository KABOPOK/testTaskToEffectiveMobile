package com.example.bankcards.controller;

import com.example.bankcards.entity.Card;
import com.example.bankcards.mappers.CardMapper;
import com.example.bankcards.service.UserCardService;
import generated.com.example.bankcards.api.UserCardApi;
import generated.com.example.bankcards.api.model.BalanceDto;
import generated.com.example.bankcards.api.model.CardDto;
import generated.com.example.bankcards.api.model.TransferDataDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class UserCardController implements UserCardApi {

    private final UserCardService userCardService;
    private final CardMapper cardMapper;

    @Override
    public List<CardDto> getAllUserCards() {
        List<Card> cardList = userCardService.getAllCards();
        return cardList.stream().map(cardMapper::mapToCardDto).toList();
    }

    @Override
    public BalanceDto getCardBalance(UUID id) {
        BalanceDto balanceDto = new BalanceDto();
        balanceDto.setBalance(userCardService.getCardBalance(id).toString());
        return balanceDto;
    }

    @Override
    public CardDto getCardById_0(UUID id) {
        return cardMapper.mapToCardDto(userCardService.getCard(id));
    }

    @Override
    public List<CardDto> getCards(Integer page, Integer size, String cardNumber) {
        return userCardService.
                getCardsByCardNumber(page, size, cardNumber).stream().map(cardMapper::mapToCardDto).collect(Collectors.toList());
    }

    @Override
    public void requestCardBlock(UUID id) {
        userCardService.requestCardBlock(id);
    }

    @Override
    public void transferBetweenCards(TransferDataDto transferDataDto) {
        userCardService.transferMoney(transferDataDto.getFromCardId(), transferDataDto.getToCardId(), BigDecimal.valueOf(transferDataDto.getAmount()));
    }
}
