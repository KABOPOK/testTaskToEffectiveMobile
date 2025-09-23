package com.example.bankcards.controller;

import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.service.CardService;
import generated.com.example.bankcards.api.AdminCardApi;
import generated.com.example.bankcards.api.AuthApi;
import generated.com.example.bankcards.api.model.CardWithUserIdDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class AdminCardController implements AdminCardApi {

    private final CardService cardService;

    @Override
    public void blockCard(UUID id) {
        cardService.blockCard(id);
    }

    @Override
    public void createCard(CardWithUserIdDto cardWithUserIdDto) {

    }

    @Override
    public void deleteCard(UUID id) {

    }

    @Override
    public List<CardWithUserIdDto> getAllCards() {
        return List.of();
    }

    @Override
    public CardWithUserIdDto getCardById(UUID id) {
        return null;
    }

    @Override
    public List<CardWithUserIdDto> getCardsToBlock() {
        return List.of();
    }

    @Override
    public void updateCard(UUID id, CardWithUserIdDto cardWithUserIdDto) {

    }
}
