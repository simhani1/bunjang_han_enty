package com.example.demo.src.product.model;

import com.example.demo.src.lastCategory.model.GetLastCategoryRes;
import com.example.demo.src.productImg.model.GetProductImgRes;
import com.example.demo.src.tag.model.GetTagRes;
import com.example.demo.src.user.model.GetUserRes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetProductRes {

    private int productId;
    private int userId;
    private String condition;
    private List<GetProductImgRes> productImgs;
    private int price;
    private Boolean pay;
    private String title;
    private String location;
    private String updatedAt;
    private int viewCnt;
    private int heartCnt;
    private int chatCnt;
    private Boolean isUsed;
    private int amount;
    private Boolean shippingFee;
    private Boolean changeable;
    private String contents;
    //새로추가
    private int firstCategoryId;
    private String firstCategoryImgUrl;
    private String firstCategory;
    private int lastCategoryId;
    //여기까지
    private String lastCategoryImgUrl;
    private String lastCategory;
    private List<GetTagRes> tags;
    private String profileImgUrl;
    private String nickname;
    private Double star;
    private int follower;
    private Boolean follow;
    private int commentCount;
    private Boolean heart;
    private Timestamp time;

}
