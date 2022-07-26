package com.example.demo.src.shop;

import com.example.demo.config.BaseException;
import com.example.demo.src.shop.model.PatchShopReq;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class ShopService {
    public final ShopDao shopDao;
    public final ShopProvider shopProvider;

    public ShopService(ShopDao shopDao, ShopProvider shopProvider){
        this.shopDao = shopDao;
        this.shopProvider = shopProvider;
    }

    public void modifyShopsInform(int userId, String inform) throws BaseException {
        try{
            if(shopDao.modifyShopsInform(userId, inform) == 0){
                throw new BaseException(MODIFY_SHOPS_INFORM_FAILD);
            }
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }

    }
}
