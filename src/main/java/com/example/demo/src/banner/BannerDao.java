package com.example.demo.src.banner;

import com.example.demo.src.banner.model.GetBannerRes;
import com.example.demo.src.user.model.GetUserRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class BannerDao {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private JdbcTemplate jdbcTemplate;

    public int getBannerLastNum(){
        String getBannerLastNumQuery = "select count(*) from banner";
        return this.jdbcTemplate.queryForObject(getBannerLastNumQuery, int.class);
    }
    @Autowired //readme 참고
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<GetBannerRes> getBanners(){
        String getUsersQuery = "select * from banner"; //User 테이블에 존재하는 모든 회원들의 정보를 조회하는 쿼리
        return this.jdbcTemplate.query(getUsersQuery,
                (rs, rowNum) -> new GetBannerRes(
//                        rs.getInt("bannerId"),
                        rs.getString("bannerImgUrl"),
                        rs.getString("bannerName")
                ) // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
        );
    }
}
