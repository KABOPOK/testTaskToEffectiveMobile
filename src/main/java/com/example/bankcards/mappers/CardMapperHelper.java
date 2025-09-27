package com.example.bankcards.mappers;

import com.example.bankcards.entity.User;
import com.example.bankcards.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CardMapperHelper {

    private final UserRepository userRepository;

    public User map(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

}
