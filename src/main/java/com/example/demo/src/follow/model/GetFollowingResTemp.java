package com.example.demo.src.follow.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetFollowingResTemp {
    private int userId;
    private String profileImgUrl;
    private String nickname;
    private int productCount;
    private int followerNum;
    private List<GetFollowingProductRes> products;
}
