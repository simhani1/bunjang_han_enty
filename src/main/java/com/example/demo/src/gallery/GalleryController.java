package com.example.demo.src.gallery;

import com.example.demo.config.BaseResponse;
import com.example.demo.src.gallery.model.Gallery;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/gallery")
public class GalleryController {
    private final GalleryProvider galleryProvider;
    private final GalleryDao galleryDao;

    public GalleryController(GalleryProvider galleryProvider, GalleryDao galleryDao){
        this.galleryProvider = galleryProvider;
        this.galleryDao = galleryDao;
    }


    @GetMapping("")
    public BaseResponse<List<Gallery>> getGallery(){
        List<Gallery> gallery = galleryProvider.getGallery();
        return new BaseResponse<>(gallery);
    }

    @PostMapping("")
    public String insertImg(@RequestBody String imgUrl){
        galleryProvider.insertImg(imgUrl);
        return "넣었다.";
    }

}
