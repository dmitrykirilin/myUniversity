package com.foxminded.university.controllers;

import com.foxminded.university.model.User;
import com.foxminded.university.services.JwtUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

@Controller
public class AuthController {

    @Autowired
    private JwtUserDetailsService userService;

    @GetMapping("/login")
    public String login(@RequestParam(name = "name", required = false, defaultValue = "Вход") String name,
                        @ModelAttribute User user,
                        @RequestParam(name = "error", required = false) Boolean error,
                        Model model) {
        if(Boolean.TRUE.equals(error)){
            model.addAttribute("error", true);
        }
        model.addAttribute("title", name);
        return "login";
    }

    @GetMapping("/registration")
    public String getRegistration(@RequestParam(name = "name", required = false, defaultValue = "Регистрация") String name,
                                  @ModelAttribute User user,
                                  Model model) {
        model.addAttribute("title", name);
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(@RequestParam(name = "name", required = false, defaultValue = "Регистрация") String name,
                          @RequestParam(name = "passwordConfirm") String passwordConfirm,
                          @Valid @ModelAttribute User user,
                          BindingResult bindingResult,
                          Model model) {
        model.addAttribute("title", name);

        boolean isSuccessConfirm = user.getPassword().equals(passwordConfirm);
        if(!isSuccessConfirm){
            model.addAttribute("passwordConfirmError", "Подтвердите правильность ввода пароля!");
        }
        if(bindingResult.hasErrors() || !isSuccessConfirm){
            model.addAttribute("user", user);
            model.addAttribute("title", name);
            return "registration";
        }

        if(!userService.add(user)){
            model.addAttribute("message", "User exists!");
            return "redirect:/registration";
        }
        return "redirect:/login";
    }
}
