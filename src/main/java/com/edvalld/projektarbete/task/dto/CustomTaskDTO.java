package com.edvalld.projektarbete.task.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CustomTaskDTO (
        @Size(max = 50, message = "Title may not be more than 50 characters")
        @NotBlank(message = "Title may not contain whitespace characters only")
        String title,
        @Size(max = 255, message = "Description may not be more than 255 characters")
        @NotBlank(message = "Description may not contain whitespace characters only")
        String description
){
}
