package com.example.bankcards.controller;

import com.example.bankcards.Generator;
import com.example.bankcards.dto.UserAdminUpdateDto;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.mappers.UserMapper;
import com.example.bankcards.service.AdminUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AdminUserControllerTest {

    @InjectMocks
    private AdminUserController controller;

    @Mock
    private AdminUserService adminUserService;

    @Mock
    private UserMapper userMapper;

    private User user;
    private UserDto userDto;
    private UserAdminUpdateDto userAdminUpdateDto;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        user = Generator.generateUser();
        userDto = Generator.generateUserDto(user);
        userAdminUpdateDto = Generator.generateUserAdminUpdateDto(user);
    }

    @Test
    void blockUser_shouldReturnOk() throws Exception {
        mockMvc.perform(put("/api/admin/user/block/{id}", user.getId()))
                .andExpect(status().isOk());

        verify(adminUserService).blockUser(user.getId());
    }

    @Test
    void deleteUser_shouldReturnOk() throws Exception {
        mockMvc.perform(delete("/api/admin/user/delete/{id}", user.getId()))
                .andExpect(status().isOk());

        verify(adminUserService).deleteUser(user.getId());
    }

    @Test
    void getAllUsers_shouldReturnDtoList() throws Exception {
        when(adminUserService.getUsers()).thenReturn(List.of(user));
        when(userMapper.map(user)).thenReturn(userDto);

        String jsonResponse = mockMvc.perform(get("/api/admin/user/get_all_users"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<UserDto> resultList = objectMapper.readValue(
                jsonResponse,
                objectMapper.getTypeFactory().constructCollectionType(List.class, UserDto.class)
        );

        assertThat(resultList).containsExactly(userDto);
    }

    @Test
    void getUserById_shouldReturnDto() throws Exception {
        when(adminUserService.getUser(user.getId())).thenReturn(user);
        when(userMapper.map(user)).thenReturn(userDto);

        String jsonResponse = mockMvc.perform(get("/api/admin/user/get/{id}", user.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserDto resultDto = objectMapper.readValue(jsonResponse, UserDto.class);
        assertThat(resultDto).isEqualTo(userDto);
    }

    @Test
    void updateUser_shouldReturnOk() throws Exception {
        when(userMapper.mapFromUserAdminDto(any(UserAdminUpdateDto.class))).thenReturn(user);

        mockMvc.perform(put("/api/admin/user/update/{id}", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userAdminUpdateDto)))
                .andExpect(status().isOk());

        verify(adminUserService).updateUser(user.getId(), user);
    }

}
