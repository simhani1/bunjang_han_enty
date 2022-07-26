package com.example.demo.src.shop;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.shop.model.PatchShopReq;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

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

    @PatchMapping("/informs/{userId}")
    public BaseResponse<String> modifyShopsInform(@PathVariable int userId,
                                                  @RequestBody PatchShopReq patchShopReq){
        try{
            if (userId != jwtService.getUserId()){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            if(inform.getClass() != String.class){
                return new BaseResponse<>(NOT_STRING_TYPE_INFORM);
            }
            shopService.modifyShopsInform(userId, inform);
            return new BaseResponse<>(MODIFY_SHOP_INFORM_SUCCESS);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

}
