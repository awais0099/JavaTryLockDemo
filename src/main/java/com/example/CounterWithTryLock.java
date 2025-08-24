package com.example;

import java.util.concurrent.locks.ReentrantLock;

public class CounterWithTryLock {
    private int count = 0;
    private final ReentrantLock lock = new ReentrantLock();

    public void increment() {
        if (lock.tryLock()) {
            try {
                count++;
            } finally {
                lock.unlock();
            }
        } else {
            System.out.println(Thread.currentThread().getName() + ": Lock busy, skipping increment");
        }
    }

    public int getCount() {
        return count;
    }

    public static void main(String[] args) throws InterruptedException {
        CounterWithTryLock counter = new CounterWithTryLock();
        System.out.println("Initial count: " + counter.getCount());

        Runnable task = () -> {
            for (int i = 0; i < 1000; i++) {
                counter.increment();
                try {
                    Thread.sleep(1); // Simulate contention
                } catch (InterruptedException e) {
                    System.out.println(Thread.currentThread().getName() + ": Interrupted");
                }
            }
        };

        Thread t1 = new Thread(task, "Thread-1");
        Thread t2 = new Thread(task, "Thread-2");

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println("Final count: " + counter.getCount()); // Likely < 2000
    }
}