package com.example.demo.src.product;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.firstCategory.FirstCategoryProvider;
import com.example.demo.src.lastCategory.LastCategoryProvider;
import com.example.demo.src.product.model.GetProductRes;
import com.example.demo.src.user.model.GetShopReviewRes;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.*;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class ProductProvider {

    int amount = 9;
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
    @Transactional(readOnly=true)
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
//            changeStarData(getProductRes);
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
    @Transactional(readOnly = true)
    public List<GetProductRes> getProducts(int userId, int page, String type) throws BaseException{

        // 상품이 아예 존재하지 않을 때
        if(getExistProductCount() <= page*amount){
            throw new BaseException(EXTRA_PAGE);
        }

        try{
            amount = 6;
            List<GetProductRes> getProductRes = new ArrayList<>();

            // 최신순
            if(type.equals("recent")){
                for(int i = amount*page; i < amount*(page+1); i++){
                    if(i >= getExistProductCount()-1){
                        getProductRes.add(productDao.getProductById(userId,productDao.getExistsProductIdListReCent().get(i)));
//                        changeStarData(getProductRes.get(i).getStar());
                        return getProductRes;
                    }
                    getProductRes.add(productDao.getProductById(userId,productDao.getExistsProductIdListReCent().get(i)));
                }
                return getProductRes;
            }

            // 낮은 가격순
            if(type.equals("ascend")){
                for(int i = amount*page; i < amount*(page+1); i++){
                    if(i >= getExistProductCount()-1){
                        getProductRes.add(productDao.getProductById(userId,productDao.getExistsProductIdListAscend().get(i)));
                        return getProductRes;
                    }
                    getProductRes.add(productDao.getProductById(userId,productDao.getExistsProductIdListAscend().get(i)));
                }
                return getProductRes;
            }

            // 높은 가격순
            if(type.equals("descend")){
                for(int i = amount*page; i < amount*(page+1); i++){
                    if(i >= getExistProductCount()-1){
                        getProductRes.add(productDao.getProductById(userId,productDao.getExistsProductIdListDescend().get(i)));
                        return getProductRes;
                    }
                    getProductRes.add(productDao.getProductById(userId,productDao.getExistsProductIdListDescend().get(i)));
                }
                return getProductRes;
            }

            throw new BaseException(GET_PRODUCTS_FAILED);



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
    @Transactional(readOnly = true)
    List<GetProductRes> getProductByCategoryId(int page, String type, int firstCategoryId, int userId) throws BaseException {
        // 상위 카테고리 음수이거나 없는 카테고리일때
        if(firstCategoryId > firstCategoryProvider.getCategoryCount()){
            throw new BaseException(NO_EXISTED_FIRST_CATEGORY);
        }
        if(getExistCategoryProductCount(firstCategoryId) <= page*amount){
            throw new BaseException(EXTRA_PAGE);
        }

        try{

            List<GetProductRes> getProductRes = new ArrayList<>();

            // 최신순
            if(type.equals("recent")){
                for(int i = amount*page; i < amount*(page+1); i++){
                    if(i >= getExistCategoryProductCount(firstCategoryId)-1){
                        getProductRes.add(productDao.getProductById(userId,productDao.getExistsProductIdListReCentByFirstCategoryId(firstCategoryId).get(i)));
                        return getProductRes;
                    }
                    getProductRes.add(productDao.getProductById(userId,productDao.getExistsProductIdListReCentByFirstCategoryId(firstCategoryId).get(i)));
                }
                return getProductRes;
            }

            // 낮은 가격순
            if(type.equals("ascend")){
                for(int i = amount*page; i < amount*(page+1); i++){
                    if(i >= getExistCategoryProductCount(firstCategoryId)-1){
                        getProductRes.add(productDao.getProductById(userId,productDao.getExistsProductIdListAscendByFirstCategoryId(firstCategoryId).get(i)));
                        return getProductRes;
                    }
                    getProductRes.add(productDao.getProductById(userId,productDao.getExistsProductIdListAscendByFirstCategoryId(firstCategoryId).get(i)));
                }
                return getProductRes;
            }

            // 높은 가격순
            if(type.equals("descend")){
                for(int i = amount*page; i < amount*(page+1); i++){
                    if(i >= getExistCategoryProductCount(firstCategoryId)-1){
                        getProductRes.add(productDao.getProductById(userId,productDao.getExistsProductIdListDescendByFirstCategoryId(firstCategoryId).get(i)));
                        return getProductRes;
                    }
                    getProductRes.add(productDao.getProductById(userId,productDao.getExistsProductIdListDescendByFirstCategoryId(firstCategoryId).get(i)));
                }
                return getProductRes;
            }
            throw new BaseException(GET_FIRST_CATEGORY_PRODUCTS_FAILD);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 하위 카테고리별 상품 조회
    @Transactional(readOnly = true)
    List<GetProductRes> getProductByLastCategoryId(int page, String type, int lastCategoryId, int userId) throws BaseException {
        // 하위 카테고리 음수이거나 없는 카테고리일때
        if(lastCategoryId > lastCategoryProvider.getLastCategoryIdCount()){
            throw new BaseException(NO_EXISTED_LAST_CATEGORY);
        }
        if(getExistLastCategoryProductCount(lastCategoryId) <= page*amount){
            throw new BaseException(EXTRA_PAGE);
        }
        try{
            List<GetProductRes> getProductRes = new ArrayList<>();

            if(type.equals("recent")){
                for(int i = amount*page; i < amount*(page+1); i++){
                    if(i >= getExistLastCategoryProductCount(lastCategoryId)-1){
                        getProductRes.add(productDao.getProductById(userId,productDao.getExistsProductIdListReCentByLastCategoryId(lastCategoryId).get(i)));
                        return getProductRes;
                    }
                    getProductRes.add(productDao.getProductById(userId,productDao.getExistsProductIdListReCentByLastCategoryId(lastCategoryId).get(i)));
                }
                return getProductRes;
            }
            // 낮은 가격순
            if(type.equals("ascend")){
                for(int i = amount*page; i < amount*(page+1); i++){
                    if(i >= getExistLastCategoryProductCount(lastCategoryId)-1){
                        getProductRes.add(productDao.getProductById(userId,productDao.getExistsProductIdListAscendByLastCategoryId(lastCategoryId).get(i)));
                        return getProductRes;
                    }
                    getProductRes.add(productDao.getProductById(userId,productDao.getExistsProductIdListAscendByLastCategoryId(lastCategoryId).get(i)));
                }
                return getProductRes;
            }

            // 높은 가격순
            if(type.equals("descend")){
                for(int i = amount*page; i < amount*(page+1); i++){
                    if(i >= getExistLastCategoryProductCount(lastCategoryId)-1){
                        getProductRes.add(productDao.getProductById(userId,productDao.getExistsProductIdListDescendByLastCategoryId(lastCategoryId).get(i)));
                        return getProductRes;
                    }
                    getProductRes.add(productDao.getProductById(userId,productDao.getExistsProductIdListDescendByLastCategoryId(lastCategoryId).get(i)));
                }
                return getProductRes;
            }
            throw new BaseException(GET_LAST_CATEGORY_PRODUCTS_FAILD);
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

    // 삭제안된 상품 수
    public int getExistProductCount(){
        return productDao.getExistProductCount();
    }
    // 특정 상위 카테고리 상품 수
    public int getExistCategoryProductCount(int firstCategoryId){
        return productDao.getExistCategoryProductCount(firstCategoryId);
    }
    // 특정 하위 카테고리 상품 수
    public int getExistLastCategoryProductCount(int lastCategoryId){
        return productDao.getExistLastCategoryProductCount(lastCategoryId);
    }
    // 상품의 주인인지 확인
    public int checkExistsUserOwnProduct(int userId, int productId){
        return productDao.checkExistsUserOwnProduct(userId, productId);
    }
    // 삭제되지 않고 판매중인지
    public int checkExistsSellProduct(int productId){
        return productDao.checkExistsSellProduct(productId);
    }

//    public void changeStarData(GetProductRes getProductRes){
//        if(0 <= getProductRes.getStar() && getProductRes.getStar()<0.5)
//            getProductRes.setStar(0);
//        else if(0.5 <= getProductRes.getStar() && getProductRes.getStar() < 1)
//            getProductRes.setStar(0.5);
//        else if(1 <= getProductRes.getStar() && getProductRes.getStar() < 1.5)
//            getProductRes.setStar(1);
//        else if(1.5 <= getProductRes.getStar() && getProductRes.getStar() < 2)
//            getProductRes.setStar(1.5);
//        else if(2 <= getProductRes.getStar() && getProductRes.getStar() < 2.5)
//            getProductRes.setStar(2);
//        else if(2.5 <= getProductRes.getStar() && getProductRes.getStar() < 3)
//            getProductRes.setStar(2.5);
//        else if(3 <= getProductRes.getStar() && getProductRes.getStar() < 3.5)
//            getProductRes.setStar(3);
//        else if(3.5 <= getProductRes.getStar() && getProductRes.getStar() < 4)
//            getProductRes.setStar(3.5);
//        else if(4 <= getProductRes.getStar() && getProductRes.getStar() < 4.5)
//            getProductRes.setStar(4);
//        else if(4.5 <= getProductRes.getStar() && getProductRes.getStar() < 5)
//            getProductRes.setStar(4.5);
//        else if(4.5 <= getProductRes.getStar())
//            getProductRes.setStar(5);
//    }
}
