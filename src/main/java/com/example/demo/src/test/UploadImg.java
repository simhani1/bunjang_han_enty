package com.example.demo.src.test;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UploadImg {
    private int ImgId;
    private String mimetype;
    private String originalName;
    private byte[] data;
    private String created;
}
