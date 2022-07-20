package com.example.demo.src.account.model;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Getter // 해당 클래스에 대한 접근자 생성
@Setter // 해당 클래스에 대한 설정자 생성
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // 해당 클래스의 파라미터가 없는 생성자를 생성, 접근제한자를 PROTECTED로 설정.

public class Account {
    private int userId;
    private String name;
    private int bankId;
    private String accountNum;
    private boolean standard;
    private String bankName;
    private String bankImgUrl;

    public Account(String name, int bankId, String accountNum, boolean standard) {
        this.name = name;
        this.bankId = bankId;
        this.accountNum = accountNum;
        this.standard = standard;
    }
}
