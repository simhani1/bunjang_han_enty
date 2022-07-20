package com.example.demo.src.product;

import com.example.demo.src.product.model.PostProductReq;
import com.example.demo.src.product.model.PostProductRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class ProductDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired //readme 참고
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int createProduct(int userId, PostProductReq postProductReq){

        String createProductQuery = "insert into product (userId, title, firstCategoryId, lastCategoryId, price, contents, amount, isUsed, changeable, pay, shippingFee) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        String insertProductImgQuery = "insert into productImg (productId, productImgUrl) values (?,?)";

        Object[] createProductParams = new Object[]{userId, postProductReq.getTitle(), postProductReq.getFirstCategoryId(), postProductReq.getLastCategoryId(), postProductReq.getPrice(), postProductReq.getContents(), postProductReq.getAmount(), postProductReq.getIsUsed(), postProductReq.getChangeable(), postProductReq.getPay(), postProductReq.getShippingFee()};

        this.jdbcTemplate.update(createProductQuery, createProductParams);

        String lastInsertIdQuery = "select count(*) from product";

        Object[] insertProductImgParams = new Object[]{lastInsertIdQuery, postProductReq.getProductImgs().get(0)};

        this.jdbcTemplate.update(insertProductImgQuery, insertProductImgParams);

        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);
    }
}
