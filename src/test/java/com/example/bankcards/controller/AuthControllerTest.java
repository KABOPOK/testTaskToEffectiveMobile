package com.example.bankcards.controller;

import com.example.bankcards.Generator;
import com.example.bankcards.entity.User;
import com.example.bankcards.mappers.UserMapper;
import com.example.bankcards.service.AdminUserService;
import com.example.bankcards.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import generated.com.example.bankcards.api.model.AuthDataDto;
import generated.com.example.bankcards.api.model.TokenDto;
import generated.com.example.bankcards.api.model.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @InjectMocks
    private AuthController controller;

    @Mock
    private AdminUserService adminUserService;

    @Mock
    private AuthService authService;

    @Mock
    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        mockMvc =  MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void loginUser_shouldReturnTokenDto() throws Exception {
        User user = Generator.generateUser();
        AuthDataDto authDataDto = Generator.generateAuthDataDto(user);
        TokenDto expectedTokenDto = Generator.generateTokenDto();
        when(userMapper.mapFromAuthData(authDataDto)).thenReturn(user);
        when(authService.getToken(user)).thenReturn(expectedTokenDto.getToken());

        String jsonResponse = mockMvc.perform(post("/api/public/user/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authDataDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        TokenDto result = objectMapper.readValue(jsonResponse, TokenDto.class);

        assertThat(result).isEqualTo(expectedTokenDto);
    }

    @Test
    void registerUser_shouldCallAdminUserService() throws Exception {
        User user = Generator.generateUser();
        UserDto userDto = Generator.generateUserDto(user);
        when(userMapper.map(userDto)).thenReturn(user);

        mockMvc.perform(post("/api/public/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk());

        verify(adminUserService).createUser(user);
    }

}
