package com.example.demo.src.firstCategory;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.firstCategory.model.GetFirstCategoryRes;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class FirstCategoryProvider {

    final private FirstCategoryDao firstCategoryDao;

    public FirstCategoryProvider(FirstCategoryDao firstCategoryDao){
        this.firstCategoryDao = firstCategoryDao;
    }

    public List<GetFirstCategoryRes> getCategories() throws BaseException {
        try{
            List<GetFirstCategoryRes> getFirstCategoryRes = firstCategoryDao.getCategories();
            return getFirstCategoryRes;

        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
