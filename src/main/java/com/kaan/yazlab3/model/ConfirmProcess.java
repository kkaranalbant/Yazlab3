/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kaan.yazlab3.model;

import com.kaan.yazlab3.service.ConcurrentConfirmManager;
import com.kaan.yazlab3.service.OrderService;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 *
 * @author root
 */
public class ConfirmProcess implements Runnable, Comparable<ConfirmProcess> {

    private User user;

    private Product product;

    private LocalDateTime orderDateTime;

    private Float priority;

    private Long timestamp;

    private Long confirmingTime;

    private boolean isActive;

    private OrderService orderService;

    private ConfirmProcess(User user, Product product, LocalDateTime orderDateTime) {
        this.user = user;
        this.product = product;
        timestamp = Instant.now().toEpochMilli();
        confirmingTime = Instant.now().toEpochMilli();
        orderService = OrderService.getInstance();
        priority = 0F;
        this.orderDateTime = orderDateTime;
    }

    private ConfirmProcess(User user, Product product, LocalDateTime orderDateTime, long timestamp) {
        this.user = user;
        this.product = product;
        this.timestamp = timestamp;
        confirmingTime = Instant.now().toEpochMilli();
        orderService = OrderService.getInstance();
        priority = 0F;
        this.orderDateTime = orderDateTime;
    }

    @Override
    public void run() {
        Order order = orderService.getByUserIdAndProductIdAndOrderDateTime(user.getId(), product.getId(), orderDateTime);
        orderService.confirmById(order.getId());
    }

    @Override
    public int compareTo(ConfirmProcess o) {
        int priorityComparison = Float.compare(o.priority, this.priority);
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
    public static void createConfirmProcess(User user, Product product , LocalDateTime orderDateTime) {
        ConfirmProcess confirmProcess = new ConfirmProcess(user, product,orderDateTime);
        confirmProcess.isActive = true;
        ConcurrentConfirmManager.getInstance().getQueue().add(confirmProcess);
    }

    public static void createOldConfirmProcess(User user, Product product, LocalDateTime orderTime) {
        long timestamp = orderTime.toInstant(ZoneOffset.UTC).toEpochMilli();
        ConfirmProcess confirmProcess = new ConfirmProcess(user, product, orderTime ,  timestamp);
        confirmProcess.isActive = true;
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

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public OrderService getOrderService() {
        return orderService;
    }

    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

    public Float getPriority() {
        return priority;
    }

    public LocalDateTime getOrderDateTime() {
        return orderDateTime;
    }

    public void setOrderDateTime(LocalDateTime orderDateTime) {
        this.orderDateTime = orderDateTime;
    }
    
    

}
