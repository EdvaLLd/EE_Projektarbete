package com.edvalld.projektarbete.user.dto;

import com.edvalld.projektarbete.user.authority.UserRole;
import com.edvalld.projektarbete.validator.ValidRoles;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.Set;

//behövdes för att registrera användare
public record RegisterUserDTO(
        @NotBlank(message = "Username must not be empty")
        @Size(min = 2, max = 25, message = "Username must be between 2 and 25 characters")
        String username,

        @NotBlank
        @Size(max=80)
        @Pattern(
                regexp = "^" +
                        "(?=.*[a-z])" +        // at least one lowercase letter
                        "(?=.*[A-Z])" +        // at least one uppercase letter
                        "(?=.*[0-9])" +        // at least one digit
                        "(?=.*[ @$!%*?&])" +   // at least one special character
                        ".+$",                 // one or more characters, until end
                message = "Password must contain at least one uppercase, one lowercase, one digit, and one special character"
        ) String password,

        String adminPass
) {}
