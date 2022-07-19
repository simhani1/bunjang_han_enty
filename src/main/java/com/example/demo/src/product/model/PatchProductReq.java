package com.example.demo.src.product.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PatchProductReq {

    private int productId;
    private int userId;
    private String title;
    private int firstCategoryId;
    private int lastCategoryId;
    private int price;
    private String contents;
    private String condition;
    private String isDeleted;
    private int amount;
    private String isUsed;
    private String changeable;
    private String pay;
    private int buyerId;
    private String shippingFee;

}
