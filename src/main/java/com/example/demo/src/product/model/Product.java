package com.example.demo.src.product.model;

import com.example.demo.src.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    private int productId;
    private int userId;
    private String title;
    private int firstCategoryId;
    private int lastCategoryId;
    private int price;
    private String contents;
    private String condition;
    private Boolean isDeleted;
    private int amount;
    private Boolean isUsed;
    private Boolean changeable;
    private Boolean pay;
    private Boolean shippingFee;
    private int buyerId;
    private LocalDateTime updatedAt;

}
