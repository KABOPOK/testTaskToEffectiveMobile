package com.example.bankcards.mappers;

import com.example.bankcards.entity.Role;
import com.example.bankcards.repository.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

@Component
@RequiredArgsConstructor
public class UserMapperHelper {

    private final RoleRepository roleRepository;

    public List<Role> mapFromString(List<String> roles) {
        List<Role> roleList = new ArrayList<>();
        for (String role : roles) {
            roleList.add(roleRepository.findByRole(role)
                    .orElseThrow(() -> new EntityNotFoundException(format("role %s not found", role))));
        }
        return roleList;
    }

    public List<String> mapFromRole(List<Role> roles) {
        List<String> roleNames = new ArrayList<>();
        for (Role role : roles) {
            roleNames.add(role.getRole()); // предполагаю, что поле называется role
        }
        return roleNames;
    }

}
