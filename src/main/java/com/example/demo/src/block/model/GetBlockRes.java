package com.example.demo.src.block.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetBlockRes {
    private int userId;
    private int blockUserId;
    private String profileImgUrl;
    private String nickname;
    private Timestamp createdAt;
}
