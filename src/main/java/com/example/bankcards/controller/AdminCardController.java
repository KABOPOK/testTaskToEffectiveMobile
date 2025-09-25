package com.example.bankcards.controller;

import com.example.bankcards.entity.Card;
import com.example.bankcards.mappers.CardMapper;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.service.CardService;
import generated.com.example.bankcards.api.AdminCardApi;
import generated.com.example.bankcards.api.AuthApi;
import generated.com.example.bankcards.api.model.CardWithUserIdDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class AdminCardController implements AdminCardApi {

    private final CardService cardService;
    private final CardMapper cardMapper;

    @Override
    public void blockCard(UUID id) {
        cardService.blockCard(id);
    }

    @Override
    public void createCard(CardWithUserIdDto cardWithUserIdDto) {
        Card card = cardMapper.map(cardWithUserIdDto);
        cardService.createCard(card);
    }

    @Override
    public void deleteCard(UUID id) {
        cardService.deleteCard(id);
    }

    @Override
    public List<CardWithUserIdDto> getAllCards(Integer page, Integer size) {
        List<Card> cardList = cardService.getCards(page, size);
        return cardList.stream().map(cardMapper::map).collect(Collectors.toList());
    }

    @Override
    public CardWithUserIdDto getCardById(UUID id) {
        Card card = cardService.getCard(id);
        return cardMapper.map(card);
    }

    @Override
    public List<CardWithUserIdDto> getCardsToBlock(Integer page, Integer size) {
        List<Card> cardListToBlock = cardService.getToBlockCards(page, size);
        return cardListToBlock.stream().map(cardMapper::map).collect(Collectors.toList());
    }

    @Override
    public void updateCard(UUID id, CardWithUserIdDto cardWithUserIdDto) {
        Card card = cardMapper.map(cardWithUserIdDto);
        cardService.updateCard(id, card);
    }

}
