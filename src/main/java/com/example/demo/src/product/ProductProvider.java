package com.example.demo.src.product;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.firstCategory.FirstCategoryProvider;
import com.example.demo.src.lastCategory.LastCategoryProvider;
import com.example.demo.src.product.model.GetProductRes;
import com.example.demo.src.user.model.GetShopReviewRes;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class ProductProvider {

    int amount = 6;
    final private ProductDao productDao;
    private final FirstCategoryProvider firstCategoryProvider;
    private final LastCategoryProvider lastCategoryProvider;

    public ProductProvider(ProductDao productDao, FirstCategoryProvider firstCategoryProvider, LastCategoryProvider lastCategoryProvider){
        this.productDao = productDao;
        this.firstCategoryProvider = firstCategoryProvider;
        this.lastCategoryProvider = lastCategoryProvider;
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

        // 상품이 아예 존재하지 않을 때
        if(getLastProductId() < page*amount){
            throw new BaseException(EXTRA_PAGE);
        }

        try{
            List<GetProductRes> getProductRes = new ArrayList<>();

            // paging
//            for(int i = (amount*page)+1; i < (amount*(page+1))+1; i++){
//                // 삭제 된 상품 예외처리
//                if(!productDao.getProductIsDeleted(i)){
//                    // i값이 productId값을 넘어갈때 오류나는것을 방지
//                    if(i >= getLastProductId()){
//                        getProductRes.add(productDao.getProductById(1,i));
//                        return getProductRes;
//                    }
//                    getProductRes.add(productDao.getProductById(1,i));
//                }
//            }

            for(int i = getLastProductId()-(amount*page); i > getLastProductId()-(amount*(page+1)); i--){
                // 삭제 된 상품 예외처리
                if(!productDao.getProductIsDeleted(i)){
                    // i값이 productId값을 넘어갈때 오류나는것을 방지
                    if(i <= 1){
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

    /**
     * 상위 카테고리 별 상품 조회
     * @param page
     * @param firstCategoryId
     * @return
     * @throws BaseException
     */
    List<GetProductRes> getProductByCategoryId(int page, int firstCategoryId) throws BaseException {
        // 상위 카테고리 음수이거나 없는 카테고리일때
        if(firstCategoryId > firstCategoryProvider.getCategoryCount()){
            throw new BaseException(NO_EXISTED_FIRST_CATEGORY);
        }
        try{
            List<GetProductRes> getProductRes = new ArrayList<>();
            for(int i = getLastProductId()-(amount*page); i > getLastProductId()-(amount*(page+1)); i--){
                if(!productDao.getProductIsDeleted(i)){
                    if(productDao.getProductById(1,i).getFirstCategoryId() == firstCategoryId){
                        if(i <= 1){
                            getProductRes.add(productDao.getProductById(1,i));
                            return getProductRes;
                        }

                        getProductRes.add(productDao.getProductById(1,i));
                    }
                }
            }
//            Collections.sort();
            return getProductRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    List<GetProductRes> getProductByLastCategoryId(int page, int lastCategoryId) throws BaseException {
        // 하위 카테고리 음수이거나 없는 카테고리일때
        if(lastCategoryId > lastCategoryProvider.getLastCategoryIdCount()){
            throw new BaseException(NO_EXISTED_LAST_CATEGORY);
        }
        try{
            List<GetProductRes> getProductRes = new ArrayList<>();
            for(int i = getLastProductId()-(amount*page); i > getLastProductId()-(amount*(page+1)); i--){
                if(!productDao.getProductIsDeleted(i)){
                    if(productDao.getProductById(1,i).getLastCategoryId() == lastCategoryId){
                        if(i <= 1){
                            getProductRes.add(productDao.getProductById(1,i));
                            return getProductRes;
                        }

                        getProductRes.add(productDao.getProductById(1,i));
                    }
                }
            }
            return getProductRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 특정 유저 판매중 상품
     * @param userId
     * @return
     * @throws BaseException
     */
    public List<GetProductRes> getSellProductByUserId(int userId) throws BaseException {
        try{
            List<GetProductRes> getProductRes = new ArrayList<>();
            List<Integer> productIdList = productDao.getProductIdList(userId);

            for(int i = 0; i < productIdList.size(); i++){
                if(!productDao.getProductIsDeleted(productIdList.get(i))){
                    if(i <= 1){
                        getProductRes.add(productDao.getProductById(userId,productIdList.get(i)));
                        return getProductRes;
                    }
                    getProductRes.add(productDao.getProductById(userId,productIdList.get(i)));
                }
            }
            return getProductRes;
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
//    public GetProductRes getProductByIdTest(int userId,int productId) throws BaseException {
//        if (userId < 0){
//            throw new BaseException(NO_EXISTED_USER);
//        }
//        if (productId < 0 || productId > getLastProductId()){
//            throw new BaseException(NO_EXISTED_PRODUCT);
//        }
//        if (productDao.getProductIsDeleted(productId)){
//            throw new BaseException(DELETED_PRODUCT);
//        }
//        try{
//            GetProductRes getProductRes = productDao.getProductByIdTest(userId,productId);
//            return getProductRes;
//        } catch (Exception exception){
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }
}

//class GetProductComparator implements Comparator<GetProductRes>{
//    @Override
//    public int compare(GetProductRes t1, GetProductRes t2) {
//}