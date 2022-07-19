package com.example.demo.src.lastCategory;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.lastCategory.model.GetLastCategoryRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.GET_LAST_CATEGORY_SUCCESS;

@RestController
@RequestMapping("/app/last/categories")
public class LastCategoryController {

    final private LastCategoryProvider lastCategoryProvider;
    final private LastCategoryService lastCategoryService;
    final private LastCategoryDao lastCategoryDao;

    @Autowired
    public LastCategoryController(LastCategoryProvider lastCategoryProvider, LastCategoryService lastCategoryService, LastCategoryDao lastCategoryDao){
        this.lastCategoryProvider = lastCategoryProvider;
        this.lastCategoryService = lastCategoryService;
        this.lastCategoryDao = lastCategoryDao;
    }

//    @GetMapping("/{firstCategoryId}")
//    public BaseResponse<List<GetLastCategoryRes>> getLastCategories(@PathVariable int firstCategoryId){
//        try{
//            List<GetLastCategoryRes> getLastCategoryRes = lastCategoryProvider.getLastCategories();
//            return new BaseResponse<>(GET_LAST_CATEGORY_SUCCESS, getLastCategoryRes);
//        } catch (BaseException exception){
//            return new BaseResponse<>(exception.getStatus());
//        }
//    }
}
