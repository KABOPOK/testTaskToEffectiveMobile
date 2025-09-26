package com.example.bankcards;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;

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
    private static final Role roleUser = new Role(UUID.randomUUID(),"ROLE_USER",null);
    private static final Role roleAdmin = new Role(UUID.randomUUID(),"ROLE_USER",null);

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
}
