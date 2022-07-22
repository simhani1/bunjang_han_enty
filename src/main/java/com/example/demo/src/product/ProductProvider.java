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


    /**
     * 마지막 상품 번호
     * @return
     */
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
        if (userId < 0){
            throw new BaseException(NO_EXISTED_USER);
        }
        if (productId < 0 || productId > getLastProductId()){
            throw new BaseException(NO_EXISTED_PRODUCT);
        }
        if (productDao.getProductIsDeleted(productId)){
            throw new BaseException(DELETED_PRODUCT);
        }
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
        int amount = 6;

        // 상품이 아예 존재하지 않을 때
        if(getLastProductId() < page*amount){
            throw new BaseException(EXTRA_PAGE);
        }

        try{
            List<GetProductRes> getProductRes = new ArrayList<>();

            // paging
            for(int i = (amount*page)+1; i < (amount*(page+1))+1; i++){

                // 삭제 된 상품 예외처리
                if(!productDao.getProductIsDeleted(i)){
                    // i값이 productId값을 넘어갈때 오류나는것을 방지
                    if(i >= getLastProductId()){
                        getProductRes.add(productDao.getProductById(1,i));
                        return getProductRes;
                    }
                    getProductRes.add(productDao.getProductById(1,i));
                }
            }

            return getProductRes;
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetProductRes> getProductsByUserId(int page) throws BaseException{
        int amount = 6;

        // 상품이 아예 존재하지 않을 때
        if(getLastProductId() < page*amount){
            throw new BaseException(EXTRA_PAGE);
        }

        try{
            List<GetProductRes> getProductRes = new ArrayList<>();

            // paging
            for(int i = (amount*page)+1; i < (amount*(page+1))+1; i++){

                // 삭제 된 상품 예외처리
                if(!productDao.getProductIsDeleted(i)){
                    // i값이 productId값을 넘어갈때 오류나는것을 방지
                    if(i >= getLastProductId()){
                        getProductRes.add(productDao.getProductById(1,i));
                        return getProductRes;
                    }
                    getProductRes.add(productDao.getProductById(1,i));
                }
            }
            return getProductRes;
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetProductRes> getSellProductByUserId(int userId) throws BaseException {
//        if (userId < 0){
//            throw new BaseException(NO_EXISTED_USER);
//        }
//        if (productId < 0 || productId > getLastProductId()){
//            throw new BaseException(NO_EXISTED_PRODUCT);
//        }
//        if (productDao.getProductIsDeleted(productId)){
//            throw new BaseException(DELETED_PRODUCT);
//        }
        try{
            List<GetProductRes> getProductRes = new ArrayList<>();
            List<Integer> productIdList = productDao.getProductIdList(userId);
            System.out.println("사이즈, 0번째 값:" + productIdList.size() + "," + productIdList.get(0));
            for(int i = 0; i < productIdList.size(); i++){
                getProductRes.add(productDao.getProductById(userId,productIdList.get(i)));
            }
            return getProductRes;
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }


}
