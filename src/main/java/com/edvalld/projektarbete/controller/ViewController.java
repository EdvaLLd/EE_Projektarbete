package com.edvalld.projektarbete.controller;

import com.edvalld.projektarbete.security.jwt.JwtAuthenticationFilter;
import com.edvalld.projektarbete.task.CustomTask;
import com.edvalld.projektarbete.task.CustomTaskRepository;
import com.edvalld.projektarbete.task.dto.CustomTaskDTO;
import com.edvalld.projektarbete.user.CustomUser;
import com.edvalld.projektarbete.user.CustomUserRepository;
import com.edvalld.projektarbete.user.authority.UserRole;
import com.edvalld.projektarbete.user.dto.CustomUserCreationDTO;
import com.edvalld.projektarbete.user.dto.RegisterUserDTO;
import com.edvalld.projektarbete.user.mapper.CustomUserMapper;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

//kopplar sökadressen till sin html-sida (ex /register till register.html)
@Controller
public class ViewController {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    // TODO - Replace with Service in the future
    private final CustomUserRepository customUserRepository;
    private final CustomTaskRepository customTaskRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserMapper customUserMapper;

    @Autowired
    public ViewController(CustomUserRepository customUserRepository, CustomTaskRepository customTaskRepository, PasswordEncoder passwordEncoder, CustomUserMapper customUserMapper) {
        this.customUserRepository = customUserRepository;
        this.customTaskRepository = customTaskRepository;
        this.passwordEncoder = passwordEncoder;
        this.customUserMapper = customUserMapper;
    }

    @GetMapping("/login")
    public String loginPage() {

        return "login";
    }

    @GetMapping("/admin")
    public String adminPage(Model model, Principal principal) {
        //skickar in alla användare förutom den som är inloggad
        model.addAttribute("users", customUserRepository.findAll()
                .stream()
                .filter(user -> !user.getId().equals(customUserRepository.findUserByUsername(principal.getName()).get().getId()))
                .toList());
        return "adminpage"; // Must Reflect .html document name
    }

    @GetMapping("/user")
    public String userPage() {

        return "userpage";
    }

    @DeleteMapping("/removeUser")
    public String removeUser(@RequestParam UUID userId) {
        customUserRepository.deleteById(userId);
        return "redirect:/admin";
    }

    // Responsible for Inserting CustomUser Entity (otherwise DTO)
    @GetMapping("/register")
    public String registerPage(Model model) {

        // Best practice: id aka AttributeName should be the same as object name
        model.addAttribute("registerUserDTO", new RegisterUserDTO("","", ""));

        return "registerpage";
    }

    @PostMapping("/register")
    public String registerUser(
            @Valid RegisterUserDTO registerUserDTO, BindingResult bindingResult
    ) {
        if(customUserRepository.findUserByUsername(registerUserDTO.username()).isPresent()) {
            bindingResult.rejectValue("username", "username.exists" , "a user with that username already exists");
        }
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



        System.out.println("Saving user... ");
        customUserRepository.save(customUser);

        return "redirect:/login";
    }

    @ModelAttribute("tasks")
    public List<CustomTask> populateTasks(Principal principal) {
        if (principal == null) return List.of();
        Optional<CustomUser> user = customUserRepository.findUserByUsername(principal.getName());
        if (user.isEmpty()) return List.of();
        return customTaskRepository.findByUserId(user.get().getId());
    }

    @GetMapping("/")
    public String homePage(Model model, Principal principal) {
        if (principal == null) return "homepage";
        Optional<CustomUser> customUser = customUserRepository.findUserByUsername(principal.getName());
        if(customUser.isEmpty()) return "homepage";

        logger.info("Is admin: {}", customUser.get().getRoles().contains(UserRole.ADMIN));

        model.addAttribute("isAdmin", customUser.get().getRoles().contains(UserRole.ADMIN));
        return "homepage";
    }

    @GetMapping("/tasks")
    public String taskPage(Model model, Principal principal) {
        model.addAttribute("customTaskDTO", new CustomTaskDTO("",""));
        return "createtask";

    }

    @DeleteMapping("/tasks")
    public String deleteTask(@RequestParam Long taskId) {
        customTaskRepository.deleteById(taskId);
        return "redirect:/tasks";
    }

    @PostMapping("/tasks")
    public String task(@Valid CustomTaskDTO customTaskDTO, BindingResult bindingResult, Principal principal){

        if (bindingResult.hasErrors()) {
            return "createtask";
        }

        Optional<CustomUser> user = customUserRepository.findUserByUsername(principal.getName());

        if(user.isPresent()){
            CustomTask task = new CustomTask();
            task.setTitle(customTaskDTO.title());
            task.setDescription(customTaskDTO.description());
            task.setUser(user.get());

            logger.info("added task '{}' to user '{}'", task.getTitle(), principal.getName());

            customTaskRepository.save(task);
        }

        return "redirect:/tasks";
    }
}
