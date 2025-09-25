package com.example.bankcards.controller;

import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.RoleRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.UserService;
import com.example.bankcards.util.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
public class SuperController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final JwtTokenUtils jwtTokenUtils;
    private final UserRepository userRepository;
    @PostMapping("/api/pandyachya_panda")
    public String createAdmin(){
        User admin = userRepository.findByLogin("superadmin").orElse(null);
        if(admin == null) {
            admin = new User();
            admin.setLogin("superadmin");   // можно сделать параметрами
            admin.setPassword(passwordEncoder.encode("supersecret"));
            admin.setName("Super Admin");
            admin.setRoles(List.of(roleRepository.findByRole("ROLE_ADMIN").get()));
            userRepository.save(admin);
        }
        UserDetails userDetails = userService.loadUserByUsername(admin.getLogin());
        return jwtTokenUtils.generateToken(userDetails);
    }

//    @PostMapping("/api/user/something")
//    public String pandaUser(){
//        return "halo";
//    }
//
//    @PostMapping("/api/admin/something")
//    public String pandaAdmin(){
//        return "halo";
//    }
}
