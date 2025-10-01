package com.example.bankcards.dto;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class ExceptionBody {

    private List<ApiError> errors = new ArrayList<>();

}
