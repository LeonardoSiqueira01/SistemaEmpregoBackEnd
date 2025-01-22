package com.lgs.security;

import jakarta.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import java.util.Base64;

public class TokenUtils {

    // Método para extrair o email do token JWT
    public static String getEmailFromToken(HttpServletRequest request) {
        // Obtém o token do header Authorization
        String token = request.getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            throw new RuntimeException("Token JWT não fornecido ou inválido");
        }

        token = token.substring(7); // Remove "Bearer " do início do token

        try {
            // Divide o token em três partes: header, payload e signature
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                throw new RuntimeException("Token JWT malformado");
            }

            // O payload está na segunda parte (índice 1)
            String payload = parts[1];

            // Decodifica a parte do payload (Base64 URL)
            String decodedPayload = new String(Base64.getUrlDecoder().decode(payload));

            // Usa JSONObject para parsear o payload
            JSONObject jsonPayload = new JSONObject(decodedPayload);

            // O campo 'sub' geralmente contém o email
            String email = jsonPayload.optString("sub");

            if (email.isEmpty()) {
                throw new RuntimeException("Email não encontrado no token");
            }

            return email;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao extrair o email do token: " + e.getMessage(), e);
        }
    }
}



