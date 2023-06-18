package com.chattingapplication.springbootserver.model;

import java.time.LocalDateTime;

import com.chattingapplication.springbootserver.entity.AccountEntity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    public User(Long id) {
        this.id = id;
    }

    private Long id;

    @NotBlank(message = "last name must not be blank")
    @Size(max = 40, min = 3, message = "last name must be between 3 and 40 characters")
    private String lastName;

    @NotBlank(message = "first name must not be blank")
    @Size(max = 40, min = 3, message = "first name must be between 3 and 40 characters")
    private String firstName;

    private String avatar;

    @Size(max = 20, min = 2, message = "gender must be between 2 and 20 characters")
    private String gender;
    private LocalDateTime dob;
}
