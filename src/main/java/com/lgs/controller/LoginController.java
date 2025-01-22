	package com.lgs.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lgs.dto.ApiResponse;
import com.lgs.dto.LoginRequestDTO;
import com.lgs.entities.User;
import com.lgs.repositories.UserRepository;
import com.lgs.security.TokenService;

@RestController
@RequestMapping("/api")
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest) {

        // Verifique se o usuário existe no banco de dados
        User user = userRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!user.isVerified()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse(false, "Usuário não verificado. Verifique sua conta antes de fazer login."));
        }
        // Verifique se a senha fornecida corresponde à senha armazenada
        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Credenciais inválidas"));
        }

        // Gerar o token com base no usuário autenticado
        String token = tokenService.generateToken(user);

        // Criar resposta com o token e tipo de usuário
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("userType", user.getType()); // Incluindo tipo de usuário no retorno

        return ResponseEntity.ok(response);
    }
}
