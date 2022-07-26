package com.example.demo.src.shop;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.shop.model.GetShopInformRes;
import com.example.demo.src.shop.model.PatchShopReq;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    // 상점 정보 조회(수정할 때 나오는 화면)
//    @GetMapping("/inform/{userId}")
//    public BaseResponse<GetShopInformRes> getShopInform(@PathVariable int userId){
//        try{
//            if (userId != jwtService.getUserId()){
//                return new BaseResponse<>(INVALID_USER_JWT);
//            }
//
//            GetShopInformRes getShopInformRes = shopProvider.getShopInform(userId);
//            return new BaseResponse<>(GET_SHOP_INFORM_SUCCESS,getShopInformRes);
//        } catch (BaseException exception){
//            return new BaseResponse<>((exception.getStatus()));
//        }
//    }
    // 상점 정보 수정
    @PatchMapping("/{userId}")
    public BaseResponse<String> modifyShopsInform(@PathVariable int userId,
                                                  @RequestBody PatchShopReq patchShopReq){
        try{
            if (userId != jwtService.getUserId()){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            // 프로필 사진이 빈칸일 때
            if (patchShopReq.getProfileImgUrl().equals("")){
                return new BaseResponse<>(EMPTY_PROFILE_IMG);
            }
            // startTime 이 빈칸일 때
            if (patchShopReq.getStartTimeId().equals("")){
                return new BaseResponse<>(EMPTY_START_TIME);
            }
            // endTime 이 빈칸일 때
            if (patchShopReq.getEndTimeId().equals("")){
                return new BaseResponse<>(EMPTY_END_TIME);
            }
            // 시작 시간이 음수이거나 24를 넘어갈 때
            if (Integer.parseInt(patchShopReq.getStartTimeId()) <= 0 || Integer.parseInt(patchShopReq.getStartTimeId()) > 24){
                return new BaseResponse<>(INVALID_START_TIME);
            }
            // 끝 시간이 음수이거나 24를 넘어갈 때
            if (Integer.parseInt(patchShopReq.getEndTimeId()) <= 0 || Integer.parseInt(patchShopReq.getEndTimeId()) > 24){
                return new BaseResponse<>(INVALID_END_TIME);
            }
            shopService.modifyShops(userId, patchShopReq);
            return new BaseResponse<>(MODIFY_SHOP_SUCCESS);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

}
