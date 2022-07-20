package com.example.demo.src.productImg.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ProductImg {

    private int productImgId;
    private int productId;
    private List<String> productImgUrl;

}
