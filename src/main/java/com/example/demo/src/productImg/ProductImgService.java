package com.example.demo.src.productImg;

import org.springframework.stereotype.Service;

@Service
public class ProductImgService {

    final private ProductImgDao productImgDao;
//    final private ProductImgService productImgService;

    public ProductImgService(ProductImgDao productImgDao){
        this.productImgDao = productImgDao;
    }
}
