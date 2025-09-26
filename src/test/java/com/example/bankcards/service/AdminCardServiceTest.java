package com.example.bankcards.service;

import com.example.bankcards.repository.CardRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AdminCardServiceTest {

    private AdminCardService adminCardService;

    @Mock
    private CardRepository cardRepository;

}
