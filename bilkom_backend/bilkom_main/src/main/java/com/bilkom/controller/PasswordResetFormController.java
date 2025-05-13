package com.bilkom.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller responsible for rendering the password reset form.
 * 
 * @author Elif Bozkurt
 * @version 1.0
 */
@Controller
public class PasswordResetFormController {

    /**
     * Displays the password reset form with the token included.
     *
     * @param token The reset token passed via URL.
     * @param model Spring Model to pass data to the view.
     * @return The name of the Thymeleaf HTML template to render.
     */
    @GetMapping("reset-password-form")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        model.addAttribute("token", token);
        return "password-reset-form";
    }
}
