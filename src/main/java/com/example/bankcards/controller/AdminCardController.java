package com.example.bankcards.controller;

import com.example.bankcards.entity.Card;
import com.example.bankcards.mappers.CardMapper;
import com.example.bankcards.service.AdminCardService;
import generated.com.example.bankcards.api.AdminCardApi;
import generated.com.example.bankcards.api.model.CardDto;
import generated.com.example.bankcards.api.model.CardWithUserIdDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class AdminCardController implements AdminCardApi {

    private final AdminCardService adminCardService;
    private final CardMapper cardMapper;

    @Override
    public void blockCard(UUID id) {
        adminCardService.blockCard(id);
    }

    @Override
    public void createCard(CardWithUserIdDto cardWithUserIdDto) {
        Card card = cardMapper.map(cardWithUserIdDto);
        card.setCardBin(card.getCardNumber().substring(0,6));
        adminCardService.createCard(card);
    }

    @Override
    public void deleteCard(UUID id) {
        adminCardService.deleteCard(id);
    }

    @Override
    public List<CardWithUserIdDto> getAllCards(Integer page, Integer size) {
        List<Card> cardList = adminCardService.getCards(page, size);
        return cardList.stream().map(cardMapper::map).collect(Collectors.toList());
    }

    @Override
    public CardWithUserIdDto getCardById(UUID id) {
        Card card = adminCardService.getCard(id);
        return cardMapper.map(card);
    }

    @Override
    public List<CardWithUserIdDto> getCardsToBlock(Integer page, Integer size) {
        List<Card> cardListToBlock = adminCardService.getToBlockCards(page, size);
        return cardListToBlock.stream().map(cardMapper::map).collect(Collectors.toList());
    }

    @Override
    public void updateCard(UUID id, CardDto cardDto) {
        Card card = cardMapper.map(cardDto);
        adminCardService.updateCard(id, card);
    }

}
