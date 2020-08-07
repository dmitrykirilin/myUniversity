package com.foxminded.university.controllers.admin;

import com.foxminded.university.model.Role;
import com.foxminded.university.model.User;
import com.foxminded.university.repository.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class UserController {

    @Autowired
    private UserDAO userDAO;

    @GetMapping("/users")
    public String users(Model model){
        model.addAttribute("users", userDAO.findAll());
        return "admin/users";
    }

    @GetMapping("/users/{user}")
    public String userEdit(@PathVariable User user,
                           Model model){
        model.addAttribute("user", user);
        model.addAttribute("roles", Role.values());
        return "admin/userEdit";
    }

    @PostMapping("/users")
    public String userSave(
            @Valid @ModelAttribute User user,
            BindingResult bindingResult,
            Model model,
            @RequestParam Map<String, String> form,
            @RequestParam(name = "userId") String userId
    ){
        user.setId(Integer.valueOf(userId));

        if(bindingResult.hasErrors()){
            model.addAttribute("user", user);
            model.addAttribute("roles", Role.values());
            return "admin/userEdit";
        }


        if(user.getRoles() != null) {
            user.getRoles().clear();
        }
        Set<String> roles = Arrays.stream(Role.values())
                .map(Role::name)
                .collect(Collectors.toSet());
        user.setRoles(new HashSet<Role>());
        for (String key : form.keySet()) {
            if(roles.contains(key)){
                user.getRoles().add(Role.valueOf(key));
            }
        }

        userDAO.save(user);
        return "redirect:/users";
    }

    @GetMapping("/users/{user}/remove")
    public String removeUser(@PathVariable(value = "user") User user){
        userDAO.delete(user);
        return "redirect:/users";
    }


}
