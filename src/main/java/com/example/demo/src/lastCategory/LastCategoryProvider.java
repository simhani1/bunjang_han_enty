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

    /**
     * 하위 카테고리 리스트 조회
     * @param firstCategoryId
     * @return
     * @throws BaseException
     */
    public List<GetLastCategoryRes> getLastCategories(int firstCategoryId) throws BaseException {
        try{
            List<GetLastCategoryRes> getLastCategoryRes = lastCategoryDao.getLastCategories(firstCategoryId);
            return getLastCategoryRes;
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);

        }
    }

    /**
     * 하위 카테고리 마지막 번호 조회
     */
    public int getLastCategoryIdCount(){
        return lastCategoryDao.getLastCategoryIdCount();
    }

}
