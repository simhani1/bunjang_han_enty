package com.example.demo.src.product;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.product.model.GetProductRes;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class ProductProvider {

    final private ProductDao productDao;

    public ProductProvider(ProductDao productDao){
        this.productDao = productDao;
    }


    public int getLastProductId(){
        return productDao.getLastProductId();
    }

    /**
     * 상품 조회
     * @param userId
     * @param productId
     * @return
     * @throws BaseException
     */
    public GetProductRes getProductById(int userId,int productId) throws BaseException {
        try{
            GetProductRes getProductRes = productDao.getProductById(userId,productId);
            return getProductRes;
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 전체 상품 조회
     * @param page
     * @return
     * @throws BaseException
     */
    public List<GetProductRes> getProducts(int page) throws BaseException{
        try{
            int amount = 6;
            List<GetProductRes> getProductRes = new ArrayList<>();

            // paging
            for(int i = (amount*page)+1; i < (amount*(page+1))+1; i++){

                // i값이 productId값을 넘어갈때 오류나는것을 방지
                if(i >= getLastProductId()){
                    getProductRes.add(productDao.getProductById(1,i));
                    return getProductRes;
                }
                getProductRes.add(productDao.getProductById(1,i));
            }

            return getProductRes;
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
