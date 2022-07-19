package com.example.demo.src.product;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.product.model.PostProductReq;
import com.example.demo.src.product.model.PostProductRes;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class ProductService {

    final private ProductDao productDao;

    public ProductService(ProductDao productDao){
        this.productDao = productDao;
    }

    public PostProductRes createProduct(int userId, PostProductReq postProductReq) throws BaseException {
        try{
            int productId = productDao.createProduct(userId, postProductReq);
            return new PostProductRes(productId);

        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
