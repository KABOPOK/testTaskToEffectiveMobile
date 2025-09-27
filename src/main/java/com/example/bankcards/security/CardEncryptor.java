package com.example.bankcards.security;

import com.example.bankcards.entity.Card;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
public class CardEncryptor {

    private static final String ALGORITHM = "AES";
    @Value("${jwt.secret}")
    private String secret;

    private final SecretKeySpec secretKeySpec;

    public CardEncryptor(@Value("${jwt.secret}") String secret) {
        this.secretKeySpec = new SecretKeySpec(secret.getBytes(), ALGORITHM);
    }

    public String encrypt(String plainText) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            return Base64.getEncoder().encodeToString(cipher.doFinal(plainText.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException("Error while encrypting card number", e);
        }
    }

    public String decrypt(String encryptedText) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            return new String(cipher.doFinal(Base64.getDecoder().decode(encryptedText)));
        } catch (Exception e) {
            throw new RuntimeException("Error while decrypting card number", e);
        }
    }

    public Card decryptCard(Card card) {
        String decrypted = decrypt(card.getCardNumber());
        card.setCardNumber(decrypted);
        return card;
    }

    public Card decryptCardAndHidden(Card card) {
        String decryptedCardNumber = decrypt(card.getCardNumber());
        String hideCardNumber = "************" + decryptedCardNumber
                .substring(decryptedCardNumber.length()-4);
        card.setCardNumber(hideCardNumber);
        return card;
    }

}

