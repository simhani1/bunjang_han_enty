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
public class LastCategory {

    private int lastCategoryId;
    private int firstCategoryId;
    private String lastCategory;
    private String lastCategoryImgUrl;
}
