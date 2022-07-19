package com.example.demo.src.lastCategory;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.lastCategory.model.GetLastCategoryRes;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class LastCategoryProvider {

    final private LastCategoryDao lastCategoryDao;

    public LastCategoryProvider(LastCategoryDao lastCategoryDao){
        this.lastCategoryDao = lastCategoryDao;
    }

    public List<GetLastCategoryRes> getLastCategories(int firstCategoryId) throws BaseException {
        try{
            List<GetLastCategoryRes> getLastCategoryRes = lastCategoryDao.getLastCategories(firstCategoryId);
            return getLastCategoryRes;
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);

        }
    }


}
