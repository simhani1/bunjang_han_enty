package com.example.demo.src.lastCategory.model;

import com.example.demo.src.firstCategory.model.FirstCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetLastCategoryRes {

    private int firstCategoryId;
    private String firstCategory;
    private String firstCategoryImgUrl;
    private int lastCategoryId;
    private String lastCategory;
    private String lastCategoryImgUrl;
}
