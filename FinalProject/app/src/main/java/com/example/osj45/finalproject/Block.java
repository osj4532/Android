package com.example.osj45.finalproject;

/**
 * Created by osj45 on 2018-06-06.
 */

public class Block {
    public int x, y;
    private int w, h;
    private int moveY;
    public static int Block_Width;

    public Block(int x, int y, int w, int h){
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        moveY = 10;
        Block_Width = 200;
    }

    public boolean move(){
        y += moveY;

        if(y > h )
            return true;
        else
            return false;
    }
}
