package com.example.demo.src.follow.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Follow {
    private int followId;
    // 팔로우 받는 유저
    private int userId;
    // 팔로우 하는 유저
    private int followUserId;
    private int status;
}
