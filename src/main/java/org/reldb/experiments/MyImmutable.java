package org.reldb.experiments;

class MyImmutable
{
    private int x;
    private int y;
    
    MyImmutable(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    int getX() {
        return x;
    }
    
    int getY() {
        return y;
    }
}
