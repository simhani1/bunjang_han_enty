package com.example.demo.src.gallery;

import com.example.demo.src.gallery.model.Gallery;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GalleryProvider {
    private final GalleryDao galleryDao;

    public GalleryProvider(GalleryDao galleryDao){
        this.galleryDao = galleryDao;
    }

    public List<Gallery> getGallery(){
        List<Gallery> gallery = galleryDao.getGallery();
        return gallery;
    }
    public void insertImg(String imgUrl){
        galleryDao.insertImg(imgUrl);
    }
}
