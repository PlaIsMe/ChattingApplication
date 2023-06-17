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
public class Message {
    private Long id;

    @Size(max = 255)
    @NotBlank
    private String content;

    private LocalDateTime createAt;
}
