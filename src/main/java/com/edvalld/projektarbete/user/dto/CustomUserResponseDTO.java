package com.edvalld.projektarbete.user.dto;

import jakarta.validation.constraints.*;

public record CustomUserResponseDTO(
        @Size(min = 2, max = 25, message = "Username length should be between 2-25")
        @NotBlank(message = "Username may not contain whitespace characters only")
        String username
) {
}
