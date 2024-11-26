package ru.practicum.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record NewUserRequest(@NotNull
                             @Size(min = 2, max = 250)
                             @NotBlank
                             String name,
                             @Email
                             @NotNull
                             @Size(min = 6, max = 254)
                             String email) {
}