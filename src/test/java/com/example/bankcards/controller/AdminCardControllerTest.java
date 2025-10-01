package com.example.bankcards.controller;

import com.example.bankcards.Generator;
import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.CardWithUserIdDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.mappers.CardMapper;
import com.example.bankcards.service.AdminCardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AdminCardControllerTest {

    @InjectMocks
    private AdminCardController controller;

    @Mock
    private AdminCardService adminCardService;

    @Mock
    private CardMapper cardMapper;

    private Card card;
    private MockMvc mockMvc;
    private CardWithUserIdDto cardDto;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc =  MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        card = Generator.generateCard();
        cardDto = Generator.generateCardWithUserIdDto(card);
    }

    @Test
    void blockCard_shouldReturnOk() throws Exception {
        mockMvc.perform(put("/api/admin/card/block/{id}", card.getId()))
                .andExpect(status().isOk());

        verify(adminCardService).blockCard(card.getId());
    }

    @Test
    void createCard_shouldReturnOk() throws Exception {
        when(cardMapper.map(any(CardWithUserIdDto.class))).thenReturn(card);

        mockMvc.perform(post("/api/admin/card/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardDto)))
                .andExpect(status().isOk());

        verify(adminCardService).createCard(card);
    }

    @Test
    void deleteCard_shouldReturnOk() throws Exception {
        mockMvc.perform(delete("/api/admin/card/delete/{id}", card.getId()))
                .andExpect(status().isOk());

        verify(adminCardService).deleteCard(card.getId());
    }

    @Test
    void getAllCards_shouldReturnDtoList() throws Exception {
        when(adminCardService.getCards(0, 10)).thenReturn(List.of(card));
        when(cardMapper.map(card)).thenReturn(cardDto);

        String jsonResponse = mockMvc.perform(get("/api/admin/card/get_all")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        List<CardWithUserIdDto> cardList = objectMapper.readValue(
                jsonResponse,
                objectMapper.getTypeFactory().constructCollectionType(List.class, CardWithUserIdDto.class)
        );

        assertThat(cardList).containsExactly(cardDto);
    }

    @Test
    void getCardById_shouldReturnDto() throws Exception {
        when(adminCardService.getCard(card.getId())).thenReturn(card);
        when(cardMapper.map(card)).thenReturn(cardDto);

        String jsonResponse = mockMvc.perform(get("/api/admin/card/get/{id}", card.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        CardWithUserIdDto resultDto = objectMapper.readValue(jsonResponse, CardWithUserIdDto.class);

        assertThat(resultDto).isEqualTo(cardDto);
    }

    @Test
    void getCardsToBlock_shouldReturnDtoList() throws Exception {
        when(adminCardService.getToBlockCards(0, 10)).thenReturn(List.of(card));
        when(cardMapper.map(card)).thenReturn(cardDto);

        String jsonResponse = mockMvc.perform(get("/api/admin/card/get_to_block")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        List<CardWithUserIdDto> resultList = objectMapper.readValue(
                jsonResponse,
                objectMapper.getTypeFactory().constructCollectionType(List.class, CardWithUserIdDto.class)
        );

        assertThat(resultList).containsExactly(cardDto);
        assertThat(resultList.get(0).getStatus()).isEqualTo(cardDto.getStatus());
    }

    @Test
    void updateCard_shouldReturnOk() throws Exception {
        when(cardMapper.map(any(CardDto.class))).thenReturn(card);

        mockMvc.perform(put("/api/admin/card/update/{id}", card.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardDto)))
                .andExpect(status().isOk());

        verify(adminCardService).updateCard(card.getId(), card);
    }

}