package com.example.demo.src.shop;

import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/app/shops")
public class ShopController {

    private final ShopProvider shopProvider;
    private final ShopService shopService;
    private final ShopDao shopDao;
    private final JwtService jwtService;

    @Autowired
    public ShopController(ShopProvider shopProvider, ShopService shopService, ShopDao shopDao, JwtService jwtService){
        this.shopProvider = shopProvider;
        this.shopService = shopService;
        this.shopDao = shopDao;
        this.jwtService = jwtService;
    }


}
