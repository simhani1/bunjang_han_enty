package com.example.demo.src.gallery;

import com.example.demo.src.gallery.model.Gallery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class GalleryDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired //readme 참고
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<Gallery> getGallery(){
        String getGalleryQuery = "select * from gallery";
        return this.jdbcTemplate.query(getGalleryQuery,
                (rs, rowNum) -> new Gallery(
                        rs.getInt("galleryId"),
                        rs.getString("galleryImgUrl")
                ));
    }
    public void insertImg(String imgUrl){
        String insertImg = "insert into galary (galaryImgUrl) values(?)";
        this.jdbcTemplate.update(insertImg, insertImg);
    }

}
