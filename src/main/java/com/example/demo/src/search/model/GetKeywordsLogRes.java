package com.example.demo.src.search.model;

import com.example.demo.src.productImg.model.GetProductImgRes;
import com.example.demo.src.tag.model.GetTagRes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Getter // 해당 클래스에 대한 접근자 생성
@Setter // 해당 클래스에 대한 설정자 생성
@AllArgsConstructor // 해당 클래스의 모든 멤버 변수(userIdx, nickname, email, password)를 받는 생성자를 생성

public class GetKeywordsLogRes {
    private String keyword;
}
