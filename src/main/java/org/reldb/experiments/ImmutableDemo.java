package org.reldb.experiments;

public class ImmutableDemo
{

    public static void main(String[] args)
    {
        var mi = new MyImmutable(3, 4) {
            int getX() {
                return 3;
            }
        };
    }

}
