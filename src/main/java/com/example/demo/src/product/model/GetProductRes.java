package com.example.demo.src.product.model;

import com.example.demo.src.lastCategory.model.GetLastCategoryRes;
import com.example.demo.src.productImg.model.GetProductImgRes;
import com.example.demo.src.tag.model.GetTagRes;
import com.example.demo.src.user.model.GetUserRes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetProductRes {

    private int productId;
    private String condition;
    private List<GetProductImgRes> productImgs;
    private int price;
    private Boolean pay;
    private String title;
    private String location;
    private LocalDateTime updatedAt;
//    private int viewCnt;
//    private int heartCnt;
//    private int chatCnt;
    private Boolean isUsed;
    private int amount;
    private Boolean shippingFee;
    private Boolean changeable;
    private String contents;
//    private GetBrandRes brand;
    private String categoryImgUrl;
    private String lastCategory;
    private List<GetTagRes> tags;
    private String profileImgUrl;
    private String nickname;
//    private int commentCount;

}
