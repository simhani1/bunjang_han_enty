package com.example.demo.src.productImg;

import org.springframework.stereotype.Service;

@Service
public class ProductImgProvider {

    final private ProductImgDao productImgDao;
//    final private ProductImgService productImgService;

    public ProductImgProvider(ProductImgDao productImgDao){
        this.productImgDao = productImgDao;
    }
}
