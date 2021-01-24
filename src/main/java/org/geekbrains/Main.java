package org.geekbrains;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    private static final Object key = new Object();
    private static volatile char curChar = 'A';

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(3);

        executorService.submit(() -> setChar('A','B'));

        executorService.submit(() -> setChar('B','C'));

        executorService.submit(() -> setChar('C','A'));

        executorService.shutdown();
    }

    private static void setChar(char char1, char char2) {
        try {
            for (int i = 0; i < 5; i++) {
                synchronized (key) {
                    while (curChar != char1) {
                        key.wait();
                    }
                    System.out.println(char1);
                    curChar = char2;
                    key.notifyAll();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
