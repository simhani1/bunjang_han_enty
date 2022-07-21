package com.example.demo.src.firstCategory;

import com.example.demo.src.firstCategory.model.GetFirstCategoryRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class FirstCategoryDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired //readme 참고
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

//    public List<GetUserRes> getUsers() {
//        String getUsersQuery = "select * from User"; //User 테이블에 존재하는 모든 회원들의 정보를 조회하는 쿼리
//        return this.jdbcTemplate.query(getUsersQuery,
//                (rs, rowNum) -> new GetUserRes(
//                        rs.getInt("userId"),
//                        rs.getString("nickname"),
//                        rs.getString("Email"),
//                        rs.getString("password")) // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
//        ); // 복수개의 회원정보들을 얻기 위해 jdbcTemplate 함수(Query, 객체 매핑 정보)의 결과 반환(동적쿼리가 아니므로 Parmas부분이 없음)
//    }

    public List<GetFirstCategoryRes> getCategories(){
        String getCategoryQuery =
                "select * " +
                "from firstCategory";

        return this.jdbcTemplate.query(getCategoryQuery,
                (rs, rowNum) -> new GetFirstCategoryRes(
                        rs.getInt("firstCategoryId"),
                        rs.getString("firstCategory"),
                        rs.getString("firstCategoryImgUrl"))
                );

    }

    public int getCategoryCount(){
        String getCategoryCountQuery =
                "select count(firstCategoryId) " +
                "from firstCategory";

        return this.jdbcTemplate.queryForObject(getCategoryCountQuery, Integer.class);
    }

}
