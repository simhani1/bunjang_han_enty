package com.example.demo.src.follow.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetFollowingProductRes {
    private int productId;
    private String productImgUrl;
    private int price;
}
