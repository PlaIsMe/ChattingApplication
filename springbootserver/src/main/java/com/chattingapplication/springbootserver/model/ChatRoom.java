package com.chattingapplication.springbootserver.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoom {
    private Long id;

    @Size(max = 40)
    @NotBlank
    private String roomName;
    private boolean isPrivate;
    public ChatRoom(@Size(max = 40) @NotBlank String roomName, boolean isPrivate) {
        this.roomName = roomName;
        this.isPrivate = isPrivate;
    }
}
