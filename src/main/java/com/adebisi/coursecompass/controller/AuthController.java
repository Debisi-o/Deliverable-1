package com.adebisi.coursecompass.controller;

import com.adebisi.coursecompass.dto.RegistrationForm;
import com.adebisi.coursecompass.model.UserRole;
import com.adebisi.coursecompass.repository.UserRepository;
import com.adebisi.coursecompass.service.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class AuthController {

    private final RegistrationService registrationService;
    private final UserRepository userRepository;

    public AuthController(RegistrationService registrationService, UserRepository userRepository) {
        this.registrationService = registrationService;
        this.userRepository = userRepository;
    }

    @ModelAttribute("registrationRoles")
    public List<UserRole> registrationRoles() {
        return List.of(UserRole.STUDENT, UserRole.INSTRUCTOR);
    }

    @GetMapping("/login")
    public String login(
            @RequestParam(required = false) String error,
            @RequestParam(required = false) String logout,
            @RequestParam(required = false) String registered,
            Authentication authentication,
            Model model) {

        if (isAuthenticated(authentication)) {
            return "redirect:/courses";
        }

        if (error != null) {
            model.addAttribute("loginError", "We could not sign you in with those credentials. Check your email and password, then try again.");
        }
        if (logout != null) {
            model.addAttribute("logoutMessage", "You have been signed out successfully.");
        }
        if (registered != null) {
            model.addAttribute("registeredMessage", "Your account is ready. Sign in to continue.");
        }

        return "auth/login";
    }

    @GetMapping("/register")
    public String registerForm(Authentication authentication, Model model) {
        if (isAuthenticated(authentication)) {
            return "redirect:/courses";
        }
        if (!model.containsAttribute("registrationForm")) {
            model.addAttribute("registrationForm", new RegistrationForm());
        }
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(
            @Valid @ModelAttribute("registrationForm") RegistrationForm registrationForm,
            BindingResult bindingResult,
            Authentication authentication) {

        if (isAuthenticated(authentication)) {
            return "redirect:/courses";
        }

        if (registrationForm.getRole() == UserRole.ADMIN) {
            bindingResult.rejectValue("role", "invalid", "Choose a student or instructor account.");
        }

        if (!bindingResult.hasFieldErrors("email")) {
            String normalizedEmail = registrationService.normalizeEmail(registrationForm.getEmail());
            if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
                bindingResult.rejectValue("email", "duplicate", "An account with that email address already exists.");
            }
        }

        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        registrationService.register(registrationForm);
        return "redirect:/login?registered";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }

    private boolean isAuthenticated(Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }
}
