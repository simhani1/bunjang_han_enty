package com.example.demo.src.user.model;

import lombok.*;

@Getter // 해당 클래스에 대한 접근자 생성
@Setter // 해당 클래스에 대한 설정자 생성
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class PostShopReviewReq {
    private int productId;
    private double star;
    private String reviewContents;
}
