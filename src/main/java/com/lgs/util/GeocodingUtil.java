package com.lgs.util;

import org.springframework.web.client.RestTemplate;
import org.json.JSONObject;

public class GeocodingUtil {

    private static final String OPENCAGE_API_URL = "https://api.opencagedata.com/geocode/v1/json";
    private static final String API_KEY = "58819662e33d47c7b9e92db01dc54d1d";

    public static double[] getCoordinates(String location) {
        try {
            // Construa a URL com o endereço da API e a chave de API
            String url = OPENCAGE_API_URL + "?q=" + location.replace(" ", "+") + "&key=" + API_KEY;

            // Realize a requisição para a API do OpenCage
            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(url, String.class);

            // Converta a resposta JSON
            JSONObject json = new JSONObject(response);

            // Verificar se há resultados válidos
            if (json.has("results") && json.getJSONArray("results").length() > 0) {
                JSONObject locationJson = json.getJSONArray("results")
                        .getJSONObject(0)
                        .getJSONObject("geometry");

                double lat = locationJson.getDouble("lat");
                double lng = locationJson.getDouble("lng");
                return new double[]{lat, lng};
            } else {
                // Nenhum resultado encontrado
                System.err.println("Nenhum resultado encontrado para a localização: " + location);
            }

        } catch (Exception e) {
            System.err.println("Erro ao obter coordenadas para a localização: " + location);
            e.printStackTrace();
        }

        // Retorna coordenadas padrão em caso de erro ou sem resultados
        return new double[]{0.0, 0.0};
    }
}
