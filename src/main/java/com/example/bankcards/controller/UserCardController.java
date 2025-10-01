package com.example.bankcards.controller;

import com.example.bankcards.dto.BalanceDto;
import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.TransferDataDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.mappers.CardMapper;
import com.example.bankcards.service.UserCardService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/card")
public class UserCardController {

    private final UserCardService userCardService;
    private final CardMapper cardMapper;

    @GetMapping("/get_all_cards")
    @Operation(summary = "Получить все карты пользователя (с пагинацией)")
    public List<CardDto> getAllUserCards(@RequestParam(defaultValue = "0") Integer page,
                                         @RequestParam(defaultValue = "10") Integer size) {
        List<Card> cardList = userCardService.getAllCards(page, size);
        return cardList.stream()
                .map(cardMapper::mapToCardDto)
                .toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить карту по ID")
    public CardDto getCardById(@PathVariable UUID id) {
        return cardMapper.mapToCardDto(userCardService.getCard(id));
    }

    @GetMapping("/get_cards_by_number")
    @Operation(summary = "Получить список своих карт по номеру карты")
    public List<CardDto> getCards(@RequestParam(defaultValue = "0") Integer page,
                                  @RequestParam(defaultValue = "10") Integer size,
                                  @RequestParam(required = false) String cardNumber) {
        return userCardService.getCardsByCardNumber(page, size, cardNumber)
                .stream()
                .map(cardMapper::mapToCardDto)
                .collect(Collectors.toList());
    }

    @PutMapping("/request_to_block/{id}")
    @Operation(summary = "Отправить запрос на блокировку карты")
    public void requestCardBlock(@PathVariable UUID id) {
        userCardService.requestCardBlock(id);
    }

    @GetMapping("/balance/{id}")
    @Operation(summary = "Получить баланс карты по ID")
    public BalanceDto getCardBalance(@PathVariable UUID id) {
        BalanceDto balanceDto = new BalanceDto();
        balanceDto.setBalance(userCardService.getCardBalance(id).toString());
        return balanceDto;
    }

    @PutMapping("/transfer")
    @Operation(summary = "Перевод средств между картами")
    public void transferBetweenCards(@Valid @RequestBody TransferDataDto transferDataDto) {
        userCardService.transferMoney(
                transferDataDto.getFromCardId(),
                transferDataDto.getToCardId(),
                BigDecimal.valueOf(transferDataDto.getAmount())
        );
    }

}
