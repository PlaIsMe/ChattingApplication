package com.chattingapplication.springbootserver.model;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;

    @NotBlank
    private String lastName;

    @NotBlank
    private String firstName;

    private String avatar;

    @Size(max = 20, min = 2)
    private String gender;
    private LocalDateTime dob;
}
