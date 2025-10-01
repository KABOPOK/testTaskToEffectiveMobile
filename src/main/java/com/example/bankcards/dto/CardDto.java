package com.example.bankcards.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class CardDto {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private UUID id;

    private String cardNumber;

    private String ownerName;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate expirationDate;

    public enum StatusEnum {
        ACTIVE("ACTIVE"),

        BLOCKED("BLOCKED"),

        TO_BLOCK("TO_BLOCK"),

        EXPIRED("EXPIRED");

        private String value;

        StatusEnum(String value) {
            this.value = value;
        }

        @JsonValue
        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static StatusEnum fromValue(String value) {
            for (StatusEnum b : StatusEnum.values()) {
                if (b.value.equals(value)) {
                    return b;
                }
            }
            throw new IllegalArgumentException("Unexpected value '" + value + "'");
        }
    }

    private StatusEnum status;

    private Double balance;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private java.time.Instant createdAt;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private java.time.Instant updatedAt;

}
