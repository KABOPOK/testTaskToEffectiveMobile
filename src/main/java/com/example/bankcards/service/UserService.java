package com.example.bankcards.service;

import com.example.bankcards.entity.User;
import com.example.bankcards.exception.EntityBlockedException;
import com.example.bankcards.repository.RoleRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.JwtTokenUtils;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class UserService extends DefaultService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public User getUser(UUID id) {
        return getOrThrow(id, userRepository::findById);
    }

    public User getUser(String login) {
        return userRepository.findByLogin(login).orElseThrow(
                ()-> new EntityNotFoundException(format("User with login %s not found", login))
        );
    }

    public void createUser(User user) {
        User savedUser = userRepository.findByLogin(user.getLogin()).orElse(null);
        if(savedUser != null){
            throw new EntityExistsException(user.getLogin());
        }
        user.setRoles(List.of(roleRepository.findByRole("ROLE_USER").get()));
        user.setStatus("ACTIVE");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByLogin(username).orElseThrow(()-> new UsernameNotFoundException(username));
        return new org.springframework.security.core.userdetails.User(
                user.getLogin(),
                user.getPassword(),
                user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getRole())).collect(Collectors.toList())
        );
    }

    public void blockUser(UUID id) {
        User user = getOrThrow(id, userRepository::findById);
        user.setStatus("BLOCKED");
        userRepository.save(user);
    }

    public void updateUser(UUID id, User updatedUser) {
        User savedUser = getOrThrow(id, userRepository::findById);
        updatedUser.setId(savedUser.getId());
        updatedUser.setStatus("ACTIVE");
        updatedUser.setCreatedAt(savedUser.getCreatedAt());
        updatedUser.setUpdatedAt(Instant.now());
        updatedUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        userRepository.save(updatedUser);
    }

    public void deleteUser(UUID id) {
        User savedUser = getOrThrow(id, userRepository::findById);
        userRepository.delete(savedUser);
    }

}
