package project;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GroqApiClient {

    private static final String API_URL = "https://api.groq.com/openai/v1/chat/completions";
    private static final String API_KEY = "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"; // Remplacez par votre clé API Groq
    private static final String DEFAULT_MODEL = "llama3-8b-8192"; // Modèle LLM

    public static String generateSQL(String description) {
        try {
            String jsonPayload = buildRequestPayload(description);
            HttpRequest request = buildHttpRequest(jsonPayload);
            HttpResponse<String> response = sendHttpRequest(request);
            return parseResponse(response);
        } catch (Exception e) {
            e.printStackTrace();
            return "-- Failed to generate SQL script. Error: " + e.getMessage();
        }
    }


    private static String buildRequestPayload(String description) throws Exception {
        HashMap<String, Object> payload = new HashMap<>();
        HashMap<String, String> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", """
        
            By default, do not give any comment or explanation
            unless it's explicitly asked in the description section.
        
            The description for generating a SQL script:
        
        """ + description);
        payload.put("messages", new HashMap[]{message});
        payload.put("model", DEFAULT_MODEL);

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(payload);
    }

    private static HttpRequest buildHttpRequest(String jsonPayload) {
        return HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Authorization", "Bearer " + API_KEY)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();
    }

    private static HttpResponse<String> sendHttpRequest(HttpRequest request) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private static String parseResponse(HttpResponse<String> response) throws Exception {
        if (response.statusCode() == 200) {
            ObjectMapper mapper = new ObjectMapper();
            String responseBody = response.body();

            HashMap<?, ?> jsonResponse = mapper.readValue(responseBody, HashMap.class);
            Object choicesObject = jsonResponse.get("choices");

            if (choicesObject instanceof java.util.ArrayList<?> choices) {

                if (!choices.isEmpty()) {
                    HashMap<?, ?> firstChoice = (HashMap<?, ?>) choices.getFirst();
                    HashMap<?, ?> message = (HashMap<?, ?>) firstChoice.get("message");
                    return (String) message.get("content");
                }
            }
            throw new RuntimeException("Invalid response format: choices array is empty or malformed");
        } else {
            throw new RuntimeException("API call failed with status code: " + response.statusCode()+"\nError message : "+response.body());
        }
    }


}
