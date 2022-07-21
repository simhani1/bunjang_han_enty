package com.example.demo.src.product;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.product.model.GetProductRes;
import com.example.demo.src.product.model.PostProductReq;
import com.example.demo.src.product.model.PostProductRes;
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
    private final JwtService jwtService;

    @Autowired
    public ProductController(ProductProvider productProvider, ProductService productService, ProductDao productDao, JwtService jwtService){
        this.productProvider = productProvider;
        this.productService = productService;
        this.productDao = productDao;
        this.jwtService = jwtService;
    }

    //jwt에서 idx 추출.
//            int userIdxByJwt = jwtService.getUserIdx();
//            //userIdx와 접근한 유저가 같은지 확인
//            if(userId != userIdxByJwt){
//                return new BaseResponse<>(INVALID_USER_JWT);
//            }

    /**
     * [POST] /app/product/:userId
     * @param userId
     * @param postProductReq
     * @return PostProductRes
     */
    @PostMapping("/{userId}")
    public BaseResponse<PostProductRes> createProduct(@PathVariable("userId") int userId, @RequestBody PostProductReq postProductReq){
        try{


//            System.out.println(productImg);

//            PostProductImgReq a = postProductReq.getProductImgs().get(2);

            int userIdByJwt = jwtService.getUserId();
            //userIdx와 접근한 유저가 같은지 확인
            if(userId != userIdByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            PostProductRes postProductRes = productService.createProduct(userIdByJwt, postProductReq);
            return new BaseResponse<>(POST_PRODUCT_SUCCESS,postProductRes);
        } catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
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
     * [GET] /app/products
     * @param page
     * @return List<GetProductRes>
     */
    @GetMapping("")
    public BaseResponse<List<GetProductRes>> getProducts(@RequestParam int page){
        try{
            List<GetProductRes> getProductRes = productProvider.getProducts(page);
            return new BaseResponse<>(GET_PRODUCTS_SUCCESS, getProductRes);
        } catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * [GET] /app/products/category/:firstCategoryId
     */
//    @GetMapping("/category/:firstCategoryId")
//    public BaseResponse<List<GetProductRes>> getProductByFirstCategoryId(@RequestParam int page,
//                                                                         @RequestParam int ){
//
//    }

}
