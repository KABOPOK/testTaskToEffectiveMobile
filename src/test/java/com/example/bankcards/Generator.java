package com.example.bankcards;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import generated.com.example.bankcards.api.model.AuthDataDto;
import generated.com.example.bankcards.api.model.CardDto;
import generated.com.example.bankcards.api.model.CardWithUserIdDto;
import generated.com.example.bankcards.api.model.TokenDto;
import generated.com.example.bankcards.api.model.TransferDataDto;
import generated.com.example.bankcards.api.model.UserAdminUpdateDto;
import generated.com.example.bankcards.api.model.UserDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class Generator {
    public static String randomStringGenerator(int length) {
        StringBuilder sb = new StringBuilder(length);
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        for (int i = 0; i < length; ++i) {
            sb.append(rnd.nextInt(0, 9));
        }
        return sb.toString();
    }
    public static final Role roleUser = new Role(UUID.randomUUID(),"ROLE_USER",null);

    public static Card generateCard() {
        Card card = new Card();
        card.setId(UUID.randomUUID());
        card.setCardNumber(randomStringGenerator(16));
        card.setCardBin(card.getCardNumber().substring(0, 6));
        card.setOwnerName("John Doe");
        card.setExpirationDate(LocalDate.now().plusYears(3));
        card.setStatus("ACTIVE");
        card.setBalance(BigDecimal.valueOf(1000));
        card.setUser(generateUser());
        return card;
    }
    public static Card generateCard(User user) {
        Card card = new Card();
        card.setId(UUID.randomUUID());
        card.setCardNumber(randomStringGenerator(16));
        card.setCardBin(card.getCardNumber().substring(0, 6));
        card.setOwnerName("John Doe");
        card.setExpirationDate(LocalDate.now().plusYears(3));
        card.setStatus("ACTIVE");
        card.setBalance(BigDecimal.valueOf(1000));
        card.setUser(user);
        return card;
    }

    public static User generateUser() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setName("panda");
        user.setLogin(randomStringGenerator(10));
        user.setPassword(randomStringGenerator(10));
        user.setStatus("ACTIVE");
        user.setRoles(List.of(roleUser));
        return user;
    }

    public static CardWithUserIdDto generateCardWithUserIdDto(Card card) {
        CardWithUserIdDto dto = new CardWithUserIdDto();
        dto.setId(card.getId());
        dto.setUserId(card.getUser().getId());
        dto.setCardNumber(card.getCardNumber());
        dto.setOwnerName(card.getOwnerName());
        dto.setExpirationDate(card.getExpirationDate());
        dto.setStatus(card.getStatus());
        return dto;

    }

    public static UserDto generateUserDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setLogin(user.getLogin());
        dto.setPassword(user.getPassword());
        dto.setStatus(UserDto.StatusEnum.valueOf(user.getStatus()));
        dto.setRoles(List.of("ROLE_USER"));
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }

    public static UserAdminUpdateDto generateUserAdminUpdateDto(User user) {
        UserAdminUpdateDto dto = new UserAdminUpdateDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setLogin(user.getLogin());
        dto.setPassword(user.getPassword());
        dto.setStatus(UserAdminUpdateDto.StatusEnum.valueOf(user.getStatus()));
        dto.setRoles(List.of("ROLE_USER"));
        return dto;
    }
    public static AuthDataDto generateAuthDataDto(User user) {
        AuthDataDto authDataDto = new AuthDataDto();
        authDataDto.setLogin(user.getLogin());
        authDataDto.setPassword(user.getPassword());
        return authDataDto;
    }

    public static TokenDto generateTokenDto() {
        TokenDto tokenDto = new TokenDto();
        tokenDto.setToken("jwt-token");
        return tokenDto;
    }

    public static CardDto generateCardDto(Card card) {
        CardDto dto = new CardDto();
        dto.setId(card.getId());
        dto.setCardNumber(card.getCardNumber());
        dto.setStatus(card.getStatus());
        dto.setBalance(Double.parseDouble(card.getBalance().toString()));
        dto.setCreatedAt(card.getCreatedAt());
        dto.setUpdatedAt(card.getUpdatedAt());
        return dto;
    }

    public static TransferDataDto generateTransferDataDto() {
        TransferDataDto transferDataDto = new TransferDataDto();
        transferDataDto.setFromCardId(UUID.randomUUID());
        transferDataDto.setToCardId(UUID.randomUUID());
        transferDataDto.setAmount(200.0);
        return transferDataDto;
    }
}
