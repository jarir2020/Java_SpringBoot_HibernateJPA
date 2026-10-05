package com.jarirahmed.springboot;

import org.springframework.boot.web.server.context.WebServerApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/** Starts a real embedded server briefly and calls it like an HTTP client. */
public final class SpringBootLesson {
    private SpringBootLesson() {
    }

    public static void run() throws Exception {
        System.out.println("\n=== PHASE 4: SPRING BOOT ===");

        try (ConfigurableApplicationContext context = SpringBootLearningApplication.run(
                "--server.port=0",
                "--spring.main.banner-mode=off",
                "--debug=false",
                "--logging.level.root=WARN",
                "--course.mode=lesson")) {
            int port = ((WebServerApplicationContext) context).getWebServer().getPort();
            HttpClient client = HttpClient.newHttpClient();

            HttpResponse<String> info = get(client, port, "/api/boot/info");
            HttpResponse<String> health = get(client, port, "/actuator/health");

            System.out.println("Embedded server port: " + port);
            System.out.println("GET /api/boot/info -> " + info.statusCode());
            System.out.println("Boot configuration: " + info.body());
            System.out.println("GET /actuator/health -> " + health.statusCode());
            System.out.println("Health response: " + health.body());
        }

        System.out.println("PHASE 4 COMPLETE");
    }

    private static HttpResponse<String> get(
            HttpClient client,
            int port,
            String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:" + port + path))
                .GET()
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
