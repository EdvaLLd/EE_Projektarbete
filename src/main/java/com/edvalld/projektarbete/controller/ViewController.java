package com.edvalld.projektarbete.controller;

import com.edvalld.projektarbete.user.CustomUser;
import com.edvalld.projektarbete.user.CustomUserRepository;
import com.edvalld.projektarbete.user.authority.UserRole;
import com.edvalld.projektarbete.user.dto.CustomUserCreationDTO;
import com.edvalld.projektarbete.user.dto.RegisterUserDTO;
import com.edvalld.projektarbete.user.mapper.CustomUserMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Set;

@Controller
public class ViewController {


    // TODO - Replace with Service in the future
    private final CustomUserRepository customUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserMapper customUserMapper;

    @Autowired
    public ViewController(CustomUserRepository customUserRepository, PasswordEncoder passwordEncoder, CustomUserMapper customUserMapper) {
        this.customUserRepository = customUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.customUserMapper = customUserMapper;
    }

    @GetMapping("/login")
    public String loginPage() {

        return "login";
    }

    @GetMapping("/logout")
    public String logoutPage() {

        return "logout";
    }

    @GetMapping("/admin")
    public String adminPage() {

        return "adminpage"; // Must Reflect .html document name
    }

    @GetMapping("/user")
    public String userPage() {

        return "userpage";
    }

    // Responsible for Inserting CustomUser Entity (otherwise DTO)
    @GetMapping("/register")
    public String registerPage(Model model) {

        // Best practice: id aka AttributeName should be the same as object name
        model.addAttribute("registerUserDTO", new RegisterUserDTO("",""));

        return "registerpage";
    }

    // Handles Business Logic - coming from SUBMIT FORM
    @PostMapping("/register")
    public String registerUser(
            @Valid RegisterUserDTO registerUserDTO, BindingResult bindingResult
    ) {

        if (bindingResult.hasErrors()) {
            return "registerpage";
        }

        CustomUser customUser = customUserMapper.toEntity(registerUserDTO);

        customUser.setPassword(
                customUser.getPassword(),
                passwordEncoder
        );

        // TODO - Verification Process STATUS: Nice To Have
        customUser.setAccountNonExpired(true);
        customUser.setAccountNonLocked(true);
        customUser.setCredentialsNonExpired(true);
        customUser.setEnabled(true);

        // TODO - Handle Roles Graciously
        customUser.setUserRoles(
                Set.of(UserRole.USER)
        );

        System.out.println("Saving user... ");
        customUserRepository.save(customUser);

        return "redirect:/login";
    }
}
