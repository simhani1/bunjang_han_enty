package com.example.demo.src.shop.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetShopInformRes {
    private int userId;
    private String profileImgUrl;
    private String nickname;
    private int startTimeId;
    private int endTimeId;
    private String startTime;
    private String endTime;
    private String introduce;
    private String policy;
    private String caution;
}
