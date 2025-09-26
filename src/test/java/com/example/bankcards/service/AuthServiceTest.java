package com.example.bankcards.service;

import com.example.bankcards.Generator;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.EntityBlockedException;
import com.example.bankcards.util.JwtTokenUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private AdminUserService adminUserService;

    @Mock
    private JwtTokenUtils jwtTokenUtils;

    // ---------------- getToken ----------------

    @Test
    void getToken_shouldReturnJwtToken_whenUserIsActive() {
        User user = Generator.generateUser();
        UserDetails userDetails = mock(UserDetails.class);

        when(adminUserService.getUser(user.getLogin())).thenReturn(user);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        when(adminUserService.loadUserByUsername(user.getLogin())).thenReturn(userDetails);
        when(jwtTokenUtils.generateToken(userDetails)).thenReturn("jwt-token");

        String result = authService.getToken(user);

        assertThat(result).isEqualTo("jwt-token");
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(adminUserService).loadUserByUsername(user.getLogin());
        verify(jwtTokenUtils).generateToken(userDetails);
    }


    @Test
    void getToken_shouldThrowException_whenUserIsBlocked() {
        User user = Generator.generateUser();
        user.setLogin("blocked");
        user.setStatus("BLOCKED");
        when(adminUserService.getUser("blocked")).thenReturn(user);

        assertThrows(EntityBlockedException.class, () -> authService.getToken(user));

        verify(adminUserService).getUser("blocked");
        verifyNoInteractions(authenticationManager, jwtTokenUtils);
    }
}
