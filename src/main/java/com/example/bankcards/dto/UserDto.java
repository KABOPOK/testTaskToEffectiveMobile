package com.example.bankcards.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class UserDto {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private UUID id;

    private String name;

    private String login;

    private String password;

    public enum StatusEnum {

        ACTIVE("ACTIVE"),

        BLOCKED("BLOCKED");

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

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private StatusEnum status;

    @Valid
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private List<String> roles = new ArrayList<>();

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private java.time.Instant createdAt;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private java.time.Instant updatedAt;

}
