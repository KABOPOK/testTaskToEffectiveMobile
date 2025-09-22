package com.example.bankcards.controller;

import generated.com.example.bankcards.api.CardsApi;
import generated.com.example.bankcards.api.model.CardDto;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.crypto.Data;
import java.util.List;
import java.util.UUID;

@RestController
public class CardController implements CardsApi {

    @Override
    public CardDto createCard(CardDto cardDto) {
        //secured data
        return new CardDto("fe","m",null,"ka");
    }

    @Override
    public void deleteCard(UUID id) {
        //admin data

    }

    @Override
    public List<CardDto> getAllCards() {
        //admin data
        return List.of();
    }

    @Override
    public CardDto getCardById(UUID id) {
        //secured data
        return null;
    }

    @Override
    public CardDto updateCard(UUID id, CardDto cardDto) {
        //admin data
        return null;
    }
}
