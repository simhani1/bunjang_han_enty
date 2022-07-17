package com.example.demo.src.banner;

import com.example.demo.config.BaseException;
import com.example.demo.src.banner.model.GetBannerRes;
import com.example.demo.src.user.model.GetUserRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class BannerProvider {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    final private BannerDao bannerDao;

    public BannerProvider(BannerDao bannerDao){
        this.bannerDao = bannerDao;
    }

    public List<GetBannerRes> getBanners() throws BaseException {
        try {
            List<GetBannerRes> getBannerRes = bannerDao.getBanners();
            return getBannerRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
