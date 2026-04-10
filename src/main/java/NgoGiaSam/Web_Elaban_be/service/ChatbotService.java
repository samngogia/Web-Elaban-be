package NgoGiaSam.Web_Elaban_be.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

@Service
public class ChatbotService {
    private final RestTemplate restTemplate = new RestTemplate();

    public String askRasa(String message) {

        String rasaUrl = "http://localhost:5005/webhooks/rest/webhook";

        Map<String, String> request = new HashMap<>();
        request.put("sender", "test");
        request.put("message", message);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> entity =
                new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                rasaUrl,
                entity,
                String.class
        );

        return response.getBody();
    }
}
