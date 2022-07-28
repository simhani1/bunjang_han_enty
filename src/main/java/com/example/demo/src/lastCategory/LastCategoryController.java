package com.example.demo.src.lastCategory;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.firstCategory.FirstCategoryProvider;
import com.example.demo.src.lastCategory.model.GetLastCategoryRes;
import com.example.demo.src.lastCategory.model.LastCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@RestController
@RequestMapping("/app/last/categories")
public class LastCategoryController {

    final private LastCategoryProvider lastCategoryProvider;
    final private LastCategoryService lastCategoryService;
    final private LastCategoryDao lastCategoryDao;
    final private FirstCategoryProvider firstCategoryProvider;

    @Autowired
    public LastCategoryController(LastCategoryProvider lastCategoryProvider, LastCategoryService lastCategoryService, LastCategoryDao lastCategoryDao, FirstCategoryProvider firstCategoryProvider){
        this.lastCategoryProvider = lastCategoryProvider;
        this.lastCategoryService = lastCategoryService;
        this.lastCategoryDao = lastCategoryDao;
        this.firstCategoryProvider = firstCategoryProvider;
    }

    @GetMapping("/{firstCategoryId}")
    public BaseResponse<List<GetLastCategoryRes>> getLastCategories(@PathVariable int firstCategoryId){
        try{
            if(firstCategoryId <= 0 || firstCategoryId > firstCategoryProvider.getCategoryCount()){
                throw new BaseException(NO_EXISTED_FIRST_CATEGORY);
            }
            List<GetLastCategoryRes> getLastCategoryRes = lastCategoryProvider.getLastCategories(firstCategoryId);
            return new BaseResponse<>(GET_LAST_CATEGORY_SUCCESS, getLastCategoryRes);
        } catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @GetMapping("")
    public BaseResponse<List<LastCategory>> getLastCategory(){
        try {
            List<LastCategory> lastCategory = lastCategoryProvider.getLastCategory();
            return new BaseResponse<>(GET_LAST_CATEGORY_SUCCESS, lastCategory);
        } catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }

    }
}
