package com.example.demo.src.productImg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app/products/imgs")
public class ProductImgController {

    final private ProductImgProvider productImgProvider;
    final private ProductImgService productImgService;
    final private ProductImgDao productImgDao;

    @Autowired
    public ProductImgController(ProductImgProvider productImgProvider, ProductImgService productImgService, ProductImgDao productImgDao){
        this.productImgProvider = productImgProvider;
        this.productImgService = productImgService;
        this.productImgDao = productImgDao;
    }

}
