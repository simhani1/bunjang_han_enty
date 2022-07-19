package com.example.demo.src.user.model;

import lombok.*;
import org.springframework.web.bind.annotation.PatchMapping;

import java.time.LocalDate;
import java.util.Date;

@Getter // 해당 클래스에 대한 접근자 생성
@Setter // 해당 클래스에 대한 설정자 생성
@AllArgsConstructor // 해당 클래스의 모든 멤버 변수(userIdx, nickname)를 받는 생성자를 생성
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // 해당 클래스의 파라미터가 없는 생성자를 생성, 접근제한자를 PROTECTED로 설정.

public class PatchUserReq {
    private int userId;
    private String phoneNum;
    private String birth;
    private boolean gender;  // true: 남자 false: 여자
    private boolean status;  // true: 활동중 false: 탈퇴
    private String key;

    public PatchUserReq(String key, String phoneNum) {
        if(key.equals("phoneNum"))
            this.phoneNum = phoneNum;
        else if(key.equals("birth"))
            this.birth = birth;
    }

    public PatchUserReq(String key, boolean value) {
        this.key = key;
        if(key.equals("gender"))
            this.setGender(value);
        else if(key.equals("status"))
            this.setStatus(value);
    }
}
