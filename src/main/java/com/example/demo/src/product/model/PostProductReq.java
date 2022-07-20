package com.example.demo.src.product.model;

import com.example.demo.src.productImg.model.PostProductImgReq;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PostProductReq {

    private List<PostProductImgReq> productImgs;
    private String title;
    private int firstCategoryId;
    private int lastCategoryId;
    private int price;
    private String contents;
    private Boolean amount;
    private Boolean isUsed;
    private Boolean changeable;
    private Boolean pay;
    private Boolean shippingFee;

}
