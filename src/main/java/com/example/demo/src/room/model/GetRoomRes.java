package com.example.demo.src.room.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetRoomRes {
    private int chatRoomId;
    private int productId;
    private int sellerId;
    private int buyerId;
    private Boolean isDeleted;
}
