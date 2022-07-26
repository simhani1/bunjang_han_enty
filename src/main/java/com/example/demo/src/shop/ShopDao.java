package com.example.demo.src.shop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class ShopDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired //readme 참고
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // 상점 소개 수정
    public int modifyShopsInform(int userId, String inform){
        String modifyShopsInformQuery = "update shop set introduce=? where userId=?";
        Object[] modifyShopsInformParams = new Object[]{inform, userId};

        return this.jdbcTemplate.update(modifyShopsInformQuery,modifyShopsInformParams);
    }
}
