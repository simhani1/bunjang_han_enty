package com.example.demo.src.user.model;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Getter // 해당 클래스에 대한 접근자 생성
@Setter // 해당 클래스에 대한 설정자 생성
@AllArgsConstructor // 해당 클래스의 모든 멤버 변수(userIdx, nickname, email, password)를 받는 생성자를 생성

public class User {
    private int userId;
    private String id;
    private String pwd;
    private String nickname;
    private String profileImgUrl;
    private String location;
    private String birth;
    private String phoneNum;
    private boolean gender;
    private boolean status;
    private Date createdAt;

    // 로그인
    public User(int userId, String pwd) {
        this.userId = userId;
        this.pwd = pwd;
    }
    public User(int userId, boolean status) {
        this.userId = userId;
        this.status = status;
    }

    public User(boolean gender) {
        this.gender = gender;
    }
}
