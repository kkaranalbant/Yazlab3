/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kaan.yazlab3.service;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author root
 */
public class ConcurrentPurchaseManager {

    private static ConcurrentPurchaseManager concurrentPurchaseManager;   

    private final PriorityBlockingQueue<Runnable> queue;

    private final ThreadPoolExecutor threadPoolExecutor;

    private final OrderService orderService;
    
    private final LogService logService ;

    private boolean isRunning;

    private ConcurrentPurchaseManager() {
        
        logService = LogService.getInstance() ;

        queue = new PriorityBlockingQueue();

        threadPoolExecutor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, queue);

        orderService = OrderService.getInstance();

        isRunning = false;
        
    }

    public static ConcurrentPurchaseManager getInstance() {
        if (concurrentPurchaseManager == null) {
            synchronized (ConcurrentPurchaseManager.class) {
                if (concurrentPurchaseManager == null) {
                    concurrentPurchaseManager = new ConcurrentPurchaseManager();
                }
            }
        }
        return concurrentPurchaseManager;
    }

    public void init() {
        isRunning = true;
        runThreads();
    }

    public PriorityBlockingQueue<Runnable> getQueue() {
        return queue;
    }

    private void runThreads() {
        Thread queueProcessor = new Thread(() -> {
            while (isRunning) {
                try {
                    Runnable task = queue.poll();
                    if (task != null) {
                        threadPoolExecutor.execute(task);
                    } else {
                        Thread.sleep(100);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });

        queueProcessor.setDaemon(true);
        queueProcessor.start();
    }

    public void stopProcessing() {
        isRunning = false;
        threadPoolExecutor.shutdown();
    }

}
