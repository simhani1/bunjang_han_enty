package com.example.demo.src.user.model;

import lombok.*;

import java.time.LocalDate;
import java.util.Date;

@Getter // 해당 클래스에 대한 접근자 생성
@Setter // 해당 클래스에 대한 설정자 생성
@AllArgsConstructor // 해당 클래스의 모든 멤버 변수(userIdx, nickname)를 받는 생성자를 생성
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // 해당 클래스의 파라미터가 없는 생성자를 생성, 접근제한자를 PROTECTED로 설정.

public class PatchUserReq {
    private int userId;
    private String phoneNum;
    private Date birth;
    private String gender;
    private String key;
    // 정보 수정
    public PatchUserReq(int userId, String key, String value) {
        this.userId = userId;
        this.key = key;
        if(key.equals("phoneNum"))
            this.phoneNum = value;
        else if(key.equals("gender"))
            this.gender = value;
    }
}
