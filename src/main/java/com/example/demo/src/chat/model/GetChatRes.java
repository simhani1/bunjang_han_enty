package com.example.demo.src.chat.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetChatRes {
    private int userId;
    private int roomId;
    private String message;
    private String messageType;
    private String createdAt;
}
