package com.example.demo.src.kakao.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class KakaoUser {

    private int userId;
    private String refreshToken;
    private String email;
    private String nickname;
    private String profileImgUrl;
    private String location;
    private String phoneNum;


}
