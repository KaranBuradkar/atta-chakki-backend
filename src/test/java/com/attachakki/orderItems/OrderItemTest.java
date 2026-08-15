package com.attachakki.orderItems;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@SpringBootTest
public class OrderItemTest {

    private static final Logger log = LoggerFactory.getLogger(OrderItemTest.class);

    @Test
    public void testHttp() {
        log.debug("request executing");
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/v1/shops/1/orderItems"))
                    .header("Authorization", "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJkZXYua2FyYW5AZ2FtaWwuY29tIiwidXNlcl9pZCI6IjEiLCJzeXN0ZW1fcm9sZSI6IkNMSUVOVCIsImlhdCI6MTc2MzkyMTMyOCwiZXhwIjoxNzY0MDA3NzI4fQ.I0OyIcbM2nedihT63qyBcUypzcEy32ZzQfuq7gGQbRgL4DuuW-cJsPk0G0huwVtrEoEd7NQj7NemP3GDLUfCwA")
                    .header("content-type", "application/json")
                    .method("POST", HttpRequest.BodyPublishers.ofString("{\n  \"name\": \"Wheat Flour\"\n}"))
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
        } catch (Exception e) {
            log.debug(e.getMessage());
        }
    }

    @Test
    public void testCreateAccount() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/auth/register"))
                    .header("content-type", "application/json")
                    .method("POST", HttpRequest.BodyPublishers.ofString("{\n  \"name\": \"करण बुरडकर\",\n  \"username\": \"dev.karan@gamil.com\",\n  \"password\": \"karan\"\n}"))
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
        } catch (RuntimeException | IOException | InterruptedException e) {
            log.error(e.getMessage());
        }
    }

    @Test
    public void testTest() {
        log.debug("it works");
    }
}
