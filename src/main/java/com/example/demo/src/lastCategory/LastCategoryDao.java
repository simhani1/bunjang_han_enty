package com.example.demo.src.lastCategory;

import com.example.demo.src.firstCategory.model.GetFirstCategoryRes;
import com.example.demo.src.lastCategory.model.GetLastCategoryRes;
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

    public List<GetLastCategoryRes> getLastCategories(int firstCategoryId){
        String getLastCategoryQuery = "select * from lastCategory where firstCategoryId = ?";
        int getCategoryParams = firstCategoryId;

        return this.jdbcTemplate.query(getLastCategoryQuery,
                (rs, rowNum) -> new GetLastCategoryRes(
                        rs.getInt("firstCategoryId"),
                        rs.getInt("lastCategoryId"),
                        rs.getString("lastCategory"),
                        rs.getString("categoryImg")),
                getCategoryParams);
    }
}
