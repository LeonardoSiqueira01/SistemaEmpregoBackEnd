package com.lgs.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.lgs.entities.Client;
import com.lgs.entities.Professional;
import com.lgs.entities.User;
import com.lgs.entities.Service.ServiceStatus;
import com.lgs.repositories.EvaluationRepository;
import com.lgs.repositories.ServiceRepository;
import com.lgs.repositories.UserRepository;
import com.lgs.security.TokenService;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    
    
    @Autowired
    private EvaluationRepository evaluationRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private EmailService emailService;

    // Verifica se o e-mail já está registrado
    public boolean isEmailUnique(String email) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        return existingUser.isEmpty();  // Retorna true se o e-mail for único
    }

    public User register(User user, String userType, String specialties, String location) {
        if (!isEmailUnique(user.getEmail())) {
            throw new RuntimeException("Este e-mail já está registrado.");
        }
        String verificationCode = UUID.randomUUID().toString();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setVerificationCode(verificationCode);
        user.setVerified(false);

        if ("profissional".equalsIgnoreCase(userType)) {
            user.setType("PROFESSIONAL");
            Professional professional = new Professional();
            professional.setEmail(user.getEmail());
            professional.setPassword(user.getPassword());
            professional.setName(user.getName());
            professional.setVerificationCode(verificationCode);
            professional.setType(user.getType());
            professional.setLocation(location);

            // Garantir que as especialidades sejam formatadas corretamente
            if (specialties != null && !specialties.trim().isEmpty()) {
                professional.setSpecialties(specialties.trim());  // Sem necessidade de "split"
            } else {
            	throw new Error("Especialidades não podem ficar vazías!");            }

            userRepository.save(professional);
            emailService.sendActivationEmail(user.getEmail(), verificationCode);
            return professional;
        } else if ("cliente".equalsIgnoreCase(userType)) {
            user.setType("CLIENT");
            Client client = new Client();
            client.setEmail(user.getEmail());
            client.setPassword(user.getPassword());
            client.setName(user.getName());
            client.setVerificationCode(verificationCode);
            client.setType(user.getType());
            userRepository.save(client);
            emailService.sendActivationEmail(user.getEmail(), verificationCode);
            return client;
        } else {
            throw new RuntimeException("Tipo de usuário inválido.");
        }
    }




    // Verifica o código de verificação enviado por e-mail
    public boolean verify(String verificationCode) {
        Optional<User> userOptional = userRepository.findByVerificationCode(verificationCode);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setVerified(true);  // Marcar o usuário como verificado
            user.setVerificationCode(null);  // Limpar o código de verificação após ativação
            userRepository.save(user);  // Salvar as alterações no banco de dados
            return true;  // Retorna true se a verificação foi bem-sucedida
        }
        return false;  // Retorna false se o código não for encontrado
    }

    // Verificação de login com senha criptografada
    public boolean login(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return passwordEncoder.matches(password, user.getPassword()); // Verificar se a senha coincide
        }
        return false;  // Se o usuário não for encontrado ou a senha não corresponder
    }

    // Verifica se o usuário está ativo (verificado)
    public boolean isUserVerified(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        return userOptional.map(User::isVerified).orElse(false);  // Retorna true se o usuário for verificado
    }
    
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null); // Retorna null se o usuário não for encontrado
    }


   

}
