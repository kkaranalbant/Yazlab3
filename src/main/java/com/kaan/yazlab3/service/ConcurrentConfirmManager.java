/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kaan.yazlab3.service;

import com.kaan.yazlab3.model.ConfirmProcess;
import com.kaan.yazlab3.model.LogType;
import com.kaan.yazlab3.model.Order;
import java.time.Instant;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author root
 */
public class ConcurrentConfirmManager {

    private static ConcurrentConfirmManager concurrentConfirmManager;

    private PriorityBlockingQueue<Runnable> queue;

    private final ThreadPoolExecutor executor;

    private final OrderService orderService;

    private final LogService logService;

    private ConcurrentConfirmManager() {
        logService = LogService.getInstance();
        queue = new PriorityBlockingQueue<>();
        executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, queue);
        orderService = OrderService.getInstance();
    }

    public static ConcurrentConfirmManager getInstance() {
        if (concurrentConfirmManager == null) {
            synchronized (ConcurrentConfirmManager.class) {
                if (concurrentConfirmManager == null) {
                    concurrentConfirmManager = new ConcurrentConfirmManager();
                }
            }
        }
        return concurrentConfirmManager;
    }

    // admin confirm all butonuna bastıgında oncelıkle onay bekleyen butun orderlar ıcın confirmprocess nesnesi olusturulacak daha sonra bu metot calıstırılacak
    // her bir confirm process active olmalı
    public synchronized void confirmAll() {
        PriorityBlockingQueue<Runnable> newQueue = new PriorityBlockingQueue<>();
        long confirmingTime = Instant.now().toEpochMilli();
        while (!queue.isEmpty()) {
            ConfirmProcess thread = (ConfirmProcess) queue.poll();
            thread.setConfirmingTime(confirmingTime);
            Float priority = thread.getUser().getUserType().getPriority() + (((thread.getConfirmingTime() - thread.getTimestamp()) / 1000F) * 0.5F);
            thread.setPriority(priority);
            newQueue.add(thread);
        }
        queue = newQueue;
        runThreads();
    }

    private void runThreads() {
        Thread queueProcessor = new Thread(() -> {
            while (!queue.isEmpty()) {
                try {
                    ConfirmProcess task = (ConfirmProcess) queue.poll();
                    Order order = task.getOrderService()
                            .getByUserIdAndProductIdAndOrderDateTime(task.getUser().getId(), task.getProduct().getId(), task.getOrderDateTime());
                    if (task != null && task.isIsActive()) {
                        executor.execute(task);
                        task.setIsActive(false);
                        logService.save(task.getUser(), order, LogType.INFO, order.getId() + " id numaralı siparis onaylandı" + "Priority : "+task.getPriority());
                    }
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });

        queueProcessor.setDaemon(true);
        queueProcessor.start();
    }

    public PriorityBlockingQueue<Runnable> getQueue() {
        return queue;
    }

}
