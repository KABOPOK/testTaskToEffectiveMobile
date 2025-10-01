package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.CardWithUserIdDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.mappers.CardMapper;
import com.example.bankcards.service.AdminCardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/card")
public class AdminCardController {

    private final AdminCardService adminCardService;
    private final CardMapper cardMapper;

    @PostMapping("/create")
    @Operation(summary = "Создать новую карту")
    @ApiResponse(responseCode = "201", description = "Создана карта")
    public void createCard(@Valid @RequestBody CardWithUserIdDto cardWithUserIdDto) {
        Card card = cardMapper.map(cardWithUserIdDto);
        card.setCardBin(card.getCardNumber().substring(0, 6));
        adminCardService.createCard(card);
    }

    @GetMapping("/get/{id}")
    @Operation(summary = "Получить карту по ID")
    public CardWithUserIdDto getCardById(@PathVariable UUID id) {
        Card card = adminCardService.getCard(id);
        return cardMapper.map(card);
    }

    @PutMapping("/update/{id}")
    @Operation(summary = "Обновить карту по ID")
    public void updateCard(@PathVariable UUID id,
                           @Valid @RequestBody CardDto cardDto) {
        Card card = cardMapper.map(cardDto);
        adminCardService.updateCard(id, card);
    }

    @PutMapping("/block/{id}")
    @Operation(summary = "Заблокировать карту по ID")
    public void blockCard(@PathVariable UUID id) {
        adminCardService.blockCard(id);
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Удалить карту по ID")
    public void deleteCard(@PathVariable UUID id) {
        adminCardService.deleteCard(id);
    }

    @GetMapping("/get_to_block")
    @Operation(summary = "Получить список карт, ожидающих блокировки (TO_BLOCK)")
    public List<CardWithUserIdDto> getCardsToBlock(@RequestParam(defaultValue = "0") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return adminCardService.getToBlockCards(page, size)
                .stream()
                .map(cardMapper::map)
                .collect(Collectors.toList());
    }

    @GetMapping("/get_all")
    @Operation(summary = "Получить список всех карт")
    public List<CardWithUserIdDto> getAllCards(@RequestParam(defaultValue = "0") Integer page,
                                               @RequestParam(defaultValue = "10") Integer size) {
        return adminCardService.getCards(page, size)
                .stream()
                .map(cardMapper::map)
                .collect(Collectors.toList());
    }

}
