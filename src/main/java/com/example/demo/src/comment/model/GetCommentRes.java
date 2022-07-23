package com.example.demo.src.comment.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetCommentRes {

    private int commentId;
    private int productId;
    private int userId;
    private String profileImgUrl;
    private String nickname;
    private String contents;
    private String createdAt;
    private Boolean isDeleted;
}
