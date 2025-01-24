/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kaan.yazlab3.model;

import com.kaan.yazlab3.service.ConcurrentPurchaseManager;
import com.kaan.yazlab3.service.OrderService;
import java.time.Instant;

/**
 *
 * @author root
 */
public class PurchaseProcess implements Runnable, Comparable<PurchaseProcess> {

    private User user;

    private Product product;

    private Integer quantity;

    private Integer priority;

    private Long timestamp;

    private OrderService orderService;

    private PurchaseProcess(User user, Product product, Integer quantity) {
        this.user = user;
        this.product = product;
        this.quantity = quantity;
        this.priority = user.getUserType().getPriority();
        timestamp = Instant.now().toEpochMilli();
        orderService = OrderService.getInstance();
    }

    @Override
    public void run() {
        orderService.add(user, product, quantity);
    }

    @Override
    public int compareTo(PurchaseProcess o) {
        int priorityComparison = Integer.compare(this.priority, o.priority);
        if (priorityComparison != 0) {
            return priorityComparison;
        }
        return Long.compare(this.timestamp, o.timestamp);
    }

    public static void createProcess(User user, Product product, Integer quantity) {
        ConcurrentPurchaseManager.getInstance().getQueue().put(new PurchaseProcess(user, product, quantity));
    }

    public User getUser() {
        return user;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public OrderService getOrderService() {
        return orderService;
    }

    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }
    
    
    
}
