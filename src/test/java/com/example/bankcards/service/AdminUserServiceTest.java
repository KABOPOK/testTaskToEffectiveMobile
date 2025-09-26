package com.example.bankcards.service;

import com.example.bankcards.Generator;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.RoleRepository;
import com.example.bankcards.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
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

    // ---------------- getUser by id ----------------
    @Test
    void getUser_shouldReturnUser_whenExists() {
        User user = Generator.generateUser();
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        User result = adminUserService.getUser(user.getId());

        assertThat(result).isEqualTo(user);
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
        User user = Generator.generateUser();
        when(userRepository.findByLogin(user.getLogin())).thenReturn(Optional.of(user));

        User result = adminUserService.getUser(user.getLogin());

        assertThat(result).isEqualTo(user);
    }

    @Test
    void getUserByLogin_shouldThrow_whenNotFound() {
        when(userRepository.findByLogin("login")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> adminUserService.getUser("login"));
    }

    // ---------------- createUser ----------------
    @Test
    void createUser_shouldSaveNewUser() {
        User user = Generator.generateUser();
        Role role = Generator.roleUser;
        when(userRepository.findByLogin(user.getLogin())).thenReturn(Optional.empty());
        when(roleRepository.findByRole("ROLE_USER")).thenReturn(Optional.of(role));
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        adminUserService.createUser(user);

        assertThat(user.getPassword()).isEqualTo("encoded");
        assertThat(user.getRoles()).contains(role);
        assertThat(user.getStatus()).isEqualTo("ACTIVE");
        verify(userRepository).save(user);
    }

    @Test
    void createUser_shouldThrow_whenAlreadyExists() {
        User user = Generator.generateUser();
        when(userRepository.findByLogin(user.getLogin())).thenReturn(Optional.of(user));

        assertThrows(EntityExistsException.class, () -> adminUserService.createUser(user));
    }

    // ---------------- loadUserByUsername ----------------
    @Test
    void loadUserByUsername_shouldReturnUserDetails() {
        User user = Generator.generateUser();
        user.setRoles(List.of(Generator.roleUser));
        when(userRepository.findByLogin(user.getLogin())).thenReturn(Optional.of(user));

        UserDetails details = adminUserService.loadUserByUsername(user.getLogin());

        assertThat(details.getUsername()).isEqualTo(user.getLogin());
        assertThat(details.getPassword()).isEqualTo(user.getPassword());
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
        User user = Generator.generateUser();
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        adminUserService.blockUser(user.getId());

        assertThat(user.getStatus()).isEqualTo("BLOCKED");
        verify(userRepository).save(eq(user));
    }

    // ---------------- updateUser ----------------
    @Test
    void updateUser_shouldUpdateUser() {
        User user = Generator.generateUser();
        User updated = Generator.generateUser();
        updated.setPassword("newPass");

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPass")).thenReturn("encodedNew");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        adminUserService.updateUser(user.getId(), updated);

        assertThat(user.getPassword()).isEqualTo("encodedNew");
        assertThat(user.getLogin()).isEqualTo(updated.getLogin());
        assertThat(user.getName()).isEqualTo(updated.getName());
        verify(userRepository).save(user);
    }

    // ---------------- deleteUser ----------------
    @Test
    void deleteUser_shouldDeleteExistingUser() {
        User user = Generator.generateUser();
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        adminUserService.deleteUser(user.getId());

        verify(userRepository).delete(user);
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