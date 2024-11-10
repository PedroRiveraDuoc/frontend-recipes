package com.example.frontend_recipes.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Value("${backend.api.url}")
    private String backendApiUrl;

    @GetMapping("/login")
    public String loginPage(Model model) {
        return "login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            Model model) {

        logger.info("Intentando iniciar sesión con el usuario: {}", username);

        String loginUrl = backendApiUrl + "/auth/login";
        RestTemplate restTemplate = new RestTemplate();

        try {
            String requestBody = String.format("{\"username\":\"%s\", \"password\":\"%s\"}", username, password);
            ResponseEntity<String> response = restTemplate.postForEntity(loginUrl, requestBody, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("Inicio de sesión exitoso para el usuario: {}", username);
                return "redirect:/home";
            } else {
                logger.error("Credenciales incorrectas para el usuario: {}", username);
                model.addAttribute("error", "Credenciales incorrectas. Intenta nuevamente.");
                return "login";
            }
        } catch (Exception e) {
            logger.error("Error al conectar con el backend: {}", e.getMessage());
            model.addAttribute("error", "Error al conectar con el servidor. Intenta más tarde.");
            return "login";
        }
    }
}
