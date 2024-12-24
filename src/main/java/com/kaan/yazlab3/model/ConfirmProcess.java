/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kaan.yazlab3.model;

import com.kaan.yazlab3.service.ConcurrentConfirmManager;
import com.kaan.yazlab3.service.OrderService;
import java.time.Instant;

/**
 *
 * @author root
 */
public class ConfirmProcess implements Runnable, Comparable<ConfirmProcess> {

    private User user;

    private Product product;

    private Float priority;

    private Long timestamp;

    private Long confirmingTime;

    private boolean isActive;

    private OrderService orderService;

    private ConfirmProcess(User user, Product product) {
        this.user = user;
        this.product = product;
        timestamp = Instant.now().toEpochMilli();
        confirmingTime = Instant.now().toEpochMilli();
        orderService = OrderService.getInstance();
    }

    @Override
    public void run() {
        Order order = orderService.getByUserIdAndProductId(user.getId(), product.getId());
        orderService.confirmById(order.getId());
    }

    @Override
    public int compareTo(ConfirmProcess o) {
        int priorityComparison = Float.compare(this.priority, o.priority);
        if (priorityComparison != 0) {
            return priorityComparison;
        }
        return Long.compare(this.timestamp, o.timestamp);
    }

    public void setPriority(Float priority) {
        this.priority = priority;
    }

    public void setConfirmingTime(Long confirmingTime) {
        this.confirmingTime = confirmingTime;
    }

    // her siparis verildigi zaman bu metotta calısacak
    public static void createConfirmProcess(User user, Product product) {
        ConfirmProcess confirmProcess = new ConfirmProcess(user, product);
        ConcurrentConfirmManager.getInstance().getQueue().add(confirmProcess);
    }

    public User getUser() {
        return user;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public Long getConfirmingTime() {
        return confirmingTime;
    }

    public boolean isIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

}
