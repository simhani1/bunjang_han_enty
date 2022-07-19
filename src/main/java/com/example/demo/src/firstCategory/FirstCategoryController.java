package com.example.demo.src.firstCategory;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.firstCategory.model.GetFirstCategoryRes;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.GET_CATEGORY_SUCCESS;

@RestController
@RequestMapping("/app/categories")
public class FirstCategoryController {

    final private FirstCategoryProvider firstCategoryProvider;
    final private FirstCategoryService firstCategoryService;
    final private FirstCategoryDao firstCategoryDao;

    public FirstCategoryController(FirstCategoryProvider firstCategoryProvider, FirstCategoryService firstCategoryService, FirstCategoryDao firstCategoryDao){
        this.firstCategoryProvider = firstCategoryProvider;
        this.firstCategoryService = firstCategoryService;
        this.firstCategoryDao = firstCategoryDao;
    }

    @GetMapping("")
    public BaseResponse<List<GetFirstCategoryRes>> getCategories(){
        try{
            List<GetFirstCategoryRes> getFirstCategoryRes = firstCategoryProvider.getCategories();
            return new BaseResponse<>(GET_CATEGORY_SUCCESS, getFirstCategoryRes);
        } catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
