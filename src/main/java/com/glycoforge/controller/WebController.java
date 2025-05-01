package com.glycoforge.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/register")
    public String registerPage() {
        return "register"; // Returns templates/register.html
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login"; // Returns templates/login.html
    }

    @GetMapping("/dashboard")
    public String dashboardPage() {
        // Authentication check will be handled by Spring Security
        return "dashboard"; // Returns templates/dashboard.html
    }

    // Redirect root path to login or dashboard depending on authentication
    @GetMapping("/")
    public String rootRedirect() {
        // Simple redirect to login for now. More sophisticated logic
        // (checking authentication) could be added.
        return "redirect:/login";
    }
}

