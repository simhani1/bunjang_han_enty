package com.example.demo.src.product;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.firstCategory.FirstCategoryProvider;
import com.example.demo.src.product.model.*;
import com.example.demo.utils.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@Slf4j
@RestController
@RequestMapping("/app/products")
public class ProductController {

    final private ProductProvider productProvider;
    final private ProductService productService;
    final private ProductDao productDao;
    final private FirstCategoryProvider firstCategoryProvider;
    final private JwtService jwtService;

    @Autowired
    public ProductController(ProductProvider productProvider, ProductService productService, ProductDao productDao, FirstCategoryProvider firstCategoryProvider, JwtService jwtService){
        this.productProvider = productProvider;
        this.productService = productService;
        this.productDao = productDao;
        this.firstCategoryProvider = firstCategoryProvider;
        this.jwtService = jwtService;
    }

    //jwt에서 idx 추출.
//            int userIdxByJwt = jwtService.getUserIdx();
//            //userIdx와 접근한 유저가 같은지 확인
//            if(userId != userIdxByJwt){
//                return new BaseResponse<>(INVALID_USER_JWT);
//            }

    /**
     * 상품등록
     * [POST] /app/product/:userId
     * @param userId
     * @param postProductReq
     * @return PostProductRes
     */
    @PostMapping("/{userId}")
    public BaseResponse<PostProductRes> createProduct(@PathVariable("userId") int userId, @RequestBody PostProductReq postProductReq){
        try{
            int userIdByJwt = jwtService.getUserId();
            // userId와 접근한 유저가 같은지 확인
            if(userId != userIdByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            // TODO: vallidation List
            // 사진 1개 이상 등록
            if(postProductReq.getProductImgs().size() < 1){
                return new BaseResponse<>(EMPTY_PRODUCT_IMG);
            }
            // 사진 12개 이하로 등록
            if(postProductReq.getProductImgs().size() > 12){
                return new BaseResponse<>(EXCESS_PRODUCT_IMG);
            }
            // 상품명 2글자 이상 입력
            if(postProductReq.getTitle().length() < 2){
                return new BaseResponse<>(NOT_ENOUGH_TITLE_LENGTH);
            }
            // 카테고리 선택 안할시에
            if(postProductReq.getFirstCategoryId() == 0){
                return new BaseResponse<>(NOT_SELECT_FIRST_CATEGORY);
            }
            // 하위 카테고리 선택 안할시에
            if(postProductReq.getLastCategoryId() == 0){
                return new BaseResponse<>(NOT_SELECT_LAST_CATEGORY);
            }
            // 태그를 입력헤주세요
            if(postProductReq.getTags().size() < 1){
                return new BaseResponse<>(EMPTY_TAGS);
            }
            // 가격을 100원 이상 입력해주세요
            if(postProductReq.getPrice() < 100){
                return new BaseResponse<>(NOT_ENOUGH_PRICE);
            }
            // 상품설명을 10글자 이상 입력헤주세요.
            if(postProductReq.getContents().length() < 10){
                return new BaseResponse<>(NOT_ENOUGH_CONTENTS);
            }
            // 수량을 1개 이상으로 입력해주세요.
            if(postProductReq.getAmount() < 1){
                return new BaseResponse<>(NOT_ENOUGH_AMOUNT);
            }
            // 중고상품-새상품 선택해주세요.
            if(postProductReq.getIsUsed() == null){
                return new BaseResponse<>(NOT_SELECT_IS_USED);
            }
            // 교환여부를 선택해주세요.
            if(postProductReq.getChangeable() == null){
                return new BaseResponse<>(NOT_SELECT_CHANGEABLE);
            }
            // 번개페이 사용 여부 선택해주세요.
            if(postProductReq.getPay() == null){
                return new BaseResponse<>(NOT_SELECT_PAY);
            }
            // 배송비 포함 여부 선택해주세요.
            if(postProductReq.getShippingFee() == null){
                return new BaseResponse<>(NOT_SELECT_SHIPPING_FEE);
            }

            PostProductRes postProductRes = productService.createProduct(userIdByJwt, postProductReq);
            return new BaseResponse<>(POST_PRODUCT_SUCCESS,postProductRes);
        } catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 상품조회
     * [GET] /app/products/:userId/:productId
     * @param userId
     * @param productId
     * @return GetProductRes
     */
    @GetMapping("/{userId}/{productId}")
    public BaseResponse<GetProductRes> getProductById(@PathVariable("userId") int userId,
                                                      @PathVariable("productId") int productId){

        try{
            GetProductRes getProductRes = productProvider.getProductById(userId,productId);
            return new BaseResponse<>(GET_PRODUCT_SUCCESS, getProductRes);
        } catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 전체상품조회
     * [GET] /app/products
     * @param page
     * @return List<GetProductRes>
     */
    @GetMapping("/{userId}")
    public BaseResponse<List<GetProductRes>> getProducts(@PathVariable int userId,
                                                         @RequestParam int page,
                                                         @RequestParam String type){

        try{
            if(page < 0){
                return new BaseResponse<>(NEGATIVE_PAGE_NUM);
            }
            if(userId != jwtService.getUserId()){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            if(type.equals("")){
                return new BaseResponse<>(EMPTY_PRODUCT_SORT_TYPE);
            }
            if(!(type.equals("recent") || type.equals("ascend") || type.equals("descend"))){
                return new BaseResponse<>(INVALID_PRODUCT_SORT_TYPE);
            }
            List<GetProductRes> getProductRes = productProvider.getProducts(userId,page,type);
            return new BaseResponse<>(GET_PRODUCTS_SUCCESS, getProductRes);
        } catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 상품수정(상품 내용만)
     * @param userId
     * @param productId
     * @param patchProductReq
     * @return
     */
    @PatchMapping("/{userId}/{productId}")
    public BaseResponse<String> modifyProduct(@PathVariable("userId") int userId,
                                              @PathVariable("productId") int productId,
                                              @RequestBody PatchProductReq patchProductReq){
        try{
            if(userId != jwtService.getUserId()){
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            productService.modifyProduct(userId, productId, patchProductReq);
            return new BaseResponse<>(MODIFY_PRODUCT_SUCCESS);
        } catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 상품 상태 변경
     * @param userId
     * @param productId
     * @param condition
     * @return
     */
    @PatchMapping("/{userId}/{productId}/condition")
    public BaseResponse<String> modifyProductCondition(@PathVariable("userId") int userId,
                                                       @PathVariable("productId") int productId,
                                                       @RequestParam String condition){
        try{
            if(userId != jwtService.getUserId()){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            if(!(condition.equals("sell") || condition.equals("sold-out") || condition.equals("reservation"))){
                return new BaseResponse<>(INVALID_CONDITION);
            }

            productService.modifyProductCondition(userId, productId, condition);
            return new BaseResponse<>(MODIFY_PRODUCT_CONDITION_SUCCESS);
        } catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 상품 삭제
     * @param userId
     * @param productId
     * @return
     */
    @PatchMapping("/{userId}/{productId}/isDeleted")
    public BaseResponse<String> modifyProductIsDeleted(@PathVariable("userId") int userId,
                                                       @PathVariable("productId") int productId){
        try{
            if(userId != jwtService.getUserId()){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            productService.modifyProductIsDeleted(userId, productId);
            return new BaseResponse<>(MODIFY_PRODUCT_IS_DELETED_SUCCESS);
        } catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     *특정 유저 판매중 상품
     * @param userId
     * @return
     */
    @GetMapping("/sell/{userId}")
    public BaseResponse<List<GetProductRes>> getSellProductByUserId(@PathVariable("userId") int userId){
        try{
            if(userId != jwtService.getUserId()){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            List<GetProductRes> getProductRes = productProvider.getSellProductByUserId(userId);
            return new BaseResponse<>(GET_PRODUCTS_SUCCESS, getProductRes);
        } catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 카테고리 별 상품 조회
     * @param page
     * @param firstCategoryId
     * @param lastCategoryId
     * @return
     */
    @GetMapping("/categories/{userId}")
    public BaseResponse<List<GetProductRes>> getProductByCategoryId(@PathVariable int userId,
                                                                    @RequestParam int page,
                                                                    @RequestParam String type,
                                                                    @RequestParam(required = false) String firstCategoryId,
                                                                    @RequestParam(required = false) String lastCategoryId){
        try{
            // 페이지가 음수일 때
            if(page < 0){
                return new BaseResponse<>(NEGATIVE_PAGE_NUM);
            }
            if(lastCategoryId == null){
                List<GetProductRes> getProductRes = productProvider.getProductByCategoryId(page,type,Integer.parseInt(firstCategoryId), userId);
                return new BaseResponse<>(GET_PRODUCT_SUCCESS,getProductRes);
            }

            List<GetProductRes> getProductRes = productProvider.getProductByLastCategoryId(page,type,Integer.parseInt(lastCategoryId), userId);
            return new BaseResponse<>(GET_PRODUCT_SUCCESS,getProductRes);

        } catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // up하기
    @PatchMapping("/up/{userId}/{productId}")
    public BaseResponse<String> upProductById(@PathVariable int userId,
                                              @PathVariable int productId){
        try{
            if(userId != jwtService.getUserId()){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            // 상품
            if(productId <= 0){
                return new BaseResponse<>(INVALID_PRODUCTID);
            }
            productService.upProductById(userId, productId);
            return new BaseResponse<>(UP_PRODUCT_SUCCESS);
        } catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @PostMapping("/flex/{userId}/{productId}")
    public BaseResponse<String> flexProduct(@PathVariable int userId,
                                            @PathVariable int productId){
        try{
            productService.flexProduct(userId, productId);
            return new BaseResponse<>(FLEX_SUCCESS);
        } catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

}
