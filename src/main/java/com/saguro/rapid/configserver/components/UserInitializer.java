package com.saguro.rapid.configserver.components;

import com.saguro.rapid.configserver.entity.Role;
import com.saguro.rapid.configserver.entity.User;
import com.saguro.rapid.configserver.repository.RoleRepository;
import com.saguro.rapid.configserver.repository.UserRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class UserInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserInitializer(UserRepository userRepository, RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @org.springframework.beans.factory.annotation.Value("${rapid.security.admin.password:admin}")
    private String defaultPassword;

    @Override
    public void run(String... args) throws Exception {
        String defaultUsername = "admin";
        String defaultRoleName = "Admin";

        // Verificar si el rol por defecto ya existe, si no, crearlo
        Role defaultRole = roleRepository.findByName(defaultRoleName)
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName(defaultRoleName);
                    role.setEditable(false); // El rol Admin no debe ser editable
                    return roleRepository.save(role);
                });

        // Verificar si el usuario ya existe
        if (userRepository.findByUsername(defaultUsername).isEmpty()) {
            // Crear el usuario con contraseña codificada
            User user = new User();
            user.setUsername(defaultUsername);
            user.setPassword(passwordEncoder.encode(defaultPassword));
            user.setRoles(Set.of(defaultRole)); // Asignar el rol al usuario

            userRepository.save(user);
            System.out.println("Usuario por defecto creado: " + defaultUsername);
        } else {
            System.out.println("El usuario por defecto ya existe: " + defaultUsername);
        }
    }
}
