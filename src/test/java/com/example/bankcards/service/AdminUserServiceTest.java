package com.example.bankcards.service;

import com.example.bankcards.Generator;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.RoleRepository;
import com.example.bankcards.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminUserServiceTest {

    @InjectMocks
    private AdminUserService adminUserService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private User activeUser;
    private Role roleUser;

    @BeforeEach
    void setUp() {
        activeUser = Generator.generateUser();
        roleUser = Generator.roleUser;

       // when(passwordEncoder.encode(any())).thenAnswer(inv -> inv.getArgument(0));
       // when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    // ---------------- getUser by id ----------------
    @Test
    void getUser_shouldReturnUser_whenExists() {
        when(userRepository.findById(activeUser.getId())).thenReturn(Optional.of(activeUser));

        User result = adminUserService.getUser(activeUser.getId());

        assertThat(result).isEqualTo(activeUser);
    }

    @Test
    void getUser_shouldThrow_whenNotFound() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> adminUserService.getUser(id));
    }

    // ---------------- getUser by login ----------------
    @Test
    void getUserByLogin_shouldReturnUser_whenExists() {
        when(userRepository.findByLogin(activeUser.getLogin())).thenReturn(Optional.of(activeUser));

        User result = adminUserService.getUser(activeUser.getLogin());

        assertThat(result).isEqualTo(activeUser);
    }

    @Test
    void getUserByLogin_shouldThrow_whenNotFound() {
        when(userRepository.findByLogin("login")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> adminUserService.getUser("login"));
    }

    // ---------------- createUser ----------------
    @Test
    void createUser_shouldSaveNewUser_whenNotExists() {
        when(userRepository.findByLogin(activeUser.getLogin())).thenReturn(Optional.empty());
        when(roleRepository.findByRole("ROLE_USER")).thenReturn(Optional.of(roleUser));
        when(passwordEncoder.encode(any())).thenAnswer(inv ->inv.getArgument(0));

        adminUserService.createUser(activeUser);

        assertThat(activeUser.getRoles()).contains(roleUser);
        assertThat(activeUser.getStatus()).isEqualTo("ACTIVE");
        verify(userRepository).save(activeUser);
        verify(passwordEncoder).encode(activeUser.getPassword());
    }

    @Test
    void createUser_shouldThrow_whenAlreadyExists() {
        when(userRepository.findByLogin(activeUser.getLogin())).thenReturn(Optional.of(activeUser));

        assertThrows(EntityExistsException.class, () -> adminUserService.createUser(activeUser));
    }

    // ---------------- loadUserByUsername ----------------
    @Test
    void loadUserByUsername_shouldReturnUserDetails() {
        activeUser.setRoles(List.of(roleUser));
        when(userRepository.findByLogin(activeUser.getLogin())).thenReturn(Optional.of(activeUser));

        UserDetails details = adminUserService.loadUserByUsername(activeUser.getLogin());

        assertThat(details.getUsername()).isEqualTo(activeUser.getLogin());
        assertThat(details.getPassword()).isEqualTo(activeUser.getPassword());
        assertThat(details.getAuthorities()).extracting("authority").contains("ROLE_USER");
    }

    @Test
    void loadUserByUsername_shouldThrow_whenNotFound() {
        when(userRepository.findByLogin("unknown")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> adminUserService.loadUserByUsername("unknown"));
    }

    // ---------------- blockUser ----------------
    @Test
    void blockUser_shouldSetBlockedStatus() {
        when(userRepository.findById(activeUser.getId())).thenReturn(Optional.of(activeUser));

        adminUserService.blockUser(activeUser.getId());

        assertThat(activeUser.getStatus()).isEqualTo("BLOCKED");
        verify(userRepository).save(activeUser);
    }

    // ---------------- updateUser ----------------
    @Test
    void updateUser_shouldUpdateUser() {
        User updated = Generator.generateUser();
        updated.setPassword("newPass");
        updated.setLogin("panda");
        when(passwordEncoder.encode(any())).thenAnswer(inv -> inv.getArgument(0));
        when(userRepository.findById(activeUser.getId())).thenReturn(Optional.of(activeUser));

        adminUserService.updateUser(activeUser.getId(), updated);

        assertThat(activeUser.getLogin()).isEqualTo(updated.getLogin());
        verify(userRepository).save(updated);
        verify(passwordEncoder).encode(updated.getPassword());
    }

    // ---------------- deleteUser ----------------
    @Test
    void deleteUser_shouldDeleteExistingUser() {
        when(userRepository.findById(activeUser.getId())).thenReturn(Optional.of(activeUser));

        adminUserService.deleteUser(activeUser.getId());

        verify(userRepository).delete(activeUser);
    }

    // ---------------- getUsers ----------------
    @Test
    void getUsers_shouldReturnAllUsers() {
        User user1 = Generator.generateUser();
        User user2 = Generator.generateUser();
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<User> users = adminUserService.getUsers();

        assertThat(users).containsExactly(user1, user2);
    }
}
