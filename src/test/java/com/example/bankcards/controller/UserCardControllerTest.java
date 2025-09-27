package com.example.bankcards.controller;

import com.example.bankcards.Generator;
import com.example.bankcards.entity.Card;
import com.example.bankcards.mappers.CardMapper;
import com.example.bankcards.service.UserCardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import generated.com.example.bankcards.api.model.BalanceDto;
import generated.com.example.bankcards.api.model.CardDto;
import generated.com.example.bankcards.api.model.TransferDataDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.math.BigDecimal;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserCardControllerTest {

    @InjectMocks
    private UserCardController controller;

    @Mock
    private UserCardService userCardService;

    @Mock
    private CardMapper cardMapper;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private Card card;
    private CardDto cardDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        card = Generator.generateCard();
        cardDto = Generator.generateCardDto(card);
    }

    @Test
    void getCardBalance_shouldReturnBalanceDto() throws Exception {
        when(userCardService.getCardBalance(card.getId())).thenReturn(card.getBalance());

        String jsonResponse = mockMvc.perform(get("/api/user/card/balance/{id}", card.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        BalanceDto result = objectMapper.readValue(jsonResponse, BalanceDto.class);

        assertThat(result.getBalance()).isEqualTo(card.getBalance().toString());
    }

    @Test
    void getCardById_shouldReturnDto() throws Exception {
        when(userCardService.getCard(card.getId())).thenReturn(card);
        when(cardMapper.mapToCardDto(card)).thenReturn(cardDto);

        String jsonResponse = mockMvc.perform(get("/api/user/card/{id}", card.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        CardDto result = objectMapper.readValue(jsonResponse, CardDto.class);

        assertThat(result).isEqualTo(cardDto);
    }

    @Test
    void getCards_shouldReturnDtoList() throws Exception {
        when(userCardService.getCardsByCardNumber(0, 10, "123")).thenReturn(List.of(card));
        when(cardMapper.mapToCardDto(card)).thenReturn(cardDto);

        String jsonResponse = mockMvc.perform(get("/api/user/card/get_cards_by_number")
                        .param("page", "0")
                        .param("size", "10")
                        .param("cardNumber", "123"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        List<CardDto> resultList = objectMapper.readValue(
                jsonResponse,
                objectMapper.getTypeFactory().constructCollectionType(List.class, CardDto.class)
        );

        assertThat(resultList).containsExactly(cardDto);
    }

    @Test
    void requestCardBlock_shouldReturnOk() throws Exception {
        mockMvc.perform(put("/api/user/card/request_to_block/{id}", card.getId()))
                .andExpect(status().isOk());

        verify(userCardService).requestCardBlock(card.getId());
    }

    @Test
    void transferBetweenCards_shouldReturnOk() throws Exception {
        TransferDataDto transferDataDto = Generator.generateTransferDataDto();

        mockMvc.perform(put("/api/user/card/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferDataDto)))
                .andExpect(status().isOk());

        verify(userCardService).transferMoney(
                transferDataDto.getFromCardId(),
                transferDataDto.getToCardId(),
                BigDecimal.valueOf(transferDataDto.getAmount())
        );
    }

}
