package com.example.osj45.selfstudy;

/**
 * Created by osj45 on 2018-05-02.
 */

public class Missile {
    public int x, y;
    private int moveY;

    public Missile(int x, int y){
        this.x = x;
        this.y = y;
        moveY = 10;
    }

    public boolean move(){
        y -= moveY;

        if(y > 0)
            return false;
        else
            return true;
    }
}
