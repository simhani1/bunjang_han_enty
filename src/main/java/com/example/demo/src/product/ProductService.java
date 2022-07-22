package com.example.demo.src.product;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.product.model.PatchProductReq;
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

    /**
     * 상품 등록
     * @param userId
     * @param postProductReq
     * @return
     * @throws BaseException
     */
    public PostProductRes createProduct(int userId, PostProductReq postProductReq) throws BaseException {
        try{
            int productId = productDao.createProduct(userId, postProductReq);
            return new PostProductRes(productId);

        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }


    /**
     * 상품 수정
     * @param userId
     * @param productId
     * @param patchProductReq
     * @throws BaseException
     */
    public void modifyProduct(int userId, int productId, PatchProductReq patchProductReq) throws BaseException {
        try{
            int result = productDao.modifyProduct(userId, productId, patchProductReq);
            if(result == 0){
                throw new BaseException(MODIFY_PRODUCT_FAILED);
            }
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 상품 상태변경
     * @param userId
     * @param productId
     * @param condition
     * @throws BaseException
     */
    public void modifyProductCondition(int userId, int productId, String condition) throws BaseException {
        try{
            int result = productDao.modifyProductCondition(userId, productId, condition);
            if(result == 0){
                throw new BaseException(MODIFY_PRODUCT_CONDITION_FAILED);
            }
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 상품 삭제여부
     * @param userId
     * @param productId
     * @param isDeleted
     * @throws BaseException
     */
    public void modifyProductIsDeleted(int userId, int productId, Boolean isDeleted) throws BaseException {
        try{
            int result = productDao.modifyProductIsDeleted(userId, productId, isDeleted);
            if(result == 0){
                throw new BaseException(MODIFY_PRODUCT_IS_DELETED_FAILED);
            }
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
