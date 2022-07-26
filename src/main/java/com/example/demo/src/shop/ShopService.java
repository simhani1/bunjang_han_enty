package com.example.demo.src.shop;

import com.example.demo.config.BaseException;
import com.example.demo.src.shop.model.PatchShopReq;
import com.example.demo.src.user.UserProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class ShopService {
    private final ShopDao shopDao;
    private final ShopProvider shopProvider;

    public ShopService(ShopDao shopDao, ShopProvider shopProvider){
        this.shopDao = shopDao;
        this.shopProvider = shopProvider;
    }

    @Transactional
    public void modifyShops(int userId, PatchShopReq patchShopReq) throws BaseException {
        // nickname 중복 확인(수정 할 때)
        if (shopProvider.checkExistsModifyNickname(userId, patchShopReq.getNickname()) == 1) {
            throw new BaseException(EXISTS_NICKNAME);
        }
        try{
            if(shopDao.modifyShops(userId, patchShopReq) == 0){
                throw new BaseException(MODIFY_SHOPS_INFORM_FAILD);
            }
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }

    }
}
