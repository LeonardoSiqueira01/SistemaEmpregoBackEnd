package com.lgs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lgs.dto.ClientSummaryResponse;
import com.lgs.dto.UserRegistrationRequest;
import com.lgs.entities.Client;
import com.lgs.entities.Professional;
import com.lgs.entities.User;
import com.lgs.security.TokenService;
import com.lgs.services.UserService;
@CrossOrigin(origins = "http://127.0.0.1:5500")  // Permite a origem específica
@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

   @Autowired
   private TokenService tokenService;
   
   @PostMapping("/register")
   public ResponseEntity<?> register(@RequestBody UserRegistrationRequest request) {
       
       User user = request.getUser();
       if (user == null || user.getPassword() == null || user.getPassword().isEmpty()) {
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A senha não pode ser vazia.");
       }
       
       try {
           User registeredUser = userService.register(user, request.getUserType(), request.getSpecialties(), request.getLocation());
           return ResponseEntity.ok("Cadastro realizado com sucesso. Código enviado ao e-mail.");
       } catch (Exception e) {
           e.printStackTrace();
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro no cadastro: " + e.getMessage());
       }
   }




    // Verificação de código de ativação
    @GetMapping("/verify")
    public ResponseEntity<String> verify(@RequestParam("code") String code) {

            boolean isVerified = userService.verify(code);
            if (isVerified) {
                return ResponseEntity.ok("Conta ativada com sucesso!");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                     .body("Código de verificação inválido!");
            }
        }
    
    @GetMapping("/client-summary")
    public ResponseEntity<?> getClientSummary(@RequestParam("email") String email) {
        try {
            // Obtém o usuário e suas informações (como serviços solicitados, concluídos e avaliação)
            User user = userService.getUserByEmail(email);
            
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado.");
            }

            int requestedServices = 0;
            int completedServices = 0;
            double averageRating = 0.0;

            // Verifica se o usuário é do tipo Client
            if (user.getType().equals("CLIENT") && user instanceof Client) {
                Client client = (Client) user;  
                requestedServices = client.getTotalServicesRequested();
                completedServices = client.getTotalServicesCompleted();
                averageRating = client.getAverageRating();
            }
            
            // Verifica se o usuário é do tipo Professional
            else if (user.getType().equals("PROFESSIONAL") && user instanceof Professional) {
                Professional professional = (Professional) user;
                requestedServices = professional.getTotalServicesRequested();
                completedServices = professional.getTotalServicesCompleted();
                averageRating = professional.getAverageRating();
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tipo de usuário inválido.");
            }

            // Retorna os dados necessários para a interface HTML
            ClientSummaryResponse summaryResponse = new ClientSummaryResponse(
                    user.getName(), requestedServices, completedServices, averageRating);

            return ResponseEntity.ok(summaryResponse);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .body("Erro ao obter o resumo do cliente: " + e.getMessage());
        }
    }


    
    
    }
