package org.reldb.experiments;

public class ThreadDemo {
    static boolean running = true;
    
    public static void main(String args[]) {
        var thread1 = new Thread(() -> {
            while (running) {
                System.out.println("I am sending");
            }
        });
        
        var thread2 = new Thread(() -> {
            while (running) {
                System.out.println("I am receiving");
            }
        });
        
        thread1.start();
        thread2.start();
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {}
        running = false;
    }
}
