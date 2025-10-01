package com.example.bankcards.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ExceptionBodyDto {

    private List<ApiErrorDto> errors;

}
