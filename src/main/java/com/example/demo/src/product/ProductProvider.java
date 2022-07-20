package com.example.demo.src.product;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.product.model.GetProductRes;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class ProductProvider {

    final private ProductDao productDao;

    public ProductProvider(ProductDao productDao){
        this.productDao = productDao;
    }

    public GetProductRes getProductById(int userId,int productId) throws BaseException {
        try{
            GetProductRes getProductRes = productDao.getProductById(userId,productId);
            return getProductRes;
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
