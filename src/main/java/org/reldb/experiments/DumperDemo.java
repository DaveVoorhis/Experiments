package org.reldb.experiments;

public class DumperDemo {

    static class Demo {
        int x = 2;
        String y = "blah";
        double z = -3.4E22;
        String[] a = {"one", "Two", "3"};
    }

    public static void main(String[] args) {
        var demo = new Demo();

        System.out.println(Dumper.dump(demo));
    }
}
