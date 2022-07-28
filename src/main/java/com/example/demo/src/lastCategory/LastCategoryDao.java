package com.example.demo.src.lastCategory;

import com.example.demo.src.firstCategory.model.GetFirstCategoryRes;
import com.example.demo.src.lastCategory.model.GetLastCategoryRes;
import com.example.demo.src.lastCategory.model.LastCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class LastCategoryDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired //readme 참고
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * 하위 카테고리 조회
     * @param firstCategoryId
     * @return
     */
    public List<GetLastCategoryRes> getLastCategories(int firstCategoryId){
        String getLastCategoryQuery =
                "select firstCategory.firstCategoryId, firstCategory.firstCategory, " +
                        "firstCategory.firstCategoryImgUrl, lastCategory.lastCategoryId, " +
                        "lastCategory.lastCategory, lastCategory.lastCategoryImgUrl " +
                "from lastCategory " +
                "left join firstCategory on firstCategory.firstCategoryId = lastCategory.firstCategoryId " +
                "where firstCategory.firstCategoryId = ?";

        int getCategoryParams = firstCategoryId;

        return this.jdbcTemplate.query(getLastCategoryQuery,
                (rs, rowNum) -> new GetLastCategoryRes(
                        rs.getInt("firstCategoryId"),
                        rs.getString("firstCategory"),
                        rs.getString("firstCategoryImgUrl"),
                        rs.getInt("lastCategoryId"),
                        rs.getString("lastCategory"),
                        rs.getString("lastCategoryImgUrl")),
                getCategoryParams);
    }

    public List<LastCategory> getLastCategory(){
        String getLastCategoryQuery = "select * from lastCategory";
        return this.jdbcTemplate.query(getLastCategoryQuery,
                (rs, rowNum) -> new LastCategory(
                        rs.getInt("lastCategoryId"),
                        rs.getInt("firstCategoryId"),

                        rs.getString("lastCategory"),
                        rs.getString("lastCategoryImgUrl")
                ));
    }
    public int getLastCategoryIdCount(){
        String getLastCategoryIdCountQuery = "select count(*) from lastCategory";
        return this.jdbcTemplate.queryForObject(getLastCategoryIdCountQuery, int.class);
    }
}
