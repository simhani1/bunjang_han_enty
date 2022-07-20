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

        int productId;
        String createProductQuery;
        String lastInsertIdQuery;
        String insertProductImgQuery;
        String insertProductTagQuery;
        Object[] createProductParams;
        Object[] insertProductImgParams;
        Object[] insertProductTagParams;

        createProductQuery = "insert into product (userId, title, firstCategoryId, lastCategoryId, price, contents, amount, isUsed, changeable, pay, shippingFee) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        createProductParams = new Object[]{userId, postProductReq.getTitle(), postProductReq.getFirstCategoryId(), postProductReq.getLastCategoryId(), postProductReq.getPrice(), postProductReq.getContents(), postProductReq.getAmount(), postProductReq.getIsUsed(), postProductReq.getChangeable(), postProductReq.getPay(), postProductReq.getShippingFee()};

        this.jdbcTemplate.update(createProductQuery, createProductParams);

        lastInsertIdQuery = "select count(*) from product";
        productId = this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);

        insertProductImgQuery = "insert into productImg (productId, productImgUrl) values (?,?)";
        insertProductTagQuery = "insert into productTag (productId, tagContents) values (?,?)";

        for(int i = 0; i < postProductReq.getProductImgs().size(); i++){
            insertProductImgParams = new Object[]{productId, postProductReq.getProductImgs().get(i)};
            this.jdbcTemplate.update(insertProductImgQuery, insertProductImgParams);
        }

        for(int i = 0; i < postProductReq.getTags().size(); i++){
            insertProductTagParams = new Object[]{productId, postProductReq.getTags().get(i)};
            this.jdbcTemplate.update(insertProductTagQuery, insertProductTagParams);
        }

        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);
    }
}
