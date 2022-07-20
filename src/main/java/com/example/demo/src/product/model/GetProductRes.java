package com.example.demo.src.product.model;

import com.example.demo.src.lastCategory.model.GetLastCategoryRes;
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
    private List<String> productImgs;
    private int price;
    private String title;
    private GetUserRes user;
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
    private GetLastCategoryRes category;
    private List<String> tags;


    private Boolean isDeleted;
    private Boolean amount;
    private Boolean changeable;
    private Boolean pay;
    private int buyerId;
    private Boolean shippingFee;
}
