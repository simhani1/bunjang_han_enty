package com.example.demo.src.test;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class TestService {
    private final TestDao testDao;

    public TestService(TestDao testDao){
        this.testDao = testDao;
    }

//    public void uploadImgFile(UploadImg uploadImg){
//        testDao.uploadImgFile(uploadImg);
//    }
}
