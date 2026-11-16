package com.example.crm;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "spring.datasource.url=jdbc:sqlite:file:crm-test?mode=memory&cache=shared")
class CustomerControllerTest {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @LocalServerPort
    private int port;

    @Autowired
    private CustomerRepository customerRepository;

    @BeforeEach
    void clearCustomers() {
        customerRepository.removeAll();
    }

    @Test
    void createsListsAndDeletesACustomer() throws Exception {
        HttpResponse<String> createResponse = postCustomer("{\"firstName\": \" Ada \", \"lastName\": \"Lovelace\", \"email\": \"ada@example.com\"}");
        assertThat(createResponse.statusCode()).isEqualTo(201);
        JsonNode created = objectMapper.readTree(createResponse.body());
        assertThat(created.get("firstName").asString()).isEqualTo("Ada");
        assertThat(created.get("lastName").asString()).isEqualTo("Lovelace");
        assertThat(created.get("email").asString()).isEqualTo("ada@example.com");
        assertThat(created.get("id").isIntegralNumber()).isTrue();
        assertThat(created.get("createdAt").asString()).isNotBlank();

        assertThat(objectMapper.readTree(send("GET", "", null).body()).size()).isEqualTo(1);

        HttpResponse<String> deleteResponse = send("DELETE", "/" + created.get("id").asLong(), null);
        assertThat(deleteResponse.statusCode()).isEqualTo(204);
        assertThat(objectMapper.readTree(send("GET", "", null).body()).size()).isZero();
    }

    @Test
    void rejectsMissingFieldsAndInvalidEmails() throws Exception {
        HttpResponse<String> missingResponse = postCustomer("{\"firstName\": \"Ada\"}");
        assertThat(missingResponse.statusCode()).isEqualTo(400);
        assertThat(objectMapper.readTree(missingResponse.body()).get("error").asString()).contains("lastName, email");

        HttpResponse<String> invalidResponse = postCustomer("{\"firstName\": \"Ada\", \"lastName\": \"Lovelace\", \"email\": \"not-an-email\"}");
        assertThat(invalidResponse.statusCode()).isEqualTo(400);
    }

    @Test
    void rejectsDuplicateEmailsRegardlessOfCase() throws Exception {
        postCustomer("{\"firstName\": \"Ada\", \"lastName\": \"Lovelace\", \"email\": \"ada@example.com\"}");
        HttpResponse<String> duplicateResponse = postCustomer("{\"firstName\": \"Ada\", \"lastName\": \"L\", \"email\": \"ADA@example.com\"}");
        assertThat(duplicateResponse.statusCode()).isEqualTo(409);
    }

    @Test
    void returns404WhenDeletingAnUnknownCustomer() throws Exception {
        assertThat(send("DELETE", "/999", null).statusCode()).isEqualTo(404);
        assertThat(send("DELETE", "/abc", null).statusCode()).isEqualTo(404);
    }

    private HttpResponse<String> postCustomer(String json) throws Exception {
        return send("POST", "", json);
    }

    private HttpResponse<String> send(String method, String path, String json) throws Exception {
        HttpRequest.BodyPublisher bodyPublisher = json == null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/api/customers" + path))
                .header("Content-Type", "application/json")
                .method(method, bodyPublisher)
                .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
