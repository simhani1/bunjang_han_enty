package com.example.demo.src.banner;

import com.example.demo.config.BaseException;
import com.example.demo.src.banner.model.GetBannerRes;
import com.example.demo.src.user.model.GetUserRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class BannerProvider {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    final private BannerDao bannerDao;

    public BannerProvider(BannerDao bannerDao){
        this.bannerDao = bannerDao;
    }

    public int getBannerLastNum(){
        return bannerDao.getBannerLastNum();
    }
    /**
     * 배너 조회
     * @return
     * @throws BaseException
     */
    public List<GetBannerRes> getBanners() throws BaseException {
        if(getBannerLastNum() < 1){
            throw new BaseException(NO_EXISTED_BANNER);
        }
        try {
            List<GetBannerRes> getBannerRes = bannerDao.getBanners();
            return getBannerRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
