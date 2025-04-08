package com.saguro.rapid.configserver.components;

import com.saguro.rapid.configserver.entity.User;
import com.saguro.rapid.configserver.repository.UserRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class UserInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        String defaultUsername = "admin";
        String defaultPassword = "admin";

        // Verificar si el usuario ya existe
        if (userRepository.findByUsername(defaultUsername).isEmpty()) {
            // Crear el usuario con contraseña codificada
            User user = new User();
            user.setUsername(defaultUsername);
            user.setPassword(passwordEncoder.encode(defaultPassword));
            user.setRoles(Set.of("ROLE_ADMIN"));

            userRepository.save(user);
            System.out.println("Usuario por defecto creado: " + defaultUsername);
        } else {
            System.out.println("El usuario por defecto ya existe: " + defaultUsername);
        }
    }
}
