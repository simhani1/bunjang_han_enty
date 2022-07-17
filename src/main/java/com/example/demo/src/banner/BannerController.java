package com.example.demo.src.banner;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.banner.model.GetBannerRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.GET_BANNER_SUCCESS;

@RestController
@RequestMapping("/app/banner")
public class BannerController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    final private BannerProvider bannerProvider;
    final private BannerService bannerService;
    final private BannerDao bannerDao;


    @Autowired
    public BannerController(BannerProvider bannerProvider, BannerService bannerService, BannerDao bannerDao){
        this.bannerProvider = bannerProvider;
        this.bannerService = bannerService;
        this.bannerDao = bannerDao;
    }

    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<GetBannerRes>> getBanners(){
        try{
            List<GetBannerRes> getBannerRes = bannerProvider.getBanners();
            return new BaseResponse<>(GET_BANNER_SUCCESS, getBannerRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }


    }
}
