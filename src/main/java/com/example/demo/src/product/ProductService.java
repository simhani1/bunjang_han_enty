package com.example.demo.src.product;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.firstCategory.FirstCategoryProvider;
import com.example.demo.src.lastCategory.LastCategoryProvider;
import com.example.demo.src.product.model.PatchProductReq;
import com.example.demo.src.product.model.PostProductReq;
import com.example.demo.src.product.model.PostProductRes;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class ProductService {

    final private ProductDao productDao;
    private final ProductProvider productProvider;
    private final FirstCategoryProvider firstCategoryProvider;
    private final LastCategoryProvider lastCategoryProvider;

    public ProductService(ProductDao productDao, ProductProvider productProvider, FirstCategoryProvider firstCategoryProvider, LastCategoryProvider lastCategoryProvider){
        this.productDao = productDao;
        this.productProvider = productProvider;
        this.firstCategoryProvider = firstCategoryProvider;
        this.lastCategoryProvider = lastCategoryProvider;
    }

    /**
     * 상품 등록
     * @param userId
     * @param postProductReq
     * @return
     * @throws BaseException
     */
    @Transactional
    public PostProductRes createProduct(int userId, PostProductReq postProductReq) throws BaseException {
        // 상위 카테고리 음수이거나 없는 카테고리일때
        if(postProductReq.getFirstCategoryId() < 0 || postProductReq.getFirstCategoryId() > firstCategoryProvider.getCategoryCount()){
            throw new BaseException(NO_EXISTED_FIRST_CATEGORY);
        }
        // 하위 카테고리 음수이거나 없는 카테고리일때
        if(postProductReq.getLastCategoryId() < 0 || postProductReq.getLastCategoryId() > lastCategoryProvider.getLastCategoryIdCount()){
            throw new BaseException(NO_EXISTED_LAST_CATEGORY);
        }
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
    @Transactional
    public void modifyProduct(int userId, int productId, PatchProductReq patchProductReq) throws BaseException {
        if (productDao.getProductIsDeleted(productId)){
            throw new BaseException(DELETED_PRODUCT);
        }
        if (userId < 0){
            throw new BaseException(NO_EXISTED_USER);
        }
        if (productId < 0 || productId > productProvider.getLastProductId()){
            throw new BaseException(NO_EXISTED_PRODUCT);
        }
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
        if (userId < 0){
            throw new BaseException(NO_EXISTED_USER);
        }
        if (productId < 0 || productId > productProvider.getLastProductId()){
            throw new BaseException(NO_EXISTED_PRODUCT);
        }
        if (productDao.getProductIsDeleted(productId)){
            throw new BaseException(DELETED_PRODUCT);
        }
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
     * 상품 삭제여부 수정
     * @param userId
     * @param productId
     * @throws BaseException
     */
    public void modifyProductIsDeleted(int userId, int productId) throws BaseException {
        if (userId < 0){
            throw new BaseException(NO_EXISTED_USER);
        }
        if (productId < 0 || productId > productProvider.getLastProductId()){
            throw new BaseException(NO_EXISTED_PRODUCT);
        }
        if (productDao.getProductIsDeleted(productId)){
            throw new BaseException(DELETED_PRODUCT);
        }
        try{
            int result = productDao.modifyProductIsDeleted(userId, productId);
            if(result == 0){
                throw new BaseException(MODIFY_PRODUCT_IS_DELETED_FAILED);
            }
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
