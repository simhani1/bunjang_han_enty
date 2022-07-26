package com.example.demo.src.shop;

import org.springframework.stereotype.Service;

@Service
public class ShopProvider {
    private final ShopDao shopDao;

    public ShopProvider(ShopDao shopDao){
        this.shopDao = shopDao;
    }

    public int checkExistsModifyNickname(int userId, String nickname){
        return shopDao.checkExistsModifyNickname(userId, nickname);
    }
}
