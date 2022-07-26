package com.example.demo.src.shop.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PatchShopReq {
    private String profileImgUrl;
    private String nickname;
    private String startTimeId;
    private String endTimeId;
    private String introduce;
    private String policy;
    private String caution;
}
