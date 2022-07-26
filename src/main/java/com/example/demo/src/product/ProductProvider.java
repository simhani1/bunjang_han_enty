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
    public List<GetProductRes> getProducts(int userId, int page, String type) throws BaseException{

        // 상품이 아예 존재하지 않을 때
        if(getExistProductCount() <= page*amount){
            throw new BaseException(EXTRA_PAGE);
        }

        try{
            List<GetProductRes> getProductRes = new ArrayList<>();

            // 최신순
            if(type.equals("recent")){
                for(int i = amount*page; i < amount*(page+1); i++){
                    if(i >= getExistProductCount()-1){
                        getProductRes.add(productDao.getProductById(userId,productDao.getExistsProductIdListReCent().get(i)));
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



//            List<GetProductRes> getProductResTemp = new ArrayList<>();
//
//            productDao.getExistsProductIdList().get(0);
//            for(int i = 1; i < getLastProductId()+1; i++){
//                if(!productDao.getProductIsDeleted(i)){
//                    getProductRes.add(productDao.getProductById(1,i));
//                }
//            }
//            Collections.sort(getProductRes, new GetProductResComparator());
//
//            System.out.println(getProductRes.size());
//            for(int i = (amount*page); i < (amount*(page+1)); i++){
//                if(i >= getProductRes.size()-1){
//                    getProductResTemp.add(getProductRes.get(i));
//                    return getProductResTemp;
//                }
//                getProductResTemp.add(getProductRes.get(i));
//            }


            // 순서대로 안하고 작동되는거 (구식)
//            for(int i = (amount*page)+1; i < (amount*(page+1)+1); i++){
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

            // 역순으로 작동되는거 (구식)
//            for(int i = getExistProductCount()-(amount*page); i > getExistProductCount()-(amount*(page+1)); i--){
//                // 삭제 된 상품 예외처리
//                if(!productDao.getProductIsDeleted(i)){
//                    // i값이 productId값을 넘어갈때 오류나는것을 방지
//                    if(i <= 1){
//                        getProductRes.add(productDao.getProductById(userId,i));
//                        return getProductRes;
//                    }
//                    getProductRes.add(productDao.getProductById(userId,i));
//                }
//            }
//            return getProductRes;

//            for(int i = (amount*page); i < (amount*(page+1)); i++){
//                // 삭제 된 상품 예외처리
//                if(!productDao.getProductIsDeleted(i+1)){
//                    // i값이 productId값을 넘어갈때 오류나는것을 방지
//                    if(i == getLastProductId()+1){
//                        getProductResTemp.add(getProductRes.get(i));
//                        return getProductRes;
//                    }
//                    getProductResTemp.add(getProductRes.get(i));
//                }
//            }

//            for(int i = 1; i <= getLastProductId(); i++){
//                if(!productDao.getProductIsDeleted(i)){
//                    if(productDao.getProductById(1,i).getFirstCategoryId() == firstCategoryId){
//                        getProductRes.add(productDao.getProductById(1,i));
//                    }
//                }
//            }
//            Collections.sort(getProductRes, new GetProductResComparator());
//
//            for(int i = (amount*page); i < (amount*(page+1)); i++){
//                getProductResTemp.add(getProductRes.get(i));
//            }

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

//            List<GetProductRes> getProductRes = new ArrayList<>();
//            List<GetProductRes> getProductResTemp = new ArrayList<>();
//
//            for(int i = 1; i < getLastProductId()+1; i++){
//                if(!productDao.getProductIsDeleted(i)){
//                    if(productDao.getProductById(1,i).getFirstCategoryId() == firstCategoryId){
//                        getProductRes.add(productDao.getProductById(1,i));
//                    }
//                }
//            }
//            Collections.sort(getProductRes, new GetProductResComparator());
//
//            for(int i = (amount*page); i < (amount*(page+1)); i++){
//                if(i >= getProductRes.size()-1){
//                    getProductResTemp.add(getProductRes.get(i));
//                    return getProductResTemp;
//                }
//                getProductResTemp.add(getProductRes.get(i));
//            }
//            return getProductResTemp;
            throw new BaseException(GET_FIRST_CATEGORY_PRODUCTS_FAILD);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 하위 카테고리별 상품 조회
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
//            List<GetProductRes> getProductRes = new ArrayList<>();
//            List<GetProductRes> getProductResTemp = new ArrayList<>();
//
//            for(int i = 1; i < getLastProductId()+1; i++){
//                if(!productDao.getProductIsDeleted(i)){
//                    if(productDao.getProductById(1,i).getLastCategoryId() == lastCategoryId){
//                        getProductRes.add(productDao.getProductById(1,i));
//                    }
//                }
//            }
//            Collections.sort(getProductRes, new GetProductResComparator());
//
//            for(int i = (amount*page); i < (amount*(page+1)); i++){
//                if(i >= getProductRes.size()-1){
//                    getProductResTemp.add(getProductRes.get(i));
//                    return getProductResTemp;
//                }
//                getProductResTemp.add(getProductRes.get(i));
//            }
//            return getProductResTemp;
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
