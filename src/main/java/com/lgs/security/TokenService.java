package com.lgs.security;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.lgs.entities.User;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    // Geração de Token
    public String generateToken(User user) {
        try {
            if (user.getType() == null) {
                throw new RuntimeException("Tipo de usuário não atribuído");
            }
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("login-auth-api")
                    .withSubject(user.getEmail())
                    .withClaim("userType", user.getType())  // Usando o tipo diretamente como String
                    .withExpiresAt(generateExpirationDate())
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro de autenticação", exception);
        }
    }

    // Validação do Token e extração do email
    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("login-auth-api")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException exception) {
            return null;  // Token inválido
        }
    }

    // Geração de Data de Expiração
    private Instant generateExpirationDate() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }

    // Extração de email do Token
    public String extractEmail(String token) {
        return extractClaim(token, "sub"); // "sub" é o claim padrão para o email
    }

    // Extração de tipo de usuário do Token
    public String extractUserType(String token) {
        return extractClaim(token, "userType");
    }

 // Extração de ID do profissional do Token
    public Long extractProfessionalId(String token) {
        try {
            // Chama o método genérico para extrair o claim com o nome "professionalId"
            String idString = extractClaim(token, "professionalId");
            return idString != null ? Long.parseLong(idString) : null;
        } catch (NumberFormatException e) {
            throw new RuntimeException("Erro ao converter o ID do profissional para Long", e);
        }
    }

    
    // Método privado para extração de claims
    private String extractClaim(String token, String claim) {
        try {
            String tokenWithoutBearer = token.startsWith("Bearer ") ? token.replace("Bearer ", "") : token;
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("login-auth-api")
                    .build()
                    .verify(tokenWithoutBearer)
                    .getClaim(claim).asString();
        } catch (JWTVerificationException exception) {
            throw new RuntimeException("Erro ao extrair claim do token", exception);
        }
    }
}
