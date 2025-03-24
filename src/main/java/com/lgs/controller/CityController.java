package com.lgs.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api")
public class CityController {

    @GetMapping("/cities")
    public ResponseEntity<List<Map<String, String>>> getCities() {
        String apiUrl = "https://servicodados.ibge.gov.br/api/v1/localidades/municipios";
        RestTemplate restTemplate = new RestTemplate();
        
        ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);
        
        if (response.getStatusCode().is2xxSuccessful()) {
            try {
                // Converte a resposta JSON para uma lista de Map (cada cidade será um objeto JSON)
                ObjectMapper objectMapper = new ObjectMapper();
                List<Map<String, Object>> rawCities = objectMapper.readValue(response.getBody(), new TypeReference<List<Map<String, Object>>>() {});

                // Filtra apenas os campos necessários: nome da cidade e estado (sigla UF)
                List<Map<String, String>> cities = rawCities.stream()
                    .map(city -> {
                        String cityName = (String) city.get("nome");

                        // Acessando o estado (UF)
                        Map<String, Object> microrregiao = (Map<String, Object>) city.get("microrregiao");
                        Map<String, Object> mesorregiao = (Map<String, Object>) microrregiao.get("mesorregiao");
                        Map<String, Object> uf = (Map<String, Object>) mesorregiao.get("UF");
                        String stateSigla = (String) uf.get("sigla");

                        return Map.of("nome", cityName, "estado", stateSigla);
                    })
                    .collect(Collectors.toList());

                return ResponseEntity.ok(cities);

            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        } else {
            return ResponseEntity.status(response.getStatusCode()).body(null);
        }
    }
}
