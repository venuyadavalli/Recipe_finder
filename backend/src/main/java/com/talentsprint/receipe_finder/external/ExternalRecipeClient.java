package com.talentsprint.recipe_finder.external;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class ExternalRecipeClient {
    private final RestTemplate rest = new RestTemplate();
    private final String BASE = "https://www.themealdb.com/api/json/v1/1";

    /**
     * Search meals by first letter (endpoint: /search.php?f=a)
     * Returns parsed JsonNode (root) or null
     */
    public JsonNode searchByFirstLetter(char letter) {
        try {
            String url = BASE + "/search.php?f=" + URLEncoder.encode(String.valueOf(letter), StandardCharsets.UTF_8);
            return rest.getForObject(url, JsonNode.class);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Search by name (optional single import)
     */
    public JsonNode searchByName(String name) {
        try {
            String q = URLEncoder.encode(name, StandardCharsets.UTF_8);
            String url = BASE + "/search.php?s=" + q;
            return rest.getForObject(url, JsonNode.class);
        } catch (Exception e) {
            return null;
        }
    }
}
