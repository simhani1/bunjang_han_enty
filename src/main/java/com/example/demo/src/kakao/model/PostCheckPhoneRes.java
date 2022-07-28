package com.example.demo.src.kakao.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostCheckPhoneRes {
    private Boolean isMember;
    private int userId;
    private String phoneNum;
    private String jwt;

    public PostCheckPhoneRes(Boolean isMember, String phoneNum) {
        this.isMember = isMember;
        this.phoneNum = phoneNum;
    }
}
