package com.example.demo.src.banner.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Banner {
    private int bannerId;
    private String bannerImgUrl;
    private String bannerName;
}
