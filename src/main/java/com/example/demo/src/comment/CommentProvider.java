package com.example.demo.src.comment;

import com.example.demo.config.BaseException;
import com.example.demo.src.comment.model.GetCommentRes;
import com.example.demo.src.product.ProductDao;
import com.example.demo.src.product.ProductProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class CommentProvider {

    final private CommentDao commentDao;
    private final ProductProvider productProvider;
    private final ProductDao productDao;

    @Autowired
    public CommentProvider(CommentDao commentDao, ProductProvider productProvider, ProductDao productDao){
        this.commentDao = commentDao;
        this.productProvider = productProvider;
        this.productDao = productDao;
    }

    public int getCommentLastId(){
        return commentDao.getCommentLastId();
    }
    /**
     * 댓글 조회
     * @param productId
     * @return
     * @throws BaseException
     */
    public List<GetCommentRes> getComments(int productId) throws BaseException {
        if (productId < 0 || productId > productProvider.getLastProductId()){
            throw new BaseException(NO_EXISTED_PRODUCT);
        }
        if (productDao.getProductIsDeleted(productId)){
            throw new BaseException(DELETED_PRODUCT);
        }
        try {
            List<GetCommentRes> getCommentRes = commentDao.getComments(productId);
            return getCommentRes;
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }

    }
}
