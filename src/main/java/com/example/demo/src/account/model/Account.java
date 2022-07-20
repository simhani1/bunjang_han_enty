package com.example.demo.src.account.model;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Getter // 해당 클래스에 대한 접근자 생성
@Setter // 해당 클래스에 대한 설정자 생성
@AllArgsConstructor

public class Account {
    private int userId;
    private String name;
    private int bankId;
    private String accountNum;
    private boolean standard;
    private String bankName;
    private String bankImgUrl;
}
