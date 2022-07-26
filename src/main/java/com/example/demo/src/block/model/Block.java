package com.example.demo.src.block.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Block {
    private int blockId;
    private int userId;
    private int blockUserId;

    public Block(int userId, int blockUserId) {
        this.userId = userId;
        this.blockUserId = blockUserId;
    }
}
