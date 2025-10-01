package com.example.bankcards.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class TransferDataDto {

    private UUID fromCardId;

    private UUID toCardId;

    private Double amount;

}
